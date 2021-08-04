package com.nowakArtur97.myMoments.feature.comment;

import com.nowakArtur97.myMoments.advice.AuthenticationControllerAdvice;
import com.nowakArtur97.myMoments.advice.GlobalResponseEntityExceptionHandler;
import com.nowakArtur97.myMoments.common.exception.ForbiddenException;
import com.nowakArtur97.myMoments.common.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.common.util.JwtUtil;
import com.nowakArtur97.myMoments.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.testUtil.generator.NameWithSpacesGenerator;
import com.nowakArtur97.myMoments.testUtil.mapper.ObjectTestMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.text.SimpleDateFormat;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("CommentController_Tests")
class CommentUpdateControllerTest {

    @LocalServerPort
    private int port;

    private final String COMMENTS_BASE_PATH = "http://localhost:" + port + "/api/v1/posts/{postId}/comments/{id}";

    private MockMvc mockMvc;

    @Mock
    private CommentService commentService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ModelMapper modelMapper;

    private static CommentTestBuilder commentTestBuilder;

    private final String pattern = "yyyy-MM-dd HH:mm:ss";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

    @BeforeAll
    static void setUpBuilders() {

        commentTestBuilder = new CommentTestBuilder();
    }

    @BeforeEach
    void setUp() {

        CommentController commentController = new CommentController(commentService, jwtUtil, modelMapper);

        GlobalResponseEntityExceptionHandler globalResponseEntityExceptionHandler
                = new GlobalResponseEntityExceptionHandler();

        mockMvc = MockMvcBuilders.standaloneSetup(commentController, globalResponseEntityExceptionHandler)
                .setControllerAdvice(new AuthenticationControllerAdvice())
                .build();
    }

