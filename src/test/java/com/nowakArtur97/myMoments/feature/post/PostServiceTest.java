package com.nowakArtur97.myMoments.feature.post;

import com.nowakArtur97.myMoments.common.exception.ForbiddenException;
import com.nowakArtur97.myMoments.common.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.feature.user.entity.UserEntity;
import com.nowakArtur97.myMoments.feature.user.entity.UserService;
import com.nowakArtur97.myMoments.feature.user.testBuilder.UserTestBuilder;
import com.nowakArtur97.myMoments.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.testUtil.generator.NameWithSpacesGenerator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("PostService_Tests")
class PostServiceTest {

    private PostService postService;

    @Mock
    private UserService userService;

    @Mock
    private PostRepository postRepository;

    private static MockedStatic<UUID> mocked;

    private static PostTestBuilder postTestBuilder;
    private static UserTestBuilder userTestBuilder;

    @BeforeAll
    static void setUpBuildersAndUUID() {

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

        postService = new PostService(postRepository, userService);
    }

    @Nested
    class CreatePostTest {

        @Test
        @SneakyThrows
        void when_create_post_should_create_post() {

            MockMultipartFile imageExpected = new MockMultipartFile("image", "image", "application/json",
                    "image.jpg".getBytes());
            PostDTO postDTOExpected = (PostDTO) postTestBuilder.withPhotosMultipart(List.of(imageExpected))
                    .build(ObjectType.CREATE_DTO);
            UserEntity userExpected = (UserEntity) userTestBuilder.build(ObjectType.ENTITY);
            PictureEntity pictureEntityExpected = new PictureEntity(imageExpected.getBytes());
            PostEntity postExpected = (PostEntity) postTestBuilder.withAuthor(userExpected)
                    .withPhotosEntity(Set.of(pictureEntityExpected)).build(ObjectType.ENTITY);

            when(userService.findByUsername(userExpected.getUsername())).thenReturn(Optional.of(userExpected));
            when(postRepository.save(postExpected)).thenReturn(postExpected);

            PostEntity postActual = postService.createPost(userExpected.getUsername(), postDTOExpected);

            assertAll(() -> assertEquals(postExpected, postActual,
                    () -> "should return post: " + postExpected + ", but was" + postActual),
                    () -> assertEquals(postExpected.getId(), postActual.getId(),
                            () -> "should return post with id: " + postExpected.getId() + ", but was"
                                    + postActual.getId()),
                    () -> assertEquals(postExpected.getCaption(), postActual.getCaption(),
                            () -> "should return post with caption: " + postExpected.getCaption() + ", but was"
                                    + postActual.getCaption()),
                    () -> assertEquals(postExpected.getAuthor(), postActual.getAuthor(),
                            () -> "should return post with author: " + postExpected.getAuthor() + ", but was"
                                    + postActual.getAuthor()),
                    () -> assertEquals(postExpected.getPhotos(), postActual.getPhotos(),
                            () -> "should return post with photos: " + postExpected.getPhotos() + ", but was"
                                    + postActual.getPhotos()),
                    () -> verify(userService, times(1)).findByUsername(userExpected.getUsername()),
                    () -> verifyNoMoreInteractions(userService),
                    () -> verify(postRepository, times(1)).save(postExpected),
                    () -> verifyNoMoreInteractions(postRepository));
        }

        @Test
        void when_create_post_but_user_does_not_exists_should_throw_exception() {

            String notExistingUsername = "iAmNotExist";
            MockMultipartFile imageExpected = new MockMultipartFile("image", "image", "application/json",
                    "image.jpg".getBytes());
            PostDTO postDTOExpected = (PostDTO) postTestBuilder.withPhotosMultipart(List.of(imageExpected))
                    .build(ObjectType.CREATE_DTO);

            when(userService.findByUsername(notExistingUsername)).thenReturn(Optional.empty());

            assertAll(() -> assertThrows(UsernameNotFoundException.class,
                    () -> postService.createPost(notExistingUsername, postDTOExpected),
                    "should throw UsernameNotFoundException but wasn't"),
                    () -> verify(userService, times(1)).findByUsername(notExistingUsername),
                    () -> verifyNoMoreInteractions(userService),
                    () -> verifyNoInteractions(postRepository));
        }
    }

    @Nested
    class UpdatePostTest {

