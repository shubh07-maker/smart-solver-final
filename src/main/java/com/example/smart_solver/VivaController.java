package com.example.smart_solver; // Matches your folder perfectly!

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
@CrossOrigin(origins = "*")
public class VivaController {

    @PostMapping("/api/solve")
    public String solveQuestion(@RequestBody String fullRequest) {
        
        // --- SECURITY UPGRADE ---
        String apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = ""; // Keep empty for upload!
        }
        
        // FIXED: Using version 1.5-flash
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        String prompt = "Act as a coding tutor. " + fullRequest + 
                        " After the code, provide the expected output. " +
                        "CRITICAL: Separate the Code and the Output with the string '#####'. " +
                        "Do not use markdown blocks (```). Just plain text.";

        Map<String, Object> contentPart = new HashMap<>();
        contentPart.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(contentPart));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(content));

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return "Error: " + e.getStatusText() + " - " + e.getResponseBodyAsString();
        } catch (RestClientException e) {
            return "Error: " + e.getMessage();
        }
    }
}