    @Test
    void when_update_comment_should_return_comment() {

        Long postId = 1L;
        Long commentId = 2L;
        String username = "username";
        String header = "Bearer token";

        CommentDTO commentDTO = (CommentDTO) commentTestBuilder.build(ObjectType.CREATE_DTO);
        CommentEntity commentEntity = (CommentEntity) commentTestBuilder.withId(commentId).build(ObjectType.ENTITY);
        CommentModel commentModel = (CommentModel) commentTestBuilder.withId(commentId).build(ObjectType.MODEL);

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
        when(commentService.updateComment(postId, commentId, username, commentDTO)).thenReturn(commentEntity);
        when(modelMapper.map(commentEntity, CommentModel.class)).thenReturn(commentModel);

        assertAll(
                () -> mockMvc.perform(put(COMMENTS_BASE_PATH, postId, commentId)
                        .header("Authorization", header)
                        .content(ObjectTestMapper.asJsonString(commentDTO))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("id", is(commentModel.getId().intValue())))
                        .andExpect(jsonPath("content", is(commentModel.getContent())))
                        .andExpect(jsonPath("createDate", is(simpleDateFormat.format(commentModel.getCreateDate()))))
                        .andExpect(jsonPath("modifyDate", is(simpleDateFormat.format(commentModel.getCreateDate())))),
                () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                () -> verifyNoMoreInteractions(jwtUtil),
                () -> verify(commentService, times(1)).updateComment(postId, commentId, username, commentDTO),
                () -> verifyNoMoreInteractions(commentService),
                () -> verify(modelMapper, times(1)).map(commentEntity, CommentModel.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }

    @ParameterizedTest(name = "{index}: For Comment content: {0}")
    @EmptySource
    @ValueSource(strings = {" "})
    void when_update_comment_without_content_should_return_error_response(String invalidContent) {

        Long postId = 1L;
        Long commentId = 2L;
        String header = "Bearer token";

        CommentDTO commentDTO = (CommentDTO) commentTestBuilder.withContent(invalidContent).build(ObjectType.CREATE_DTO);

        assertAll(
                () -> mockMvc.perform(put(COMMENTS_BASE_PATH, postId, commentId)
                        .header("Authorization", header)
                        .content(ObjectTestMapper.asJsonString(commentDTO))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("{comment.content.notBlank}")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verifyNoInteractions(jwtUtil),
                () -> verifyNoInteractions(commentService),
                () -> verifyNoInteractions(modelMapper));
    }

    @Test
    void when_update_comment_by_not_existing_user_should_return_error_response() {

        Long postId = 1L;
        Long commentId = 2L;
        String username = "username";
        String header = "Bearer token";

        CommentDTO commentDTO = (CommentDTO) commentTestBuilder.build(ObjectType.CREATE_DTO);

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
        doThrow(new UsernameNotFoundException("User with name: '" + username + "' not found."))
                .when(commentService).updateComment(postId, commentId, username, commentDTO);

        assertAll(
                () -> mockMvc.perform(put(COMMENTS_BASE_PATH, postId, commentId)
                        .header("Authorization", header)
                        .content(ObjectTestMapper.asJsonString(commentDTO))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isUnauthorized())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(jsonPath("status", is(401)))
                        .andExpect(jsonPath("errors[0]", is("User with name: '" + username + "' not found.")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                () -> verifyNoMoreInteractions(jwtUtil),
                () -> verify(commentService, times(1)).updateComment(postId, commentId, username, commentDTO),
                () -> verifyNoMoreInteractions(commentService),
                () -> verifyNoInteractions(modelMapper));
    }

    @Test
    void when_update_comment_to_not_existing_post_should_return_error_response() {

        Long postId = 1L;
        Long commentId = 2L;
        String username = "username";
        String header = "Bearer token";

        CommentDTO commentDTO = (CommentDTO) commentTestBuilder.build(ObjectType.CREATE_DTO);

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
        doThrow(new ResourceNotFoundException("Post", postId)).when(commentService)
                .updateComment(postId, commentId, username, commentDTO);

        assertAll(
                () -> mockMvc.perform(put(COMMENTS_BASE_PATH, postId, commentId)
                        .header("Authorization", header)
                        .content(ObjectTestMapper.asJsonString(commentDTO))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(jsonPath("status", is(404)))
                        .andExpect(jsonPath("errors[0]", is("Post with id: '" + postId + "' not found.")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                () -> verifyNoMoreInteractions(jwtUtil),
                () -> verify(commentService, times(1)).updateComment(postId, commentId, username, commentDTO),
                () -> verifyNoMoreInteractions(commentService),
                () -> verifyNoInteractions(modelMapper));
    }

    @Test
    void when_update_not_existing_comment_should_return_error_response() {

        Long postId = 1L;
        Long commentId = 2L;
        String username = "username";
        String header = "Bearer token";

        CommentDTO commentDTO = (CommentDTO) commentTestBuilder.build(ObjectType.CREATE_DTO);

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
        doThrow(new ResourceNotFoundException("Comment", commentId)).when(commentService)
                .updateComment(postId, commentId, username, commentDTO);

        assertAll(
                () -> mockMvc.perform(put(COMMENTS_BASE_PATH, postId, commentId)
                        .header("Authorization", header)
                        .content(ObjectTestMapper.asJsonString(commentDTO))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(jsonPath("status", is(404)))
                        .andExpect(jsonPath("errors[0]",
                                is("Comment with id: '" + commentId + "' not found.")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                () -> verifyNoMoreInteractions(jwtUtil),
                () -> verify(commentService, times(1)).updateComment(postId, commentId, username, commentDTO),
                () -> verifyNoMoreInteractions(commentService),
                () -> verifyNoInteractions(modelMapper));
    }

    @Test
    void when_update_not_existing_comment_on_specific_post_should_return_error_response() {

        Long postId = 1L;
        Long commentId = 2L;
        String username = "username";
        String header = "Bearer token";

        CommentDTO commentDTO = (CommentDTO) commentTestBuilder.build(ObjectType.CREATE_DTO);

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
        doThrow(new ResourceNotFoundException("Comment with id: '" + commentId + "' in the post with id: '" + postId
                + "' not found.")).when(commentService)
                .updateComment(postId, commentId, username, commentDTO);

        assertAll(
                () -> mockMvc.perform(put(COMMENTS_BASE_PATH, postId, commentId)
                        .header("Authorization", header)
                        .content(ObjectTestMapper.asJsonString(commentDTO))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(jsonPath("status", is(404)))
                        .andExpect(jsonPath("errors[0]",
                                is("Comment with id: '" + commentId + "' in the post with id: '" + postId + "' not found.")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                () -> verifyNoMoreInteractions(jwtUtil),
                () -> verify(commentService, times(1)).updateComment(postId, commentId, username, commentDTO),
                () -> verifyNoMoreInteractions(commentService),
                () -> verifyNoInteractions(modelMapper));
    }

    @Test
    void when_update_some_other_user_comment_should_return_error_response() {

        Long postId = 1L;
        Long commentId = 2L;
        String username = "username";
        String header = "Bearer token";

        CommentDTO commentDTO = (CommentDTO) commentTestBuilder.build(ObjectType.CREATE_DTO);

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
        doThrow(new ForbiddenException("User can only change his own comments.")).when(commentService)
                .updateComment(postId, commentId, username, commentDTO);

        assertAll(
                () -> mockMvc.perform(put(COMMENTS_BASE_PATH, postId, commentId)
                        .header("Authorization", header)
                        .content(ObjectTestMapper.asJsonString(commentDTO))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isForbidden())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(jsonPath("status", is(403)))
                        .andExpect(jsonPath("errors[0]", is("User can only change his own comments.")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                () -> verifyNoMoreInteractions(jwtUtil),
                () -> verify(commentService, times(1)).updateComment(postId, commentId, username, commentDTO),
                () -> verifyNoMoreInteractions(commentService),
                () -> verifyNoInteractions(modelMapper));
    }
}
