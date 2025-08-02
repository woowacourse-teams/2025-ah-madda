package com.ahmadda.infra.push.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;

@Component
public class FirebaseConfig {

    @PostConstruct
    public void initFirebase() {
        try (FileInputStream serviceAccount =
                     new FileInputStream("src/main/resources/security/firebase/service_account_key.json")) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps()
                    .isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Firebase를 초기화할 수 없습니다.", e);
        }
    }
}
