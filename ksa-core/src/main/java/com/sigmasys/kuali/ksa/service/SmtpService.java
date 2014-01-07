package com.sigmasys.kuali.ksa.service;

/**
 * A simple SMTP client service
 *
 * @author Michael Ivanov
 */
public interface SmtpService {

    /**
     * Sends email without an attachment.
     *
     * @param recipients Email recipients
     * @param subject    Email subject
     * @param message    Email message body
     */
    void sendEmail(String[] recipients, String subject, String message);


    /**
     * Sends email without an attachment.
     *
     * @param fromAddress Client email address
     * @param recipients  Email recipients
     * @param subject     Email subject
     * @param message     Email message body
     */
    void sendEmail(String fromAddress, String[] recipients, String subject, String message);

    /**
     * Sends email with the attachment.
     *
     * @param fromAddress           Client email address
     * @param recipients            Email recipients
     * @param subject               Email subject
     * @param message               Email message body
     * @param attachmentName        Attachment name
     * @param attachmentContentType Attachment content type
     * @param attachment            Attachment body
     */
    void sendEmail(String fromAddress,
                   String[] recipients,
                   String subject,
                   String message,
                   String attachmentName,
                   String attachmentContentType,
                   byte[] attachment);

}
