package com.nowakArtur97.myMoments.feature.user;

import com.nowakArtur97.myMoments.common.util.JwtUtil;
import com.nowakArtur97.myMoments.configuration.JwtConfigurationProperties;
import lombok.RequiredArgsConstructor;
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
class UserRegistrationController {

    private final UserService userService;

    private final CustomUserDetailsService customUserDetailsService;

    private final JwtUtil jwtUtil;

    private final JwtConfigurationProperties jwtConfigurationProperties;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody @Valid UserDTO userDTO) throws RoleNotFoundException {

        UserEntity newUser = userService.register(userDTO);

        UserDetails userDetails = new User(newUser.getUsername(), newUser.getPassword(),
                customUserDetailsService.getAuthorities(newUser.getRoles()));

        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(token, jwtConfigurationProperties.getJwtTokenValidity()));
    }
}
