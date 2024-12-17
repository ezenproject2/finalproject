let pgData = {
    channelKey: "",
    pg: "",
    pay_method: "",
    merchant_uid: "",
    name : "",
    amount : 0,
    notice_url: "",
    m_redirect_url: "http://localhost:8087"
};

document.querySelector('[data-pay-btn-container="payBtnContainer"]').addEventListener('click', (e) => {

    if(e.target.classList.contains('pay-btn')) {
        pgData.pg = selectPg(e.target.classList);
    }

    if(e.target.id == 'orderBtn') {
        if(pgData.pg == "") {
            console.log('결제 수단을 선택해주세요.');
        } else {
            // getPayDataFromServer(pgData.pg);

            payWithIamport(pgData);
            // pgData 초기화
            pgData = {
                channelKey: "",
                pg: "",
                pay_method: "",
                merchant_uid: "",
                name : "",
                amount : 0,
                notice_url: "",
                m_redirect_url: "http://localhost:8087"
            };
        }
    }
})


function selectPg(targetClassList) {
    
    let pgVal = "";

    if(targetClassList.contains('credit-card-btn')) {
        pgVal = "페이팔, pg 상점 아이디 필요";
    } else if (targetClassList.contains('phone-btn')) {
        pgVal = "danal";
    } else if (targetClassList.contains('naverpay-btn')) {
        pgVal = "네이버, pg 상점 아이디 필요";
    } else if (targetClassList.contains('kakaopay-btn')) {
        pgVal = "kakaopay";
    } else if (targetClassList.contains('tosspay-btn')) {
        pgVal = "tosspay_v2";
    } else {
        console.log(`It's not a valid button.`);
    }
    
    console.log("selected pg: " + pgVal);
    return pgVal;
}


async function getPayDataFromServer(selectedPg) {
    const url = `/payment/payout/get`
}


async function payWithIamport(pgData) {
    // TODO: 결제 진행 시 서버에서 uuid 생성할 것.
    // 현재 해당 기능이 구현되어 있지 않아서 여기서 생성 중.
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
        m_redirect_url: "http://localhost:8087" // 모바일 전용 리디렉트 프로퍼티. 결제 완료 페이지로 payment/complete를 만들어야 할 듯?
    }

    IMP.init('imp73014361'); // iamport_customer_verification_code의 값이다.
    IMP.request_pay(paymentData, function (impResponse) {
        console.log("Payment completed.");
        if(impResponse.success) {
            console.log("Start payment information verification.");
            // 결제 데이터를 DB에 저장하기 전에 검증 먼저 실행.
            // 결제 검증 1. 결제할 액수와 결제 결과로 반환된 결제 액수 동등성 비교.
            const verifyOneResult = comparePayment(paymentData.amount, impResponse.paid_amount);
            // 결제 검증 2. 포트원의 "결제 단건 조회" API를 통해 imp_uid와 액수 비교.
            let verifyTwoResult = false;
            if(verifyOneResult) {
                verifyTwoResult = sendPaymentResultToServer(impResponse);
            }
            // 모든 검증 후 DB에 결제 자료 입력
            preservePaymentInfoToServer(impResponse);
        } else if (impResponse.error_code != null) {
            alert("Fail -> code(" + impResponse.error_code + ") / Message(" + impResponse.error_msg + ")");
        }
    });
}


function comparePayment (supposeAmount, actualAmount) {
    if(supposeAmount == actualAmount) {
        console.log("Verification 1: Pass");
        return true;
    } else if (supposeAmount != actualAmount) {
        console.log("Verification 1: Fail");
        return false;
    } else {
        console.log("Verification 1: Unknown");
        return null;
    }
}

async function sendPaymentResultToServer(impResponse) {
    console.log("sendPaymentResultToServer operating.")

    const url = `/payment/payout/result`;
    const config = {
        method: "POST",
        headers: { "Content-Type": "application/json; charset=utf-8" },
        body: JSON.stringify({
            imp_uid: impResponse.imp_uid,
            paid_amount: impResponse.paid_amount
        })
    };

    const response = await fetch(url, config);
    const result = await response.text();
    if(result == "1") {
        console.log("Verification 2: Pass");
        return true;
    } else if (result == "0") {
        console.log("Verification 2: Fail");
        return false;
    } else {
        console.log("Verification 2: Unknown");
        return null;
    }
}


// 여기서부터 데이터를 DB에 저장하는 함수
async function preservePaymentInfoToServer(impResponse) {
    const url = `/payment/payout/preserve`;
    const sendData = {
        // TODO: orno는 주문에서 받아와야 해서 임시로 1로 주었음.
        orno: 1,
        measure: impResponse.pay_method,
        price: impResponse.paid_amount,
        status: "",
        // DB의 reg_at은 mapper에서 now()로 설정할 예정.
        cardName: impResponse.card_name,
        impUid: impResponse.imp_uid,
        uuid: impResponse.merchant_uid
    };
    sendData.status = selectStatus(impResponse.status);
    const config = {
        method: "POST",
        headers: { "Content-Type": "application/json; charset=utf-8" },
        body: JSON.stringify(sendData)
    };

    const response = await fetch(url, config);
    const result = await response.text();
    console.log("The result: " + result);
}

// impResponse의 status에 따라 DB에 저장할 status의 값을 결정함
function selectStatus(impResponseStatus) {
    let status = "";
    switch(impResponseStatus) {
        case "ready":
            status = "pending";
            break;
        case "failed":
            status = "failed";
            break;
        case "paid":
            status = "completed";
            break;
        default:
            status = "unknown";
            break;
    }
    return status;
}