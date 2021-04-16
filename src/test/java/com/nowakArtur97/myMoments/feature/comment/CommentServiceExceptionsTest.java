package com.nowakArtur97.myMoments.feature.comment;

import com.nowakArtur97.myMoments.common.exception.ForbiddenException;
import com.nowakArtur97.myMoments.common.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.feature.post.PostEntity;
import com.nowakArtur97.myMoments.feature.post.PostService;
import com.nowakArtur97.myMoments.feature.post.PostTestBuilder;
import com.nowakArtur97.myMoments.feature.user.entity.UserEntity;
import com.nowakArtur97.myMoments.feature.user.entity.UserService;
import com.nowakArtur97.myMoments.feature.user.testBuilder.UserTestBuilder;
import com.nowakArtur97.myMoments.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.testUtil.generator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("CommentService_Tests")
class CommentServiceExceptionsTest {

    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @Mock
    private PostService postService;

    private static CommentTestBuilder commentTestBuilder;
    private static PostTestBuilder postTestBuilder;
    private static UserTestBuilder userTestBuilder;

    @BeforeAll
    static void setUpBuilders() {

        commentTestBuilder = new CommentTestBuilder();
        postTestBuilder = new PostTestBuilder();
        userTestBuilder = new UserTestBuilder();
    }

    @BeforeEach
    void setUp() {

        commentService = new CommentService(commentRepository, userService, postService);
    }

    @Test
    void when_add_comment_by_not_existing_user_should_throw_exception() {

        Long postId = 2L;
        String notExistingUsername = "iAmNotExist";

        CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.build(ObjectType.CREATE_DTO);

        when(userService.findByUsername(notExistingUsername)).thenReturn(Optional.empty());

        assertAll(() -> assertThrows(UsernameNotFoundException.class,
                () -> commentService.addComment(postId, notExistingUsername, commentDTOExpected),
                "should throw UsernameNotFoundException but wasn't"),
                () -> verify(userService, times(1)).findByUsername(notExistingUsername),
                () -> verifyNoMoreInteractions(userService),
                () -> verifyNoInteractions(postService),
                () -> verifyNoInteractions(commentRepository));
    }

    @Test
    void when_add_comment_to_not_existing_post_should_throw_exception() {

        Long notExistingPostId = 2L;

        CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.build(ObjectType.CREATE_DTO);
        UserEntity userExpected = (UserEntity) userTestBuilder.build(ObjectType.ENTITY);

        when(userService.findByUsername(userExpected.getUsername())).thenReturn(Optional.of(userExpected));
        when(postService.findById(notExistingPostId)).thenReturn(Optional.empty());

        assertAll(() -> assertThrows(ResourceNotFoundException.class,
                () -> commentService.addComment(notExistingPostId, userExpected.getUsername(), commentDTOExpected),
                "should throw ResourceNotFoundException but wasn't"),
                () -> verify(userService, times(1)).findByUsername(userExpected.getUsername()),
                () -> verifyNoMoreInteractions(userService),
                () -> verify(postService, times(1)).findById(notExistingPostId),
                () -> verifyNoMoreInteractions(postService),
                () -> verifyNoInteractions(commentRepository));
    }

    @Test
    void when_update_comment_by_not_existing_user_should_throw_exception() {

        String notExistingUsername = "iAmNotExist";
        Long postId = 2L;
        Long commentId = 3L;

        CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.build(ObjectType.CREATE_DTO);

        when(userService.findByUsername(notExistingUsername)).thenReturn(Optional.empty());

        assertAll(() -> assertThrows(UsernameNotFoundException.class,
                () -> commentService.updateComment(postId, commentId, notExistingUsername, commentDTOExpected),
                "should throw UsernameNotFoundException but wasn't"),
                () -> verify(userService, times(1)).findByUsername(notExistingUsername),
                () -> verifyNoMoreInteractions(userService),
                () -> verifyNoInteractions(postService),
                () -> verifyNoInteractions(commentRepository));
    }

