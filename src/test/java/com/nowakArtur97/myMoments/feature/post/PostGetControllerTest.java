package com.nowakArtur97.myMoments.feature.post;

import com.nowakArtur97.myMoments.advice.AuthenticationControllerAdvice;
import com.nowakArtur97.myMoments.advice.GlobalResponseEntityExceptionHandler;
import com.nowakArtur97.myMoments.common.util.JwtUtil;
import com.nowakArtur97.myMoments.feature.comment.CommentEntity;
import com.nowakArtur97.myMoments.feature.comment.CommentModel;
import com.nowakArtur97.myMoments.feature.comment.CommentTestBuilder;
import com.nowakArtur97.myMoments.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.testUtil.generator.NameWithSpacesGenerator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;
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
@Tag("PostController_Tests")
public class PostGetControllerTest {

    private final String POSTS_BASE_PATH = "http://localhost:8080/api/v1/posts/{id}";

    private MockMvc mockMvc;

    @Mock
    private PostService postService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PostObjectMapper postObjectMapper;

    @Mock
    private ModelMapper modelMapper;

    private static CommentTestBuilder commentTestBuilder;
    private static PostTestBuilder postTestBuilder;

    @BeforeAll
    static void setUpBuildersAndUUID() {

        commentTestBuilder = new CommentTestBuilder();
        postTestBuilder = new PostTestBuilder();
    }

    @BeforeEach
    void setUp() {

        PostController postController = new PostController(postService, jwtUtil, postObjectMapper, modelMapper);

        GlobalResponseEntityExceptionHandler globalResponseEntityExceptionHandler
                = new GlobalResponseEntityExceptionHandler();

        mockMvc = MockMvcBuilders.standaloneSetup(postController, globalResponseEntityExceptionHandler)
                .setControllerAdvice(new AuthenticationControllerAdvice())
                .build();
    }

    @Test
    @SneakyThrows
    void when_find_existing_post_should_return_post_with_comments() {

        Long postId = 1L;
        String header = "Bearer token";

        CommentEntity commentEntity = (CommentEntity) commentTestBuilder.build(ObjectType.ENTITY);
        MockMultipartFile imageEntity = new MockMultipartFile("image", "image",
                "application/json", "image.jpg".getBytes());
        PictureEntity pictureEntity = new PictureEntity(imageEntity.getBytes());
        PostEntity postEntity = (PostEntity) postTestBuilder.withId(postId).withCommentsEntity(Set.of(commentEntity))
                .withPhotosEntity(Set.of(pictureEntity)).build(ObjectType.ENTITY);

        CommentModel commentModel = (CommentModel) commentTestBuilder.build(ObjectType.MODEL);
        PictureModel pictureModel = new PictureModel(1L, imageEntity.getBytes());
        PostModel postModel = (PostModel) postTestBuilder.withId(postId).withCommentsModel(Set.of(commentModel))
                .withPhotosModel(Set.of(pictureModel)).build(ObjectType.MODEL);

        when(postService.findById(postId)).thenReturn(Optional.of(postEntity));
        when(modelMapper.map(postEntity, PostModel.class)).thenReturn(postModel);

        assertAll(
                () -> mockMvc.perform(get(POSTS_BASE_PATH, postId)
                        .header("Authorization", header))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("id", is(postModel.getId().intValue())))
                        .andExpect(jsonPath("caption", is(postModel.getCaption())))
                        .andExpect(jsonPath("photos[0].id", is(pictureModel.getId().intValue())))
                        .andExpect(jsonPath("photos[0].photo", is(notNullValue())))
                        .andExpect(jsonPath("photos", hasSize(1)))
                        .andExpect(jsonPath("comments[0].id", is(commentModel.getId().intValue())))
                        .andExpect(jsonPath("comments[0].content", is(commentModel.getContent())))
                        .andExpect(jsonPath("comments[0].createDate", is(notNullValue())))
                        .andExpect(jsonPath("comments[0].modifyDate", is(notNullValue())))
                        .andExpect(jsonPath("comments", hasSize(1))),
                () -> verify(postService, times(1)).findById(postId),
                () -> verifyNoMoreInteractions(postService),
                () -> verify(modelMapper, times(1)).map(postEntity, PostModel.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verifyNoInteractions(jwtUtil),
                () -> verifyNoInteractions(postObjectMapper));
    }

    @Test
    @SneakyThrows
    void when_find_existing_post_without_comments_should_return_post_without_comments() {

        Long postId = 1L;
        String header = "Bearer token";

        MockMultipartFile imageEntity = new MockMultipartFile("image", "image",
                "application/json", "image.jpg".getBytes());
        PictureEntity pictureEntity = new PictureEntity(imageEntity.getBytes());
        PostEntity postEntity = (PostEntity) postTestBuilder.withId(postId).withPhotosEntity(Set.of(pictureEntity))
                .build(ObjectType.ENTITY);

        PictureModel pictureModel = new PictureModel(1L, imageEntity.getBytes());
        PostModel postModel = (PostModel) postTestBuilder.withId(postId).withPhotosModel(Set.of(pictureModel))
                .build(ObjectType.MODEL);

        when(postService.findById(postId)).thenReturn(Optional.of(postEntity));
        when(modelMapper.map(postEntity, PostModel.class)).thenReturn(postModel);

        assertAll(
                () -> mockMvc.perform(get(POSTS_BASE_PATH, postId)
                        .header("Authorization", header))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("id", is(postModel.getId().intValue())))
                        .andExpect(jsonPath("caption", is(postModel.getCaption())))
                        .andExpect(jsonPath("photos[0].id", is(pictureModel.getId().intValue())))
                        .andExpect(jsonPath("photos[0].photo", is(notNullValue())))
                        .andExpect(jsonPath("photos", hasSize(1)))
                        .andExpect(jsonPath("comments[0]").doesNotExist())
                        .andExpect(jsonPath("comments", hasSize(0))),
                () -> verify(postService, times(1)).findById(postId),
                () -> verifyNoMoreInteractions(postService),
                () -> verify(modelMapper, times(1)).map(postEntity, PostModel.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verifyNoInteractions(jwtUtil),
                () -> verifyNoInteractions(postObjectMapper));
    }

    @Test
    void when_find_not_existing_post_should_return_error_response() {

        Long postId = 1L;
        String header = "Bearer token";

        when(postService.findById(postId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(get(POSTS_BASE_PATH, postId)
                        .header("Authorization", header))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(jsonPath("status", is(404)))
                        .andExpect(jsonPath("errors[0]", is("Post with id: '" + postId + "' not found.")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(postService, times(1)).findById(postId),
                () -> verifyNoMoreInteractions(postService),
                () -> verifyNoInteractions(jwtUtil),
                () -> verifyNoInteractions(postObjectMapper),
                () -> verifyNoInteractions(modelMapper));
    }
}
