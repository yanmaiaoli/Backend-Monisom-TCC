package com.monisom.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AllocationRequest(@NotNull UUID teacherId) {
}
