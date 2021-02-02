package com.nowakArtur97.myMoments.feature.user;

import com.nowakArtur97.myMoments.common.baseModel.ErrorResponse;
import com.nowakArtur97.myMoments.common.util.JwtUtil;
import com.nowakArtur97.myMoments.configuration.JwtConfigurationProperties;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/authentication")
@RequiredArgsConstructor
@EnableConfigurationProperties(value = JwtConfigurationProperties.class)
@Api(tags = {AuthenticationTag.RESOURCE})
class AuthenticationController {

    private final CustomUserDetailsService customUserDetailsService;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    private final JwtConfigurationProperties jwtConfigurationProperties;

    @PostMapping
    @ApiOperation(value = "Generate API key", notes = "Generate API key")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully generated API key", response = AuthenticationResponse.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<AuthenticationResponse> loginUser(@RequestBody @ApiParam(value = "User credentials", name = "user",
            required = true) AuthenticationRequest authenticationRequest) {

        String userNameOrEmail = authenticationRequest.getUserName() != null
                ? authenticationRequest.getUserName()
                : authenticationRequest.getEmail();

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userNameOrEmail);

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userNameOrEmail, authenticationRequest.getPassword()));

        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(token, jwtConfigurationProperties.getJwtTokenValidity()));
    }
}