        @Test
        @SneakyThrows
        void when_update_post_should_update_post() {

            Long postId = 1L;
            MockMultipartFile imageExpected = new MockMultipartFile("image", "image", "application/json",
                    "image.jpg".getBytes());
            PostDTO postDTOExpected = (PostDTO) postTestBuilder.withPhotosMultipart(List.of(imageExpected))
                    .build(ObjectType.CREATE_DTO);
            PictureEntity pictureEntityExpectedBeforeUpdate = new PictureEntity("old image".getBytes());
            UserEntity userExpected = (UserEntity) userTestBuilder.build(ObjectType.ENTITY);
            PostEntity postExpectedBeforeUpdate = (PostEntity) postTestBuilder.withCaption("old caption").withAuthor(userExpected)
                    .withPhotosEntity(new HashSet<>(Set.of(pictureEntityExpectedBeforeUpdate))).build(ObjectType.ENTITY);
            userExpected.addPost(postExpectedBeforeUpdate);
            PictureEntity pictureEntityExpected = new PictureEntity(imageExpected.getBytes());
            PostEntity postExpected = (PostEntity) postTestBuilder.withCaption("new caption").withAuthor(userExpected)
                    .withPhotosEntity(Set.of(pictureEntityExpected)).build(ObjectType.ENTITY);

            when(userService.findByUsername(userExpected.getUsername())).thenReturn(Optional.of(userExpected));
            when(postRepository.findById(postId)).thenReturn(Optional.of(postExpectedBeforeUpdate));
            when(postRepository.save(postExpected)).thenReturn(postExpected);

            PostEntity postActual = postService.updatePost(postId, userExpected.getUsername(), postDTOExpected);

            assertAll(() -> assertEquals(postExpected, postActual,
                    () -> "should return post: " + postExpected + ", but was" + postActual),
                    () -> assertEquals(postExpected.getId(), postActual.getId(),
                            () -> "should return post with id: " + postExpected.getId() + ", but was"
                                    + postActual.getId()),
                    () -> assertEquals(postExpected.getCaption(), postActual.getCaption(),
                            () -> "should return post with caption: " + postExpected.getCaption() + ", but was"
                                    + postActual.getCaption()),
                    () -> assertEquals(postExpected.getAuthor(), postActual.getAuthor(),
                            () -> "should return post with author: " + postExpected.getAuthor() + ", but was"
                                    + postActual.getAuthor()),
                    () -> assertEquals(postExpected.getPhotos(), postActual.getPhotos(),
                            () -> "should return post with photos: " + postExpected.getPhotos() + ", but was"
                                    + postActual.getPhotos()),
                    () -> verify(userService, times(1)).findByUsername(userExpected.getUsername()),
                    () -> verifyNoMoreInteractions(userService),
                    () -> verify(postRepository, times(1)).save(postExpected),
                    () -> verify(postRepository, times(1)).findById(postId),
                    () -> verifyNoMoreInteractions(postRepository));
        }

        @Test
        void when_update_post_of_not_existing_user_should_throw_exception() {

            Long postId = 1L;
            String username = "username";
            MockMultipartFile imageExpected = new MockMultipartFile("image", "image", "application/json",
                    "image.jpg".getBytes());
            PostDTO postDTOExpected = (PostDTO) postTestBuilder.withPhotosMultipart(List.of(imageExpected))
                    .build(ObjectType.CREATE_DTO);

            when(userService.findByUsername(username)).thenReturn(Optional.empty());

            assertAll(() -> assertThrows(UsernameNotFoundException.class,
                    () -> postService.updatePost(postId, username, postDTOExpected),
                    "should throw UsernameNotFoundException but wasn't"),
                    () -> verify(userService, times(1)).findByUsername(username),
                    () -> verifyNoMoreInteractions(userService),
                    () -> verifyNoInteractions(postRepository));
        }

        @Test
        void when_update_not_existing_post_should_throw_exception() {

            Long postId = 1L;
            UserEntity userExpected = (UserEntity) userTestBuilder.build(ObjectType.ENTITY);
            MockMultipartFile imageExpected = new MockMultipartFile("image", "image", "application/json",
                    "image.jpg".getBytes());
            PostDTO postDTOExpected = (PostDTO) postTestBuilder.withPhotosMultipart(List.of(imageExpected))
                    .build(ObjectType.CREATE_DTO);

            when(userService.findByUsername(userExpected.getUsername())).thenReturn(Optional.of(userExpected));
            when(postRepository.findById(postId)).thenReturn(Optional.empty());

            assertAll(() -> assertThrows(ResourceNotFoundException.class,
                    () -> postService.updatePost(postId, userExpected.getUsername(), postDTOExpected),
                    "should throw ResourceNotFoundException but wasn't"),
                    () -> verify(userService, times(1)).findByUsername(userExpected.getUsername()),
                    () -> verifyNoMoreInteractions(userService),
                    () -> verify(postRepository, times(1)).findById(postId),
                    () -> verifyNoMoreInteractions(postRepository));
        }