    @Test
    void when_update_comment_to_not_existing_post_should_throw_exception() {

        Long notExistingPostId = 2L;
        Long commentId = 3L;

        CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.build(ObjectType.CREATE_DTO);
        UserEntity userExpected = (UserEntity) userTestBuilder.build(ObjectType.ENTITY);

        when(userService.findByUsername(userExpected.getUsername())).thenReturn(Optional.of(userExpected));
        when(postService.findById(notExistingPostId)).thenReturn(Optional.empty());

        assertAll(() -> assertThrows(ResourceNotFoundException.class,
                () -> commentService.updateComment(notExistingPostId, commentId, userExpected.getUsername(), commentDTOExpected),
                "should throw ResourceNotFoundException but wasn't"),
                () -> verify(userService, times(1)).findByUsername(userExpected.getUsername()),
                () -> verifyNoMoreInteractions(userService),
                () -> verify(postService, times(1)).findById(notExistingPostId),
                () -> verifyNoMoreInteractions(postService),
                () -> verifyNoInteractions(commentRepository));
    }

    @Test
    void when_update_not_existing_comment_on_specific_post_should_throw_exception() {

        Long postId = 2L;
        Long notExistingCommentId = 3L;
        String updatedContent = "new content";

        CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.withContent(updatedContent).build(ObjectType.CREATE_DTO);
        UserEntity userExpected = (UserEntity) userTestBuilder.build(ObjectType.ENTITY);
        PostEntity postExpected = (PostEntity) postTestBuilder.withAuthor(userExpected).build(ObjectType.ENTITY);
        postExpected.setId(postId);
        userExpected.addPost(postExpected);
        CommentEntity commentExpectedBeforeUpdate = (CommentEntity) commentTestBuilder.withAuthor(userExpected)
                .withRelatedPost(postExpected).build(ObjectType.ENTITY);
        userExpected.addComment(commentExpectedBeforeUpdate);

        when(userService.findByUsername(userExpected.getUsername())).thenReturn(Optional.of(userExpected));
        when(postService.findById(postId)).thenReturn(Optional.of(postExpected));
        when(commentRepository.findById(notExistingCommentId)).thenReturn(Optional.empty());

        assertAll(() -> assertThrows(ResourceNotFoundException.class,
                () -> commentService.updateComment(postId, notExistingCommentId, userExpected.getUsername(), commentDTOExpected),
                "should throw ResourceNotFoundException but wasn't"),
                () -> verify(userService, times(1)).findByUsername(userExpected.getUsername()),
                () -> verifyNoMoreInteractions(userService),
                () -> verify(postService, times(1)).findById(postId),
                () -> verifyNoMoreInteractions(postService),
                () -> verify(commentRepository, times(1)).findById(notExistingCommentId),
                () -> verifyNoMoreInteractions(commentRepository));
    }

    @Test
    void when_update_not_existing_comment_should_throw_exception() {

        Long postId = 2L;
        Long notExistingCommentId = 3L;
        String updatedContent = "new content";

        CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.withContent(updatedContent).build(ObjectType.CREATE_DTO);
        UserEntity userExpected = (UserEntity) userTestBuilder.build(ObjectType.ENTITY);
        PostEntity postExpected = (PostEntity) postTestBuilder.withAuthor(userExpected).build(ObjectType.ENTITY);
        postExpected.setId(postId);
        userExpected.addPost(postExpected);
        CommentEntity commentExpectedBeforeUpdate = (CommentEntity) commentTestBuilder.withAuthor(userExpected)
                .withRelatedPost(postExpected).build(ObjectType.ENTITY);
        userExpected.addComment(commentExpectedBeforeUpdate);
        postExpected.addComment(commentExpectedBeforeUpdate);

        when(userService.findByUsername(userExpected.getUsername())).thenReturn(Optional.of(userExpected));
        when(postService.findById(postId)).thenReturn(Optional.of(postExpected));
        when(commentRepository.findById(notExistingCommentId)).thenReturn(Optional.empty());

        assertAll(() -> assertThrows(ResourceNotFoundException.class,
                () -> commentService.updateComment(postId, notExistingCommentId, userExpected.getUsername(), commentDTOExpected),
                "should throw ResourceNotFoundException but wasn't"),
                () -> verify(userService, times(1)).findByUsername(userExpected.getUsername()),
                () -> verifyNoMoreInteractions(userService),
                () -> verify(postService, times(1)).findById(postId),
                () -> verifyNoMoreInteractions(postService),
                () -> verify(commentRepository, times(1)).findById(notExistingCommentId),
                () -> verifyNoMoreInteractions(commentRepository));
    }

