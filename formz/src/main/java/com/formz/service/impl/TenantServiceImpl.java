package com.formz.service.impl;

import com.formz.constants.Role;
import com.formz.constants.TokenType;
import com.formz.dto.TenantDto;
import com.formz.exception.BadRequestException;
import com.formz.model.Account;
import com.formz.model.Form;
import com.formz.model.User;
import com.formz.multitenancy.MultiTenantDataSourceConfig;
import com.formz.multitenancy.TenantContext;
import com.formz.repo.AccountRepository;
import com.formz.repo.FormRepository;
import com.formz.security.JwtProvider;
import com.formz.service.TenantService;
import com.formz.utils.EmailUtils;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MultiTenantDataSourceConfig multiTenantDataSourceConfig;

    @Value("${spring.datasource.username}")
    private String userName;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${reset.token.validity}")
    private Long resetTokenExpiry;

    @Value("${mail.activate-user-url}")
    private String activateUserApiUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private EmailUtils emailUtils;

    @Autowired
    private FormRepository formRepository;

    @Value("${application.directory.base.path}")
    private String applicationDirectoryBasePath;

    private static final Logger log = LogManager.getLogger(TenantServiceImpl.class);
    public static final String USER_ROLE_INSERT = "insert into user_role(user_id, role_id) values (?,?)";
    public static final String USER_INSERT = "insert into user(id,email, is_delete,name) values (?,?,?,?)";
    public static final String USER_TOKEN_INSERT = "insert into token(expiry_date, is_expired, token, token_type,user_id) values(?,?,?,?,?)";

    @Override
    public void addTenant(TenantDto tenantDto) throws SQLException, IOException {
        if (tenantDto == null || tenantDto.getTenant() == null) {
            throw new BadRequestException("Invalid request");
        }
        if(tenantDto.getTenant().contains(" ") || tenantDto.getTenant().contains("-")){
            throw new BadRequestException("Tenant name should not contain space or hyphen");
        }
        Account account = modelMapper.map(tenantDto, Account.class);
        Account existingTenant = accountRepository.findByTenant(tenantDto.getTenant());
        if (existingTenant != null) {
            throw new BadRequestException("Tenant already exist");
        }
        account.setEmail(tenantDto.getUserDto().getEmail());
        accountRepository.save(account);
        Connection connection = multiTenantDataSourceConfig.getAnyConnection();
        connection.createStatement().execute("CREATE DATABASE " + tenantDto.getTenant());
        connection.createStatement().execute("USE " + tenantDto.getTenant());
        ScriptRunner scriptRunner = new ScriptRunner(connection);
        ClassPathResource c = new ClassPathResource("database/tenant/V1__main_script.sql");
        Reader reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
        scriptRunner.runScript(reader);
        addTenantAdmin(tenantDto);
        createFormDirectory(tenantDto.getTenant());
        connection.close();

    }

    @Async
    private void addTenantAdmin(TenantDto tenantDto) throws SQLException {

        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + tenantDto.getTenant(), userName, password);
            PreparedStatement userInsert = connection.prepareStatement(USER_INSERT);
            userInsert.setInt(1, 1);
            userInsert.setString(2, tenantDto.getUserDto().getEmail());
            userInsert.setBoolean(3, false);
            userInsert.setString(4,tenantDto.getUserDto().getName());

            PreparedStatement userRolesInsert = connection.prepareStatement
                    (USER_ROLE_INSERT);
            userRolesInsert.setLong(1, Role.ADMIN.getId());
            userRolesInsert.setLong(2, Role.ADMIN.getId());

            userInsert.executeUpdate();
            userRolesInsert.executeUpdate();
//                send activation email to tenant user
            String resetToken = jwtProvider.generateToken(modelMapper.map(tenantDto.getUserDto(), User.class));
            PreparedStatement tokenInsert = connection.prepareStatement
                    (USER_TOKEN_INSERT);
            tokenInsert.setDate(1, new java.sql.Date(new Date(System.currentTimeMillis() + resetTokenExpiry * 1000).getTime()));
            tokenInsert.setBoolean(2, Boolean.TRUE);
            tokenInsert.setString(3, resetToken);
            tokenInsert.setString(4, TokenType.RESET_PASSWORD_TOKEN.name());
            tokenInsert.setLong(5, 1);
            tokenInsert.executeUpdate();
            String message = "Hello " + tenantDto.getUserDto().getName() + "Please activate your account by clicking this link";
            String activationUrl = emailUtils.buildUrl(resetToken, activateUserApiUrl);
            emailUtils.sendInvitationEmail(tenantDto.getUserDto().getEmail(), "Invitation", message, fromEmail, activationUrl);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            connection.close();
        }
        log.info("Tenant admin saved successfully");

    }

    private void createFormDirectory(String tenantName) throws IOException {

        File file = new File("src/main/resources/templates/" + tenantName + "/forms/");
        if (!file.exists()) {
            Files.createDirectories(Paths.get(file.toURI()));
        }
        File requestJsonDirectory = new File(applicationDirectoryBasePath + "/" + TenantContext.getCurrentTenant() + "/request-json-strings/");
        if (!requestJsonDirectory.exists())
            requestJsonDirectory.mkdir();
        File downloadPdfDirectory = new File(applicationDirectoryBasePath + "/" + TenantContext.getCurrentTenant() + "/pdfs/");
        if (!downloadPdfDirectory.exists())
            downloadPdfDirectory.mkdir();
    }
}
