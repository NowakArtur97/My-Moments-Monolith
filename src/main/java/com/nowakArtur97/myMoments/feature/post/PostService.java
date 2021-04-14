package com.nowakArtur97.myMoments.feature.post;

import com.nowakArtur97.myMoments.feature.picture.PictureEntity;
import com.nowakArtur97.myMoments.feature.user.entity.UserEntity;
import com.nowakArtur97.myMoments.feature.user.entity.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.io.IOException;
import java.io.UncheckedIOException;

@Service
@RequiredArgsConstructor
@Validated
class PostService {

    private final PostRepository postRepository;

    private final UserService userService;

    PostEntity createPost(String username, @Valid PostDTO postDTO) {

        UserEntity userEntity = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with name: '" + username + "' not found."));

        PostEntity postEntity = new PostEntity(postDTO.getCaption(), userEntity);

        setPhotos(postDTO, postEntity);

        userEntity.addPost(postEntity);

        return postRepository.save(postEntity);
    }

    private void setPhotos(PostDTO postDTO, PostEntity postEntity) {

        postDTO.getPhotos().forEach(photo -> {
            try {
                postEntity.addPhoto(new PictureEntity(photo.getBytes()));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
}
