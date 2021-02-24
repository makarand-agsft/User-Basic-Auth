package com.user.auth.service.impl;

import com.user.auth.constants.TokenType;
import com.user.auth.dto.request.AddressDto;
import com.user.auth.dto.request.TenantDto;
import com.user.auth.exception.InvalidRequestException;
import com.user.auth.model.Account;
import com.user.auth.model.TenantInfo;
import com.user.auth.multitenancy.MultiTenantDataSourceConfig;
import com.user.auth.repository.AccountRepository;
import com.user.auth.repository.TenantInfoRepository;
import com.user.auth.service.TenantService;
import com.user.auth.utils.EmailUtils;
import com.user.auth.utils.UserAuthUtils;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;

@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private MultiTenantDataSourceConfig multiTenantDataSourceConfig;

    @Autowired
    private UserAuthUtils userAuthUtils;

    @Autowired
    private TenantInfoRepository tenantInfoRepository;

    @Autowired
    private EmailUtils emailUtils;

    @Value("${reset.token.validity}")
    private Long resetTokenExpiry;

    @Value("${spring.datasource.username}")
    private String userName;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${reset.otp.size}")
    private int otpSize;

    @Value("${spring.mail.username}")
    private String fromEmail;


    @Value("${mail.activate-user-url}")
    private String activateUserApiUrl;

    @Value("${mail.profile.activation.subject}")
    private String activationEmailSubject;

    Logger log = LoggerFactory.getLogger(TenantServiceImpl.class);

    @Override
    public void addTenant(TenantDto tenantDto) throws SQLException, IOException {

        if (tenantDto.getTenantName() == null) {
            throw new InvalidRequestException(messageSource.getMessage("invalid.request", null, Locale.ENGLISH));
        }
        Account existingTenant = accountRepository.findByName(tenantDto.getTenantName());
        if (existingTenant != null) {
            throw new InvalidRequestException(messageSource.getMessage("tenant.already.exist", null, Locale.ENGLISH));
        }
        Account tenant = modelMapper.map(tenantDto, Account.class);
        Account savedTenant = accountRepository.save(tenant);
        Connection connection = multiTenantDataSourceConfig.getAnyConnection();
        connection.createStatement().execute("CREATE DATABASE " + tenant.getName());
        connection.createStatement().execute("USE " + tenant.getName());
        ScriptRunner scriptRunner = new ScriptRunner(connection);
        ClassPathResource c = new ClassPathResource("db/tenant1.sql");
        Reader reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
        scriptRunner.runScript(reader);
        addTenantAdmin(tenantDto);
        TenantInfo tenantInfo = new TenantInfo();
        tenantInfo.setAddedBy(userAuthUtils.getLoggedInUserName());
        tenantInfo.setTenantId(savedTenant.getId());
        tenantInfo.setActive(Boolean.TRUE);
        tenantInfoRepository.save(tenantInfo);
        connection.close();

    }

    private void addTenantAdmin(TenantDto tenantDto) throws SQLException {

        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + tenantDto.getTenantName(), userName, password);
            PreparedStatement adminRoleInsert = connection.prepareStatement("insert into role(role) values (?)");
            adminRoleInsert.setString(1, com.user.auth.constants.Role.ADMIN.name());
            PreparedStatement userRoleInsert = connection.prepareStatement("insert into role(role) values (?)");
            userRoleInsert.setString(1, com.user.auth.constants.Role.USER.name());
            PreparedStatement userInsert = connection.prepareStatement("insert into user(id,email, is_deleted) values (?,?,?)");
            userInsert.setInt(1, 1);
            userInsert.setString(2, tenantDto.getUserDto().getEmail());
            userInsert.setBoolean(3, false);

            PreparedStatement userProfileInsert = connection.prepareStatement
                    ("insert into user_profile(id,created_by,created_date, first_name, is_active, last_name, mobile, user_id) values (?,?,?,?,?,?,?,?)");
            userProfileInsert.setInt(1, 1);
            userProfileInsert.setString(2, "");
            userProfileInsert.setDate(3, null);
            userProfileInsert.setString(4, tenantDto.getUserDto().getUserProfile().getFirstName());
            userProfileInsert.setString(6, tenantDto.getUserDto().getUserProfile().getLastName());
            userProfileInsert.setBoolean(5, true);
            userProfileInsert.setLong(7, tenantDto.getUserDto().getUserProfile().getMobileNumber());
            userProfileInsert.setInt(8, 1);

            PreparedStatement userRolesInsert = connection.prepareStatement
                    ("insert into user_roles(user_id, role_id) values (?,?)");
            userRolesInsert.setInt(1, 1);
            userRolesInsert.setInt(2, 1);

            userInsert.executeUpdate();
            adminRoleInsert.executeUpdate();
            userRoleInsert.executeUpdate();
            userProfileInsert.executeUpdate();
            userRolesInsert.executeUpdate();
            for (AddressDto addressDto : tenantDto.getUserDto().getAddresses()) {
                PreparedStatement addressInsert = connection.prepareStatement
                        ("insert into address(address_string, address_type, city, country, pincode, state, user_id) values(?,?,?,?,?,?,?)");

                addressInsert.setString(1, addressDto.getAddressString());
                addressInsert.setString(2, addressDto.getAddressType().name());
                addressInsert.setString(3, addressDto.getCity());
                addressInsert.setString(4, addressDto.getCountry());
                addressInsert.setLong(5, addressDto.getPincode());
                addressInsert.setString(6, addressDto.getState());
                addressInsert.setLong(7, 1);
                addressInsert.executeUpdate();
            }
            //send activation email to tenant user
            String resetToken = userAuthUtils.generateKey(otpSize);
            PreparedStatement tokenInsert = connection.prepareStatement
                    ("insert into token(expiry_date, is_expired, token, token_type,user_id) values(?,?,?,?,?)");
            tokenInsert.setDate(1, new java.sql.Date(new Date(System.currentTimeMillis() + resetTokenExpiry * 1000).getTime()));
            tokenInsert.setBoolean(2, Boolean.TRUE);
            tokenInsert.setString(3, resetToken);
            tokenInsert.setString(4, TokenType.RESET_PASSWORD_TOKEN.name());
            tokenInsert.setLong(5, 1);
            tokenInsert.executeUpdate();
            String message = "Hello " + tenantDto.getUserDto().getUserProfile().getFirstName() + "Please activate your account by clicking this link." +
                    "This is your temporary password use this to reset your password, " + resetToken;
            String activationUrl = emailUtils.buildUrl(resetToken,activateUserApiUrl,tenantDto.getUserDto().getEmail());
            emailUtils.sendInvitationEmail(tenantDto.getUserDto().getEmail(), activationEmailSubject, message, fromEmail,activationUrl);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            connection.close();
        }
        log.info("Tenant admin saved successfully");

    }
}
