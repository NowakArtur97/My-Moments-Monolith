package com.nowakArtur97.myMoments.feature.comment;

import com.nowakArtur97.myMoments.common.baseModel.ErrorResponse;
import com.nowakArtur97.myMoments.common.util.JwtUtil;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Api(tags = {CommentTag.RESOURCE})
class CommentController {

    private final CommentService commentService;

    private final JwtUtil jwtUtil;

    @PostMapping(path = "{id}/comments")
    @ApiOperation(value = "Add a comment to the post", notes = "Add a comment to the post")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successfully added comment", response = ResponseEntity.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    ResponseEntity<CommentEntity> addCommentToPost(
            @ApiParam(value = "Id of the Post being commented", name = "id", type = "integer", required = true, example = "1")
            @PathVariable("id") Long id,
            @ApiParam(value = "Comment content", name = "comment", required = true) @RequestBody @Valid CommentDTO commentDTO,
            @ApiParam(hidden = true) @RequestHeader("Authorization") String authorizationHeader
    ) {

        String username = jwtUtil.extractUsernameFromHeader(authorizationHeader);

        CommentEntity commentEntity = commentService.addComment(id, username, commentDTO);

        return new ResponseEntity<>(commentEntity, HttpStatus.CREATED);
    }
}
