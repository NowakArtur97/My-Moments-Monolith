package com.nowakArtur97.myMoments.feature.user.resource;

import com.nowakArtur97.myMoments.advice.AuthenticationControllerAdvice;
import com.nowakArtur97.myMoments.advice.GlobalResponseEntityExceptionHandler;
import com.nowakArtur97.myMoments.common.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.common.util.JwtUtil;
import com.nowakArtur97.myMoments.feature.post.*;
import com.nowakArtur97.myMoments.feature.user.entity.CustomUserDetailsService;
import com.nowakArtur97.myMoments.feature.user.entity.UserService;
import com.nowakArtur97.myMoments.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.testUtil.generator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("UserController_Tests")
class UserGetPostsControllerTest {

    private final String USER_BASE_PATH = "http://localhost:8080/api/v1/users/me/posts";
    private final String USER_BASE_PATH_WITH_ID = "http://localhost:8080/api/v1/users/{id}/posts";

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserObjectMapper userObjectMapper;

    @Mock
    private ModelMapper modelMapper;

    private static PictureTestBuilder pictureTestBuilder;
    private static PostTestBuilder postTestBuilder;

    @BeforeAll
    static void setUpBuilders() {

        pictureTestBuilder = new PictureTestBuilder();
        postTestBuilder = new PostTestBuilder();
    }

    @BeforeEach
    void setUp() {

        UserController userController
                = new UserController(userService, customUserDetailsService, jwtUtil, userObjectMapper, modelMapper);

        GlobalResponseEntityExceptionHandler globalResponseEntityExceptionHandler
                = new GlobalResponseEntityExceptionHandler();

        mockMvc = MockMvcBuilders.standaloneSetup(userController, globalResponseEntityExceptionHandler)
                .setControllerAdvice(new AuthenticationControllerAdvice())
                .build();
    }

    @Test
    void when_get_existing_user_posts_by_username_should_return_posts() {

        String header = "Bearer token";
        String username = "username";

        PictureEntity pictureEntityExpected = (PictureEntity) pictureTestBuilder.withId(1L).build(ObjectType.ENTITY);
        PostEntity postEntityExpected = (PostEntity) postTestBuilder.withId(1L).withCaption("first post")
                .withPhotosEntity(Set.of(pictureEntityExpected)).build(ObjectType.ENTITY);
        PictureModel pictureModelExpected = (PictureModel) pictureTestBuilder.withId(1L).build(ObjectType.MODEL);
        PostModel postModelExpected = (PostModel) postTestBuilder.withId(1L).withCaption("first post")
                .withPhotosModel(Set.of(pictureModelExpected)).build(ObjectType.MODEL);

        PictureEntity pictureEntityExpected2 = (PictureEntity) pictureTestBuilder.withId(2L).build(ObjectType.ENTITY);
        PostEntity postEntityExpected2 = (PostEntity) postTestBuilder.withId(2L).withCaption("second post")
                .withPhotosEntity(Set.of(pictureEntityExpected2)).build(ObjectType.ENTITY);
        PictureModel pictureModelExpected2 = (PictureModel) pictureTestBuilder.withId(2L).build(ObjectType.MODEL);
        PostModel postModelExpected2 = (PostModel) postTestBuilder.withId(2L).withCaption("second post")
                .withPhotosModel(Set.of(pictureModelExpected2)).build(ObjectType.MODEL);

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
        when(userService.getUsersPosts(username)).thenReturn(Set.of(postEntityExpected, postEntityExpected2));
        when(modelMapper.map(postEntityExpected, PostModel.class)).thenReturn(postModelExpected);
        when(modelMapper.map(postEntityExpected2, PostModel.class)).thenReturn(postModelExpected2);

        assertAll(
                () -> mockMvc.perform(get(USER_BASE_PATH)
                        .header("Authorization", header))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("posts", hasSize(2)))
                        .andExpect(jsonPath("posts[0].id", is(notNullValue())))
                        .andExpect(jsonPath("posts[0].caption", is(notNullValue())))
                        .andExpect(jsonPath("posts[0].photos[0].id", is(notNullValue())))
                        .andExpect(jsonPath("posts[0].photos[0].photo", is(notNullValue())))
                        .andExpect(jsonPath("posts[0].photos", hasSize(1)))
                        .andExpect(jsonPath("posts[1].id", is(notNullValue())))
                        .andExpect(jsonPath("posts[1].caption", is(notNullValue())))
                        .andExpect(jsonPath("posts[1].photos[0].id", is(notNullValue())))
                        .andExpect(jsonPath("posts[1].photos[0].photo", is(notNullValue())))
                        .andExpect(jsonPath("posts[1].photos", hasSize(1))),
                () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                () -> verifyNoMoreInteractions(jwtUtil),
                () -> verify(userService, times(1)).getUsersPosts(username),
                () -> verifyNoMoreInteractions(userService),
                () -> verify(modelMapper, times(1)).map(postEntityExpected, PostModel.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verifyNoInteractions(customUserDetailsService),
                () -> verifyNoInteractions(userObjectMapper));
    }

