package com.ezen.books.handler;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class OpenAIClient {

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    // OpenAI API 키를 클래스에서 동적으로 설정
    public static String getChatbotAnswer(String userMessage, String apiKey) {
        try {
            // 요청 본문을 생성
            String requestBody = "{"
                    + "\"model\": \"gpt-3.5-turbo\","
                    + "\"messages\": [{\"role\": \"user\", \"content\": \"" + userMessage + "\"}]"
                    + "}";

            // HttpClient 생성
            HttpClient client = HttpClient.newHttpClient();

            // 요청 보내기
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OPENAI_API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)  // 동적으로 API 키 사용
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // 응답 받기
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            // 응답에서 챗봇의 답변만 추출
            return extractAnswerFromResponse(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error in getting response from chatbot.";
        }
    }

    // 응답에서 챗봇의 답변만 추출하는 방법
    private static String extractAnswerFromResponse(String responseBody) {
        JSONObject jsonResponse = new JSONObject(responseBody); // JSON 객체로 파싱
        return jsonResponse.getJSONArray("choices")
                .getJSONObject(0)         // 첫 번째 choice
                .getJSONObject("message")  // message 객체
                .getString("content");     // content 값 추출
    }
}