package com.user.auth.security;

import com.user.auth.model.Account;
import com.user.auth.multitenancy.TenantContext;
import com.user.auth.repository.AccountRepository;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class represents jwt token filter process
 * @author makarand
 */
@Component
@Order(2)
public class JwtFilter extends OncePerRequestFilter {


    private JwtProvider jwtProvider;

    private UserDetailsService userDetailsService;

    private AccountRepository accountRepository;

    public JwtFilter(JwtProvider jwtProvider,UserDetailsService userDetailsService
//            AccountRepository accountRepository
    ) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
//        this.accountRepository =accountRepository;
    }



    Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String userAgent= request.getHeader("User-Agent");
        logger.info("User agent info is {}"+userAgent);
        String username = null;
        if (header != null) {
            try {

                username = jwtProvider.getUsernameFromToken(header);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                    if (jwtProvider.validateToken(header, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        logger.info("authenticated user " + username + ", setting security context");
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        catch (IllegalArgumentException e) {
            logger.error("an error occured during getting username from token", e);
        } catch (ExpiredJwtException e) {
            logger.warn("the token is expired and not valid anymore", e);
        } catch(SignatureException e){
            logger.error("Authentication Failed. Username or Password not valid.");
        }
        }

        filterChain.doFilter(request, response);
    }

}
