package com.user.auth.utils;

import com.user.auth.model.Role;
import com.user.auth.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
public class UserAuthUtils {

    @Value("${jwt.header}")
    private String jwtHeader;
    @Autowired
    private JwtProvider jwtProvider;

    public String generateKey(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }
        @Deprecated
        public boolean checkAccess(HttpServletRequest httpServletRequest) {
            if (null == httpServletRequest) {
                throw new RuntimeException("Request is null");
            }
            String token = httpServletRequest.getHeader(jwtHeader);
            if (null == token && StringUtils.isEmpty(token)) {
                throw new RuntimeException("Authorization failed");
            }
            List<Role> roleList = jwtProvider.getRolesfromToken(token);

            for (Role role : roleList) {
                if (role.getRole().equals("ADMIN")) {
                    return true;
                }
            }
            throw new RuntimeException("Access Denied");
        }

}
