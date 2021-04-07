package com.nowakArtur97.myMoments.feature.user.shared;

import com.nowakArtur97.myMoments.feature.user.registration.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.management.relation.RoleNotFoundException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated(UserValidationGroupSequence.class)
@Slf4j
public class UserService {

    @Value("${my-moments.default-user-role:USER_ROLE}")
    private String defaultUserRole;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;
    private final UserMapper userMapper;

//    private final PasswordEncoder bCryptPasswordEncoder;

    private final RoleService roleService;

    public boolean isUsernameAlreadyInUse(String username) {

        return userRepository.existsUserByUsername(username);
    }

    public boolean isEmailAlreadyInUse(String email) {

        return userRepository.existsUserByEmail(email);
    }

    public Optional<UserEntity> findById(Long id) {

        return userRepository.findById(id);
    }

    public UserEntity registerUser(@Valid UserDTO userDTO, MultipartFile image) throws RoleNotFoundException, IOException {

        UserEntity newUser = setupUserEntity(userDTO, image);

        RoleEntity role = roleService.findByName(defaultUserRole)
                .orElseThrow(() -> new RoleNotFoundException("Role with name: '" + defaultUserRole + "' not found."));

        newUser.addRole(role);

        return userRepository.save(newUser);
    }

    public UserEntity updateUser(UserEntity userEntity, @Valid UserDTO userDTO, MultipartFile image) throws IOException {

        log.info("BEFORE");
        log.info(userEntity.toString());
        log.info(userEntity.getProfile().toString());

//        userEntity = setupUserEntity(userDTO, image);
        userMapper.convertDTOToEntity(userDTO, userEntity);

        if (image != null) {
            userEntity.getProfile().setImage(image.getBytes());
        }

        log.info("AFTER");
        log.info(userEntity.toString());
        log.info(userEntity.getProfile().toString());

        return userRepository.save(userEntity);
    }

    private UserEntity setupUserEntity(UserDTO userDTO, MultipartFile image) throws IOException {

        UserEntity userEntity;

        if (userDTO.getProfile() != null) {
            userDTO.getProfile().setGender(userDTO.getProfile().getGender().toUpperCase());
        }

        userEntity = modelMapper.map(userDTO, UserEntity.class);

        if (image != null) {
            userEntity.getProfile().setImage(image.getBytes());
        }

//        userEntity.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));

        return userEntity;
    }
}
