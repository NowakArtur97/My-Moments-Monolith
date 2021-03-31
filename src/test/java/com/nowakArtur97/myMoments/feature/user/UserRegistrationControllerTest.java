package com.nowakArtur97.myMoments.feature.user;

import com.nowakArtur97.myMoments.advice.GlobalResponseEntityExceptionHandler;
import com.nowakArtur97.myMoments.common.util.JwtUtil;
import com.nowakArtur97.myMoments.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.testUtil.generator.NameWithSpacesGenerator;
import com.nowakArtur97.myMoments.testUtil.mapper.ObjectTestMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("UserRegistrationController_Tests")
public class UserRegistrationControllerTest {

    private final String AUTHENTICATION_BASE_PATH = "http://localhost:8080/api/v1/registration";
    private final String REGISTRATION_BASE_PATH = AUTHENTICATION_BASE_PATH + "/register";

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    private static UserProfileTestBuilder userProfileTestBuilder;
    private static UserTestBuilder userTestBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        userProfileTestBuilder = new UserProfileTestBuilder();
        userTestBuilder = new UserTestBuilder();
    }

    @BeforeEach
    private void setUp() {

        UserRegistrationController userRegistrationController
                = new UserRegistrationController(userService, customUserDetailsService, jwtUtil);

        GlobalResponseEntityExceptionHandler globalResponseEntityExceptionHandler
                = new GlobalResponseEntityExceptionHandler();

        mockMvc = MockMvcBuilders.standaloneSetup(userRegistrationController, globalResponseEntityExceptionHandler)
                .build();

        ReflectionTestUtils.setField(userRegistrationController,"validity",36000000);
    }

    @Test
    @SneakyThrows
    void when_register_valid_user_should_register_user() {

        UserDTO userDTO = UserTestBuilder.DEFAULT_USER_DTO_WITHOUT_PROFILE;
        UserEntity userEntity = UserTestBuilder.DEFAULT_USER_ENTITY;

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("user"));
        User userDetails = new User(userEntity.getUsername(),
                userEntity.getPassword(), authorities);

        String token = "generatedToken";
        int expirationTimeInMilliseconds = 36000000;

        when(userService.register(userDTO)).thenReturn(userEntity);
        when(customUserDetailsService.getAuthorities(userEntity.getRoles())).thenReturn(authorities);
        when(jwtUtil.generateToken(userDetails)).thenReturn(token);

        assertAll(
                () -> mockMvc
                        .perform(post(REGISTRATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("token", is(token)))
                        .andExpect(jsonPath("expirationTimeInMilliseconds", is(expirationTimeInMilliseconds))),
                () -> verify(userService, times(1)).register(userDTO),
                () -> verifyNoMoreInteractions(userService),
                () -> verify(customUserDetailsService, times(1)).getAuthorities(userEntity.getRoles()),
                () -> verifyNoMoreInteractions(customUserDetailsService),
                () -> verify(jwtUtil, times(1)).generateToken(userDetails),
                () -> verifyNoMoreInteractions(jwtUtil));
    }

    @Test
    void when_register_user_with_null_fields_should_return_error_response() {

        UserDTO userDTO = (UserDTO) userTestBuilder.withUsername(null).withPassword(null).withMatchingPassword(null)
                .withEmail(null).build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(REGISTRATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("{user.name.notBlank}")))
                        .andExpect(jsonPath("errors", hasItem("{user.password.notBlank}")))
                        .andExpect(jsonPath("errors", hasItem("{user.matchingPassword.notBlank}")))
                        .andExpect(jsonPath("errors", hasItem("{user.email.notBlank}")))
                        .andExpect(jsonPath("errors", hasSize(4))),
                () -> verifyNoInteractions(userService),
                () -> verifyNoInteractions(customUserDetailsService),
                () -> verifyNoInteractions(jwtUtil));
    }

    @ParameterizedTest(name = "{index}: For User name: {0}")
    @EmptySource
    @ValueSource(strings = {" "})
    void when_register_user_with_blank_user_name_should_return_error_response(String invalidUsername) {

        UserDTO userDTO = (UserDTO) userTestBuilder.withUsername(invalidUsername).build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(REGISTRATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("{user.name.notBlank}")))
                        .andExpect(jsonPath("errors", hasItem("{user.name.size}")))
                        .andExpect(jsonPath("errors", hasSize(2))),
                () -> verifyNoInteractions(userService),
                () -> verifyNoInteractions(customUserDetailsService),
                () -> verifyNoInteractions(jwtUtil));
    }

    @Test
    void when_register_user_with_too_short_user_name_should_return_error_response() {

        UserDTO userDTO = (UserDTO) userTestBuilder.withUsername("u").build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(REGISTRATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("{user.name.size}")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verifyNoInteractions(userService),
                () -> verifyNoInteractions(customUserDetailsService),
                () -> verifyNoInteractions(jwtUtil));
    }

    @Test
    void when_register_user_with_blank_email_should_return_error_response() {

        UserDTO userDTO = (UserDTO) userTestBuilder.withEmail("     ").build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(REGISTRATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("{user.email.notBlank}")))
                        .andExpect(jsonPath("errors", hasItem("{user.email.wrongFormat}")))
                        .andExpect(jsonPath("errors", hasSize(2))),
                () -> verifyNoInteractions(userService),
                () -> verifyNoInteractions(customUserDetailsService),
                () -> verifyNoInteractions(jwtUtil));
    }

    @ParameterizedTest(name = "{index}: For User email: {0}")
    @ValueSource(strings = {"wrongformat", "wrong.format"})
    void when_register_user_with_an_incorrect_format_email_should_return_error_response(String invalidEmail) {

        UserDTO userDTO = (UserDTO) userTestBuilder.withEmail(invalidEmail).build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(REGISTRATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("{user.email.wrongFormat}")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verifyNoInteractions(userService),
                () -> verifyNoInteractions(customUserDetailsService),
                () -> verifyNoInteractions(jwtUtil));
    }

    @ParameterizedTest(name = "{index}: For User password: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void when_register_user_with_blank_password_should_return_error_response(String invalidPassword) {

        UserDTO userDTO = (UserDTO) userTestBuilder.withPassword(invalidPassword).build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(REGISTRATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("{user.password.notBlank}")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verifyNoInteractions(userService),
                () -> verifyNoInteractions(customUserDetailsService),
                () -> verifyNoInteractions(jwtUtil));
    }


    @ParameterizedTest(name = "{index}: For User matching password: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void when_register_user_with_blank_matching_password_should_return_error_response(String invalidMatchingPassword) {

        UserDTO userDTO = (UserDTO) userTestBuilder.withMatchingPassword(invalidMatchingPassword).build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(REGISTRATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("{user.matchingPassword.notBlank}")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verifyNoInteractions(userService),
                () -> verifyNoInteractions(customUserDetailsService),
                () -> verifyNoInteractions(jwtUtil));
    }
}
