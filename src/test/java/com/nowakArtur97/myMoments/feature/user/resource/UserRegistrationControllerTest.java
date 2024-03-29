package com.nowakArtur97.myMoments.feature.user.resource;

import com.nowakArtur97.myMoments.feature.user.entity.Gender;
import com.nowakArtur97.myMoments.feature.user.testBuilder.UserProfileTestBuilder;
import com.nowakArtur97.myMoments.feature.user.testBuilder.UserTestBuilder;
import com.nowakArtur97.myMoments.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.testUtil.generator.NameWithSpacesGenerator;
import com.nowakArtur97.myMoments.testUtil.mapper.ObjectTestMapper;
import org.flywaydb.core.Flyway;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("UserRegistrationController_Tests")
class UserRegistrationControllerTest {

    @Value("${my-moments.jwt.validity:36000000}")
    private Long validity;

    @LocalServerPort
    private int port;

    private final String REGISTRATION_BASE_PATH = "http://localhost:" + port + "/api/v1/registration/register";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Flyway flyway;

    private static UserProfileTestBuilder userProfileTestBuilder;
    private static UserTestBuilder userTestBuilder;

    @BeforeAll
    static void setUpBuilders() {

        userProfileTestBuilder = new UserProfileTestBuilder();
        userTestBuilder = new UserTestBuilder();
    }

    @AfterEach
    void cleanUp() {

        flyway.clean();
        flyway.migrate();
    }

    @Test
    void when_register_valid_user_without_profile_should_register_user() {

        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withUsername("validUser")
                .withEmail("validUser123@email.com").withPassword("ValidPassword123!").withMatchingPassword("ValidPassword123!")
                .build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("token", CoreMatchers.notNullValue()))
                        .andExpect(jsonPath("expirationTimeInMilliseconds", is(validity.intValue()))));
    }

    @Test
    void when_register_valid_user_with_profile_should_register_user() {

        UserProfileDTO userProfileDTO = (UserProfileDTO) userProfileTestBuilder.withAbout("about")
                .withInterests("interests").withLanguages("languages").withLocation("location")
                .withGender(Gender.FEMALE).build(ObjectType.UPDATE_DTO);
        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withUsername("validUserWithProfile")
                .withEmail("validUser123Profile@email.com").withPassword("ValidPassword123!")
                .withMatchingPassword("ValidPassword123!").withProfile(userProfileDTO).build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("token", notNullValue()))
                        .andExpect(jsonPath("expirationTimeInMilliseconds", is(validity.intValue()))));
    }

    @Test
    void when_register_valid_user_with_null_profile_fields_should_register_user() {

        UserProfileDTO userProfileDTO = (UserProfileDTO) userProfileTestBuilder.withAbout(null).withInterests(null)
                .withLanguages(null).withLocation(null).build(ObjectType.CREATE_DTO);
        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withUsername("validUserWithProfile")
                .withEmail("validUser123Profile@email.com").withPassword("ValidPassword123!")
                .withMatchingPassword("ValidPassword123!").withProfile(userProfileDTO)
                .build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("token", notNullValue()))
                        .andExpect(jsonPath("expirationTimeInMilliseconds", is(validity.intValue()))));
    }

    @Test
    void when_register_valid_user_with_empty_profile_fields_should_register_user() {

        UserProfileDTO userProfileDTO = (UserProfileDTO) userProfileTestBuilder.withAbout("").withInterests("")
                .withLanguages("").withLocation("").build(ObjectType.CREATE_DTO);
        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withUsername("validUserWithProfile")
                .withEmail("validUser123Profile@email.com").withPassword("ValidPassword123!")
                .withMatchingPassword("ValidPassword123!").withProfile(userProfileDTO)
                .build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("token", notNullValue()))
                        .andExpect(jsonPath("expirationTimeInMilliseconds", is(validity.intValue()))));
    }

    @Test
    void when_register_valid_user_with_profile_and_image_should_register_user() {

        UserProfileDTO userProfileDTO = (UserProfileDTO) userProfileTestBuilder.build(ObjectType.CREATE_DTO);
        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userTestBuilder.withUsername("serWithImage")
                .withEmail("ser123Image@email.com").withPassword("ValidPassword123!")
                .withMatchingPassword("ValidPassword123!").withProfile(userProfileDTO)
                .build(ObjectType.CREATE_DTO);

        String userAsString = ObjectTestMapper.asJsonString(userRegistrationDTO);

        MockMultipartFile userData = new MockMultipartFile("user", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, userAsString.getBytes(StandardCharsets.UTF_8));

        MockMultipartFile image = new MockMultipartFile("image", "image", MediaType.IMAGE_JPEG_VALUE,
                "image.jpg".getBytes());

        assertAll(
                () -> mockMvc
                        .perform(multipart(REGISTRATION_BASE_PATH)
                                .file(userData)
                                .file(image)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("token", notNullValue()))
                        .andExpect(jsonPath("expirationTimeInMilliseconds", is(validity.intValue()))));
    }
}