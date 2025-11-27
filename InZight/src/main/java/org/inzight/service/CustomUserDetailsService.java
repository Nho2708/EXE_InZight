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

        // Nếu có '@' -> cố gắng tìm bằng email
        if (login.contains("@")) {
            user = userRepository.findByEmail(login)
                    .orElse(null);
        }

        // Nếu chưa tìm được -> tìm bằng username
        if (user == null) {
            user = userRepository.findByUsername(login)
                    .orElse(null);
        }


        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + login);
        }

        // Trả về UserDetails. GHI CHÚ: setUsername là user.getUsername() để nhất quán
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())   // dùng username cố định
                .password(user.getPassword())
                .authorities("USER")
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

}
