package com.user.auth.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class EmailUtils {

    @Autowired private JavaMailSender emailSender;

    @Value("${mail.application.url}")
    private String activationUrl;

    public void sendInvitationEmail(String to, String subject, String text,String from,String activationUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text+"\r\n"+ activationUrl);
        emailSender.send(message);
    }

    public String buildUrl(String token,String apiURL,String email) throws UnsupportedEncodingException {
            String activationLink = new String();
            activationLink=activationLink.concat(activationUrl).concat(apiURL)
                  .concat("?token=").concat(token).concat("&email=")
            .concat(email);
        return activationLink;

    }

}
