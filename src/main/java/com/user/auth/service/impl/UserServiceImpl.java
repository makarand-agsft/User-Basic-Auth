package com.user.auth.service.impl;

import com.user.auth.dto.UserListResponseDto;
import com.user.auth.dto.UserLoginReqDto;
import com.user.auth.dto.UserLoginResDto;
import com.user.auth.dto.ForgotPasswordDto;
import com.user.auth.dto.UserRegisterReqDto;
import com.user.auth.enums.TokenType;
import com.user.auth.exception.UserNotFoundException;
import com.user.auth.model.Role;
import com.user.auth.model.Token;
import com.user.auth.model.User;
import com.user.auth.repository.RoleRepository;
import com.user.auth.repository.TokenRepository;
import com.user.auth.repository.UserRepository;
import com.user.auth.security.JwtProvider;
import com.user.auth.service.UserService;
import com.user.auth.utils.EmailUtils;
import com.user.auth.utils.UserAuthUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
    private Long resetTokenExpiry;

    @Value("${jwt.tokenValidity}")
    private Long jwTokenExpiry;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public boolean registerNewUser(UserRegisterReqDto dto) {
        Optional<User> dupUser =  userRepository.findByEmail(dto.getEmail());
        if(!dupUser.isPresent()){
            User user = modelMapper.map(dto, User.class);
            user.setActive(Boolean.FALSE);
            user.setCreatedBy("");
            List<Role> roles = new ArrayList<>();
            for(String r : dto.getRole()){
                Optional<Role> role = roleRepository.findByRole(r);
                if(role.isPresent())
                    roles.add(role.get());
            }
            user.setRoles(roles);
            Token token = new Token();
            token.setToken(userAuthUtils.generateKey(10));
            token.setTokenType(TokenType.RESET_PASSWORD_TOKEN);
            token.setUsers(user);
            token.setExpiryDate(new Date(System.currentTimeMillis()+resetTokenExpiry*1000));
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
                    throw new UserNotFoundException();
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
        if(user.getEmail()!=null ){
            String token=userAuthUtils.generateKey(10);
            String subject="Forgot Password auto generated mail.";
            String text=" Hello "+user.getFirstName()+" , \n your requested token is "+token +" \n Use this token to change or reset your password.";

            Token tokenToBeSave= new Token();
            tokenToBeSave.setToken(token);
            tokenToBeSave.setTokenType(TokenType.FORGOT_PASSWORD_TOKEN);
            tokenToBeSave.setUsers(user);
            tokenToBeSave.setExpiryDate(new Date(System.currentTimeMillis()+jwTokenExpiry*1000));
            tokenRepository.save(tokenToBeSave);
            emailUtils.sendInvitationEmail(user.getEmail(),subject,text,fromEmail);
            return true;
        }
        return false;
    }

    @Override
    public UserLoginResDto loginUser(UserLoginReqDto dto) {
        Optional<User> optUser = userRepository.findByEmail(dto.getEmail());
        if (optUser.isPresent()) {
            User user = optUser.get();
            if (passwordEncoder.matches(dto.getPassword(), user.getPassword()) && user.getActive().equals(Boolean.TRUE)) {
                Token token = new Token();
                token.setToken(jwtProvider.generateToken(user));
                token.setTokenType(TokenType.LOGIN_TOKEN);
                token.setCreatedBy(user.getFirstName() + "." + user.getLastName());
                token.setUsers(user);
                token.setCreatedDate(new Date());
                token.setExpiryDate(new Date(System.currentTimeMillis() + jwTokenExpiry * 1000));
                tokenRepository.save(token);
                UserLoginResDto resDto = modelMapper.map(user, UserLoginResDto.class);
                resDto.setToken(token.getToken());
                return resDto;
            }
        }
        return null;
    }

    @Override public UserListResponseDto getAllAdminUsers() {
        Optional<Role> role = roleRepository.findByRole("ADMIN");
        UserListResponseDto userListResponseDto = new UserListResponseDto();
        if (role.isPresent()){
            List<User> users=userRepository.findByRoles(role.get());
            if(users!=null)
            userListResponseDto.setUserList(users.stream().map(x->modelMapper.map(x,UserRegisterReqDto.class)).collect(Collectors.toList()));
        }
      return  userListResponseDto;
    }

}
