package com.nowakArtur97.myMoments.feature.user;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class UserService {

    @Value("${my-moments.default-user-role:ROLE_USER}")
    private String defaultUserRole;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder bCryptPasswordEncoder;

    private final RoleService roleService;

    Optional<UserEntity> findByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    Optional<UserEntity> findByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    public UserEntity register(UserDTO userDTO) throws RoleNotFoundException {

        UserEntity newUser = modelMapper.map(userDTO, UserEntity.class);

        newUser.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));

        RoleEntity role = roleService.findByName(defaultUserRole)
                .orElseThrow(() -> new RoleNotFoundException("Role with name: '" + defaultUserRole + "' not found."));

        newUser.addRole(role);

        return userRepository.save(newUser);
    }
}
