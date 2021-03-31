package com.nowakArtur97.myMoments.feature.user.registration;

import com.nowakArtur97.myMoments.testUtil.builder.UserTestBuilder;
import com.nowakArtur97.myMoments.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.testUtil.generator.NameWithSpacesGenerator;
import com.nowakArtur97.myMoments.testUtil.mapper.ObjectTestMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("UserRegistrationController_Tests")
class UserRegistrationControllerWithCustomValidationTest {

    private final String AUTHENTICATION_BASE_PATH = "http://localhost:8080/api/v1/registration";
    private final String REGISTRATION_BASE_PATH = AUTHENTICATION_BASE_PATH + "/register";

    @Autowired
    private MockMvc mockMvc;

    private static UserTestBuilder userTestBuilder;

    @BeforeAll
    static void setUpBuilders() {

        userTestBuilder = new UserTestBuilder();
    }

    @Test
    @Order(1)
    void when_register_valid_user_should_register_user() {

        UserDTO userDTO = (UserDTO) userTestBuilder.withUsername("validUser").withEmail("validUser123@email.com")
                .withPassword("ValidPassword123!").withMatchingPassword("ValidPassword123!").build(ObjectType.DTO);

        int expirationTimeInMilliseconds = 36000000;

        assertAll(
                () -> mockMvc
                        .perform(post(REGISTRATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("token", notNullValue()))
                        .andExpect(jsonPath("expirationTimeInMilliseconds", is(expirationTimeInMilliseconds))));
    }

    @Test
    @Order(2)
    void when_register_user_with_user_name_already_taken_should_return_error_response() {

        UserDTO userDTO = (UserDTO) userTestBuilder.withUsername("validUser").withEmail("validUserEmail123@email.com")
                .build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(REGISTRATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("User name: '" + userDTO.getUsername() + "' is already taken.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    @Order(2)
    void when_register_user_with_email_already_taken_should_return_error_response() {

        UserDTO userDTO = (UserDTO) userTestBuilder.withUsername("validUser123").withEmail("validUser123@email.com")
                .build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(REGISTRATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Email: '" + userDTO.getEmail() + "' is already taken.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_register_user_with_not_matching_passwords_should_return_error_response() {

        UserDTO userDTO = (UserDTO) userTestBuilder.withUsername("usertest415").withEmail("usertest415@email.com")
                .withPassword("Password123!@#").withMatchingPassword("#@!321drowssaP").build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(REGISTRATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Passwords don't match.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_register_user_with_password_containing_user_name_should_return_error_response() {

        UserDTO userDTO = (UserDTO) userTestBuilder.withUsername("usertest").withEmail("usertest415@email.com")
                .withPassword("Pausertest1!").withMatchingPassword("Pausertest1!").build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(REGISTRATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Password contains user name: '" + userDTO.getUsername() + "'.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_register_user_with_password_containing_white_spaces_should_return_error_response() {

        UserDTO userDTO = (UserDTO) userTestBuilder.withUsername("usertest").withEmail("usertest415@email.com")
                .withPassword("Pass Word 123 !@#").withMatchingPassword("Pass Word 123 !@#").build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(REGISTRATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Password contains a whitespace character.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_register_user_with_password_containing_a_sequence_of_repeating_characters_should_return_error_response() {

        UserDTO userDTO = (UserDTO) userTestBuilder.withUsername("usertest").withEmail("usertest415@email.com")
                .withPassword("PAAA123a!@#").withMatchingPassword("PAAA123a!@#").build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(REGISTRATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Password contains a repetitive string: 'AAA'.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For User password: {0}")
    @ValueSource(strings = {"123456", "qwerty", "iloveyou"})
    void when_register_user_with_popular_password_should_return_error_response(String popularPassword) {

        UserDTO userDTO = (UserDTO) userTestBuilder.withUsername("usertest").withEmail("usertest415@email.com")
                .withPassword(popularPassword).withMatchingPassword(popularPassword).build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(REGISTRATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Password contains a popular phrase: '" + popularPassword + "'."))));
    }

    @Test
    void when_register_user_with_too_short_password_should_return_error_response() {

        UserDTO userDTO = (UserDTO) userTestBuilder.withUsername("usertest415").withEmail("usertest415@email.com")
                .withPassword("Pa1!").withMatchingPassword("Pa1!").build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(REGISTRATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Password must be 7 or more characters in length.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_register_user_with_too_long_password_should_return_error_response() {

        UserDTO userDTO = (UserDTO) userTestBuilder.withUsername("usertest415").withEmail("usertest415@email.com")
                .withPassword("Pa1!Pa1!Pa1!Pa1!Pa1!Pa1!Pa1!Pa1!").withMatchingPassword("Pa1!Pa1!Pa1!Pa1!Pa1!Pa1!Pa1!Pa1!").build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(REGISTRATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Password must be no more than 30 characters in length.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_register_user_with_lowercase_password_that_does_not_meet_at_least_two_requirements_should_return_error_response() {

        UserDTO userDTO = (UserDTO) userTestBuilder.withUsername("usertest415").withEmail("usertest415@email.com")
                .withPassword("zdcsdfrg").withMatchingPassword("zdcsdfrg").build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(REGISTRATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Password matches 1 of 4 character rules, but 2 are required.")))
                        .andExpect(jsonPath("errors", hasItem("Password must contain 1 or more uppercase characters.")))
                        .andExpect(jsonPath("errors", hasItem("Password must contain 1 or more special characters.")))
                        .andExpect(jsonPath("errors", hasItem("Password must contain 1 or more digit characters.")))
                        .andExpect(jsonPath("errors", hasSize(4))));
    }

    @Test
    void when_register_user_with_uppercase_password_that_does_not_meet_at_least_two_requirements_should_return_error_response() {

        UserDTO userDTO = (UserDTO) userTestBuilder.withUsername("usertest415").withEmail("usertest415@email.com")
                .withPassword("ONLYUPPERCASELETTERS").withMatchingPassword("ONLYUPPERCASELETTERS").build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(REGISTRATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Password matches 1 of 4 character rules, but 2 are required.")))
                        .andExpect(jsonPath("errors", hasItem("Password must contain 1 or more lowercase characters.")))
                        .andExpect(jsonPath("errors", hasItem("Password must contain 1 or more special characters.")))
                        .andExpect(jsonPath("errors", hasItem("Password must contain 1 or more digit characters.")))
                        .andExpect(jsonPath("errors", hasSize(4))));
    }
}