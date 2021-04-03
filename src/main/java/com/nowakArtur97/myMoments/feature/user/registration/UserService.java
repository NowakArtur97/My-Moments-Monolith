package com.nowakArtur97.myMoments.feature.user.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowakArtur97.myMoments.feature.user.shared.RoleEntity;
import com.nowakArtur97.myMoments.feature.user.shared.UserEntity;
import com.nowakArtur97.myMoments.feature.user.shared.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.management.relation.RoleNotFoundException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated(UserValidationGroupSequence.class)
public class UserService {

    @Value("${my-moments.default-user-role:ROLE_USER}")
    private String defaultUserRole;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder bCryptPasswordEncoder;

    private final RoleService roleService;

    public Optional<UserEntity> findByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    public Optional<UserEntity> findByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    UserEntity register(@Valid UserDTO userDTO, MultipartFile image) throws RoleNotFoundException, IOException {

        if (userDTO.getProfile() != null) {
            userDTO.getProfile().setGender(userDTO.getProfile().getGender().toUpperCase());
        }

        UserEntity newUser = modelMapper.map(userDTO, UserEntity.class);

        if (image != null) {
            newUser.getProfile().setImage(image.getBytes());
        }

        newUser.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));

        RoleEntity role = roleService.findByName(defaultUserRole)
                .orElseThrow(() -> new RoleNotFoundException("Role with name: '" + defaultUserRole + "' not found."));

        newUser.addRole(role);

        return userRepository.save(newUser);
    }

    UserDTO getUserDTOFromString(String userAsString) {

        try {

            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readValue(userAsString, UserDTO.class);

        } catch (IOException exception) {
            log.info("IOException: " + exception.toString());
        }

        return new UserDTO();
    }
}
