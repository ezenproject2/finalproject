// 카카오페이 결제 후 받은 응답 객체(response)
const result = 
{
    apply_num: "",
    bank_name: null,
    buyer_addr: "",
    buyer_email: "qpfm111@naver.com",
    buyer_name: "박준희",
    buyer_postcode: "",
    buyer_tel: "01071882723",
    card_name: null,
    card_number: "",
    card_quota: 0,
    currency: "KRW",
    custom_data: null,
    imp_uid: "imp_072061542863",
    merchant_uid: "cd2574cf-c157-42cf-a72b-3e8e06a3a2d4",
    name: "주문명: 결제 테스트",
    paid_amount: 100,
    paid_at: 1733810092,
    pay_method: "point",
    pg_provider: "kakaopay",
    pg_tid: "T757d78d28c50508573a",
    pg_type: "payment",
    receipt_url: "https://mockup-pg-web.kakao.com/v1/confirmation/p/T757d78d28c50508573a/07b67e9fb88e3c5d1b373801e45749faaf510e957b46468feaef6941e94434b6",
    status: "paid",
    success: true
}

// 받아온 토큰 결과 예시
const tokenExample = {
    "code":0,
    "message":null,
    "response":
        {"access_token":"7e037025cb3cabb58941924a959e49145ba818e0",
            "now":1733895332,
            "expired_at":1733897132}
};