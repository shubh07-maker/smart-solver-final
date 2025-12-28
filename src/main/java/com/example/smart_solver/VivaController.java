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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
@CrossOrigin(origins = "*")
public class VivaController {

    private String getGeminiResponse(String prompt) {
        String apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) return "Error: API Key missing.";

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

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
        } catch (RestClientException e) {
            return "Error: " + e.getMessage();
        }
    }

    // UPDATED: Now accepts ANY language request
    @PostMapping("/api/solve")
    public String solveQuestion(@RequestBody String fullRequest) {
        // We removed the word "Java" here. Now it just says "Write code for..."
        String prompt = "Task: Write code for: " + fullRequest + 
                        ". Rules: 1. No explanations. 2. Return ONLY the code. " +
                        "3. After code, print '#####' on a new line. " +
                        "4. After #####, print expected output. 5. No markdown.";
        return getGeminiResponse(prompt);
    }

    @PostMapping("/api/explain")
    public String explainQuestion(@RequestBody String fullRequest) {
        String prompt = "Explain this concept or code briefly in simple English: " + fullRequest;
        return getGeminiResponse(prompt);
    }
}