package com.monisom.dto;

import java.util.UUID;

public record ClassGroupResponse(UUID id, String name, String turno, Integer capacidade) {
}
