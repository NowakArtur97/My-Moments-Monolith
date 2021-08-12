package com.nowakArtur97.myMoments.feature.post;

import com.nowakArtur97.myMoments.common.baseModel.ErrorResponse;
import com.nowakArtur97.myMoments.common.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.common.util.JwtUtil;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Api(tags = {PostTag.RESOURCE})
@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
class PostController {

    private final PostService postService;

    private final JwtUtil jwtUtil;

    private final PostObjectMapper postObjectMapper;

    private final ModelMapper modelMapper;

    @GetMapping(path = "/{id}")
    @ApiOperation(value = "Get a post", notes = "Provide an id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully found post", response = PostModel.class),
            @ApiResponse(code = 404, message = "Could not find Post with provided id", response = ErrorResponse.class)})
    ResponseEntity<PostModel> getPost(
            @ApiParam(value = "Id of the Post being looked up", name = "id", type = "integer",
                    required = true, example = "1") @PathVariable("id") Long id,
            @ApiParam(hidden = true) @RequestHeader("Authorization") String authorizationHeader
    ) {

        PostEntity postEntity = postService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", id));

        return new ResponseEntity<>(modelMapper.map(postEntity, PostModel.class), HttpStatus.OK);
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiImplicitParams(value = {
            @ApiImplicitParam(dataType = "__file", value = "The post's photos", name = "photos",
                    required = true, paramType = "form")
    })
    @ApiOperation("Create a post")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successfully created post", response = PostModel.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    ResponseEntity<PostModel> cretePost(
            @RequestPart(value = "photos") List<MultipartFile> photos,
            @ApiParam(value = "The post's data", name = "post") @RequestPart(value = "post", required = false) String post,
            @ApiParam(hidden = true) @RequestHeader("Authorization") String authorizationHeader
    ) {

        PostDTO postDTO = postObjectMapper.getPostDTOFromString(post, photos);

        String username = jwtUtil.extractUsernameFromHeader(authorizationHeader);

        PostEntity postEntity = postService.createPost(username, postDTO);

        return new ResponseEntity<>(modelMapper.map(postEntity, PostModel.class), HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation(value = "Update a post", notes = "Provide an id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated post", response = PostModel.class),
            @ApiResponse(code = 400, message = "Invalid Post's id supplied or incorrectly entered data"),
            @ApiResponse(code = 404, message = "Could not find Post with provided id", response = ErrorResponse.class)})
    ResponseEntity<PostModel> updatePost(
            @ApiParam(value = "Id of the Post being updated", name = "id", type = "integer",
                    required = true, example = "1") @PathVariable("id") Long id,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos,
            @ApiParam(value = "The post's data", name = "post") @RequestPart(value = "post", required = false) String post,
            @ApiParam(hidden = true) @RequestHeader("Authorization") String authorizationHeader
    ) {

        PostDTO postDTO = postObjectMapper.getPostDTOFromString(post, photos);

        String username = jwtUtil.extractUsernameFromHeader(authorizationHeader);

        PostEntity postEntity = postService.updatePost(id, username, postDTO);

        return new ResponseEntity<>(modelMapper.map(postEntity, PostModel.class), HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Added to remove the default 200 status added by Swagger
    @ApiOperation(value = "Delete a post", notes = "Provide an id")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Successfully deleted a post"),
            @ApiResponse(code = 400, message = "Invalid Post's id supplied"),
            @ApiResponse(code = 404, message = "Could not find Post with provided id", response = ErrorResponse.class)})
    ResponseEntity<Void> deletePost(
            @ApiParam(value = "Id of the Post being deleted", name = "id", type = "integer",
                    required = true, example = "1")
            @PathVariable("id") Long id,
            @ApiParam(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {

        String username = jwtUtil.extractUsernameFromHeader(authorizationHeader);

        postService.deletePost(id, username);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
