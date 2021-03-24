package com.formz.multitenancy;

import com.formz.exception.InvalidTenantException;
import com.formz.model.Account;
import com.formz.repo.AccountRepository;
import com.formz.security.JwtProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
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

@Component
public class TenantInterceptor extends OncePerRequestFilter {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        TenantContext.clear();
        if (request.getHeader("tenant") != null) {
            String tenant = request.getHeader("tenant");

            Account account = accountRepository.findByTenant(tenant);
            try {
                if (account == null) {
                    throw new InvalidTenantException();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            TenantContext.setCurrentTenant(tenant);
            try {
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null) {

                    String username = jwtProvider.getUsernameFromToken(authHeader);

                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                        if (jwtProvider.validateToken(authHeader, userDetails)) {
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            logger.info("authenticated user " + username + ", setting security context");
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }

                }
            } catch (IllegalArgumentException e) {
                logger.error("An error occurred during getting username from token", e);
            } catch (ExpiredJwtException e) {
                logger.warn("The token is expired and not valid anymore", e);
            } catch (SignatureException e) {
                logger.error("Authentication Failed. Username or Password not valid.");
            }
        }
        filterChain.doFilter(request, response);
    }
}
