package com.user.auth.security;

import com.user.auth.model.Role;
import com.user.auth.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUserFactory {

    public static JwtUser create(User user) {
        return new JwtUser(user.getUserId(), user.getEmail(), "user.getUserProfile().getFirstName()",
                mapToGrantedAuthorities(new ArrayList<>(user.getRoles())));

    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(ArrayList<Role> authorities) {
        return authorities.stream().map(authority -> new SimpleGrantedAuthority(authority.getRole())).collect(Collectors.toList());
    }
}
