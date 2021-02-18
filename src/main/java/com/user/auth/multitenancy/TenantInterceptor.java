package com.user.auth.multitenancy;

import com.user.auth.model.Account;
import com.user.auth.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
@Order(1)
public class TenantInterceptor extends OncePerRequestFilter {


    @Autowired AccountRepository accountRepository;

    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getHeader("tenant") != null) {
            String tenant = request.getHeader("tenant");
           Account account= accountRepository.findByName(tenant);
           try {
               if (account == null) {
                   throw new Exception("Invalid tenant");
               }
           }catch (Exception e) {
               e.printStackTrace();
           }
            TenantContext.setCurrentTenant(tenant);
        }
        filterChain.doFilter(request, response);
    }
}
