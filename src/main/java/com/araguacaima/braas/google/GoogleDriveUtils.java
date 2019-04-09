package com.araguacaima.braas.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleDriveUtils {
    private static final String APPLICATION_NAME = "BRaaS";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @param credentials    Stream that contains a json object with user credentials info
     * @return An authorized Credential object.
     * @throws IOException If the credentials stream is invalid.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, InputStream credentials) throws IOException {
        // Load client secrets.
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(credentials));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * @param fileId      Identifier of the file to retrieve
     * @param credentials Stream that contains a json object with user credentials info
     * @return A stream with the requested file
     * @throws GeneralSecurityException If there is any security issue trying tio use the provided credentials
     * @throws IOException              If the credentials stream is invalid.
     */
    public static ByteArrayOutputStream getSpreadsheet(String fileId, InputStream credentials) throws GeneralSecurityException, IOException {
        return getFile(fileId, credentials);
    }

    /**
     * @param fileId      Identifier of the file to retrieve
     * @param credentials Stream that contains a json object with user credentials info
     * @return A stream with the requested file
     * @throws GeneralSecurityException If there is any security issue trying tio use the provided credentials
     * @throws IOException              If the credentials stream is invalid.
     */
    public static ByteArrayOutputStream getFile(String fileId, InputStream credentials) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
        service.files().get(fileId).executeMediaAndDownloadTo(outputStream);
        return outputStream;
    }
}