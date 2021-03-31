package com.nowakArtur97.myMoments.feature.user.registration;

import com.nowakArtur97.myMoments.common.baseModel.ErrorResponse;
import com.nowakArtur97.myMoments.common.util.JwtUtil;
import com.nowakArtur97.myMoments.configuration.security.JwtConfigurationProperties;
import com.nowakArtur97.myMoments.feature.user.shared.AuthenticationResponse;
import com.nowakArtur97.myMoments.feature.user.shared.CustomUserDetailsService;
import com.nowakArtur97.myMoments.feature.user.shared.UserEntity;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.relation.RoleNotFoundException;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/registration")
@RequiredArgsConstructor
@EnableConfigurationProperties(value = JwtConfigurationProperties.class)
@Api(tags = {UserRegistrationTag.RESOURCE})
class UserRegistrationController {

    @Value("${my-moments.jwt.validity:36000000}")
    private long validity;

    private final UserService userService;

    private final CustomUserDetailsService customUserDetailsService;

    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    @ApiOperation(value = "Create an account", notes = "Create an account. Required for generating API key.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully created a new account", response = String.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    ResponseEntity<AuthenticationResponse> registerUser(@ApiParam(value = "User data", name = "user", required = true)
                                                        @RequestBody @Valid UserDTO userDTO) throws RoleNotFoundException {

        UserEntity newUser = userService.register(userDTO);

        UserDetails userDetails = new User(newUser.getUsername(), newUser.getPassword(),
                customUserDetailsService.getAuthorities(newUser.getRoles()));

        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(token, validity));
    }
}
