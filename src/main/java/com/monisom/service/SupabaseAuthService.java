package com.monisom.service;

import com.monisom.dto.SupabaseUserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.UUID;

@Service
public class SupabaseAuthService {

    private final RestClient restClient;
    private final String supabaseUrl;
    private final String serviceRoleKey;

    public SupabaseAuthService(
            @Value("${supabase.url}") String supabaseUrl,
            @Value("${supabase.service-role-key}") String serviceRoleKey) {
        this.supabaseUrl = supabaseUrl;
        this.serviceRoleKey = serviceRoleKey;
        this.restClient = RestClient.builder().build();
    }

    public UUID createUser(String email, String password) {
        Map<String, Object> body = Map.of(
                "email", email,
                "password", password,
                "email_confirm", true);

        SupabaseUserResponse response = restClient.post()
                .uri(supabaseUrl + "/auth/v1/admin/users")
                .header("Authorization", "Bearer " + serviceRoleKey)
                .header("apikey", serviceRoleKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(SupabaseUserResponse.class);

        return response.id();
    }
}
