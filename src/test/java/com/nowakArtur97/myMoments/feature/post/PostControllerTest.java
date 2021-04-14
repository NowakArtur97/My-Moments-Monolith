package com.nowakArtur97.myMoments.feature.post;

import com.nowakArtur97.myMoments.common.exception.RoleNotFoundException;
import com.nowakArtur97.myMoments.common.util.JwtUtil;
import com.nowakArtur97.myMoments.feature.user.entity.*;
import com.nowakArtur97.myMoments.feature.user.testBuilder.UserProfileTestBuilder;
import com.nowakArtur97.myMoments.feature.user.testBuilder.UserTestBuilder;
import com.nowakArtur97.myMoments.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.testUtil.generator.NameWithSpacesGenerator;
import com.nowakArtur97.myMoments.testUtil.mapper.ObjectTestMapper;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("PostController_Tests")
class PostControllerTest {

    private final String POSTS_BASE_PATH = "http://localhost:8080/api/v1/posts";

    @Value("${my-moments.default-user-role:USER_ROLE}")
    private String defaultUserRole;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Flyway flyway;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private static PostTestBuilder postTestBuilder;
    private static UserProfileTestBuilder userProfileTestBuilder;
    private static UserTestBuilder userTestBuilder;

    private String token;
    private UserEntity userEntity;

    @BeforeAll
    static void setUpBuilders() {

        postTestBuilder = new PostTestBuilder();
        userProfileTestBuilder = new UserProfileTestBuilder();
        userTestBuilder = new UserTestBuilder();
    }

    @BeforeEach
    void setUp() {

        RoleEntity roleEntity = roleRepository.findByName(defaultUserRole)
                .orElseThrow(() -> new RoleNotFoundException("Role: '" + defaultUserRole + "' not found."));

        UserProfileEntity userProfileEntity = (UserProfileEntity) userProfileTestBuilder.build(ObjectType.ENTITY);
        userEntity = (UserEntity) userTestBuilder.withUsername("user456").withEmail("u4se5r@em6ail.com")
                .withPassword("userASD!").withMatchingPassword("userASD!").withProfile(userProfileEntity)
                .withRoles(Set.of(roleEntity)).build(ObjectType.ENTITY);
        userProfileEntity.setUser(userEntity);

        userRepository.save(userEntity);

        token = jwtUtil.generateToken(new User(userEntity.getUsername(), userEntity.getPassword(),
                List.of(new SimpleGrantedAuthority(defaultUserRole))));
    }

    @AfterEach
    void cleanUp() {

        flyway.clean();
        flyway.migrate();

        userRepository.delete(userEntity);
    }

    @Test
    void when_create_valid_post_should_return_post() {

        String caption = "description";
        PostDTO postDTO = (PostDTO) postTestBuilder.withCaption(caption).build(ObjectType.CREATE_DTO);

        String postAsString = ObjectTestMapper.asJsonString(postDTO);

        MockMultipartFile postData = new MockMultipartFile("post", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));

        MockMultipartFile photosData = new MockMultipartFile("photos", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(POSTS_BASE_PATH)
                                .file(postData)
                                .file(photosData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("id", is(notNullValue())))
                        .andExpect(jsonPath("caption", is(caption)))
                        .andExpect(jsonPath("photos[0].id", is(notNullValue())))
                        .andExpect(jsonPath("photos[0].photo", is(notNullValue())))
                        .andExpect(jsonPath("photos", hasSize(1))));
    }

    @Test
    void when_create_valid_post_without_caption_should_return_post_with_empty_caption() {

        PostDTO postDTO = (PostDTO) postTestBuilder.build(ObjectType.CREATE_DTO);

        String postAsString = ObjectTestMapper.asJsonString(postDTO);

        MockMultipartFile postData = new MockMultipartFile("post", "request",
                MediaType.APPLICATION_JSON_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));

        MockMultipartFile photosData = new MockMultipartFile("photos", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(POSTS_BASE_PATH)
                                .file(postData)
                                .file(photosData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("id", is(notNullValue())))
                        .andExpect(jsonPath("caption", is("")))
                        .andExpect(jsonPath("photos[0].id", is(notNullValue())))
                        .andExpect(jsonPath("photos[0].photo", is(notNullValue())))
                        .andExpect(jsonPath("photos", hasSize(1))));
    }

    @Test
    void when_create_post_without_photos_should_return_error_response() {

        PostDTO postDTO = (PostDTO) postTestBuilder.withCaption("description").build(ObjectType.CREATE_DTO);

        String postAsString = ObjectTestMapper.asJsonString(postDTO);

        MockMultipartFile postData = new MockMultipartFile("post", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(POSTS_BASE_PATH)
                                .file(postData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("At least one photo must be added.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_create_post_without_photos_and_caption_should_return_error_response() {

        PostDTO postDTO = (PostDTO) postTestBuilder.build(ObjectType.CREATE_DTO);

        String postAsString = ObjectTestMapper.asJsonString(postDTO);

        MockMultipartFile postData = new MockMultipartFile("post", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(POSTS_BASE_PATH)
                                .file(postData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("At least one photo must be added.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_create_post_with_too_many_photos_should_return_error_response() {

        PostDTO postDTO = (PostDTO) postTestBuilder.build(ObjectType.CREATE_DTO);

        String postAsString = ObjectTestMapper.asJsonString(postDTO);

        MockMultipartFile postData = new MockMultipartFile("post", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));

        MockMultipartFile photosData = new MockMultipartFile("photos", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));


        assertAll(
                () -> mockMvc
                        .perform(multipart(POSTS_BASE_PATH)
                                .file(postData)
                                .file(photosData)
                                .file(photosData)
                                .file(photosData)
                                .file(photosData)
                                .file(photosData)
                                .file(photosData)
                                .file(photosData)
                                .file(photosData)
                                .file(photosData)
                                .file(photosData)
                                .file(photosData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Number of photos must be between 1 and 10.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_create_post_with_too_long_caption_should_return_error_response() {

        PostDTO postDTO = (PostDTO) postTestBuilder.withCaption("caption123".repeat(100) + "!").build(ObjectType.CREATE_DTO);

        String postAsString = ObjectTestMapper.asJsonString(postDTO);

        MockMultipartFile postData = new MockMultipartFile("post", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));

        MockMultipartFile photosData = new MockMultipartFile("photos", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(multipart(POSTS_BASE_PATH)
                                .file(postData)
                                .file(photosData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Caption cannot be longer than 1000.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }
}
