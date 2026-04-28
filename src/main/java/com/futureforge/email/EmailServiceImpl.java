package com.futureforge.email;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    public void sendOtp(String to, String otp) {
        try {
            OkHttpClient client = new OkHttpClient();

            String json = "{"
                    + "\"from\":\"onboarding@resend.dev\","
                    + "\"to\":[\"" + to + "\"],"
                    + "\"subject\":\"OTP Verification\","
                    + "\"html\":\"<h2>Your OTP is: " + otp + "</h2>\""
                    + "}";

            Request request = new Request.Builder()
                    .url("https://api.resend.com/emails")
                    .post(RequestBody.create(json, MediaType.parse("application/json")))
                    .addHeader("Authorization", "Bearer " + System.getenv("RESEND_API_KEY"))
                    .addHeader("Content-Type", "application/json")
                    .build();

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                log.error("Email failed: {}", response.body().string());
                throw new RuntimeException("Email sending failed");
            }

            log.info("EMAIL SENT SUCCESSFULLY ✅");

        } catch (Exception e) {
            log.error("EMAIL FAILED ❌", e);
            throw new RuntimeException("Email sending failed");
        }
    }
}