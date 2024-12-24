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
        log.info("orderDetailProductGroup is empty or not :{}", orderDetailProductGroup.isEmpty());

        boolean isOrderEmpty = orderListService.isOrderEmpty(mno);

        // 화면에 띄울 사용자 정보와 등급 정보를 가져옴.
//        MemberVO memberInfo = orderListService.getMember(mno);
//        GradeVO memberGrade = orderListService.getMemberGrade(memberInfo.getGno());

        model.addAttribute("mno", mno);
        model.addAttribute("isOrderEmpty", isOrderEmpty);
        model.addAttribute("orderDetailProductGroup", orderDetailProductGroup);
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


//    @GetMapping("/cart")
//    public String showOrderList(@RequestParam("mno") long mno, Model model) {
//        log.info(" >>> OrderListController: showOrderList start.");
////
////        List<OrderWithDetailDTO> orderWithDetailList = new ArrayList<>();
////
////        // 사용자의 주문 내역을 모두 가져옴
////        List<OrdersVO> orderList = orderListService.getAllOrderList(mno);
////        log.info("The order List: {}", orderList);
////
////        // 사용자의 주문 내역이 비었는지 아닌지 판별하기 위한 값
////        boolean isOrderListEmpty;
////        if(orderList.isEmpty()) {
////            isOrderListEmpty = true;
////        } else {
////            isOrderListEmpty = false;
////        }
////
////        List<List<OrderDetailVO>> listOfDetailList = new ArrayList<>();
////        if(isOrderListEmpty) {
////            //
////        } else {
////            for(OrdersVO order : orderList) {
////                // 주문에 맞는 모든 주문 상세 가져오기. Mapper에서 List<>로 받을 것.
////                List<OrderDetailVO> orderDetailList = orderListService.getOrderDetailList(order.getOrno());
////                log.info("The order detail List: {}", orderDetailList);
////
////                listOfDetailList.add(orderDetailList);
////            }
////
////            log.info("The list to store detail list is: {}", listOfDetailList);
////        }
////
////        model.addAttribute("mno", mno);
////        model.addAttribute("orderList", orderList);
////        model.addAttribute("listOfDetailList", listOfDetailList);
//        return "/mypage/order-list";
//    }

}
