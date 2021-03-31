package com.nowakArtur97.myMoments.feature.user.registration;

import com.nowakArtur97.myMoments.feature.user.shared.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByName(String name);
}
