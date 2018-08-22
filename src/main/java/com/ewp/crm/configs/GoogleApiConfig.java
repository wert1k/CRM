package com.ewp.crm.configs;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class GoogleApiConfig {

    private static Logger logger = LoggerFactory.getLogger(GoogleApiConfig.class);

    private Sheets sheets;

    public GoogleApiConfig(){
        String APPLICATION_NAME = "GoogleTable";
        try {
            List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);
            GoogleCredential cr = GoogleCredential
                    .fromStream(new FileInputStream("src/main/resources/googleApi.json"))
                    .createScoped(scopes);
            GoogleCredential credential= new GoogleCredential.Builder()
                    .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                    .setJsonFactory(JacksonFactory.getDefaultInstance())
                    .setServiceAccountScopes(scopes)
                    .setServiceAccountId(cr.getServiceAccountId())
                    .setServiceAccountPrivateKey(cr.getServiceAccountPrivateKey())
                    .setServiceAccountPrivateKeyId(cr.getServiceAccountPrivateKeyId())
                    .setTokenServerEncodedUrl(cr.getTokenServerEncodedUrl())
                    .setServiceAccountUser("servicesjavaacc@fourth-banner-213821.iam.gserviceaccount.com").build();
            this.sheets =  new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), credential).setApplicationName(APPLICATION_NAME).build();
        } catch (IOException e) {
            logger.error("googleApi.json hasn't parsed");
        } catch (GeneralSecurityException e) {
            logger.error("Sheets cant be create");
        }
    }

    public Sheets getSheets() {
        return sheets;
    }

}
