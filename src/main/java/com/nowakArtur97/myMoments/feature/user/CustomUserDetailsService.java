package com.nowakArtur97.myMoments.feature.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String value) throws UsernameNotFoundException {

        UserEntity userEntity = userService.findByUsernameOrEmail(value, value)
                .orElseThrow(() -> new UsernameNotFoundException("User with name/email: '" + value + "' not found."));

        return new User(userEntity.getUsername(), userEntity.getPassword(), getAuthorities(userEntity.getRoles()));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Set<RoleEntity> roles) {

        List<GrantedAuthority> userAuthorities = new ArrayList<>();

        for (RoleEntity role : roles) {
            userAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        return userAuthorities;
    }
}
