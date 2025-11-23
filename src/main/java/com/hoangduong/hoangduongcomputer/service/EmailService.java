package com.hoangduong.hoangduongcomputer.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    @Value("${brevo.api-key}")
    private String brevoApiKey;

    @Value("${brevo.admin-email.address}")
    private String adminEmailAddress;

    @Value("${brevo.admin-email.name}")
    private String adminEmailName;

    private final OkHttpClient client = new OkHttpClient();

    public void sendEmail(String recipientEmail, String customSubject, String htmlContent) throws IOException {
        String jsonBody = String.format(
                """
                {
                    "sender": {
                        "name": "%s",
                        "email": "%s"
                    },
                    "to": [
                        {
                            "email": "%s"
                        }
                    ],
                    "subject": "%s",
                    "htmlContent": "%s"
                }
                """,
                adminEmailName,
                adminEmailAddress,
                recipientEmail,
                customSubject,
                escapeJson(htmlContent)
        );

        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url("https://api.brevo.com/v3/smtp/email")
                .post(body)
                .addHeader("accept", "application/json")
                .addHeader("api-key", brevoApiKey)
                .addHeader("content-type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to send email: " + response.body().string());
            }
        }
    }

    private String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public void sendVerificationEmail(String recipientEmail, String verificationLink) throws IOException {
        String subject = "Trello MERN Stack Advanced: Please verify your email before using our services!";
        String htmlContent = String.format(
                """
                <h3>Here is your verification link:</h3>
                <h3>%s</h3>
                <h3>Sincerely,<br/> - HaiNguyen - </h3>
                """,
                verificationLink
        );
        sendEmail(recipientEmail, subject, htmlContent);
    }
}
