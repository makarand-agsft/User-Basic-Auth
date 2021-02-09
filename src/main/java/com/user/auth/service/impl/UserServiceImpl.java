package com.user.auth.service.impl;

import com.user.auth.dto.ForgotPasswordDto;
import com.user.auth.dto.UserRegisterReqDto;
import com.user.auth.enums.TokenType;
import com.user.auth.model.Role;
import com.user.auth.model.Token;
import com.user.auth.model.User;
import com.user.auth.repository.RoleRepository;
import com.user.auth.repository.TokenRepository;
import com.user.auth.repository.UserRepository;
import com.user.auth.service.UserService;
import com.user.auth.utils.EmailUtils;
import com.user.auth.utils.UserAuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserAuthUtils userAuthUtils;

    @Autowired
    private EmailUtils emailUtils;

    @Value("${reset.token.validity}")
    private Long tokenExpiry = 3000l;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public boolean registerNewUser(UserRegisterReqDto dto) {
        Optional<User> dupUser =  userRepository.findByEmail(dto.getEmail());
        if(!dupUser.isPresent()){
            User user = new User();
            user.setEmail(dto.getEmail());
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setActive(Boolean.FALSE);
            user.setAddress(dto.getAddress());
            user.setMobileNumber(dto.getMobile());
            List<Role> roles = new ArrayList<>();
            for(Role r : dto.getRoles()){
                Optional<Role> role = roleRepository.findByRole(r.getRole());
                if(role.isPresent())
                    roles.add(role.get());
            }
            user.setRoles(roles);
            Token token = new Token();
            token.setToken(userAuthUtils.generateKey(10));
            token.setTokenType(TokenType.RESET_PASSWORD_TOKEN);
            token.setUsers(user);
            token.setExpiryDate(new Date(System.currentTimeMillis()+tokenExpiry*1000));
            user.setTokens(Collections.singletonList(token));
            userRepository.save(user);
            tokenRepository.save(token);
            //send email
            String message ="Hello "+user.getFirstName() +"This is your temporary password ,use this to change your password :"+token.getToken();
            emailUtils.sendInvitationEmail(user.getEmail(),"Invitation",message,fromEmail);
            return true;
        }else
            return false;
    }

    @Override
    public int forgotPassword(ForgotPasswordDto forgotDto) throws Exception {
        if(forgotDto!=null && forgotDto.getEmail()!=null){
            int responseErrorCode;
            if(userAuthUtils.validateEmail(forgotDto.getEmail())){
                Optional<User> userFromDb=userRepository.findByEmail(forgotDto.getEmail());
                if(!Objects.nonNull(userFromDb)) {
                    throw new Exception("User not found with this email id...!");
                }
                if(sendPasswordToUser(userFromDb.get())){
                    responseErrorCode=200;
                }else{
                    responseErrorCode=400;
                }
                return responseErrorCode;
            }else{
                throw new Exception("Invalid Email Id, Please provide valid email id...!");
            }
        }else{
            throw new Exception("Email not provided...!");
        }
    }

    private boolean sendPasswordToUser(User user) {
        if(user.getEmail()!=null && user!=null){
            String token=userAuthUtils.generateKey(10);
            EmailUtils emailSender= new EmailUtils();
            String subject="Forgot Password auto generated mail.";
            String text=" Hello "+user.getEmail()+" , \n your requested token is "+token +" \n \t Use this token to change or reset your password.";

            Token tokenToBeSave= new Token();
            tokenToBeSave.setToken(token);
            tokenToBeSave.setTokenType(TokenType.FORGOT_PASSWORD_TOKEN);
            tokenToBeSave.setUsers(user);
            tokenToBeSave.setExpiryDate(new Date(System.currentTimeMillis()+tokenExpiry*1000));
            tokenRepository.save(tokenToBeSave);
            emailSender.sendInvitationEmail(user.getEmail(),subject,text,fromEmail);
            return true;
        }
        return false;
    }

}
