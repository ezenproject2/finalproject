package com.ezen.books.service;

import com.ezen.books.domain.*;
import com.ezen.books.repository.OfflineMapper;
import com.ezen.books.repository.PayoutMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutServiceImpl implements PayoutService {

    private PayoutMapper payoutMapper;
    private OfflineMapper offlineMapper;
    private String iamportApiKey;
    private String iamportApiSecret;

    @Autowired
    public PayoutServiceImpl(
            PayoutMapper payoutMapper,
            OfflineMapper offlineMapper,
            @Value("${iamport_rest_api_key}") String iamportApiKey,
            @Value("${iamport_rest_api_secret}") String iamportApiSecret) {
        this.payoutMapper = payoutMapper;
        this.iamportApiKey = iamportApiKey;
        this.iamportApiSecret = iamportApiSecret;
        this.offlineMapper = offlineMapper;
    }

    @Override
    public AddressVO getDefaultAddress(long mno) {
        AddressVO defaultAddress = payoutMapper.getDefaultAddress(mno);
        log.info(">>>> defaultAddress > {}", defaultAddress);
        return defaultAddress;
    }

    @Override
    public int saveOrdersToServer(OrdersVO ordersVO) {
        return payoutMapper.saveOrdersToServer(ordersVO);
    }

    @Override
    public int saveOrderDetailToServer(OrderDetailVO orderDetail) {
        return payoutMapper.saveOrderDetailToServer(orderDetail);
    }

    @Override
    public int savePaymentToServer(PaymentVO paymentData) {
        return payoutMapper.savePaymentToServer(paymentData);
    }

    @Override
    public int removeCartToServer(long mno, long prno) {
        try {
            payoutMapper.removeCartToServer(mno, prno);
            return 1;
        } catch (Exception e) {
            log.info("Error during removing cart table. Content: {}", e);
        }
        return 0;
    }

    @Override
    public int registerDefaultAddress(AddressVO addressData) {
        addressData.setIsDefault("Y");
        return payoutMapper.registerDefaultAddress(addressData);
    }

    @Override
    public int saveDeliveryToServer(DeliveryVO deliveryData) {
        return payoutMapper.saveDeliveryToServer(deliveryData);
    }

    @Override
    public List<OfflineStoreVO> getPickupStores(List<CartVO> cartList) {
        List<Long> osnoList = null;
        List<OfflineStoreVO> storeList = new ArrayList<>();

        for (CartVO cartVO : cartList) {
            long prno = cartVO.getPrno();
            int bookQty = cartVO.getBookQty();

            List<Long> availableStores = offlineMapper.getPickupStoreOsno(prno, bookQty);

            if (osnoList == null) {
                osnoList = availableStores;
            } else {
                osnoList.retainAll(availableStores);
            }

            if (osnoList.isEmpty()) {
                break;
            }
        }

        // 결과 리스트 반환 (교집합된 매장 목록)
        for(Long osno : osnoList){
            storeList.add(offlineMapper.getStoreVOByOsno(osno));
        }
        return storeList;
    }

    @Override
    public boolean checkSinglePayment(String impUid, String amount) throws IOException, URISyntaxException, InterruptedException {
        log.info(" >>> PaymentServiceImpl: checkSinglePayment start.");
        IamportAccessToken iamportToken = PayoutService.super.issueIamportToken(iamportApiKey, iamportApiSecret);

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.iamport.kr/payments/" + impUid;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        headers.set("Authorization", "Bearer " + iamportToken.getToken());

        HttpEntity<String> sendImpUidAndToken = new HttpEntity<>(null, headers);

        // NOTE: 한글을 인식하지 못해 result body의 사용자 이름이 유니코드로 나옴.
        // 이 메서드에서 사용자 이름을 사용하지는 않지만 주의 요망.
        ResponseEntity<String> result = restTemplate.exchange(
                url,
                HttpMethod.GET,
                sendImpUidAndToken,
                String.class
        );

        log.info("The response status: {}", result.getStatusCode());

        String responseBody = result.getBody();
        ObjectMapper objectMapper = new ObjectMapper();

        String resultImpUid = "";
        String resultAmount = "";
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            resultImpUid = jsonNode.get("response").get("imp_uid").asText();
            resultAmount = jsonNode.get("response").get("amount").asText();
        } catch (Exception e) {
            log.info("Exception occurred. Content: {}", e);
        }

        // 클라이언트가 보낸 imp_uid, amount와 응답으로 받은 imp_uid, amount 비교
        if(impUid.equals(resultImpUid) && amount.equals(resultAmount)) {
            return true;
        } else {
            return false;
        }
    }

}
