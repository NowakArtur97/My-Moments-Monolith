package com.nowakArtur97.myMoments.feature.user.resource;

import com.nowakArtur97.myMoments.common.baseModel.ErrorResponse;
import com.nowakArtur97.myMoments.common.util.JwtUtil;
import com.nowakArtur97.myMoments.feature.post.PostEntity;
import com.nowakArtur97.myMoments.feature.post.PostModel;
import com.nowakArtur97.myMoments.feature.user.authentication.AuthenticationResponse;
import com.nowakArtur97.myMoments.feature.user.entity.CustomUserDetailsService;
import com.nowakArtur97.myMoments.feature.user.entity.UserEntity;
import com.nowakArtur97.myMoments.feature.user.entity.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Api(tags = {UserTag.RESOURCE})
@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Permission to the resource is prohibited"),
        @ApiResponse(code = 403, message = "Access to the resource is prohibited")})
class UserController {

    @Value("${my-moments.jwt.validity:36000000}")
    private long validity;

    private final UserService userService;

    private final CustomUserDetailsService customUserDetailsService;

    private final JwtUtil jwtUtil;

    private final UserObjectMapper userObjectMapper;

    private final ModelMapper modelMapper;

    @GetMapping("/me/posts")
    @ApiOperation("Find User's Posts")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User's posts found", response = UsersPostsModel.class),
            @ApiResponse(code = 404, message = "Could not find User with provided token", response = ErrorResponse.class)})
    ResponseEntity<UsersPostsModel> getAuthenticatedUsersPosts(
            @ApiParam(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {

        String username = jwtUtil.extractUsernameFromHeader(authorizationHeader);

        Set<PostEntity> usersPostsEntities = userService.getUsersPosts(username);

        Set<PostModel> usersPostsModels = usersPostsEntities.stream().map(post -> modelMapper.map(post, PostModel.class))
                .collect(Collectors.toSet());

        return new ResponseEntity<>(new UsersPostsModel(usersPostsModels), HttpStatus.OK);
    }

    @GetMapping("/{id}/posts")
    @ApiOperation(value = "Find User's Posts by id", notes = "Provide an id to look up specific User")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User's posts found", response = UsersPostsModel.class),
            @ApiResponse(code = 400, message = "Invalid User's id supplied"),
            @ApiResponse(code = 404, message = "Could not find User with provided id", response = ErrorResponse.class)})
    ResponseEntity<UsersPostsModel> getUsersPosts(
            @ApiParam(value = "Id of the User being looked up", name = "id", type = "integer", required = true, example = "1")
            @PathVariable("id") Long id) {

        Set<PostEntity> usersPostsEntities = userService.getUsersPosts(id);

        Set<PostModel> usersPostsModels = usersPostsEntities.stream().map(post -> modelMapper.map(post, PostModel.class))
                .collect(Collectors.toSet());

        return new ResponseEntity<>(new UsersPostsModel(usersPostsModels), HttpStatus.OK);
    }

    @PutMapping(path = "/me", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation("Update an account")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated account", response = UserModel.class),
            @ApiResponse(code = 400, message = "Invalid User's token supplied or incorrectly entered data"),
            @ApiResponse(code = 404, message = "Could not find User with provided token", response = ErrorResponse.class)})
    ResponseEntity<UserModel> updateUser(
            @ApiParam(value = "The user's data", name = "user", required = true)
            @RequestPart(value = "user", required = false) String user,
            @ApiParam(value = "The user's image", name = "image")
            @RequestPart(value = "image", required = false) MultipartFile image,
            @ApiParam(hidden = true) @RequestHeader("Authorization") String authorizationHeader) throws IOException {

        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userObjectMapper.getUserDTOFromString(user, UserUpdateDTO.class);

        String username = jwtUtil.extractUsernameFromHeader(authorizationHeader);

        UserEntity updatedUserEntity = userService.updateUser(username, userUpdateDTO, image);

        UserModel userModel = modelMapper.map(updatedUserEntity, UserModel.class);

        UserDetails userDetails = new User(updatedUserEntity.getUsername(), updatedUserEntity.getPassword(),
                customUserDetailsService.getAuthorities(updatedUserEntity.getRoles()));

        String newToken = jwtUtil.generateToken(userDetails);

        userModel.setAuthenticationResponse(new AuthenticationResponse(newToken, validity));

        return new ResponseEntity<>(userModel, HttpStatus.OK);
    }

    @DeleteMapping(path = "/me")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Added to remove the default 200 status added by Swagger
    @ApiOperation("Delete an account")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Successfully deleted an account"),
            @ApiResponse(code = 400, message = "Invalid User's token supplied"),
            @ApiResponse(code = 404, message = "Could not find User with provided token", response = ErrorResponse.class)})
    ResponseEntity<Void> deleteUser(
            @ApiParam(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {

        String username = jwtUtil.extractUsernameFromHeader(authorizationHeader);

        userService.deleteUser(username);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
