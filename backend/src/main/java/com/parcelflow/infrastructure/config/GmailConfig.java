package com.parcelflow.infrastructure.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Configuration
@ConfigurationProperties(prefix = "gmail")
public class GmailConfig {

    private String clientId;
    private String clientSecret;
    private String refreshToken;

    public void setClientId(String clientId) { this.clientId = clientId; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    @Bean
    public JsonFactory jsonFactory() {
        return GsonFactory.getDefaultInstance();
    }

    @Bean
    public Gmail gmailService(JsonFactory jsonFactory) throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        
        Credential credential;
        
        if (refreshToken != null && !refreshToken.isBlank() && clientId != null && !clientId.isBlank()) {
             // Use explicit OAuth configuration
             credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setClientSecrets(clientId, clientSecret)
                .build()
                .setRefreshToken(refreshToken);
        } else {
            // Fallback to Application Default Credentials (ADC)
            GoogleCredential adc = GoogleCredential.getApplicationDefault();
            if (adc.createScopedRequired()) {
                credential = adc.createScoped(java.util.Collections.singletonList("https://www.googleapis.com/auth/gmail.readonly"));
            } else {
                credential = adc;
            }
        }

        return new Gmail.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("Parcel-Flow")
                .build();
    }

    private void validateConfig() {
        // Validation relaxed to allow ADC fallback
    }
}
