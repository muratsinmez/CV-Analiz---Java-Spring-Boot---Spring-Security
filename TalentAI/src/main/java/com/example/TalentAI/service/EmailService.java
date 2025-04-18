package com.example.TalentAI.service;

import com.example.TalentAI.model.Company;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
/*
    public void sendActivationEmail(Company company) {
        String activationCode = company.getActivationCode();
        String to = company.getEmail();
        String subject = "TalenAI - Aktivasyon Kodunuz";

        String content = """
                Merhaba %s Şirketi,

                TalentAI sistemine gösterdiğiniz ilgiden dolayı teşekkür ederiz. 🎉
                Yapmış olduğunuz satın alma işlemi başarıyla gerçekleşmiştir.

                Sisteme erişim sağlayabilmeniz için aktivasyon kodunuz aşağıda yer almaktadır:

                🔐 Aktivasyon Kodu: %s

                Bu kodu sisteme girerek şirket hesabınızı aktif hale getirebilir ve CV analiz hizmetlerimizden faydalanmaya başlayabilirsiniz.

                📩 Sorularınız için bize her zaman ulaşabilirsiniz.  
                İyi çalışmalar dileriz.  
                TalentAI Ekibi 💼
                """.formatted(company.getCompanyName(), activationCode);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, false); // true ise HTML olur, şu an düz metin

            mailSender.send(message);
            System.out.println("✅ Aktivasyon maili gönderildi: " + to);
        } catch (MessagingException e) {
            System.err.println("❌ Mail gönderimi başarısız: " + e.getMessage());
        }
    }*/
    
    public void sendActivationEmail(String to, String activationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("TalentAI Aktivasyon Kodu");
        message.setText("Merhaba,\n\nÖdemeniz onaylandı. Aktivasyon kodunuz: " + activationCode + "\n\nTeşekkürler,\nTalentAI Ekibi");
        mailSender.send(message);
    }
}
