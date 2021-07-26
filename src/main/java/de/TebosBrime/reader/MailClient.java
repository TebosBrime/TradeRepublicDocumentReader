package de.TebosBrime.reader;

import de.TebosBrime.reader.configuration.Config;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

public class MailClient {

    public MailClient() throws SQLException {
        Config config = Main.getInstance().getConfig();
        List<String> knownSubjects = Main.getInstance().getDatabaseClient().getKnownSubjects();

        try {
            Properties props = new Properties();
            props.put("mail.imaps.ssl.protocols", "TLSv1.2");
            Session session = Session.getDefaultInstance(props, null);

            Store store = session.getStore("imaps");

            System.out.println("Connecting to IMAP server: " + config.getMailServer());
            store.connect(config.getMailServer(), config.getMailServerPort(), config.getMailUsername(),
                    config.getMailPassword());

            Folder inboxFolder = store.getFolder("INBOX");
            inboxFolder.open(Folder.READ_WRITE);

            Message[] messages = inboxFolder.getMessages();
            System.out.printf("Found %s messages in inbox folder.\n", messages.length);

            for (Message message : messages) {
                String subject = message.getSubject();
                String sender = ((InternetAddress) message.getFrom()[0]).getAddress();
                if (!config.getWhitelistedSender().contains(sender)) {
                    System.out.printf("Skip %s. Non whitelisted sender.\n", subject);
                    continue;
                }
                if (knownSubjects.contains(subject)) {
                    System.out.printf("Skip %s. Already processed. Check database.\n", subject);
                    continue;
                }
                System.out.printf("Read mail: %s\n", subject);

                if (message.getContentType().contains("multipart")) {
                    Multipart multipart = (Multipart) message.getContent();

                    for (int e = 0; e < multipart.getCount(); e++) {
                        MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(e);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            String fileName = part.getFileName();
                            String[] splitNames = fileName.split("/");

                            System.out.printf("Found attachment with name %s\n", fileName);

                            decryptAttachment(part.getInputStream(), splitNames[splitNames.length - 1],
                                    config.getTradeRepublicPassword(), config.getDecryptedPath());

                            Main.getInstance().getDatabaseClient().addAttachment(fileName, subject,
                                    new Timestamp(message.getReceivedDate().getTime()), part.getInputStream());
                        }
                    }
                }
            }

            inboxFolder.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void decryptAttachment(InputStream pdfStream, String filename, String traderepublicPassword,
                                          String safePath) throws IOException {
        PDDocument pddocument = PDDocument.load(pdfStream, traderepublicPassword);
        pddocument.setAllSecurityToBeRemoved(true);

        File saveFolder = new File(safePath);
        saveFolder.mkdirs();

        File savedFile = new File(saveFolder, filename);
        System.out.printf("Save decrypted file to %s\n", savedFile.getAbsolutePath());
        if (savedFile.createNewFile()) {
            pddocument.save(savedFile);
        }

        pddocument.close();
    }
}
