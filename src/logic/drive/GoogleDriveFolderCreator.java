package logic.drive;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class GoogleDriveFolderCreator {

    private static final String APPLICATION_NAME = "Mi Proyecto Java";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public static String createFolder(String folderName, String parentFolderId) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, GoogleDriveUploader.getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        File fileMetadata = new File();
        fileMetadata.setName(folderName);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        if (parentFolderId != null && !parentFolderId.isEmpty()) {
            fileMetadata.setParents(Collections.singletonList(parentFolderId));
        }

        File folder = service.files().create(fileMetadata)
                .setFields("id")
                .execute();

        return folder.getId();
    }

    public static String getFolderIdByName(String folderName, String parentFolderId) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, GoogleDriveUploader.getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        String query = "mimeType = 'application/vnd.google-apps.folder' and name = '" + folderName.replace("'", "\\'") + "' and trashed = false";
        if (parentFolderId != null && !parentFolderId.isEmpty()) {
            query += " and '" + parentFolderId + "' in parents";
        }

        Drive.Files.List request = service.files().list().setQ(query).setFields("files(id, name)").setPageSize(1);
        java.util.List<File> files = request.execute().getFiles();
        if (files != null && !files.isEmpty()) {
            return files.get(0).getId();
        }
        return null;
    }

    public static String createOrGetFolder(String folderName, String parentFolderId) throws IOException, GeneralSecurityException {
        String folderId = getFolderIdByName(folderName, parentFolderId);
        if (folderId != null) {
            return folderId;
        }
        return createFolder(folderName, parentFolderId);
    }

}