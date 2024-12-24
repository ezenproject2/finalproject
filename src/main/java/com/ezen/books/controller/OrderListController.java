package com.ezen.books.controller;

import com.ezen.books.domain.*;
import com.ezen.books.service.OrderListService;
import com.ezen.books.service.PayoutService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/mypage/order-list")
@Controller
public class OrderListController  {

    private final OrderListService orderListService;

    // NOTE: Notion > 코드 보관함의 PaymentController에 있던 코드들.
    @GetMapping("/intro")
    public String showOrderList(@RequestParam("mno") long mno, Model model) {
        log.info(" >>> OrderListController: showOrderList start.");

        // 화면에 띄울 order_detail과 product의 정보를 가져옴.
        List<List<OrderDetailProductDTO>> orderDetailProductGroup = orderListService.getOrderDetailProductList(mno);
//        log.info("The orderDetailProductGroup: {}", orderDetailProductGroup);
        log.info("orderDetailProductGroup is empty or not :{}", orderDetailProductGroup.isEmpty());

        boolean isOrderEmpty = orderListService.isOrderEmpty(mno);

        // 화면에 띄울 사용자 정보와 등급 정보를 가져옴.
        MemberVO memberInfo = orderListService.getMember(mno);
        GradeVO memberGrade = orderListService.getMemberGrade(memberInfo.getGno());

        model.addAttribute("mno", mno);
        model.addAttribute("isOrderEmpty", isOrderEmpty);
        model.addAttribute("orderDetailProductGroup", orderDetailProductGroup);
        model.addAttribute("memberInfo", memberInfo);
        model.addAttribute("memberGrade", memberGrade);
        return "/mypage/order_list";
    }

    @PostMapping("/refund")
    @ResponseBody
    public ResponseEntity<String> getRefundDataFromClient(@RequestBody String refundData) throws IOException, URISyntaxException, InterruptedException {
        log.info("getRefundDataFromClient start.");

        log.info("The refundData from the client: {}", refundData);

        long odno = 0L;
        String orno = "";
        int qtyPrice = 0;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode refundNode = objectMapper.readTree(refundData);
            odno = refundNode.get("odno").asLong();
            orno = refundNode.get("orno").asText();
            qtyPrice = refundNode.get("qtyPrice").asInt();
        } catch (Exception e) {
            log.info("Error during parsing mnoNode. Content: {}", e);
        }

        int isDone = orderListService.refundWithIamport(odno, orno, qtyPrice);

        return (0 < isDone) ?
                new ResponseEntity<>("1", HttpStatus.OK) :
                new ResponseEntity<>("0", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/go-to-index")
    public String goToIndex() {
        return "redirect:/";
    }

}
