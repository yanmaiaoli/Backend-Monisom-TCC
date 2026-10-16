package com.monisom.kafka;

import com.monisom.kafka.model.Alert;
import com.monisom.kafka.model.LeqStatus;
import com.monisom.kafka.model.ReadingAggregate;
import com.monisom.kafka.model.WindowedReading;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JacksonJsonSerde;

public final class KafkaSerdes {

    private KafkaSerdes() {
    }

    public static final Serde<String> STRING = Serdes.String();

    public static final Serde<WindowedReading> WINDOWED_READING =
            new JacksonJsonSerde<>(WindowedReading.class).noTypeInfo();

    public static final Serde<LeqStatus> LEQ_STATUS =
            new JacksonJsonSerde<>(LeqStatus.class).noTypeInfo();

    public static final Serde<Alert> ALERT =
            new JacksonJsonSerde<>(Alert.class).noTypeInfo();

    public static final Serde<ReadingAggregate> READING_AGGREGATE =
            new JacksonJsonSerde<>(ReadingAggregate.class).noTypeInfo();
}
