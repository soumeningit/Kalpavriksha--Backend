package com.soumen.kalpavriksha.Auth;

import com.soumen.kalpavriksha.Utills.Response;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
public class EmailService
{
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TemplateEngine templateEngine;

    public Map<String , Object> sendUserVerifyMail(String email, String name, String token, String logoURL,
                                                   String url, String expiryMinutes, String supportEmail)
    {
        System.out.println("Inside sendUserVerifyMail method in service");

        /*
        System.out.println("email : " + email);
        System.out.println("name : " + name);
        System.out.println("token : " + token);
        System.out.println("logoURL : " + logoURL);
        System.out.println("url : " + url);
        System.out.println("expiryMinutes : " + expiryMinutes);
        System.out.println("supportEmail : " + supportEmail);

         */

        try{
            Context context = new Context();

            context.setVariable("app_name", "Kalpavriksha");
            context.setVariable("logo_url", logoURL);
            context.setVariable("name", name);
            context.setVariable("verification_link", url);
            context.setVariable("expiry_minutes", expiryMinutes);
            context.setVariable("support_email", supportEmail);
            context.setVariable("token", token);

            // render the HTML template
            String htmlContent = templateEngine.process("VerifyUser", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("Verify your email address");
            helper.setText(htmlContent, true);
            mailSender.send(message);

            return Response.success("Mail sent successfully");
        } catch(Exception e){
            System.out.println("Inside sendUserVerifyMail method exception");
            e.printStackTrace();
            return Response.error("error", e.getMessage());
        }

    }

    public Map<String, Object> sendResetPasswordEmail(String email, String name, String logoURL,
                                                      String url, String expiryMinutes, String supportEmail, String year)
    {
        System.out.println("INSIDE SEND HTML RESET EMAIL Service ....");
        try {


            // Prepare dynamic data
            Context context = new Context();
            context.setVariable("app_name", "Kalpavriksha");
            context.setVariable("logo_url", logoURL);
            context.setVariable("name", name);
            context.setVariable("verification_link", url);
            context.setVariable("expiry_minutes", expiryMinutes);
            context.setVariable("support_email", supportEmail);
            context.setVariable("year", year);


            // Render HTML content
            String htmlContent = templateEngine.process("ForgotPassword", context); // refers to otp-email.html

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Your Forget Password Code");
            helper.setText(htmlContent, true);

            mailSender.send(message);

            return Response.success("Email Send Successfully");

        } catch (Exception e) {
            System.out.println(e.getMessage());

            return Response.error("Email sending failed");
        }
    }
}
