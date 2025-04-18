package com.example.TalentAI.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "my-super-secret-key-must-be-32-chars!";
    private static final String ENCRYPTION_KEY = "0123456789abcdefghijklmnopqrstuv";


    public String generateToken(String email, String role) {
        try {
            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);
            Date exp = new Date(nowMillis + 1000 * 60 * 60 * 10); // 10 saat geçerli

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(email)
                    .claim("role", "ROLE_" + role.toUpperCase())
                    .issueTime(now)
                    .expirationTime(exp)
                    .build();

            byte[] signingKeyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec signingKey = new SecretKeySpec(signingKeyBytes, "HmacSHA256");

            JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.HS256)
                    .type(JOSEObjectType.JWT)
                    .build();

            SignedJWT signedJWT = new SignedJWT(jwsHeader, claimsSet);
            MACSigner signer = new MACSigner(signingKey);
            signedJWT.sign(signer);

            byte[] encryptionKeyBytes = ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec encryptionKey = new SecretKeySpec(encryptionKeyBytes, "AES");

            JWEHeader jweHeader = new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A256GCM)
                    .contentType("JWT")
                    .build();

            JWEObject jweObject = new JWEObject(jweHeader, new Payload(signedJWT));
            DirectEncrypter encrypter = new DirectEncrypter(encryptionKey);
            jweObject.encrypt(encrypter);

            return jweObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Token oluşturulurken hata oluştu", e);
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, JWTClaimsSet::getSubject);
    }

    public String extractRole(String token) throws ParseException {
        return extractAllClaims(token).getStringClaim("role");
    }

    public <T> T extractClaim(String token, Function<JWTClaimsSet, T> claimsResolver) {
        JWTClaimsSet claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private JWTClaimsSet extractAllClaims(String token) {
        try {
            JWEObject jweObject = JWEObject.parse(token);
            byte[] encryptionKeyBytes = ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec encryptionKey = new SecretKeySpec(encryptionKeyBytes, "AES");

            DirectDecrypter decrypter = new DirectDecrypter(encryptionKey);
            jweObject.decrypt(decrypter);

            SignedJWT signedJWT = jweObject.getPayload().toSignedJWT();
            if (signedJWT == null) {
                throw new RuntimeException("Token içeriği SignedJWT değil");
            }

            byte[] signingKeyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec signingKey = new SecretKeySpec(signingKeyBytes, "HmacSHA256");
            MACVerifier verifier = new MACVerifier(signingKey);
            if (!signedJWT.verify(verifier)) {
                throw new RuntimeException("Token imzası doğrulanamadı");
            }

            return signedJWT.getJWTClaimsSet();
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException("Token doğrulanırken hata oluştu", e);
        }
    }

    public boolean isTokenValid(String token, String username) {
        try {
            String extractedUsername = extractUsername(token);
            boolean expired = isTokenExpired(token);
            return extractedUsername.equals(username) && !expired;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        JWTClaimsSet claims = extractAllClaims(token);
        return claims.getExpirationTime().before(new Date());
    }
}
