package com.ezen.books.service;

import com.ezen.books.domain.*;
import com.ezen.books.repository.OrderListMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderListServiceImpl implements OrderListService {

    private OrderListMapper orderListMapper;
    private String iamportApiKey;
    private String iamportApiSecret;

    @Autowired
    public OrderListServiceImpl(
            OrderListMapper orderListMapper,
            @Value("${iamport_rest_api_key}") String iamportApiKey,
            @Value("${iamport_rest_api_secret}") String iamportApiSecret) {
        this.orderListMapper = orderListMapper;
        this.iamportApiKey = iamportApiKey;
        this.iamportApiSecret = iamportApiSecret;
    }

    @Override
    public List<OrdersVO> getAllOrderList(long mno) {
        return orderListMapper.getAllOrderList(mno);
    }

    @Override
    public List<OrderDetailVO> getOrderDetailList(String orno) {
        return orderListMapper.getOrderDetailList(orno);
    }

    @Override
    public List<List<OrderDetailProductDTO>> getOrderDetailProductGroup(long mno) {
        return generateOrderDetailProductGroup(mno);
    }

    @Override
    public boolean isOrderEmpty(long mno) {
        // 사용자의 주문 orno를 모두 가져옴
        List<String> ornoList = orderListMapper.getOrnoList(mno);
        return ornoList.isEmpty();
    }

    @Override
    public MemberVO getMember(long mno) {
        return orderListMapper.getMember(mno);
    }

    @Override
    public GradeVO getMemberGrade(Long gno) {
        return orderListMapper.getMemberGrade(gno);
    }

    @Override
    public OrdersVO getOrder(String orno) {
        return orderListMapper.getOrder(orno);
    }

    @Override
    public List<OrderDetailProductDTO> getOrderDetailProductList(String orno) {
        return generateOrderDetailProductList(orno);
    }

    @Override
    public DeliveryVO getDelivery(String orno) {
        return orderListMapper.getDelivery(orno);
    }

    @Override
    public PickUpVO getPickUp(String orno) {
        return orderListMapper.getPickUp(orno);
    }

    @Override
    public OfflineStoreVO getOfflineStore(long osno) {
        return orderListMapper.getOfflineStore(osno);
    }

    @Override
    public PaymentVO getPayment(String orno) {
        return orderListMapper.getPayment(orno);
    }

    @Override
    public int refundWithIamport(long odno, String orno, int qtyPrice) throws IOException, URISyntaxException, InterruptedException {
        IamportAccessToken iamportToken = OrderListService.super.issueIamportToken(iamportApiKey, iamportApiSecret);

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.iamport.kr/payments/cancel";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json")); // StandardCharsets.UTF_8
        headers.set("Authorization", "Bearer " + iamportToken.getToken());

        String impUid = orderListMapper.getImpUid(orno);
        // NOTE: 화면에서 가져올 수도 있음.
        String reason = "단순 변심";

        String requestBody = "{" +
                "\"imp_uid\": \"" + impUid + "\", " +
                "\"merchant_uid\": \"" + orno + "\"" +
                "\"amount\": \"" + qtyPrice + "\"" +
                "\"reason\": \"" + reason + "\"" +
                "}";

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> result = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        log.info("The response status code: {}", result.getStatusCode());
//        log.info("The response body: {}", result.getBody());

        // 환불이 끝나면 odno와 orno에 해당하는 order_detail의 status를 "refunded"로 바꿈
        int isDone = orderListMapper.applyRefundToOrderDetail(odno, orno);

        return isDone;
    }

    private List<OrderDetailProductDTO> generateOrderDetailProductList(String orno) {
        List<OrderDetailProductDTO> orderDetailProductList = new ArrayList<>();

        List<Long> odnoList = orderListMapper.getOdnoList(orno);

        for(Long odno : odnoList) {
            OrderDetailProductDTO detailProduct = new OrderDetailProductDTO();

            OrderDetailVO orderDetail = orderListMapper.findOrderDetail(odno);
            ProductVO product = orderListMapper.getProduct(orderDetail.getPrno());

            detailProduct.setOrderDetailVO(orderDetail);
            detailProduct.setProductVO(product);

            orderDetailProductList.add(detailProduct);
        }

        log.info("The orderDetailProductList: {}", orderDetailProductList);
        return orderDetailProductList;
    }

    private List<List<OrderDetailProductDTO>> generateOrderDetailProductGroup(long mno) {
        List<List<OrderDetailProductDTO>> orderDetailList = new ArrayList<>();

        // 사용자의 주문 orno를 모두 가져옴
        List<String> ornoList = orderListMapper.getOrnoList(mno);

        // 사용자의 주문 내역(orno)이 비었는지 아닌지 판별하기 위한 값
        boolean isListEmpty;
        if(ornoList.isEmpty()) {
            isListEmpty = true;
        } else {
            isListEmpty = false;
        }

        if(isListEmpty) {
            // NOTE: 주문 내역이 비었을 때 처리할 로직이 들어감.
        } else {
            for (String orno : ornoList) {
                List<OrderDetailProductDTO> detailProductList = new ArrayList<>();

                // 한 orno 당 하나의 List<OrderDetailProductDTO>, 즉 detailProductList를 가져옴
                List<OrderDetailVO> orderDetails = orderListMapper.getOrderDetailList(orno);
                log.info("The orderDetails: {}", orderDetails);

                // 한 orno에 대응하는 List<OrderDetailVO>의 OrderDetailVO마다 대응하는 ProductVO 가져오기
                for (OrderDetailVO orderDetail : orderDetails) {
                    ProductVO product = orderListMapper.getProduct(orderDetail.getPrno());

                    // productList에 realPrice 설정: ProductController의 /detail에 있는 공식 사용
                    double discountedPrice = product.getDiscount() * (1 - product.getDiscountRate() / 100.0);
                    int roundedPrice = (int) Math.floor(discountedPrice / 10.0) * 10;
                    product.setRealPrice(roundedPrice);

                    // 가져온 ProductVO와 orderDetail을 가지고 OrderDetailProdutDTO 생성
                    OrderDetailProductDTO detailProduct = new OrderDetailProductDTO(orderDetail, product);
                    // 하나의 orno에 대응하는 List<OrderDetailVO>의 크기만큼 OrderProductDTO를 넣음
                    detailProductList.add(detailProduct);
                }

                orderDetailList.add(detailProductList);
            }
        }

        return orderDetailList;
    }

}
