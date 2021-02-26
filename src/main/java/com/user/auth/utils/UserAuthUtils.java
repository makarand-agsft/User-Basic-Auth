package com.user.auth.utils;


import com.user.auth.model.Role;
import com.user.auth.model.Token;
import com.user.auth.model.User;
import com.user.auth.repository.TokenRepository;
import com.user.auth.repository.UserRepository;
import com.user.auth.security.JwtProvider;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.velocity.app.VelocityEngine;
@Component
public class UserAuthUtils {


    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserRepository userRepository;

    @Value("${upload.directory}")
    private String UPLOAD_DIRECTORY ;

    @Autowired
    private TokenRepository tokenRepository;

    @Value("${jwt.header}")
    private String jwtHeader;

    @Autowired
    private  VelocityEngine velocityEngine;

    public String generateKey(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }

    /**
     * Get Username of Current login user
     * @Author Akshay
     * @return
     */
    public String getLoggedInUserName(){
            Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (userDetails instanceof UserDetails) {
                return ((UserDetails) userDetails).getUsername();
            }
            return null;
    }

    public User getLoggedInUser() {
        String userName = getLoggedInUserName();
        Optional<User> user = userRepository.findByEmail(userName);
        if (user.isPresent())
            return user.get();
        return null;

    }
    public Optional<User> getUserFromToken(String token){
        Optional<Token> optToken = tokenRepository.findByToken(token);
        if(optToken.isPresent() && !optToken.get().getExpiryDate().before(new Date()))
            return Optional.of(optToken.get().getUsers());
        return Optional.empty();
    }
    public String saveProfileImage(MultipartFile file, User user){
        String profile_path = null;
        if(file !=null){
            try {
                String fileName = getFileName(file, user);
                profile_path = UPLOAD_DIRECTORY + fileName;
                File f = new File(UPLOAD_DIRECTORY);
                if(!f.exists())
                    Files.createDirectories(f.toPath());
                Path path = Paths.get(profile_path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(user.getUserProfile().getProfilePicture()!=null)
            return user.getUserProfile().getProfilePicture();
        return profile_path;
    }

    public String getFileName(MultipartFile file, User user) {
        return user.getEmail().replace(".com", file.getContentType().replace("/", "."));
    }

    /**
     * checks if email is valid or not
     * @param email
     * @Author dipak
     * @return true if email is valid
     */
    public boolean validateEmail(String email) {

        Matcher matcher = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE).matcher(email);
        return email==null?false:matcher.find();
    }

    public boolean validateMobileNumber(Long mobile){
        if(mobile.toString().length()!=10)
            return false;
        return true;
    }
    /**
     * checks if user is Admin
     * @param httpServletRequest
     * @Author Akshay
     * @return boolean
     */
    public boolean checkAccess(HttpServletRequest httpServletRequest) {
        if (null == httpServletRequest) {
            throw new RuntimeException("Request is null");
        }
        String token = httpServletRequest.getHeader(jwtHeader);
        if (null == token && token.isEmpty()) {
            throw new RuntimeException("Authorization failed");
        }
        List<Role> roleList =jwtProvider.getRolesfromToken(token);
        return roleList.stream().anyMatch(role ->role.getRole().equalsIgnoreCase(com.user.auth.constants.Role.ADMIN.toString()));


    }

    public String getTemplatetoText(String templateName, Map<String, Object> props) {
        StringWriter stringWriter = new StringWriter();
        velocityEngine.mergeTemplate(templateName, "UTF-8", new VelocityContext(props), stringWriter);
        return stringWriter.toString();
    }
}
