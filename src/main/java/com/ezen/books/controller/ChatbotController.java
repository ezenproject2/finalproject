package com.ezen.books.controller;

import org.json.JSONObject;

import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@PropertySource("classpath:/application-secrets.properties")
@RestController
@RequestMapping("/chatbot")
public class ChatbotController {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @PostMapping
    public ResponseEntity<Map<String, String>> getChatbotResponse(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message"); // 클라이언트에서 보낸 사용자 메시지

        // 특정 질문에 대해 고정된 답변 제공
        String fixedResponse = getFixedResponse(userMessage);
        if (fixedResponse != null) {
            Map<String, String> response = new HashMap<>();
            response.put("answer", fixedResponse);
            return ResponseEntity.ok(response);
        }

        // OpenAI API 호출
        String chatbotResponse = getChatbotAnswer(userMessage);

        // 챗봇의 답변 반환
        Map<String, String> response = new HashMap<>();
        response.put("answer", chatbotResponse);
        return ResponseEntity.ok(response);
    }

    // 특정 질문에 대해 고정된 답변 반환
    private String getFixedResponse(String userMessage) {
        if (userMessage.equalsIgnoreCase("안녕하세요")) {
            return "안녕하세요! 무엇을 도와드릴까요?";
        } else if (userMessage.equalsIgnoreCase("오늘 날씨 어때?")) {
            return "오늘 날씨는 맑음입니다. 우산은 필요 없어요!";
        }
        // 고정된 질문-답변 쌍 추가 가능
        return null; // 고정된 답변이 없으면 null 반환
    }

    private String getChatbotAnswer(String userMessage) {
        // OpenAI API 호출 로직
        return "여기서 OpenAI API를 통해 받은 답변을 반환합니다.";
    }
}
