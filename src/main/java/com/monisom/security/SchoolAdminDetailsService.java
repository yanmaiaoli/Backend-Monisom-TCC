package com.monisom.security;

import com.monisom.repository.SchoolAdminRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SchoolAdminDetailsService implements UserDetailsService {

    private final SchoolAdminRepository repository;

    public SchoolAdminDetailsService(SchoolAdminRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email)
                .map(admin -> User.withUsername(admin.getEmail())
                        .password(admin.getPasswordHash())
                        .roles("ADMIN")
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Admin não encontrado"));
    }
}
