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

    @PostMapping("/refund")
    @ResponseBody
    public ResponseEntity<String> getRefundDataFromClient(@RequestBody String refundData)
            throws IOException, URISyntaxException, InterruptedException {
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

}
