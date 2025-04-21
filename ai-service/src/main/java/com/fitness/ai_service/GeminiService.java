package com.fitness.ai_service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public String getAnswer(String prompt) {
        return """
        {
          "candidates": [
            {
              "content": {
                "parts": [
                  {
                    "text": "{\\\"analysis\\\":{\\\"overall\\\":\\\"Great walk!\\\",\\\"pace\\\":\\\"5.8\\\",\\\"heartRate\\\":\\\"Normal\\\",\\\"caloriesBurned\\\":\\\"150\\\"},\\\"improvements\\\":[{\\\"area\\\":\\\"Posture\\\",\\\"recommendation\\\":\\\"Keep your shoulders back\\\"}],\\\"suggestions\\\":[{\\\"workout\\\":\\\"Stretching\\\",\\\"description\\\":\\\"After a walk, do 5 min stretching\\\"}],\\\"safety\\\":[\\\"Stay hydrated\\\"]}"
                  }
                ]
              }
            }
          ]
        }
        """;
    }


}
