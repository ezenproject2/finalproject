let pgData = null;
document.getElementById('payBtnContainer').addEventListener('click', (e) => {

    if(e.target.classList.contains('pay-btn')) {
        pgData = identifyPayBtn(e.target.classList);
    }

    if(e.target.id == 'orderBtn') {
        if(pgData == null) {
            console.log('결제 수단을 선택해주세요.');
        } else {
            payWithIamport(pgData);
            pgData = null;
        }
    }
})


function identifyPayBtn(targetClassList) {
    
    const pgData = {
        pgVal: "", // pg provider
        payMethodVal: "",
        channelKeyVal: ""
    }

    if(targetClassList.contains('credit-card-btn')) {
        pgData.pgVal = "페이팔, pg 상점 아이디 필요";
        pgData.payMethodVal = "card";
        pgData.channelKeyVal = "";
    } else if (targetClassList.contains('phone-btn')) {
        pgData.pgVal = "danal";
        pgData.payMethodVal = "phone";
        pgData.channelKeyVal = "channel-key-bd169ac7-dd07-48a1-a122-d3558620fc7c";
    } else if (targetClassList.contains('naverpay-btn')) {
        pgData.pgVal = "네이버, pg 상점 아이디 필요";
        pgData.payMethodVal = "naverpay";
        pgData.channelKeyVal = "";
    } else if (targetClassList.contains('kakaopay-btn')) {
        pgData.pgVal = "kakaopay";
        pgData.payMethodVal = "card"; // 다른 payMethod도 지원하니 참고.
        pgData.channelKeyVal = "channel-key-a95bda2d-2a2f-43fb-8e53-7ba806a637f4";
    } else if (targetClassList.contains('tosspay-btn')) {
        pgData.pgVal = "tosspay_v2";
        pgData.payMethodVal = "tosspay";
        pgData.channelKeyVal = "channel-key-03b07e29-1b6d-463c-801e-c23bb3fd86f5";
    } else {
        console.log(`It's not a valid button.`);
    }

    return pgData;
}

async function payWithIamport(pgData) {
    // 주문의 UUID 정보를 가져와야 하지만
    // 주문이 구현되지 않아서 임시로 UUID를 생성했음.
    let merchantUuid = crypto.randomUUID();

    // 고객 정보 및 상품 정보 테스트용 입력값임.
    const paymentData = {
        channelKey: pgData.channelKeyVal,
        pg: pgData.pgVal, // pg provider
        pay_method: pgData.payMethodVal,
        merchant_uid: merchantUuid, // 상점에서 생성한 고유 주문번호
        name : "주문명: 결제 테스트", // 상품 이름. 영수증에 나옴.
        amount : 100, // 최소 결제 가능 금액이 100원임.
        buyer_email : "qpfm111@naver.com",
        buyer_name : "박준희",
        buyer_tel : "01071882723",
        notice_url: "http://localhost:8087/payment/verify-iamport/webhook" // 웹훅을 수신할 url 주소
    }

    IMP.init('imp73014361'); // iamport_customer_verification_code
    IMP.request_pay(paymentData, function (response) {
        if(response.success) {
            console.log("response >>>> " + response);
            alert("Success -> imp_uid : " + response.imp_uid + "/ merchant_uid(orderKey): " + response.merchant_uid);
            handleIamportResult(response);
        } else if (response.error_code != null) {
            alert("Fail -> code(" + response.error_code + ") / Message(" + response.error_msg + ")");
        }
    });
    // 이게 웹훅을 받는 코드인가?
    // curl -H "Content-Type: application/json" -X POST -d '{ "imp_uid": "imp_1234567890", "merchant_uid": "order_id_8237352", "status": "paid" }' { NotificationURL }
}

async function handleIamportResult(response) {
    // const url = `/payment/verify-iamport/${response.imp_uid}`;
    const url = `/payment/verify-iamport/webhook`;
    const config = {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        // imp_uid와 merchant_uid, 주문 정보를 서버에 전달
        body: JSON.stringify({
            imp_uid: response.imp_uid,
            merchant_uid: response.merchant_uid
        })
    };

    const verifiedResult = await fetch(url, config);
    const result = await verifiedResult.text();
    console.log("The result is " + result);
    return verifiedResult; // 검증이 끝나면 거래 기록을 DB에 저장하기
}



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
// 