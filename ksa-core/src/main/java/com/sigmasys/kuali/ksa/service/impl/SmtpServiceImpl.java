package com.sigmasys.kuali.ksa.service.impl;

import com.sigmasys.kuali.ksa.config.ConfigService;
import com.sigmasys.kuali.ksa.model.Constants;
import com.sigmasys.kuali.ksa.service.SmtpService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.util.Date;
import java.util.Properties;

/**
 * SMTP service implementation.
 *
 * @author Michael Ivanov
 */
@Service("smtpService")
public class SmtpServiceImpl implements SmtpService {


    private static final Log logger = LogFactory.getLog(SmtpServiceImpl.class);


    @Autowired
    private ConfigService configService;


    private String getParameter(String parameterName) {
        String parameterValue = configService.getParameter(parameterName);
        if (StringUtils.isBlank(parameterValue)) {
            String errMsg = parameterName + " parameter is required";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }
        return parameterValue;
    }


    /**
     * Sends email without an attachment.
     *
     * @param recipients Email recipients
     * @param subject    Email subject
     * @param message    Email message body
     */
    @Override
    public void sendEmail(String[] recipients, String subject, String message) {
        String fromAddress = configService.getParameter(Constants.MAIL_ADDRESS_FROM);
        sendEmail(fromAddress, recipients, subject, message, null, null, null);
    }


    /**
     * Sends email without an attachment.
     *
     * @param fromAddress Client email address
     * @param recipients  Email recipients
     * @param subject     Email subject
     * @param message     Email message body
     */
    @Override
    public void sendEmail(String fromAddress, String[] recipients, String subject, String message) {
        sendEmail(fromAddress, recipients, subject, message, null, null, null);
    }

    /**
     * Sends email with the attachment.
     *
     * @param fromAddress           Client email address
     * @param recipients            Email recipients
     * @param subject               Email subject
     * @param messageBody           Email message body
     * @param attachmentName        Attachment name
     * @param attachmentContentType Attachment content type
     * @param attachment            Attachment body
     */
    @Override
    public void sendEmail(String fromAddress,
                          String[] recipients,
                          String subject,
                          String messageBody,
                          String attachmentName,
                          String attachmentContentType,
                          byte[] attachment) {

        try {

            Properties properties = new Properties();

            final boolean requiresAuthentication = Boolean.parseBoolean(getParameter(Constants.MAIL_AUTH));
            final String host = getParameter(Constants.MAIL_HOST);
            final int port = Integer.valueOf(getParameter(Constants.MAIL_PORT));
            final boolean tlsEnabled = Boolean.parseBoolean(getParameter(Constants.MAIL_TLS_ENABLED));
            final String mailProtocol = getParameter(Constants.MAIL_PROTOCOL);
            final String user = getParameter(Constants.MAIL_USER);
            final String password = getParameter(Constants.MAIL_PASSWORD);

            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", port);
            properties.put("mail.smtp.auth", requiresAuthentication);
            properties.put("mail.smtp.starttls.enable", tlsEnabled);

            properties.put("mail.transport.protocol", mailProtocol);

            properties.put("mail.recipients", recipients);

            Authenticator authenticator = requiresAuthentication ? new Authenticator() {

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, password);
                }

            } : null;


            Session session = Session.getDefaultInstance(properties, authenticator);

            Transport transport = session.getTransport(mailProtocol);

            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(fromAddress));

            InternetAddress[] addresses = new InternetAddress[recipients.length];

            for (int i = 0; i < recipients.length; i++) {
                addresses[i] = new InternetAddress(recipients[i]);
            }

            message.setRecipients(Message.RecipientType.TO, addresses);
            message.setSentDate(new Date());
            message.setSubject(subject);

            if (attachment != null && attachment.length > 0) {

                // Set the email message text.
                MimeBodyPart messagePart = new MimeBodyPart();
                messagePart.setText(messageBody);

                // Set the email attachment file
                MimeBodyPart attachmentPart = new MimeBodyPart();
                ByteArrayDataSource bds = new ByteArrayDataSource(attachment, attachmentContentType);
                attachmentPart.setDataHandler(new DataHandler(bds));

                attachmentPart.setFileName(attachmentName);

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messagePart);
                multipart.addBodyPart(attachmentPart);

                message.setContent(multipart);

            } else {
                message.setText(messageBody);
            }

            // Synchronization is required by the statement "assert Thread.holdsLock(this);" in SMTPTransport class
            synchronized (transport) {
                transport.connect();
                transport.sendMessage(message, addresses);
                transport.close();
            }

        } catch (Exception e) {
            logger.error("Cannot send email notification: " + e.getMessage(), e);
            throw new RuntimeException("Cannot send email notification: " + e.getMessage(), e);
        }

    }


}
