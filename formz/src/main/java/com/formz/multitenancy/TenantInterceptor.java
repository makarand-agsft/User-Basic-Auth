package com.formz.multitenancy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formz.exception.InvalidTenantException;
import com.formz.model.Account;
import com.formz.model.GenericRequest;
import com.formz.repo.AccountRepository;
import com.formz.security.JwtProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.apache.logging.log4j.core.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import top.jfunc.json.impl.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

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
        XSSRequestWrapper xssRequestWrapper = new XSSRequestWrapper((HttpServletRequest) request) ;
        String body = IOUtils.toString(xssRequestWrapper.getReader());
        ObjectMapper mapper = new ObjectMapper();
        GenericRequest genericRequest = mapper.readValue(body, GenericRequest.class);
        String encryptedString = genericRequest.getPayloadString();
        String tenantId = genericRequest.getClientId();
        byte[] output;
        String encryptedText = null;
        try {
            String key="aakash";
            Key secretKey=new SecretKeySpec(key.getBytes(),"DES");
            Cipher cipher=Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE,secretKey);
            output=cipher.doFinal(body.getBytes());
            encryptedText = Base64.getEncoder().encodeToString(output);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String j ;
        if(!"".equals(body)) {
            String key = "aakash";
            byte[] keyData = key.getBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyData, "DES");
            try {
                Cipher cipher=Cipher.getInstance("DES");
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
                byte []decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
                j = new String(decrypted);
                xssRequestWrapper.resetInputStream(j.getBytes());
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        Account account = accountRepository.findByTenant(tenantId);
            try {
                if (account == null) {
                    throw new InvalidTenantException();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            TenantContext.setCurrentTenant(tenantId);
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
        filterChain.doFilter(xssRequestWrapper, response);
    }
}
