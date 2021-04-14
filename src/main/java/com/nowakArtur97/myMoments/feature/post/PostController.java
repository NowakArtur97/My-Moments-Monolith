package com.nowakArtur97.myMoments.feature.post;

import com.nowakArtur97.myMoments.common.baseModel.ErrorResponse;
import com.nowakArtur97.myMoments.common.util.JwtUtil;
import com.nowakArtur97.myMoments.configuration.security.JwtConfigurationProperties;
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
class PostController {

    private final PostService postService;

    private final JwtUtil jwtUtil;

    private final JwtConfigurationProperties jwtConfigurationProperties;

    private final PostObjectMapper postObjectMapper;

    private final ModelMapper modelMapper;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation(value = "Create a post", notes = "Create a post")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successfully created post", response = ResponseEntity.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    ResponseEntity<PostModel> cretePost(
            @ApiParam(value = "The post's photos", name = "photos", required = true)
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos,
            @ApiParam(value = "The post's data", name = "post") @RequestPart(value = "post", required = false) String post,
            @ApiParam(hidden = true) @RequestHeader("Authorization") String authorizationHeader
    ) {

        PostDTO postDTO = postObjectMapper.getPostDTOFromString(post, photos);

        String jwt = authorizationHeader.substring(jwtConfigurationProperties.getAuthorizationHeaderLength());
        String username = jwtUtil.extractUsername(jwt);

        PostEntity postEntity = postService.createPost(username, postDTO);

        return new ResponseEntity<>(modelMapper.map(postEntity, PostModel.class), HttpStatus.CREATED);
    }
}