package com.example.TalentAI.service;

import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

@Service
public class OpenAiService {

    private static final String OPENROUTER_API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String OPENROUTER_API_KEY = "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";

    public String analyzeCvText(String rawText) {
        RestTemplate restTemplate = new RestTemplate();

        String prompt = """
                Bir CV'nin aşağıdaki başlıklar altında aday hakkında HR departmanı için özet bilgi sağla:
                
                Adayın Genel Profili ve Uygun Pozisyon Önerisi: Adayın geçmiş deneyimleri, becerileri ve eğitim geçmişine göre en uygun pozisyon nedir? Adayın belirgin güçlü yönleri nelerdir ve hangi pozisyonlarda başarılı olabilir?
                
                Anahtar Beceriler: Adayın sahip olduğu en güçlü beceriler nelerdir? Bu beceriler, özellikle hangi iş pozisyonları için uygundur? (Teknik beceriler, liderlik, takım çalışması, problem çözme gibi)
                
                İş Deneyimi: Adayın geçmiş iş deneyimlerine göre hangi alanlarda deneyimi var? Hangi sektörlerde veya iş pozisyonlarında daha fazla deneyim sahibi ve bu deneyimlerin hangi pozisyonlara değer kattığını belirt.
                
                Eğitim Durumu ve Sertifikalar: Adayın eğitim geçmişi ve sahip olduğu sertifikalar ne düzeyde? Bu eğitim ve sertifikalar hangi pozisyonlar için uygundur?
                
                Potansiyel Gelişim Alanları: Adayın gelişim gösterebileceği alanlar nelerdir? Önerilen pozisyon için eksik kalan beceriler veya deneyimler var mı?
                
                Kariyer Hedefleri ve İlerleme: Adayın kariyer hedefleri hakkında herhangi bir bilgi var mı?
                Adayın kariyerinde hangi tür pozisyonlarda ilerlemek istediği ve bu hedeflere nasıl ulaşabileceği üzerine bir değerlendirme yap.
                """ + rawText;

        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", prompt);

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "openai/gpt-3.5-turbo");
        requestBody.put("messages", new org.json.JSONArray().put(message));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(OPENROUTER_API_KEY);
        headers.set("HTTP-Referer", "https://talentai.ai");
        headers.set("X-Title", "TalentAI CV Analyzer");

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(OPENROUTER_API_URL, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject responseBody = new JSONObject(response.getBody());
            String aiResponse = responseBody.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
            return aiResponse;
        }

        return "⚠️ AI analizi başarısız oldu.";
    }
}
