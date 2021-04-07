package com.nowakArtur97.myMoments.feature.user.shared;

import com.nowakArtur97.myMoments.feature.user.registration.UserRegistrationDTO;
import com.nowakArtur97.myMoments.feature.user.resource.UserUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.management.relation.RoleNotFoundException;
import javax.persistence.Basic;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated({Basic.class, UserValidationGroupSequence.class})
public class UserService {

    @Value("${my-moments.default-user-role:USER_ROLE}")
    private String defaultUserRole;

    private final UserRepository userRepository;

    private final UserMapper userMapper;

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

    public UserEntity registerUser(@Valid UserRegistrationDTO userRegistrationDTO, MultipartFile image)
            throws RoleNotFoundException, IOException {

        RoleEntity role = roleService.findByName(defaultUserRole)
                .orElseThrow(() -> new RoleNotFoundException("Role with name: '" + defaultUserRole + "' not found."));

        UserEntity newUserEntity = userMapper.convertDTOToEntity(userRegistrationDTO, image, role);

        return userRepository.save(newUserEntity);
    }

    public UserEntity updateUser(UserEntity userEntity, @Valid UserUpdateDTO userUpdateDTO, MultipartFile image) throws IOException {

        userMapper.convertDTOToEntity(userEntity, userUpdateDTO, image);

        if (image != null) {
            userEntity.getProfile().setImage(image.getBytes());
        }

        return userRepository.save(userEntity);
    }
}
