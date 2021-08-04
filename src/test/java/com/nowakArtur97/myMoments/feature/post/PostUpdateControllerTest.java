package com.nowakArtur97.myMoments.feature.post;

import com.nowakArtur97.myMoments.common.exception.RoleNotFoundException;
import com.nowakArtur97.myMoments.common.util.JwtUtil;
import com.nowakArtur97.myMoments.feature.comment.CommentEntity;
import com.nowakArtur97.myMoments.feature.comment.CommentTestBuilder;
import com.nowakArtur97.myMoments.feature.user.entity.*;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("PostController_Tests")
class PostUpdateControllerTest {

    @LocalServerPort
    private int port;

    private final String POSTS_BASE_PATH = "http://localhost:" + port + "/api/v1/posts/{id}";

    @Value("${my-moments.default-user-role:USER_ROLE}")
    private String defaultUserRole;

    @Autowired
    private MockMvc mockMvc;

    private MockMultipartHttpServletRequestBuilder mockRequestBuilder;

    @Autowired
    private Flyway flyway;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private static CommentTestBuilder commentTestBuilder;
    private static PostTestBuilder postTestBuilder;
    private static UserProfileTestBuilder userProfileTestBuilder;
    private static UserTestBuilder userTestBuilder;

    private String token;
    private CommentEntity commentEntity;
    private PostEntity postEntity;
    private UserEntity userEntity;

    @BeforeAll
    static void setUpBuilders() {

        commentTestBuilder = new CommentTestBuilder();
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

        commentEntity = (CommentEntity) commentTestBuilder.build(ObjectType.ENTITY);

        PictureEntity pictureEntity = new PictureEntity("image".getBytes());
        postEntity = new PostEntity("caption", userEntity, Set.of(pictureEntity), new HashSet<>());
        pictureEntity.setRelatedPost(postEntity);

        postEntity.addComment(commentEntity);
        userEntity.addComment(commentEntity);
        userEntity.addPost(postEntity);
        userRepository.save(userEntity);

        token = jwtUtil.generateToken(new User(userEntity.getUsername(), userEntity.getPassword(),
                List.of(new SimpleGrantedAuthority(defaultUserRole))));

        mockRequestBuilder = MockMvcRequestBuilders.multipart(POSTS_BASE_PATH, postEntity.getId());
        mockRequestBuilder.with(request -> {
            request.setMethod("PUT");
            return request;
        });
    }

    @AfterEach
    void cleanUp() {

        flyway.clean();
        flyway.migrate();

        userRepository.delete(userEntity);
    }

    @Test
    void when_update_valid_post_should_return_post() {

        String caption = "description";
        PostDTO postDTO = (PostDTO) postTestBuilder.withCaption(caption).build(ObjectType.CREATE_DTO);

        String postAsString = ObjectTestMapper.asJsonString(postDTO);

        MockMultipartFile postData = new MockMultipartFile("post", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));

