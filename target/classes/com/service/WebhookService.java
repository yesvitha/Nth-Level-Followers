package com.example.service;

import com.example.model.WebhookRequest;
import com.example.model.WebhookResponse;
import com.example.model.ResultOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class WebhookService {
    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);
    private static final String GENERATE_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook";
    private final RestTemplate restTemplate;
    
    public WebhookService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public WebhookResponse generateWebhook(String name, String regNo, String email) {
        WebhookRequest request = new WebhookRequest();
        request.setName(name);
        request.setRegNo(regNo);
        request.setEmail(email);
        
        log.info("Sending request to generate webhook");
        return restTemplate.postForObject(GENERATE_WEBHOOK_URL, request, WebhookResponse.class);
    }
    
    public void sendResult(String webhookUrl, String accessToken, String regNo, List<Integer> outcome) {
        ResultOutput result = new ResultOutput();
        result.setRegNo(regNo);
        result.setOutcome(outcome);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<ResultOutput> entity = new HttpEntity<>(result, headers);
        
        sendWithRetry(webhookUrl, entity, 0);
    }
    
    private void sendWithRetry(String url, HttpEntity<ResultOutput> entity, int attempt) {
        try {
            log.info("Sending result to webhook, attempt: {}", attempt + 1);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            log.info("Successfully sent result to webhook: {}", response.getStatusCode());
        } catch (Exception e) {
            log.error("Error sending result to webhook: {}", e.getMessage());
            if (attempt < 3) { // Retry up to 4 times (0-3)
                log.info("Retrying in 2 seconds...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                sendWithRetry(url, entity, attempt + 1);
            } else {
                log.error("Failed to send result after 4 attempts");
            }
        }
    }
}