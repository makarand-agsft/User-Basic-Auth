package com.user.auth.security;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import springfox.documentation.service.ApiKey;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Optional;

@Component
public class ApiKeyFilter extends GenericFilterBean {

    private static final Logger LOG = LoggerFactory.getLogger(ApiKeyFilter.class);



    @Value("${x-api-key}")
    private String apiKey;

    static String[] publicApis ={"/user-auth/api/user/forgotPassword","/user-auth/api/user/changePassword",
"/user-auth/api/user/login","/user-auth/api/user/resetPassword"};
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI();

        if(Arrays.asList(publicApis).contains(path)== false){
            chain.doFilter(request, response);
            return;
        }

        String key = req.getHeader("api-key") == null ? "" : req.getHeader("api-key");
        LOG.info("Trying key: " + key);

        try {
            if (apiKey.equalsIgnoreCase(key)) {
                chain.doFilter(request, response);
            } else {
                HttpServletResponse resp = (HttpServletResponse) response;
                String error = "Invalid API KEY";
                OutputStream outStream = resp.getOutputStream();
                outStream.write(new ObjectMapper().writeValueAsString(error).getBytes());

                outStream.flush();
            }
        }catch (AuthenticationException e){

        }
    }

}
