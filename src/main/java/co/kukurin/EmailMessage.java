package co.kukurin;

import co.kukurin.validation.Email;

public class EmailMessage {

    @Email
    String from;

    @Email
    String to;

    String content;

    EmailAttachment attachment;

}
