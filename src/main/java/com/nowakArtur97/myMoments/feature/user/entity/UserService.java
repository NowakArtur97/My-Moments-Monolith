package com.nowakArtur97.myMoments.feature.user.entity;

import com.nowakArtur97.myMoments.common.exception.ForbiddenException;
import com.nowakArtur97.myMoments.common.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.feature.post.PostEntity;
import com.nowakArtur97.myMoments.feature.user.resource.UserRegistrationDTO;
import com.nowakArtur97.myMoments.feature.user.resource.UserUpdateDTO;
import com.nowakArtur97.myMoments.feature.user.validation.UserValidationGroupSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.management.relation.RoleNotFoundException;
import javax.persistence.Basic;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

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

    public Optional<UserEntity> findByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    public UserEntity registerUser(@Valid UserRegistrationDTO userRegistrationDTO, MultipartFile image)
            throws RoleNotFoundException, IOException {

        RoleEntity roleEntity = roleService.findByName(defaultUserRole)
                .orElseThrow(() -> new RoleNotFoundException("Role with name: '" + defaultUserRole + "' not found."));

        UserEntity newUserEntity = userMapper.convertDTOToEntity(userRegistrationDTO, image, roleEntity);

        return userRepository.save(newUserEntity);
    }

    public UserEntity updateUser(String username, @Valid UserUpdateDTO userUpdateDTO, MultipartFile image)
            throws IOException {

        UserEntity userEntity = findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username: '" + username + "' not found."));

        if (!isUserChangingOwnData(userEntity.getUsername())) {
            throw new ForbiddenException("User can only update his own account.");
        }

        userUpdateDTO.setId(userEntity.getId());

        userMapper.convertDTOToEntity(userEntity, userUpdateDTO, image);

        return userRepository.save(userEntity);
    }

    public void deleteUser(String username) {

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username: '" + username + "' not found."));

        if (isUserChangingOwnData(userEntity.getUsername())) {
            userRepository.delete(userEntity);
        } else {
            throw new ForbiddenException("User can only delete his own account.");
        }
    }

    public boolean isUserChangingOwnData(String username) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String usernameInContext = auth != null ? auth.getName() : "";

        return username.equals(usernameInContext);
    }

    public Set<PostEntity> getUsersPosts(String username) {
        return findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username: '" + username + "' not found."))
                .getPosts();
    }

    public Set<PostEntity> getUsersPosts(Long id) {

        return findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: '" + id + "' not found."))
                .getPosts();
    }
}
