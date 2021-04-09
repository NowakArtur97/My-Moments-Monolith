package com.nowakArtur97.myMoments.feature.user.resource;

import com.nowakArtur97.myMoments.common.baseModel.ErrorResponse;
import com.nowakArtur97.myMoments.common.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.common.util.JwtUtil;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Api(tags = {UserTag.RESOURCE})
class UserController {

    @Value("${my-moments.jwt.validity:36000000}")
    private long validity;

    private final UserService userService;

    private final CustomUserDetailsService customUserDetailsService;

    private final JwtUtil jwtUtil;

    private final UserObjectMapper userObjectMapper;

    private final ModelMapper modelMapper;

    @PutMapping(path = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation(value = "Update an account", notes = "Update an account")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated account", response = String.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    ResponseEntity<UserModel> updateUser(
            @ApiParam(value = "Id of the User being updated", name = "id", type = "integer",
                    required = true, example = "1")
            @PathVariable("id") Long id,
            @ApiParam(value = "The user's data", name = "user", required = true)
            @RequestPart("user") String user,
            @ApiParam(value = "The user's image", name = "image")
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        UserEntity userEntity = userService.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User with id: '" + id + "' not found."));

        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) userObjectMapper.getUserDTOFromString(user, UserUpdateDTO.class);

        UserEntity updatedUserEntity = userService.updateUser(id, userEntity, userUpdateDTO, image);

        UserModel userModel = modelMapper.map(updatedUserEntity, UserModel.class);

        UserDetails userDetails = new User(updatedUserEntity.getUsername(), updatedUserEntity.getPassword(),
                customUserDetailsService.getAuthorities(updatedUserEntity.getRoles()));

        String newToken = jwtUtil.generateToken(userDetails);

        userModel.setAuthenticationResponse(new AuthenticationResponse(newToken, validity));

        return new ResponseEntity<>(userModel, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Added to remove the default 200 status added by Swagger
    @ApiOperation(value = "Delete an account", notes = "Delete an account")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Successfully deleted an account"),
            @ApiResponse(code = 400, message = "Invalid User's id supplied"),
            @ApiResponse(code = 404, message = "Could not find User with provided id", response = ErrorResponse.class)})
    ResponseEntity<Void> deleteUser(
            @ApiParam(value = "Id of the User being deleted", name = "id", type = "integer",
                    required = true, example = "1")
            @PathVariable("id") Long id) {

        userService.deleteUser(id).orElseThrow(() -> new ResourceNotFoundException("User", id));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
