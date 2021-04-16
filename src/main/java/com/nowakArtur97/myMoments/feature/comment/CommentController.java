package com.nowakArtur97.myMoments.feature.comment;

import com.nowakArtur97.myMoments.common.baseModel.ErrorResponse;
import com.nowakArtur97.myMoments.common.util.JwtUtil;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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

    private final ModelMapper modelMapper;

    @PostMapping(path = "{postId}/comments")
    @ApiOperation(value = "Add a comment to the post", notes = "Add a comment to the post")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successfully added comment", response = ResponseEntity.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    ResponseEntity<CommentModel> addCommentToPost(
            @ApiParam(value = "Id of the Post being commented", name = "postId", type = "integer", required = true, example = "1")
            @PathVariable("postId") Long postId,
            @ApiParam(value = "Comment content", name = "comment", required = true) @RequestBody @Valid CommentDTO commentDTO,
            @ApiParam(hidden = true) @RequestHeader("Authorization") String authorizationHeader
    ) {

        String username = jwtUtil.extractUsernameFromHeader(authorizationHeader);

        CommentEntity commentEntity = commentService.addComment(postId, username, commentDTO);

        return new ResponseEntity<>(modelMapper.map(commentEntity, CommentModel.class), HttpStatus.CREATED);
    }

    @PutMapping(path = "{postId}/comments/{id}")
    @ApiOperation(value = "Update a comment in the post", notes = "Update a comment in the post")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated comment", response = ResponseEntity.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    ResponseEntity<CommentModel> updateCommentInPost(
            @ApiParam(value = "Id of the Post Comment being updated", name = "postId", type = "integer", required = true, example = "1")
            @PathVariable("postId") Long postId,
            @ApiParam(value = "Id of the Comment being updated", name = "id", type = "integer", required = true, example = "1")
            @PathVariable("id") Long id,
            @ApiParam(value = "Updated comment content", name = "comment", required = true) @RequestBody @Valid CommentDTO commentDTO,
            @ApiParam(hidden = true) @RequestHeader("Authorization") String authorizationHeader
    ) {

        String username = jwtUtil.extractUsernameFromHeader(authorizationHeader);

        CommentEntity commentEntity = commentService.updateComment(postId, id, username, commentDTO);

        return new ResponseEntity<>(modelMapper.map(commentEntity, CommentModel.class), HttpStatus.OK);
    }
}
