package logic.drive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.client.http.FileContent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

public class GoogleDriveUploader {

    private static final String APPLICATION_NAME = "Mi Proyecto Java";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Arrays.asList(
            DriveScopes.DRIVE_FILE,
            "https://www.googleapis.com/auth/gmail.send"
    );
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String FOLDER_ID = "1rnY4TPY-MjCPMbGOCAtOlhR24bT1AHh5";

    public static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = GoogleDriveUploader.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("No se encontró el archivo de credenciales: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static String uploadFile(String filePath) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        File fileMetadata = new File();
        fileMetadata.setName(Paths.get(filePath).getFileName().toString());
        fileMetadata.setParents(Collections.singletonList(FOLDER_ID));

        java.io.File file = new java.io.File(filePath);
        FileContent mediaContent = new FileContent("application/octet-stream", file);

        File uploadedFile = service.files().create(fileMetadata, mediaContent)
                .setFields("id, webViewLink")
                .execute();

        return uploadedFile.getWebViewLink();
    }

    public static String uploadFile(String filePath, String folderId) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        File fileMetadata = new File();
        fileMetadata.setName(Paths.get(filePath).getFileName().toString());
        fileMetadata.setParents(Collections.singletonList(folderId));

        java.io.File file = new java.io.File(filePath);
        FileContent mediaContent = new FileContent("application/octet-stream", file);

        File uploadedFile = service.files().create(fileMetadata, mediaContent)
                .setFields("id, webViewLink")
                .execute();

        return uploadedFile.getWebViewLink();
    }
}