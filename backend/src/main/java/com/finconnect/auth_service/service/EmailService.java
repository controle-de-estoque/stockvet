package com.finconnect.auth_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.finconnect.auth_service.dto.SendEmailRequest;
import com.finconnect.auth_service.entity.Email;
import com.finconnect.auth_service.repository.EmailRepository;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private EmailRepository emailRepository;

    @Value("${spring.mail.username}")
    private String origin;

    @Async
    public void sendHtmlEmail(SendEmailRequest request) {
        // Limpeza agressiva e segura
        String rawEmail = request.destination() != null ? request.destination().trim() : "";
        
        try {
            // Usa InternetAddress para extrair um e-mail válido limpo de qualquer sujeira
            InternetAddress[] addresses = InternetAddress.parse(rawEmail, true);
            String destinatario = addresses[0].getAddress();

            logger.info("Enviando e-mail HTML assíncrono para: {}", destinatario);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(origin);
            helper.setTo(destinatario);
            helper.setSubject(request.subject());

            String htmlContent = buildHtmlTemplate(request.subject(), request.message());
            helper.setText(htmlContent, true);

            Email email = new Email();
            email.setOrigin(origin);
            email.setDestination(destinatario);
            email.setSubject(request.subject());
            email.setContent(request.message());

            this.emailRepository.save(email);
            javaMailSender.send(mimeMessage);
            
            logger.info("E-mail enviado com sucesso para: {}", destinatario);
        } catch (Exception e) {
            logger.error("Erro fatal ao processar envio de e-mail para [{}]. Erro: {}", rawEmail, e.getMessage());
        }
    }

    private String buildHtmlTemplate(String subject, String message) {
        String formattedMessage = message.replace("\n", "<br>");
        
        // Usamos substituição direta para evitar conflitos com o motor de formatação de Strings do Java
        String template = """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <body style="margin:0; padding:0; font-family:sans-serif; background-color:#f3f4f6;">
                <div style="width:100%; padding:40px 20px; box-sizing:border-box;">
                    <div style="max-width:600px; margin:0 auto; background-color:#ffffff; border-radius:8px; border:1px solid #e5e7eb;">
                        <div style="background-color:#4e43e7; padding:24px; text-align:center;">
                            <h1 style="margin:0; color:#ffffff; font-size:24px;">StockVet</h1>
                        </div>
                        <div style="padding:32px 24px;">
                            <h2 style="margin-top:0; color:#111827; font-size:20px;">SUBJECT_PLACEHOLDER</h2>
                            <div style="color:#374151; font-size:15px; line-height:1.6;">MESSAGE_PLACEHOLDER</div>
                        </div>
                        <div style="background-color:#f9fafb; padding:20px 24px; text-align:center; border-top:1px solid #e5e7eb;">
                            <p style="margin:0; color:#6b7280; font-size:13px;">Mensagem automática - não responda.</p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """;
            
        return template.replace("SUBJECT_PLACEHOLDER", subject)
                       .replace("MESSAGE_PLACEHOLDER", formattedMessage);
    }
}