        MockMultipartFile photosData = new MockMultipartFile("photos", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(postData)
                                .file(photosData)
                                .file(photosData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("id", is(postEntity.getId().intValue())))
                        .andExpect(jsonPath("caption", is(caption)))
                        .andExpect(jsonPath("photos[0].id", is(notNullValue())))
                        .andExpect(jsonPath("photos[0].photo", is(notNullValue())))
                        .andExpect(jsonPath("photos[1].id", is(notNullValue())))
                        .andExpect(jsonPath("photos[1].photo", is(notNullValue())))
                        .andExpect(jsonPath("photos", hasSize(2)))
                        .andExpect(jsonPath("comments[0].id", is(commentEntity.getId().intValue())))
                        .andExpect(jsonPath("comments[0].content", is(commentEntity.getContent())))
                        .andExpect(jsonPath("comments[0].createDate", is(notNullValue())))
                        .andExpect(jsonPath("comments[0].modifyDate", is(notNullValue())))
                        .andExpect(jsonPath("comments", hasSize(1))));
    }

    @Test
    void when_update_valid_post_without_caption_should_return_post_with_empty_caption() {

        PostDTO postDTO = (PostDTO) postTestBuilder.build(ObjectType.CREATE_DTO);

        String postAsString = ObjectTestMapper.asJsonString(postDTO);

        MockMultipartFile postData = new MockMultipartFile("post", "request",
                MediaType.APPLICATION_JSON_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));

        MockMultipartFile photosData = new MockMultipartFile("photos", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(postData)
                                .file(photosData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("id", is(postEntity.getId().intValue())))
                        .andExpect(jsonPath("caption", is("")))
                        .andExpect(jsonPath("photos[0].id", is(notNullValue())))
                        .andExpect(jsonPath("photos[0].photo", is(notNullValue())))
                        .andExpect(jsonPath("photos", hasSize(1)))
                        .andExpect(jsonPath("comments[0].id", is(commentEntity.getId().intValue())))
                        .andExpect(jsonPath("comments[0].content", is(commentEntity.getContent())))
                        .andExpect(jsonPath("comments[0].createDate", is(notNullValue())))
                        .andExpect(jsonPath("comments[0].modifyDate", is(notNullValue())))
                        .andExpect(jsonPath("comments", hasSize(1))));
    }

    @Test
    void when_update_post_without_photos_should_return_error_response() {

        PostDTO postDTO = (PostDTO) postTestBuilder.withCaption("description").build(ObjectType.CREATE_DTO);

        String postAsString = ObjectTestMapper.asJsonString(postDTO);

        MockMultipartFile postData = new MockMultipartFile("post", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
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
    void when_update_post_without_photos_and_caption_should_return_error_response() {

        PostDTO postDTO = (PostDTO) postTestBuilder.build(ObjectType.CREATE_DTO);

        String postAsString = ObjectTestMapper.asJsonString(postDTO);

        MockMultipartFile postData = new MockMultipartFile("post", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
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
    void when_update_post_with_too_many_photos_should_return_error_response() {

        PostDTO postDTO = (PostDTO) postTestBuilder.build(ObjectType.CREATE_DTO);

        String postAsString = ObjectTestMapper.asJsonString(postDTO);

        MockMultipartFile postData = new MockMultipartFile("post", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));

        MockMultipartFile photosData = new MockMultipartFile("photos", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));


        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
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
    void when_update_post_with_too_long_caption_should_return_error_response() {

        PostDTO postDTO = (PostDTO) postTestBuilder.withCaption("caption123".repeat(100) + "!").build(ObjectType.CREATE_DTO);

        String postAsString = ObjectTestMapper.asJsonString(postDTO);

        MockMultipartFile postData = new MockMultipartFile("post", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));

        MockMultipartFile photosData = new MockMultipartFile("photos", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
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

    @Test
    void when_update_user_post_without_token_should_return_error_response() {

        PostDTO postDTO = (PostDTO) postTestBuilder.withCaption("description").build(ObjectType.CREATE_DTO);

        String postAsString = ObjectTestMapper.asJsonString(postDTO);

        MockMultipartFile postData = new MockMultipartFile("post", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));

        MockMultipartFile photosData = new MockMultipartFile("photos", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(postData)
                                .file(photosData)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isUnauthorized())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(401)))
                        .andExpect(jsonPath("errors[0]", is("JWT token is missing in request headers.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_other_user_post_should_return_error_response() {

        PostDTO postDTO = (PostDTO) postTestBuilder.withCaption("description").build(ObjectType.CREATE_DTO);

        String postAsString = ObjectTestMapper.asJsonString(postDTO);

        MockMultipartFile postData = new MockMultipartFile("post", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));

        MockMultipartFile photosData = new MockMultipartFile("photos", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));

        token = jwtUtil.generateToken(new User("user", "user",
                List.of(new SimpleGrantedAuthority(defaultUserRole))));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(postData)
                                .file(photosData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isForbidden())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(403)))
                        .andExpect(jsonPath("errors[0]", is("User can only change his own posts.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_not_existing_post_should_return_error_response() {

        Long notExistingPostId = 10L;

        PostDTO postDTO = (PostDTO) postTestBuilder.withCaption("description").build(ObjectType.CREATE_DTO);

        String postAsString = ObjectTestMapper.asJsonString(postDTO);

        MockMultipartFile postData = new MockMultipartFile("post", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));

        MockMultipartFile photosData = new MockMultipartFile("photos", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));

        mockRequestBuilder = MockMvcRequestBuilders.multipart(POSTS_BASE_PATH, notExistingPostId);
        mockRequestBuilder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(postData)
                                .file(photosData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(404)))
                        .andExpect(jsonPath("errors[0]", is("Post with id: '" + notExistingPostId + "' not found.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_not_existing_user_post_should_return_error_response() {

        String notExistingUsername = "iAmNotExist";

        PostDTO postDTO = (PostDTO) postTestBuilder.withCaption("description").build(ObjectType.CREATE_DTO);

        String postAsString = ObjectTestMapper.asJsonString(postDTO);

        MockMultipartFile postData = new MockMultipartFile("post", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));

        MockMultipartFile photosData = new MockMultipartFile("photos", "request",
                MediaType.MULTIPART_FORM_DATA_VALUE, postAsString.getBytes(StandardCharsets.UTF_8));

        token = jwtUtil.generateToken(new User(notExistingUsername, notExistingUsername,
                List.of(new SimpleGrantedAuthority(defaultUserRole))));

        assertAll(
                () -> mockMvc
                        .perform(mockRequestBuilder
                                .file(postData)
                                .file(photosData)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isUnauthorized())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(401)))
                        .andExpect(jsonPath("errors[0]",
                                is("User with name/email: '" + notExistingUsername + "' not found.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }
}
