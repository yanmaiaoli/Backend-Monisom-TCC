package com.monisom.dto;

import java.util.UUID;

public record SupabaseUserResponse(UUID id, String email) {
}
