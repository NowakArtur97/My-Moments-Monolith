package com.nowakArtur97.myMoments.feature.user.resource;

import com.nowakArtur97.myMoments.common.baseModel.ErrorResponse;
import com.nowakArtur97.myMoments.feature.user.registration.UserDTO;
import com.nowakArtur97.myMoments.feature.user.shared.UserService;
import com.nowakArtur97.myMoments.feature.user.shared.UserEntity;
import com.nowakArtur97.myMoments.feature.user.shared.UserObjectMapper;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Api(tags = {UserTag.RESOURCE})
@Slf4j
class UserController {

    private final UserService userService;

    private final UserObjectMapper userObjectMapper;

    @PutMapping(path = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation(value = "Update an account", notes = "Update an account")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully updated account", response = String.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    ResponseEntity<UserEntity> updateUser(
            @ApiParam(value = "Id of the User being updated", name = "id", type = "integer",
                    required = true, example = "1")
            @PathVariable("id") Long id,
            @ApiParam(value = "The user's data", name = "user", required = true)
            @RequestPart("user") String user,
            @ApiParam(value = "The user's image", name = "image")
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        UserEntity userEntity = userService.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User with id: '" + id + "' not found."));

        UserDTO userDTO = userObjectMapper.getUserDTOFromString(user);

        userDTO.setId(id);

        UserEntity updatedUserEntity = userService.updateUser(userEntity, userDTO, image);

        return new ResponseEntity<>(updatedUserEntity, HttpStatus.OK);
    }
}
