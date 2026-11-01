package com.smartjobportal.security;

import com.smartjobportal.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository users;
    public CustomUserDetailsService(UserRepository users) { this.users = users; }
    @Override @Transactional(readOnly=true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = users.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return User.withUsername(user.getEmail()).password(user.getPasswordHash())
                .roles(user.getRole().name()).build();
    }
}
