package com.nowakArtur97.myMoments.feature.comment;

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
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("CommentService_Tests")
class CommentServiceTest {

    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @Mock
    private PostService postService;

    private static MockedStatic mocked;

    private static CommentTestBuilder commentTestBuilder;
    private static PostTestBuilder postTestBuilder;
    private static UserTestBuilder userTestBuilder;

    @BeforeAll
    private static void setUpBuildersAndUUID() {

        commentTestBuilder = new CommentTestBuilder();
        postTestBuilder = new PostTestBuilder();
        userTestBuilder = new UserTestBuilder();

        UUID uuid = UUID.randomUUID();
        mocked = mockStatic(UUID.class);
        mocked.when(UUID::randomUUID).thenReturn(uuid);
    }

    @AfterAll
    private static void cleanUp() {

        if (!mocked.isClosed()) {
            mocked.close();
        }
    }

    @BeforeEach
    private void setUp() {

        commentService = new CommentService(commentRepository, userService, postService);
    }

    @Nested
    class CreateCommentTest {

        @Test
        void when_add_comment_should_create_comment() {

            Long postId = 2L;

            CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.build(ObjectType.CREATE_DTO);
            UserEntity userExpected = (UserEntity) userTestBuilder.build(ObjectType.ENTITY);
            PostEntity postExpected = (PostEntity) postTestBuilder.withAuthor(userExpected).build(ObjectType.ENTITY);
            postExpected.setId(postId);
            CommentEntity commentExpected = (CommentEntity) commentTestBuilder.withAuthor(userExpected).withRelatedPost(postExpected)
                    .build(ObjectType.ENTITY);

            when(userService.findByUsername(userExpected.getUsername())).thenReturn(Optional.of(userExpected));
            when(postService.findById(postId)).thenReturn(Optional.of(postExpected));
            when(commentRepository.save(commentExpected)).thenReturn(commentExpected);

            CommentEntity commentActual = commentService.addComment(postId, userExpected.getUsername(), commentDTOExpected);

            assertAll(() -> assertEquals(commentExpected, commentActual,
                    () -> "should return comment: " + commentExpected + ", but was" + commentActual),
                    () -> assertEquals(commentExpected.getId(), commentActual.getId(),
                            () -> "should return comment with id: " + commentExpected.getId() + ", but was"
                                    + commentActual.getId()),
                    () -> assertEquals(commentExpected.getContent(), commentActual.getContent(),
                            () -> "should return comment with content: " + commentExpected.getContent() + ", but was"
                                    + commentActual.getContent()),
                    () -> assertEquals(commentExpected.getAuthor(), commentActual.getAuthor(),
                            () -> "should return comment with author: " + commentExpected.getAuthor() + ", but was"
                                    + commentActual.getAuthor()),
                    () -> assertEquals(commentExpected.getRelatedPost(), commentActual.getRelatedPost(),
                            () -> "should return comment with related post: " + commentExpected.getRelatedPost() + ", but was"
                                    + commentActual.getRelatedPost()),
                    () -> verify(userService, times(1)).findByUsername(userExpected.getUsername()),
                    () -> verifyNoMoreInteractions(userService),
                    () -> verify(postService, times(1)).findById(postId),
                    () -> verifyNoMoreInteractions(postService),
                    () -> verify(commentRepository, times(1)).save(commentExpected),
                    () -> verifyNoMoreInteractions(commentRepository));
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
    }

    @Nested
    class UpdateCommentTest {

        @Test
        void when_update_comment_should_update_comment() {

            Long postId = 2L;
            Long commentId = 3L;
            String updatedContent = "new content";

            CommentDTO commentDTOExpected = (CommentDTO) commentTestBuilder.withContent(updatedContent).build(ObjectType.CREATE_DTO);
            UserEntity userExpected = (UserEntity) userTestBuilder.build(ObjectType.ENTITY);
            PostEntity postExpected = (PostEntity) postTestBuilder.withAuthor(userExpected).build(ObjectType.ENTITY);
            postExpected.setId(postId);
            userExpected.addPost(postExpected);
            CommentEntity commentExpectedBeforeUpdate = (CommentEntity) commentTestBuilder.withId(commentId).withAuthor(userExpected)
                    .withRelatedPost(postExpected).build(ObjectType.ENTITY);
            userExpected.addComment(commentExpectedBeforeUpdate);
            postExpected.addComment(commentExpectedBeforeUpdate);
            CommentEntity commentExpected = (CommentEntity) commentTestBuilder.withContent(updatedContent).withAuthor(userExpected)
                    .withRelatedPost(postExpected).build(ObjectType.ENTITY);

            when(userService.findByUsername(userExpected.getUsername())).thenReturn(Optional.of(userExpected));
            when(userService.isUserChangingOwnData(userExpected.getUsername())).thenReturn(true);
            when(postService.findById(postId)).thenReturn(Optional.of(postExpected));
            when(commentRepository.findById(commentId)).thenReturn(Optional.of(commentExpectedBeforeUpdate));
            when(commentRepository.save(commentExpected)).thenReturn(commentExpected);

            CommentEntity commentActual = commentService.updateComment(postId, commentId, userExpected.getUsername(),
                    commentDTOExpected);

            assertAll(() -> assertEquals(commentExpected, commentActual,
                    () -> "should return comment: " + commentExpected + ", but was" + commentActual),
                    () -> assertEquals(commentExpected.getId(), commentActual.getId(),
                            () -> "should return comment with id: " + commentExpected.getId() + ", but was"
                                    + commentActual.getId()),
                    () -> assertEquals(commentExpected.getContent(), commentActual.getContent(),
                            () -> "should return comment with content: " + commentExpected.getContent() + ", but was"
                                    + commentActual.getContent()),
                    () -> assertEquals(commentExpected.getAuthor(), commentActual.getAuthor(),
                            () -> "should return comment with author: " + commentExpected.getAuthor() + ", but was"
                                    + commentActual.getAuthor()),
                    () -> assertEquals(commentExpected.getRelatedPost(), commentActual.getRelatedPost(),
                            () -> "should return comment with related post: " + commentExpected.getRelatedPost() + ", but was"
                                    + commentActual.getRelatedPost()),
                    () -> verify(userService, times(1)).findByUsername(userExpected.getUsername()),
                    () -> verify(userService, times(1)).isUserChangingOwnData(userExpected.getUsername()),
                    () -> verifyNoMoreInteractions(userService),
                    () -> verify(postService, times(1)).findById(postId),
                    () -> verifyNoMoreInteractions(postService),
                    () -> verify(commentRepository, times(1)).findById(commentId),
                    () -> verify(commentRepository, times(1)).save(commentExpected),
                    () -> verifyNoMoreInteractions(commentRepository));
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
    }
}
