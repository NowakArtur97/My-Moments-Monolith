package com.nowakArtur97.myMoments.feature.user.resource;

import com.nowakArtur97.myMoments.common.baseModel.ErrorResponse;
import com.nowakArtur97.myMoments.common.util.JwtUtil;
import com.nowakArtur97.myMoments.feature.user.authentication.AuthenticationResponse;
import com.nowakArtur97.myMoments.feature.user.entity.CustomUserDetailsService;
import com.nowakArtur97.myMoments.feature.user.entity.UserEntity;
import com.nowakArtur97.myMoments.feature.user.entity.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.management.relation.RoleNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/registration")
@RequiredArgsConstructor
@Api(tags = {UserRegistrationTag.RESOURCE})
class UserRegistrationController {

    @Value("${my-moments.jwt.validity:36000000}")
    private long validity;

    private final UserService userService;

    private final CustomUserDetailsService customUserDetailsService;

    private final JwtUtil jwtUtil;

    private final UserObjectMapper userObjectMapper;

    @PostMapping(value = "/register", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation(value = "Create an account", notes = "Required for generating API key")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully created a new account", response = AuthenticationResponse.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    ResponseEntity<AuthenticationResponse> registerUser(@ApiParam(value = "The user's data", name = "user", required = true)
                                                        @RequestPart(value = "user", required = false) String user,
                                                        // required = false - Not required to bypass the exception with a missing
                                                        // request part and return a validation failed message
                                                        @ApiParam(value = "The user's image", name = "image")
                                                        @RequestPart(value = "image", required = false) MultipartFile image)
            throws RoleNotFoundException, IOException {

        UserRegistrationDTO userRegistrationDTO = (UserRegistrationDTO) userObjectMapper
                .getUserDTOFromString(user, UserRegistrationDTO.class);

        UserEntity newUser = userService.registerUser(userRegistrationDTO, image);

        UserDetails userDetails = new User(newUser.getUsername(), newUser.getPassword(),
                customUserDetailsService.getAuthorities(newUser.getRoles()));

        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(token, validity));
    }
}
