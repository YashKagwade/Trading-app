package com.trading.services;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendVerifiationOtpEmail(String mail, String otp)
            throws MessagingException {
    	//create message object
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        ///create MimeMessageHelper to set value to mail 
        MimeMessageHelper helper =
                new MimeMessageHelper(mimeMessage, true, "UTF-8");
        
        String subject = "Verify OTP";
        String text = "Your verification Code is: " + otp;

        helper.setTo(mail);
        helper.setSubject(subject);
        helper.setText(text, false);

        javaMailSender.send(mimeMessage);
    }
}
