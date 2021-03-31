package com.nowakArtur97.myMoments.feature.user.registration;

import com.nowakArtur97.myMoments.testUtil.builder.UserTestBuilder;
import com.nowakArtur97.myMoments.testUtil.generator.NameWithSpacesGenerator;
import com.nowakArtur97.myMoments.testUtil.mapper.ObjectTestMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("UserRegistrationController_Tests")
class UserRegistrationWithProfileControllerTest {

    private final String AUTHENTICATION_BASE_PATH = "http://localhost:8080/api/v1/registration";
    private final String REGISTRATION_BASE_PATH = AUTHENTICATION_BASE_PATH + "/register";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void when_register_valid_user_with_profile_should_register_user() {

        UserDTO userDTO = UserTestBuilder.DEFAULT_USER_DTO_WITH_PROFILE;

        String token = "generatedToken";
        int expirationTimeInMilliseconds = 36000000;

        assertAll(
                () -> mockMvc
                        .perform(post(REGISTRATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("token", is(token)))
                        .andExpect(jsonPath("expirationTimeInMilliseconds", is(expirationTimeInMilliseconds))));
    }
}