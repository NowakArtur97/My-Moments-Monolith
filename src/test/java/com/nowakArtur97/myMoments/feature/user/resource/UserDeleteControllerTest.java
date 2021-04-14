package com.nowakArtur97.myMoments.feature.user.resource;

import com.nowakArtur97.myMoments.advice.AuthenticationControllerAdvice;
import com.nowakArtur97.myMoments.advice.GlobalResponseEntityExceptionHandler;
import com.nowakArtur97.myMoments.common.exception.NotAuthorizedException;
import com.nowakArtur97.myMoments.common.util.JwtUtil;
import com.nowakArtur97.myMoments.feature.user.entity.CustomUserDetailsService;
import com.nowakArtur97.myMoments.feature.user.entity.UserEntity;
import com.nowakArtur97.myMoments.feature.user.entity.UserService;
import com.nowakArtur97.myMoments.feature.user.testBuilder.UserTestBuilder;
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

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("UserController_Tests")
class UserDeleteControllerTest {

    private final String USERS_BASE_PATH = "http://localhost:8080/api/v1/users/{id}";

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

    private static UserTestBuilder userTestBuilder;

    @BeforeAll
    static void setUpBuilders() {

        userTestBuilder = new UserTestBuilder();
    }

    @BeforeEach
    private void setUp() {

        UserController userController
                = new UserController(userService, customUserDetailsService, jwtUtil, userObjectMapper, modelMapper);

        GlobalResponseEntityExceptionHandler globalResponseEntityExceptionHandler
                = new GlobalResponseEntityExceptionHandler();

        mockMvc = MockMvcBuilders.standaloneSetup(userController, globalResponseEntityExceptionHandler)
                .setControllerAdvice(new AuthenticationControllerAdvice())
                .build();
    }

    @Test
    void when_delete_existing_user_should_not_return_content() {

        Long userId = 1L;

        UserEntity userEntity = (UserEntity) userTestBuilder.build(ObjectType.ENTITY);

        when(userService.deleteUser(userId)).thenReturn(Optional.of(userEntity));

        assertAll(
                () -> mockMvc.perform(delete(USERS_BASE_PATH, userId))
                        .andExpect(status().isNoContent())
                        .andExpect(jsonPath("$").doesNotExist()),
                () -> verify(userService, times(1)).deleteUser(userId),
                () -> verifyNoMoreInteractions(userService),
                () -> verifyNoInteractions(customUserDetailsService),
                () -> verifyNoInteractions(jwtUtil),
                () -> verifyNoInteractions(userObjectMapper),
                () -> verifyNoInteractions(modelMapper));
    }

    @Test
    void when_delete_not_existing_user_should_return_error_response() {

        Long userId = 1L;

        when(userService.deleteUser(userId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(delete(USERS_BASE_PATH, userId))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(jsonPath("status", is(404)))
                        .andExpect(jsonPath("errors[0]", is("User with id: '" + userId + "' not found.")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(userService, times(1)).deleteUser(userId),
                () -> verifyNoMoreInteractions(userService),
                () -> verifyNoInteractions(customUserDetailsService),
                () -> verifyNoInteractions(jwtUtil),
                () -> verifyNoInteractions(userObjectMapper),
                () -> verifyNoInteractions(modelMapper));
    }

    @Test
    void when_delete_not_owned_account_should_return_error_response() {

        Long userId = 1L;

        when(userService.deleteUser(userId)).thenThrow(new NotAuthorizedException("User can only delete his own account."));

        assertAll(
                () -> mockMvc.perform(delete(USERS_BASE_PATH, userId))
                        .andExpect(status().isUnauthorized())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(jsonPath("status", is(401)))
                        .andExpect(jsonPath("errors[0]", is("User can only delete his own account.")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(userService, times(1)).deleteUser(userId),
                () -> verifyNoMoreInteractions(userService),
                () -> verifyNoInteractions(customUserDetailsService),
                () -> verifyNoInteractions(jwtUtil),
                () -> verifyNoInteractions(userObjectMapper),
                () -> verifyNoInteractions(modelMapper));
    }
}