    @Test
    void when_update_some_other_user_got_from_jwt_should_throw_exception() {

        Long postId = 2L;
        Long commentId = 3L;
        String updatedContent = "new content";

        CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.withContent(updatedContent).build(ObjectType.CREATE_DTO);
        UserEntity userExpected = (UserEntity) userTestBuilder.build(ObjectType.ENTITY);
        PostEntity postExpected = (PostEntity) postTestBuilder.withAuthor(userExpected).build(ObjectType.ENTITY);
        postExpected.setId(postId);
        userExpected.addPost(postExpected);
        CommentEntity commentExpectedBeforeUpdate = (CommentEntity) commentTestBuilder.withAuthor(userExpected)
                .withRelatedPost(postExpected).build(ObjectType.ENTITY);
        userExpected.addComment(commentExpectedBeforeUpdate);
        postExpected.addComment(commentExpectedBeforeUpdate);

        when(userService.findByUsername(userExpected.getUsername())).thenReturn(Optional.of(userExpected));
        when(userService.isUserChangingOwnData(userExpected.getUsername())).thenReturn(false);
        when(postService.findById(postId)).thenReturn(Optional.of(postExpected));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(commentExpectedBeforeUpdate));

        assertAll(() -> assertThrows(ForbiddenException.class,
                () -> commentService.updateComment(postId, commentId, userExpected.getUsername(), commentDTOExpected),
                "should throw ForbiddenException but wasn't"),
                () -> verify(userService, times(1)).findByUsername(userExpected.getUsername()),
                () -> verify(userService, times(1)).isUserChangingOwnData(userExpected.getUsername()),
                () -> verifyNoMoreInteractions(userService),
                () -> verify(postService, times(1)).findById(postId),
                () -> verifyNoMoreInteractions(postService),
                () -> verify(commentRepository, times(1)).findById(commentId),
                () -> verifyNoMoreInteractions(commentRepository));
    }

    @Test
    void when_update_some_other_user_comment_should_throw_exception() {

        Long postId = 2L;
        Long commentId = 3L;
        String updatedContent = "new content";

        CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.withContent(updatedContent).build(ObjectType.CREATE_DTO);
        UserEntity userExpected = (UserEntity) userTestBuilder.build(ObjectType.ENTITY);
        UserEntity someOtherUserExpected = (UserEntity) userTestBuilder.withUsername("some-other-user").build(ObjectType.ENTITY);
        PostEntity postExpected = (PostEntity) postTestBuilder.withAuthor(userExpected).build(ObjectType.ENTITY);
        postExpected.setId(postId);
        userExpected.addPost(postExpected);
        CommentEntity commentExpectedBeforeUpdate = (CommentEntity) commentTestBuilder.withAuthor(userExpected)
                .withRelatedPost(postExpected).build(ObjectType.ENTITY);
        userExpected.addComment(commentExpectedBeforeUpdate);
        postExpected.addComment(commentExpectedBeforeUpdate);

        when(userService.findByUsername(someOtherUserExpected.getUsername())).thenReturn(Optional.of(someOtherUserExpected));
        when(userService.isUserChangingOwnData(someOtherUserExpected.getUsername())).thenReturn(true);
        when(postService.findById(postId)).thenReturn(Optional.of(postExpected));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(commentExpectedBeforeUpdate));

        assertAll(() -> assertThrows(ForbiddenException.class,
                () -> commentService.updateComment(postId, commentId, someOtherUserExpected.getUsername(), commentDTOExpected),
                "should throw ForbiddenException but wasn't"),
                () -> verify(userService, times(1)).findByUsername(someOtherUserExpected.getUsername()),
                () -> verify(userService, times(1))
                        .isUserChangingOwnData(someOtherUserExpected.getUsername()),
                () -> verifyNoMoreInteractions(userService),
                () -> verify(postService, times(1)).findById(postId),
                () -> verifyNoMoreInteractions(postService),
                () -> verify(commentRepository, times(1)).findById(commentId),
                () -> verifyNoMoreInteractions(commentRepository));
    }
}

