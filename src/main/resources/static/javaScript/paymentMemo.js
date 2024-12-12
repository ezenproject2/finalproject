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

// 발급받은 토큰
const tokenExample = {
    "code":0,
    "message":null,
    "response":
        {"access_token":"7e037025cb3cabb58941924a959e49145ba818e0",
            "now":1733895332,
            "expired_at":1733897132}
};

// 토큰 발급받아서 결제 단건조회 받은 후 결과
// 요청: body에 imp_uid, header에 토큰을 줌.
const checkOneResult = {
    "code": 0,
    "message": null,
    "response": {
      "amount": 100,
      "apply_num": "",
      "bank_code": null,
      "bank_name": null,
      "buyer_addr": null,
      "buyer_email": "qpfm111@naver.com",
      "buyer_name": "박준희",
      "buyer_postcode": null,
      "buyer_tel": "01071882723",
      "cancel_amount": 0,
      "cancel_history": [],
      "cancel_reason": null,
      "cancel_receipt_urls": [],
      "cancelled_at": 0,
      "card_code": null,
      "card_name": null,
      "card_number": null,
      "card_quota": 0,
      "card_type": null,
      "cash_receipt_issued": false,
      "channel": "pc",
      "currency": "KRW",
      "custom_data": null,
      "customer_uid": null,
      "customer_uid_usage": null,
      "emb_pg_provider": null,
      "escrow": false,
      "fail_reason": null,
      "failed_at": 0,
      "imp_uid": "imp_406540408880",
      "merchant_uid": "bfa513d4-79d9-45e5-bfaf-8768772870c0",
      "name": "주문명: 결제 테스트",
      "paid_at": 1733969551,
      "pay_method": "point",
      "pg_id": "TC0ONETIME",
      "pg_provider": "kakaopay",
      "pg_tid": "T75a4684286a7fa9983f",
      "receipt_url": "https://mockup-pg-web.kakao.com/v1/confirmation/p/T75a4684286a7fa9983f/806429d0a013f986cf8a7bd8f2d8949ff11ecc1a6d8eba30b4c204260e0ccb46",
      "started_at": 1733969540,
      "status": "paid",
      "user_agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36",
      "vbank_code": null,
      "vbank_date": 0,
      "vbank_holder": null,
      "vbank_issued_at": 0,
      "vbank_name": null,
      "vbank_num": null
    }
  }
  