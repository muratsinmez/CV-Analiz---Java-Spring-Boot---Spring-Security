# 🧠 CV Analiz Uygulaması (AI Destekli) – Spring Boot

Bu proje, şirketlerin yüklediği özgeçmişleri yapay zekâ ile analiz ederek onlara öneriler ve değerlendirmeler sunan bir **AI destekli backend uygulamasıdır.**  
Java & Spring Boot kullanılarak geliştirilmiştir.

---

## 🚀 Özellikler

- 🧾 **CV Yükleme:** Şirketler PDF formatındaki CV'leri yükleyebilir.  
- 🧠 **Yapay Zekâ Analizi:** OpenAI entegrasyonu ile CV içeriği analiz edilir.  
- 🔐 **JWT ile Güvenli Giriş:** Admin ve şirket rolleri için token bazlı doğrulama.  
- 🧑‍💼 **Rol Bazlı Yetkilendirme:**  
  - `Admin`: Tüm şirketleri ve analiz geçmişlerini görebilir  
  - `Company`: Kendi analiz geçmişine erişebilir  
- 💳 **Premium Kontrol:**  
  - Ücretsiz şirketler en fazla 3 analiz yapabilir  
  - Premium şirketler sınırsız analiz & geçmiş erişimi hakkına sahiptir  
- 📊 **Analiz Geçmişi:** Şirketler daha önce yapılan analizleri görebilir ve yeniden görüntüleyebilir.

---

## ⚙️ Kullanılan Teknolojiler

- **Java 17**  
- **Spring Boot 3.1+**  
- **Spring Security**  
- **JWT (JSON Web Token)**  
- **MySQL**  
- **OpenAI API**  
- **RESTful API**

---

## 📁 Proje Yapısı

## 📁 Proje Yapısı

cv-analiz-uygulamasi/
│
├── src/
│   ├── controller/
│   ├── model/
│   ├── repository/
│   ├── security/
│   ├── service/
│   └── dto/
│
├── application.properties
├── pom.xml
└── README.md

---

## 🔐 Roller ve Giriş

| Rol     | Özellikler                         |
|---------|------------------------------------|
| Admin   | Şirketleri yönetir, tüm analizleri görebilir |
| Company | CV yükler, analiz alır, kendi geçmişini görür |

JWT Token sistemiyle giriş yapılarak rol doğrulaması yapılmaktadır.

---

## 🧠 AI Entegrasyonu

OpenAI üzerinden yapılan isteklerde CV içerikleri anlamlandırılır ve kullanıcıya:

- Güçlü yönler
- Eksik alanlar
- İyileştirme önerileri

sunulur. Uygulamanın analitik gücü bu sayede artırılmıştır.

---

## 📷 Görseller

*(İsteğe bağlı ekran görüntüleri eklenecekse buraya koyulabilir.)*

---

## 🛠 Kurulum

1. Projeyi klonlayın:  
   `git clone https://github.com/kullanici-adi/cv-analiz-uygulamasi.git`
2. MySQL veritabanı oluşturun ve bilgileri `application.properties` dosyasına girin  
3. OpenAI API key'inizi ekleyin  
4. Projeyi çalıştırın:  
   `./mvnw spring-boot:run`

---

## 📬 İletişim

> Proje sahibi: **Murat SİNMEZ**  
> Geliştirme sürecinde ChatGPT'den destek alınmıştır.  
> 📫 İletişim & öneriler için: [[LinkedIn profili]](https://www.linkedin.com/in/murat-sinmez-980185225/)

---

## ⭐ Katkı Sağlamak

Projeyi geliştirmek isterseniz PR gönderebilir, issue açabilirsiniz 🙏  
Her türlü öneri ve geri bildirim memnuniyetle karşılanır!

---

## ⚠️ Not

Bu proje sadece bireysel kullanım ve portföy amaçlıdır. Ticari kullanım için izin gereklidir.