        @Test
        @SneakyThrows
        void when_update_other_user_post_should_throw_exception() {

            Long postId = 1L;
            UserEntity userExpected = (UserEntity) userTestBuilder.build(ObjectType.ENTITY);
            MockMultipartFile imageExpected = new MockMultipartFile("image", "image", "application/json",
                    "image.jpg".getBytes());
            PostDTO postDTOExpected = (PostDTO) postTestBuilder.withPhotosMultipart(List.of(imageExpected))
                    .build(ObjectType.CREATE_DTO);
            PictureEntity pictureEntityExpected = new PictureEntity(imageExpected.getBytes());
            PostEntity postExpected = (PostEntity) postTestBuilder.withCaption("new caption").withAuthor(userExpected)
                    .withPhotosEntity(Set.of(pictureEntityExpected)).build(ObjectType.ENTITY);

            when(userService.findByUsername(userExpected.getUsername())).thenReturn(Optional.of(userExpected));
            when(postRepository.findById(postId)).thenReturn(Optional.of(postExpected));

            assertAll(() -> assertThrows(ForbiddenException.class,
                    () -> postService.updatePost(postId, userExpected.getUsername(), postDTOExpected),
                    "should throw ForbiddenException but wasn't"),
                    () -> verify(userService, times(1)).findByUsername(userExpected.getUsername()),
                    () -> verifyNoMoreInteractions(userService),
                    () -> verify(postRepository, times(1)).findById(postId),
                    () -> verifyNoMoreInteractions(postRepository));
        }
    }

    @Nested
    class DeletePostTest {

        @Test
        void when_delete_post_should_delete_post() {

            Long userId = 1L;
            PictureEntity pictureEntityExpected = new PictureEntity("image".getBytes());
            PostEntity postExpected = (PostEntity) postTestBuilder
                    .withPhotosEntity(Set.of(pictureEntityExpected)).build(ObjectType.ENTITY);
            UserEntity userExpected = (UserEntity) userTestBuilder.withPosts(new HashSet<>(Set.of(postExpected)))
                    .build(ObjectType.ENTITY);
            postExpected.setAuthor(userExpected);

            when(userService.findByUsername(userExpected.getUsername())).thenReturn(Optional.of(userExpected));
            when(postRepository.findById(userId)).thenReturn(Optional.of(postExpected));

            assertAll(() -> assertDoesNotThrow(() -> postService.deletePost(userId, userExpected.getUsername()),
                    "should not throw any exception but was"),
                    () -> verify(userService, times(1)).findByUsername(userExpected.getUsername()),
                    () -> verifyNoMoreInteractions(userService),
                    () -> verify(postRepository, times(1)).findById(userId),
                    () -> verify(postRepository, times(1)).delete(postExpected),
                    () -> verifyNoMoreInteractions(postRepository));
        }

        @Test
        void when_delete_not_existing_user_post_should_throw_exception() {

            Long userId = 1L;
            UserEntity userExpected = (UserEntity) userTestBuilder.build(ObjectType.ENTITY);

            when(userService.findByUsername(userExpected.getUsername())).thenReturn(Optional.empty());

            assertAll(() -> assertThrows(UsernameNotFoundException.class,
                    () -> postService.deletePost(userId, userExpected.getUsername()),
                    "should throw UsernameNotFoundException but wasn't"),
                    () -> verify(userService, times(1)).findByUsername(userExpected.getUsername()),
                    () -> verifyNoMoreInteractions(userService),
                    () -> verifyNoInteractions(postRepository));
        }

