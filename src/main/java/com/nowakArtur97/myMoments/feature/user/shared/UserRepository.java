package com.nowakArtur97.myMoments.feature.user.shared;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsernameOrEmail(String username, String email);

    boolean existsUserByUsername(String username);

    boolean existsUserByEmail(String email);
}
