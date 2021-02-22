package com.user.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.auth.constants.ApiStatus;
import com.user.auth.dto.response.ResponseDto;
import com.user.auth.dto.response.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class represents exception handling for invalid jwt
 *
 * @author makarand
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e)
            throws IOException, ServletException {
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ResponseDto resp =
                new ResponseDto(new ResponseObject(HttpStatus.UNAUTHORIZED.value(),
                        "Unauthorzied Access to API", null), ApiStatus.SUCCESS);
        OutputStream outStream = httpServletResponse.getOutputStream();
        outStream.write(new ObjectMapper().writeValueAsString(resp).getBytes());
        outStream.flush();
    }
}
