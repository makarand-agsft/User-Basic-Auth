package com.formz.config;

import com.formz.multitenancy.TenantContext;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfiguration {

    @Value("${application.directory.base.path}")
    private String baseDirectory;

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new FileInputStream(baseDirectory.concat("/").concat(TenantContext.getCurrentTenant()).concat("/").
                        concat(TenantContext.getCurrentTenant() + "_FireBaseConfiguration.json")));
        FirebaseOptions firebaseOptions = FirebaseOptions
                .builder()
                .setCredentials(googleCredentials)
                .build();
        FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "push-notification-demo-91522");
        return FirebaseMessaging.getInstance(app);
    }

}
