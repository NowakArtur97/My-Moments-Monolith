package com.nowakArtur97.myMoments.feature.comment;

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

    private static MockedStatic<UUID> mocked;

    private static CommentTestBuilder commentTestBuilder;
    private static PostTestBuilder postTestBuilder;
    private static UserTestBuilder userTestBuilder;

    @BeforeAll
    static void setUpBuildersAndUUID() {

        commentTestBuilder = new CommentTestBuilder();
        postTestBuilder = new PostTestBuilder();
        userTestBuilder = new UserTestBuilder();

        UUID uuid = UUID.randomUUID();
        mocked = mockStatic(UUID.class);
        mocked.when(UUID::randomUUID).thenReturn(uuid);
    }

    @AfterAll
    static void cleanUp() {

        if (!mocked.isClosed()) {
            mocked.close();
        }
    }

    @BeforeEach
    void setUp() {

        commentService = new CommentService(commentRepository, userService, postService);
    }

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
                () -> verifyNoMoreInteractions(userService),
                () -> verify(postService, times(1)).findById(postId),
                () -> verifyNoMoreInteractions(postService),
                () -> verify(commentRepository, times(1)).findById(commentId),
                () -> verify(commentRepository, times(1)).save(commentExpected),
                () -> verifyNoMoreInteractions(commentRepository));
    }

    @Test
    void when_delete_comment_should_delete_comment() {

        Long postId = 2L;
        Long commentId = 3L;

        UserEntity userExpected = (UserEntity) userTestBuilder.build(ObjectType.ENTITY);
        PostEntity postExpected = (PostEntity) postTestBuilder.withAuthor(userExpected).build(ObjectType.ENTITY);
        postExpected.setId(postId);
        userExpected.addPost(postExpected);
        CommentEntity commentExpected = (CommentEntity) commentTestBuilder.withAuthor(userExpected).withRelatedPost(postExpected)
                .build(ObjectType.ENTITY);
        userExpected.addComment(commentExpected);
        postExpected.addComment(commentExpected);

        when(userService.findByUsername(userExpected.getUsername())).thenReturn(Optional.of(userExpected));
        when(postService.findById(postId)).thenReturn(Optional.of(postExpected));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(commentExpected));

        assertAll(() -> assertDoesNotThrow(() -> commentService.deleteComment(postId, commentId, userExpected.getUsername()),
                "should not throw any exception but was"),
                () -> verify(userService, times(1)).findByUsername(userExpected.getUsername()),
                () -> verifyNoMoreInteractions(userService),
                () -> verify(postService, times(1)).findById(postId),
                () -> verifyNoMoreInteractions(postService),
                () -> verify(commentRepository, times(1)).findById(commentId),
                () -> verify(commentRepository, times(1)).delete(commentExpected),
                () -> verifyNoMoreInteractions(commentRepository));
    }
}
