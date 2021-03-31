package com.nowakArtur97.myMoments.feature.user.shared;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String value) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByUsernameOrEmail(value, value)
                .orElseThrow(() -> new UsernameNotFoundException("User with name/email: '" + value + "' not found."));

        return new User(userEntity.getUsername(), userEntity.getPassword(), getAuthorities(userEntity.getRoles()));
    }

    public List<GrantedAuthority> getAuthorities(Set<RoleEntity> roles) {

        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }
}
