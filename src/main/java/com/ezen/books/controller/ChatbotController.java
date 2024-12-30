package com.ezen.books.controller;

import com.ezen.books.domain.OfflineStoreVO;
import com.ezen.books.domain.ProductVO;
import com.ezen.books.handler.OpenAIClient;
import com.ezen.books.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/chatbot")
@RestController
@PropertySource("classpath:/application-secrets.properties")
public class ChatbotController {

    private final ChatbotService chatbotService;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    // 텍스트형 질문에 대한 답변을 내보냄
    @PostMapping
    public ResponseEntity<Map<String, String>> getChatbotResponse(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message"); // 클라이언트에서 보낸 사용자 메시지

        // 질문과 답변 사이에 딜레이 시간 부여
        try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

        // 1. 준비된 답변을 생성하는 메서드를 통해 응답 생성 ( 주로 DB에서 정보를 가져올 때 이용 )
        if(request.get("classify").equals("a-type")){
            // "1차" 질문에 대응하는 메서드로 이동
            Map<String, String> fixedResponse = getFixedResponse(userMessage);
            if (fixedResponse != null) {
                // 3. 응답 형태를 만들어 보냄
                return createResponse(fixedResponse.get("answer"), fixedResponse.get("classify"));
            }
        }else if(request.get("classify").equals("b-type")){
            // "b-type" 질문에 대응하는 메서드로 이동
            Map<String, String> fixedResponse = getBTypeResponse(userMessage);
            if (fixedResponse != null) {
                // 3. 응답 형태를 만들어 보냄
                return createResponse(fixedResponse.get("answer"), fixedResponse.get("classify"));
            }
        }else if(request.get("classify").equals("c-type")){
            // "b-type" 질문에 대응하는 메서드로 이동
            Map<String, String> fixedResponse = getCTypeResponse(userMessage);
            if (fixedResponse != null) {
                // 3. 응답 형태를 만들어 보냄
                return createResponse(fixedResponse.get("answer"), fixedResponse.get("classify"));
            }
        }

        // 고정된 답변을 받는 메서드에서 null 값을 받을 경우 : 일반 챗 GPT로 연결함
        // 2. OpenAI API 호출하여 응답 생성
        try {
            String chatbotResponse = OpenAIClient.getChatbotAnswer(userMessage, openAiApiKey);
            // 3. 응답 형태를 만들어 보냄
            return createResponse(chatbotResponse, "a-type");
        } catch (Exception e) {
            return createResponse("챗봇 응답을 가져오는 중 문제가 발생했습니다.", "a-type");
        }
    }

    // (1차) 특정 질문에 대한 고정된 답변 제공
    private Map<String, String> getFixedResponse(String userMessage) {
        Map<String, String> answer = new HashMap<>();

        // "책 검색해줘" 관련 질문 처리
        if (userMessage.matches("(?i).*책 검색.*")
                || userMessage.matches("(?i).*책 찾기.*")
                || userMessage.matches("(?i).*책 찾고.*")
                || userMessage.matches("(?i).*책 찾는.*")
                || userMessage.matches("(?i).*책 찾아.*")) {
            answer.put("answer", "무슨 책을 찾고 싶어요? 책 제목만 입력해주세요 :)");
            answer.put("classify", "b-type"); // 다음 질문은 b-type으로 분류
            return answer;
        }

        // "매장 안내" 관련 질문 처리
        if (userMessage.matches("(?i).*가까운 매장.*")
                || userMessage.matches("(?i).*근처 매장.*")) {
            answer.put("answer", "가까운 매장을 찾아볼게요! 위치 정보를 받습니다!");
            answer.put("classify", "c-type"); // 다음 질문은 b-type으로 분류
            return answer;
        }

        // 자연어에 대한 이젠문고 ver 답변
        if (userMessage.matches("(?i).*안녕.*")
                || userMessage.matches("(?i).*하이.*")
                || userMessage.matches("(?i).*헬로.*")) {// "안녕하세요"가 포함된 메시지
            answer.put("answer", "안녕하세요!! 무엇을 도와드릴까요?");
            answer.put("classify", "a-type");
            return answer;
        }

        if (userMessage.matches("(?i).*누구.*")) { // "날씨"가 포함된 메시지
            answer.put("answer", "저는 이젠문고의 챗봇 알리미입니다! 반가워요 :) ");
            answer.put("classify", "a-type");
            return answer;
        }


        return null; // 고정된 답변이 없으면 null 반환
    }

    // (b-type) 책 검색 관련 질문에 대한 고정된 답변
    private Map<String, String> getBTypeResponse(String userMessage) {
        Map<String, String> answer = new HashMap<>();

        ProductVO productVO = chatbotService.chatGetProductVO(userMessage);
        if(productVO != null){
            answer.put("answer", "현재 판매중인 상품이 있습니다. 이동하시겠어요? <a href='/product/detail?isbn=" + productVO.getIsbn() + "'>상품 페이지로 이동</a>");
            answer.put("classify", "a-type");
            return answer;
        }else{
            answer.put("answer", "죄송합니다. 해당 상품은 판매중이지 않아요!");
            answer.put("classify", "a-type");
            return answer;
        }
    }

    // (c-type) 책 검색 관련 질문에 대한 고정된 답변
    private Map<String, String> getCTypeResponse(String userMessage) {
        Map<String, String> answer = new HashMap<>();

        OfflineStoreVO offlineStoreVO = chatbotService.chatGetOfflineStoreVO(userMessage);

        if(offlineStoreVO !=  null){
            answer.put("answer", "[매장 안내]<br>매장위치: " + offlineStoreVO.getAddress() + "<br>영업시간: " + offlineStoreVO.getHours());
            answer.put("classify", "a-type");
            return answer;
        }else{
            answer.put("answer", "매장 정보를 찾는 중 오류가 발생했습니다.");
            answer.put("classify", "a-type");
            return answer;
        }
    }

    // 3. String 형식의 응답을 Map 형식으로 변환해줌.
    private ResponseEntity<Map<String, String>> createResponse(String answer, String classify) {
        Map<String, String> response = new HashMap<>();
        response.put("answer", answer);
        response.put("classify", classify);
        return ResponseEntity.ok(response);
    }

}