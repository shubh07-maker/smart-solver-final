package com.example.smart_solver;

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
        
        String apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = ""; 
        }
        
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        // --- THE NEW STRICT PROMPT ---
        String prompt = "Task: Write Java code for: " + fullRequest + 
                        ". Rules: 1. No explanations. 2. No conversational text. " +
                        "3. Return ONLY the code. " +
                        "4. After the code, print the string '#####' on a new line. " +
                        "5. After the #####, print the expected output of the code. " +
                        "6. Do not use markdown backticks (```).";

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