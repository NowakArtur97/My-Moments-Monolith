package com.nowakArtur97.myMoments.feature.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
class UserService {

    private final UserRepository userRepository;

    Optional<UserEntity> findByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    Optional<UserEntity> findByEmail(String email) {

        return userRepository.findByEmail(email);
    }
}
