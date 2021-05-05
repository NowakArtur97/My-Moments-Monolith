package com.nowakArtur97.myMoments.feature.comment;

import com.nowakArtur97.myMoments.advice.AuthenticationControllerAdvice;
import com.nowakArtur97.myMoments.advice.GlobalResponseEntityExceptionHandler;
import com.nowakArtur97.myMoments.common.exception.ForbiddenException;
import com.nowakArtur97.myMoments.common.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.common.util.JwtUtil;
import com.nowakArtur97.myMoments.testUtil.generator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("CommentController_Tests")
class CommentDeleteControllerTest {

    private final String COMMENTS_BASE_PATH = "http://localhost:8080/api/v1/posts/{postId}/comments/{id}";

    private MockMvc mockMvc;

    @Mock
    private CommentService commentService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ModelMapper modelMapper;

    private static CommentTestBuilder commentTestBuilder;

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
    void when_delete_comment_should_not_return_content() {

        Long postId = 1L;
        Long commentId = 2L;
        String username = "username";
        String header = "Bearer token";

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);

        assertAll(
                () -> mockMvc.perform(delete(COMMENTS_BASE_PATH, postId, commentId)
                        .header("Authorization", header))
                        .andExpect(status().isNoContent())
                        .andExpect(jsonPath("$").doesNotExist()),
                () -> verify(commentService, times(1)).deleteComment(postId, commentId, username),
                () -> verifyNoMoreInteractions(commentService),
                () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                () -> verifyNoMoreInteractions(jwtUtil),
                () -> verifyNoInteractions(modelMapper));
    }

    @Test
    void when_delete_comment_of_not_existing_user_should_return_error_response() {

        Long postId = 1L;
        Long commentId = 2L;
        String username = "username";
        String header = "Bearer token";

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
        doThrow(new UsernameNotFoundException("User with name: '" + username + "' not found."))
                .when(commentService).deleteComment(postId, commentId, username);

        assertAll(
                () -> mockMvc.perform(delete(COMMENTS_BASE_PATH, postId, commentId)
                        .header("Authorization", header))
                        .andExpect(status().isUnauthorized())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(jsonPath("status", is(401)))
                        .andExpect(jsonPath("errors[0]", is("User with name: '" + username + "' not found.")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(commentService, times(1)).deleteComment(postId, commentId, username),
                () -> verifyNoMoreInteractions(commentService),
                () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                () -> verifyNoMoreInteractions(jwtUtil),
                () -> verifyNoInteractions(modelMapper));
    }

    @Test
    void when_delete_not_existing_comment_should_return_error_response() {

        Long postId = 1L;
        Long commentId = 2L;
        String username = "username";
        String header = "Bearer token";

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
        doThrow(new ResourceNotFoundException("Post", postId))
                .when(commentService).deleteComment(postId, commentId, username);

        assertAll(
                () -> mockMvc.perform(delete(COMMENTS_BASE_PATH, postId, commentId)
                        .header("Authorization", header))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(jsonPath("status", is(404)))
                        .andExpect(jsonPath("errors[0]", is("Post with id: '" + postId + "' not found.")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(commentService, times(1)).deleteComment(postId, commentId, username),
                () -> verifyNoMoreInteractions(commentService),
                () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                () -> verifyNoMoreInteractions(jwtUtil),
                () -> verifyNoInteractions(modelMapper));
    }

    @Test
    void when_delete_some_other_user_comment_should_return_error_response() {

        Long postId = 1L;
        Long commentId = 2L;
        String username = "username";
        String header = "Bearer token";

        when(jwtUtil.extractUsernameFromHeader(header)).thenReturn(username);
        doThrow(new ForbiddenException("User can only delete his own posts."))
                .when(commentService).deleteComment(postId, commentId, username);

        assertAll(
                () -> mockMvc.perform(delete(COMMENTS_BASE_PATH, postId, commentId)
                        .header("Authorization", header))
                        .andExpect(status().isForbidden())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(jsonPath("status", is(403)))
                        .andExpect(jsonPath("errors[0]", is("User can only delete his own posts.")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(commentService, times(1)).deleteComment(postId, commentId, username),
                () -> verifyNoMoreInteractions(commentService),
                () -> verify(jwtUtil, times(1)).extractUsernameFromHeader(header),
                () -> verifyNoMoreInteractions(jwtUtil),
                () -> verifyNoInteractions(modelMapper));
    }
}
