package backendspring.com.backendspring.serviceImpl;

import backendspring.com.backendspring.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
  @Autowired
  private JavaMailSender mailSender;

  public void sendEmail(String email, String resetUrl) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true);

      helper.setTo(email);
      helper.setSubject("Recuperación de contraseña");
      //helper.setText("Haga clic en el siguiente enlace para restablecer su contraseña: " + resetUrl);
      String htmlContent = "<html>" +
          "<body>" +
          "<h1 style=\"color: #4285F4;\">Recuperación de contraseña</h1>" +
          "<p>Haga clic en el siguiente enlace para restablecer su contraseña:</p>" +
          "<a href=\"" + resetUrl + "\" style=\"display: inline-block; background-color: #4285F4; color: white; padding: 10px 20px; text-decoration: none; text-transform: uppercase; font-weight: bold;\">RECUPERAR CONTRASEÑA</a>" +
          "<br><br>" +
          "<p>Si no solicitó el cambio de contraseña haga caso omiso a este email.</p>" +
          "</body>" +
          "</html>";
      helper.setText(htmlContent,true);
      mailSender.send(message);
    } catch (MessagingException e) {
      // Manejar cualquier error al enviar el correo electrónico
      e.printStackTrace();
    }
  }
}
