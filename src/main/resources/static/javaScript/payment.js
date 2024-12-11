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
        notice_url: "http://localhost:8087/payment/verify-iamport/webhook", // 웹훅을 수신할 url 주소
        m_redirect_url: "http://localhost:8087" // 결제 완료 페이지로 payment/complete를 만들어야 할 듯?
    }

    IMP.init('imp73014361'); // iamport_customer_verification_code의 값이다.
    IMP.request_pay(paymentData, function (response) {
        if(response.success) {
            // 결제 데이터 저장 전에 수행되는 검증 로직.
            console.log("Payment completed.");
            console.log("Start payment information verification.");
            getIamportToken();
            // handleIamportResult(response);
            verifyPaymentInfo(response.imp_uid);
        } else if (response.error_code != null) {
            alert("Fail -> code(" + response.error_code + ") / Message(" + response.error_msg + ")");
        }
    });
}

// 검증을 백엔에서 하려고 했는데, 프엔에서 하는 걸로 바꿈.
// 그래서 얘를 안씀.
// async function handleIamportResult(response) {
//     // const url = `/payment/verify-iamport/${response.imp_uid}`;
//     const url = `/payment/verify-iamport/webhook`;
//     const config = {
//         method: "POST",
//         headers: { "Content-Type": "application/json" },
//         // imp_uid와 merchant_uid, 주문 정보를 서버에 전달
//         body: JSON.stringify({
//             imp_uid: response.imp_uid,
//             merchant_uid: response.merchant_uid
//         })
//     };

//     const verifiedResult = await fetch(url, config);
//     const result = await verifiedResult.text();
//     console.log("The result is " + result);
//     return verifiedResult;
// }

async function getIamportToken() {
    try {
        const url = "/payment/get-token";
        const config = {
            method: "get",
            headers: {
                'Content-Type': 'application/json; charset=utf-8'
            }
        };
        const response = await fetch(url, config);
        const result = await response.text();
        const iamportToken = JSON.parse(result);
        console.log("The token is: " + iamportToken.token);
        return iamportToken;
    } catch (error) {
        console.log(error);
    }
}

async function verifyPaymentInfo(imp_uid) {
    const url = `https://api.iamport.kr//payments/${imp_uid}`;
    const config = {
        headers: {
            'Content-Type': 'application/json'
        }
    };
    const response = await fetch(url, config);
    const result = await response.text();

}