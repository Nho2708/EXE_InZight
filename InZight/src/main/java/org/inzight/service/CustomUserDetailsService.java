package org.inzight.service;


import org.inzight.entity.User;
import org.inzight.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

        User user = null;

        if (login.contains("@")) {
            user = userRepository.findByEmail(login).orElse(null);
        }

        if (user == null) {
            user = userRepository.findByUsername(login).orElse(null);
        }

        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + login);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole()) // 👈 LẤY QUYỀN TỪ DB
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

}
