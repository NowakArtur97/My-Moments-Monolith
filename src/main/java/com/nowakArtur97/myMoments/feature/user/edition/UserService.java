package com.nowakArtur97.myMoments.feature.user.edition;

import com.nowakArtur97.myMoments.feature.user.registration.UserDTO;
import com.nowakArtur97.myMoments.feature.user.shared.UserEntity;
import com.nowakArtur97.myMoments.feature.user.shared.UserRepository;
import com.nowakArtur97.myMoments.feature.user.shared.UserValidationGroupSequence;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated(UserValidationGroupSequence.class)
 class UserService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    public Optional<UserEntity> findById(Long id) {

        return userRepository.findById(id);
    }

    public UserEntity updateUser(UserEntity userEntity, @Valid UserDTO userDTO, MultipartFile image) {

        return null;
    }
}
