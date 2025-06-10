package logic.gmail;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class GmailService {

    public static void sendEmail(String to, String subject, String bodyText) throws IOException, MessagingException {
        final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
        Gmail service = new Gmail.Builder(
                HTTP_TRANSPORT,
                GsonFactory.getDefaultInstance(),
                logic.drive.GoogleDriveUploader.getCredentials(HTTP_TRANSPORT)
        )
                .setApplicationName("Mi Proyecto Java")
                .build();

        MimeMessage email = createEmail(to, "tu@gmail.com", subject, bodyText);
        Message message = createMessageWithEmail(email);

        service.users().messages().send("me", message).execute();
        System.out.println("✉️ Correo enviado a: " + to);
    }

    private static MimeMessage createEmail(String to, String from, String subject, String bodyText) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);

        return email;
    }

    public static void sendEmailWithAttachment(String to, String subject, String bodyText, File attachment) throws IOException, MessagingException {
        final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
        Gmail service = new Gmail.Builder(
                HTTP_TRANSPORT,
                GsonFactory.getDefaultInstance(),
                logic.drive.GoogleDriveUploader.getCredentials(HTTP_TRANSPORT)
        )
                .setApplicationName("Mi Proyecto Java")
                .build();

        MimeMessage email = createEmailWithAttachment(to, "tu@gmail.com", subject, bodyText, attachment);
        Message message = createMessageWithEmail(email);

        service.users().messages().send("me", message).execute();
        System.out.println("✉️ Correo enviado a: " + to);
    }

    private static MimeMessage createEmailWithAttachment(String to, String from, String subject, String bodyText, File attachment) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));email.setSubject(subject);
        //Se realiza aqui el import completo de la clase ya que causa conflicto con com.google.api.services.gmail.model.Message

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(bodyText, "utf-8");

        MimeBodyPart attachmentPart = new MimeBodyPart();
        try {
            attachmentPart.attachFile(attachment);
        } catch (IOException e) {
            throw new MessagingException("No se pudo adjuntar el archivo", e);
        }

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(attachmentPart);

        email.setContent(multipart);

        return email;
    }

    private static Message createMessageWithEmail(MimeMessage email) throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = com.google.api.client.util.Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }
}