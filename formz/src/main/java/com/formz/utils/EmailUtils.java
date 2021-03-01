package com.formz.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class EmailUtils {

    @Autowired
    private JavaMailSender emailSender;

    @Value("${mail.application.url}")
    private String activationUrl;

    Logger log = LoggerFactory.getLogger(EmailUtils.class);

    public void sendInvitationEmail(String to, String subject, String text,String from,String activationUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text+"\r\n"+ activationUrl);
        emailSender.send(message);
        log.info("Email sent successfully :"+to);
    }

    public String buildUrl(String token,String apiURL) throws UnsupportedEncodingException {
            String activationLink = new String();
            activationLink=activationLink.concat(activationUrl).concat(apiURL)
                  .concat("?token=").concat(token);
        return activationLink;

    }

}
