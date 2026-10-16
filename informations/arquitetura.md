# MoniSom — Arquitetura (colinha)

## Stakeholders
- **Professor** — app mobile: inicia/encerra o monitoramento, escolhe a sala, responde percepção/estresse.
- **Diretor/Administrador** — web: acompanha o ruído ao vivo, consulta histórico e indicadores, gerencia cadastros.
- **Aluno** — fonte do ruído na sala (não interage diretamente com o sistema).

## Componentes

| Bloco | O que faz | Tecnologia |
|---|---|---|
| **Mobile (App)** | captura áudio, ponderação A, LAeq/s, grava em background, envia leituras | Kotlin, Compose, `AudioRecord`, foreground service, Supabase (PostgREST) |
| **Frontend (Web)** | painel do diretor: ao vivo (cor por nível), finalizados (gráfico), indicadores, CRUD | React + Vite, MUI, Axios, Recharts, JWT |
| **API REST** | endpoints HTTP, autenticação, CRUD e leitura dos monitoramentos | Spring Boot (Java 21), Spring Security (JWT), JPA |
| **Producer** | poll das leituras no banco → publica em `monitoring-reading` (banco → Kafka) | Spring `@Scheduled` + `KafkaTemplate` |
| **Kafka Streams (topologias)** | processamento em tempo real (Kafka → Kafka): 2 topologias (janelas de 2min; nível/alertas) | Kafka Streams, RocksDB |
| **Consumers** | consomem os resultados e persistem no banco (Kafka → banco) | Spring `@KafkaListener` |
| **Jobs** | consolidação final e manutenção (ver abaixo) | Spring `@Scheduled`, SQL nativo |
| **Banco de Dados** | cadastros + leituras cruas + resultados consolidados + auth | Supabase (PostgreSQL) |
| **Kafka Cluster** | armazena/distribui os tópicos | Apache Kafka 3.7.1, 3 brokers, KRaft (sem Zookeeper) |

## Fluxo do dado
```
App → Supabase → Producer → monitoring-reading → Streams → janelas/alerts → Consumers → Supabase → Diretor
```

## Jobs — por que a consolidação final é feita aqui

**Última janela:** o `suppress` do Kafka só emite uma janela quando o "stream time" avança; quando o monitoramento termina, a última janela (parcial) nunca é emitida. O job a preenche a partir das leituras cruas.
**Soberania do Kafka:** nas janelas, o Kafka grava com `ON CONFLICT DO UPDATE` (pode sobrescrever); o backend usa `DO NOTHING` (só preenche a lacuna, nunca sobrescreve o Kafka).

**Cálculo geral (sumário: Leq total + L10/L50/L90):** feito no backend (SQL), porque:
1. É **batch** (uma vez, ao finalizar), não streaming — Kafka é para tempo real.
2. Não há **"evento de fim"** no Kafka (o app só grava `finished_at` no banco; fazer no Kafka exigiria evento de fim ou punctuator wall-clock).
3. O **dado bruto já está no banco** (durável) — o total é só uma agregação sobre ele; recalcular no Kafka duplicaria a lógica.
4. **Percentis** precisam da distribuição completa — no SQL é um `percentile_cont`; no Kafka seria estado ilimitado.
5. **"Kafka é mais seguro" tem um equívoco:** as topologias rodam DENTRO do backend (mesma JVM). Se o backend cair, o Streams também para; o broker só armazena, não calcula. Os dados brutos ficam no Supabase (replicado), e o job é idempotente (re-executa a cada 30s).
6. **Separação de responsabilidades:** Kafka = tempo real (janelas, alertas, nível); banco/backend = consolidação final (batch).

## Relações entre os componentes
As partes do backend **não se chamam diretamente** — comunicam-se de forma assíncrona via **tópicos Kafka** e via **tabelas do banco** (desacoplamento / arquitetura orientada a eventos). É o que justifica o uso do Kafka.
