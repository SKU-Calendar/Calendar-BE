package com.example.demo.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.chat.dto.AiEventResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class AiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public AiService(@Value("${openai.api.key}") String apiKey,
                     @Value("${openai.api.url}") String apiUrl) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    public AiEventResponseDto generateEvent(String userMessage) {
        String systemPrompt = """
                당신은 일정 관리 AI 어시스턴트입니다.
                사용자의 자연어 입력을 받아 일정(Event)과 슬롯(Slot)을 JSON 형식으로 생성합니다.
                
                반드시 다음 JSON 형식으로만 응답하세요:
                {
                  "title": "일정 제목",
                  "description": "일정 설명",
                  "startAt": "2024-01-15T14:00:00",
                  "endAt": "2024-01-15T16:00:00",
                  "slots": [
                    {
                      "slotTitle": "슬롯 제목",
                      "slotNote": "슬롯 메모",
                      "slotStartAt": "2024-01-15T14:00:00",
                      "slotEndAt": "2024-01-15T15:00:00"
                    }
                  ]
                }
                
                주의사항:
                - 반드시 유효한 JSON 형식으로만 응답
                - 날짜는 ISO-8601 형식 사용
                - slots 배열에는 최소 1개 이상의 슬롯 포함
                - 다른 텍스트나 설명 없이 JSON만 반환
                """;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini");
        
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);
        
        Map<String, String> userMessageMap = new HashMap<>();
        userMessageMap.put("role", "user");
        userMessageMap.put("content", userMessage);
        
        requestBody.put("messages", java.util.List.of(systemMessage, userMessageMap));
        requestBody.put("response_format", Map.of("type", "json_object"));
        requestBody.put("temperature", 0.7);

        String responseJson = webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(jsonNode -> jsonNode.get("choices").get(0).get("message").get("content").asText())
                .block();

        if (responseJson == null || responseJson.trim().isEmpty()) {
            throw new RuntimeException("AI 응답이 비어있습니다");
        }

        try {
            return objectMapper.readValue(responseJson, AiEventResponseDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("AI 응답 파싱 실패: " + e.getMessage(), e);
        }
    }
}

