package com.nowakArtur97.myMoments.feature.user;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
class UserService {

    @Value("${my-moments.default-user-role:user}")
    private String defaultUserRole;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final RoleService roleService;

    Optional<UserEntity> findByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    Optional<UserEntity> findByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    Optional<UserEntity> findByUsernameOrEmail(String username, String email) {

        return userRepository.findByUsernameOrEmail(username, email);
    }

    public UserEntity register(UserDTO userDTO) {

        UserEntity newUser = modelMapper.map(userDTO, UserEntity.class);

        newUser.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));

        RoleEntity role = roleService.findByName(defaultUserRole)
                .orElseThrow(() -> RoleNotFoundException("Role with name: '" + defaultUserRole + "' not found."));

        newUser.addRole(role);

        return userRepository.save(newUser);
    }
}
