package com.monisom.dto;

import java.util.UUID;

public record LoginResponse(String token, String name, UUID schoolId, String email) {
}
