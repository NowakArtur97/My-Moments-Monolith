package com.nowakArtur97.myMoments.feature.post;


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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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

    private static MockedStatic mocked;

    private static PostTestBuilder postTestBuilder;
    private static UserTestBuilder userTestBuilder;

    @BeforeAll
    private static void setUpBuildersAndUUID() {

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

        postService = new PostService(postRepository, userService);
    }

    @Test
    @SneakyThrows
    void when_create_post_should_create_post() {

        UserEntity userExpected = (UserEntity) userTestBuilder.build(ObjectType.ENTITY);
        MockMultipartFile imageExpected = new MockMultipartFile("image", "image", "application/json",
                "image.jpg".getBytes());
        PostDTO postDTOExpected = (PostDTO) postTestBuilder.withPhotosMultipart(List.of(imageExpected))
                .build(ObjectType.CREATE_DTO);
        PictureEntity pictureEntityExpected = new PictureEntity(imageExpected.getBytes());
        PostEntity postExpected = (PostEntity) postTestBuilder.withAuthor(userExpected)
                .withPhotosEntity(Set.of(pictureEntityExpected)).build(ObjectType.ENTITY);

        when(userService.findByUsername(userExpected.getUsername())).thenReturn(Optional.of(userExpected));
        when(postRepository.save(postExpected)).thenReturn(postExpected);

        PostEntity postActual = postService.createPost(userExpected.getUsername(), postDTOExpected);

        assertAll(() -> assertEquals(postExpected, postActual,
                () -> "should return post: " + userExpected + ", but was" + postActual),
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
    @SneakyThrows
    void when_create_post_but_user_does_not_exists_should_throw_error() {

        UserEntity userExpected = (UserEntity) userTestBuilder.build(ObjectType.ENTITY);
        MockMultipartFile imageExpected = new MockMultipartFile("image", "image", "application/json",
                "image.jpg".getBytes());
        PostDTO postDTOExpected = (PostDTO) postTestBuilder.withPhotosMultipart(List.of(imageExpected))
                .build(ObjectType.CREATE_DTO);

        when(userService.findByUsername(userExpected.getUsername())).thenReturn(Optional.empty());

        assertAll(() -> assertThrows(UsernameNotFoundException.class,
                () -> postService.createPost(userExpected.getUsername(), postDTOExpected),
                "should throw UsernameNotFoundException but wasn't"),
                () -> verify(userService, times(1)).findByUsername(userExpected.getUsername()),
                () -> verifyNoMoreInteractions(userService),
                () -> verifyNoInteractions(postRepository));
    }
}
