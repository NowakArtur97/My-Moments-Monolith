package com.nowakArtur97.myMoments.feature.comment;

import com.nowakArtur97.myMoments.common.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.feature.post.PostEntity;
import com.nowakArtur97.myMoments.feature.post.PostService;
import com.nowakArtur97.myMoments.feature.user.entity.UserEntity;
import com.nowakArtur97.myMoments.feature.user.entity.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class CommentService {

    private final CommentRepository commentRepository;

    private final UserService userService;

    private final PostService postService;

    CommentEntity addComment(Long postId, String username, CommentDTO commentDTO) {

        UserEntity userEntity = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with name: '" + username + "' not found."));
        PostEntity postEntity = postService.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", postId));

        CommentEntity commentEntity = new CommentEntity(commentDTO.getContent());
        userEntity.addComment(commentEntity);
        postEntity.addComment(commentEntity);

        return commentRepository.save(commentEntity);
    }
}