    @Test
    void when_get_existing_user_posts_by_username_but_user_does_not_have_any_posts_should_return_empty_list() {

        String header = "Bearer token";
        String username = "username";

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
        when(userService.getUsersPosts(username)).thenReturn(Set.of());

        assertAll(
                () -> mockMvc.perform(get(USER_BASE_PATH)
                        .header("Authorization", header))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("posts", hasSize(0)))
                        .andExpect(jsonPath("posts[0].id").doesNotExist())
                        .andExpect(jsonPath("posts[0].caption").doesNotExist())
                        .andExpect(jsonPath("posts[0].photos[0].id").doesNotExist())
                        .andExpect(jsonPath("posts[0].photos[0].photo").doesNotExist()),
                () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                () -> verifyNoMoreInteractions(jwtUtil),
                () -> verify(userService, times(1)).getUsersPosts(username),
                () -> verifyNoMoreInteractions(userService),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(customUserDetailsService),
                () -> verifyNoInteractions(userObjectMapper));
    }

    @Test
    void when_get_not_existing_users_posts_by_username_should_return_error_response() {

        String header = "Bearer token";
        String notExistingUsername = "iAmNotExist";

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(notExistingUsername);
        doThrow(new ResourceNotFoundException("User with username: '" + notExistingUsername + "' not found."))
                .when(userService).getUsersPosts(notExistingUsername);

        assertAll(
                () -> mockMvc.perform(get(USER_BASE_PATH)
                        .header("Authorization", header))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(jsonPath("status", is(404)))
                        .andExpect(jsonPath("errors[0]",
                                is("User with username: '" + notExistingUsername + "' not found.")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                () -> verifyNoMoreInteractions(jwtUtil),
                () -> verify(userService, times(1)).getUsersPosts(notExistingUsername),
                () -> verifyNoMoreInteractions(userService),
                () -> verifyNoInteractions(customUserDetailsService),
                () -> verifyNoInteractions(userObjectMapper),
                () -> verifyNoInteractions(modelMapper));
    }

    @Test
    void when_get_existing_user_posts_by_id_should_return_posts() {

        String header = "Bearer token";
        Long id = 1L;

        PictureEntity pictureEntityExpected = (PictureEntity) pictureTestBuilder.withId(1L).build(ObjectType.ENTITY);
        PostEntity postEntityExpected = (PostEntity) postTestBuilder.withId(1L).withCaption("first post")
                .withPhotosEntity(Set.of(pictureEntityExpected)).build(ObjectType.ENTITY);
        PictureModel pictureModelExpected = (PictureModel) pictureTestBuilder.withId(1L).build(ObjectType.MODEL);
        PostModel postModelExpected = (PostModel) postTestBuilder.withId(1L).withCaption("first post")
                .withPhotosModel(Set.of(pictureModelExpected)).build(ObjectType.MODEL);

        PictureEntity pictureEntityExpected2 = (PictureEntity) pictureTestBuilder.withId(2L).build(ObjectType.ENTITY);
        PostEntity postEntityExpected2 = (PostEntity) postTestBuilder.withId(2L).withCaption("second post")
                .withPhotosEntity(Set.of(pictureEntityExpected2)).build(ObjectType.ENTITY);
        PictureModel pictureModelExpected2 = (PictureModel) pictureTestBuilder.withId(2L).build(ObjectType.MODEL);
        PostModel postModelExpected2 = (PostModel) postTestBuilder.withId(2L).withCaption("second post")
                .withPhotosModel(Set.of(pictureModelExpected2)).build(ObjectType.MODEL);

        when(userService.getUsersPosts(id)).thenReturn(Set.of(postEntityExpected, postEntityExpected2));
        when(modelMapper.map(postEntityExpected, PostModel.class)).thenReturn(postModelExpected);
        when(modelMapper.map(postEntityExpected2, PostModel.class)).thenReturn(postModelExpected2);

        assertAll(
                () -> mockMvc.perform(get(USER_BASE_PATH_WITH_ID, id)
                        .header("Authorization", header))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("posts", hasSize(2)))
                        .andExpect(jsonPath("posts[0].id", is(notNullValue())))
                        .andExpect(jsonPath("posts[0].caption", is(notNullValue())))
                        .andExpect(jsonPath("posts[0].photos[0].id", is(notNullValue())))
                        .andExpect(jsonPath("posts[0].photos[0].photo", is(notNullValue())))
                        .andExpect(jsonPath("posts[0].photos", hasSize(1)))
                        .andExpect(jsonPath("posts[1].id", is(notNullValue())))
                        .andExpect(jsonPath("posts[1].caption", is(notNullValue())))
                        .andExpect(jsonPath("posts[1].photos[0].id", is(notNullValue())))
                        .andExpect(jsonPath("posts[1].photos[0].photo", is(notNullValue())))
                        .andExpect(jsonPath("posts[1].photos", hasSize(1))),
                () -> verify(userService, times(1)).getUsersPosts(id),
                () -> verifyNoMoreInteractions(userService),
                () -> verify(modelMapper, times(1)).map(postEntityExpected, PostModel.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verifyNoInteractions(jwtUtil),
                () -> verifyNoInteractions(customUserDetailsService),
                () -> verifyNoInteractions(userObjectMapper));
    }

    @Test
    void when_get_existing_user_posts_by_id_but_user_does_not_have_any_posts_should_return_empty_list() {

        String header = "Bearer token";
        Long id = 1L;

        when(userService.getUsersPosts(id)).thenReturn(Set.of());

        assertAll(
                () -> mockMvc.perform(get(USER_BASE_PATH_WITH_ID, id)
                        .header("Authorization", header))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("posts", hasSize(0)))
                        .andExpect(jsonPath("posts[0].id").doesNotExist())
                        .andExpect(jsonPath("posts[0].caption").doesNotExist())
                        .andExpect(jsonPath("posts[0].photos[0].id").doesNotExist())
                        .andExpect(jsonPath("posts[0].photos[0].photo").doesNotExist()),
                () -> verify(userService, times(1)).getUsersPosts(id),
                () -> verifyNoMoreInteractions(userService),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(jwtUtil),
                () -> verifyNoInteractions(customUserDetailsService),
                () -> verifyNoInteractions(userObjectMapper));
    }

    @Test
    void when_get_not_existing_users_posts_by_id_should_return_error_response() {

        String header = "Bearer token";
        Long notExistingId = 1L;

        doThrow(new ResourceNotFoundException("User with id: '" + notExistingId + "' not found."))
                .when(userService).getUsersPosts(notExistingId);

        assertAll(
                () -> mockMvc.perform(get(USER_BASE_PATH_WITH_ID, notExistingId)
                        .header("Authorization", header))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(jsonPath("status", is(404)))
                        .andExpect(jsonPath("errors[0]", is("User with id: '" + notExistingId + "' not found.")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(userService, times(1)).getUsersPosts(notExistingId),
                () -> verifyNoMoreInteractions(userService),
                () -> verifyNoInteractions(jwtUtil),
                () -> verifyNoInteractions(customUserDetailsService),
                () -> verifyNoInteractions(userObjectMapper),
                () -> verifyNoInteractions(modelMapper));
    }
}
