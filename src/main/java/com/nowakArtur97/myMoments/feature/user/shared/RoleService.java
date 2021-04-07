package com.nowakArtur97.myMoments.feature.user.shared;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
class RoleService {

    private final RoleRepository roleRepository;

    public Optional<RoleEntity> findByName(String name) {

        return roleRepository.findByName(name);
    }
}