        @Test
        void when_delete_not_existing_post_should_throw_exception() {

            Long userId = 1L;
            UserEntity userExpected = (UserEntity) userTestBuilder.build(ObjectType.ENTITY);

            when(userService.findByUsername(userExpected.getUsername())).thenReturn(Optional.of(userExpected));
            when(postRepository.findById(userId)).thenReturn(Optional.empty());

            assertAll(() -> assertThrows(ResourceNotFoundException.class,
                    () -> postService.deletePost(userId, userExpected.getUsername()),
                    "should throw ResourceNotFoundException but wasn't"),
                    () -> verify(userService, times(1)).findByUsername(userExpected.getUsername()),
                    () -> verifyNoMoreInteractions(userService),
                    () -> verify(postRepository, times(1)).findById(userId),
                    () -> verifyNoMoreInteractions(postRepository));
        }

        @Test
        void when_delete_some_other_user_post_should_throw_exception() {

            Long userId = 1L;
            PictureEntity pictureEntityExpected = new PictureEntity("image".getBytes());
            PostEntity postExpected = (PostEntity) postTestBuilder
                    .withPhotosEntity(Set.of(pictureEntityExpected)).build(ObjectType.ENTITY);
            UserEntity someOtherUserExpected = (UserEntity) userTestBuilder.withPosts(Set.of(postExpected)).build(ObjectType.ENTITY);
            postExpected.setAuthor(someOtherUserExpected);
            UserEntity userExpected = (UserEntity) userTestBuilder.build(ObjectType.ENTITY);

            when(userService.findByUsername(userExpected.getUsername())).thenReturn(Optional.of(userExpected));
            when(postRepository.findById(userId)).thenReturn(Optional.of(postExpected));

            assertAll(() -> assertThrows(ForbiddenException.class,
                    () -> postService.deletePost(userId, userExpected.getUsername()),
                    "should throw ForbiddenException but wasn't"),
                    () -> verify(userService, times(1)).findByUsername(userExpected.getUsername()),
                    () -> verifyNoMoreInteractions(userService),
                    () -> verify(postRepository, times(1)).findById(userId),
                    () -> verifyNoMoreInteractions(postRepository));
        }
    }

    @Nested
    class OtherPostTest {

        @Test
        void when_post_exists_and_find_by_id_should_return_user() {

            Long expectedId = 1L;
            PictureEntity pictureEntityExpected = new PictureEntity("image".getBytes());
            UserEntity userExpected = (UserEntity) userTestBuilder.build(ObjectType.ENTITY);
            PostEntity postExpected = (PostEntity) postTestBuilder.withAuthor(userExpected)
                    .withPhotosEntity(Set.of(pictureEntityExpected)).build(ObjectType.ENTITY);

            when(postRepository.findById(expectedId)).thenReturn(Optional.of(postExpected));

            Optional<PostEntity> postActualOptional = postService.findById(expectedId);

            assertTrue(postActualOptional.isPresent(), () -> "shouldn't return empty optional");

            PostEntity postActual = postActualOptional.get();

            assertAll(() -> assertEquals(postExpected, postActual,
                    () -> "should return post: " + postExpected + ", but was" + postActual),
                    () -> assertEquals(postExpected.getId(), postActual.getId(),
                            () -> "should return post with id: " + postExpected.getId() + ", but was"
                                    + postActual.getId()),
                    () -> assertEquals(postExpected.getCaption(), postActual.getCaption(),
                            () -> "should return post with caption: " + postExpected.getCaption() + ", but was"
                                    + postActual.getCaption()),
                    () -> assertEquals(postExpected.getAuthor(), postActual.getAuthor(),
                            () -> "should return post with author: " + postExpected.getAuthor() + ", but was"
                                    + postActual.getAuthor()),
                    () -> assertEquals(postExpected.getPhotos(), postActual.getPhotos(),
                            () -> "should return post with photos: " + postExpected.getPhotos() + ", but was"
                                    + postActual.getPhotos()),
                    () -> verify(postRepository, times(1)).findById(expectedId),
                    () -> verifyNoMoreInteractions(postRepository),
                    () -> verifyNoInteractions(userService));
        }

        @Test
        void when_post_not_exists_and_find_by_id_should_return_empty_optional() {

            Long notExistingId = 1L;

            when(postRepository.findById(notExistingId)).thenReturn(Optional.empty());

            Optional<PostEntity> postActualOptional = postService.findById(notExistingId);

            assertAll(() -> assertTrue(postActualOptional.isEmpty(),
                    () -> "should return empty optional, but was: " + postActualOptional.get()),
                    () -> verify(postRepository, times(1)).findById(notExistingId),
                    () -> verifyNoMoreInteractions(postRepository),
                    () -> verifyNoInteractions(userService));
        }
    }
}
