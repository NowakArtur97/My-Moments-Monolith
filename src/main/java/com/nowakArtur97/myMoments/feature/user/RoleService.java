package com.nowakArtur97.myMoments.feature.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
class RoleService {

    private final RoleRepository roleRepository;

    Optional<RoleEntity> findByName(String name) {

        return roleRepository.findByName(name);
    }
}
