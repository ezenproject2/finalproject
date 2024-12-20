let pgData = {
    channelKey: "",
    pg: "",
    pay_method: "",
    merchant_uid: "",
    name : "",
    amount : 0,
    notice_url: "http://localhost:8087/payment/verify-iamport/webhook",
    m_redirect_url: "http://localhost:8087"
};

document.querySelector('[data-pay-btn-container="payBtnContainer"]').addEventListener('click', (e) => {

    if(e.target.classList.contains('pay-btn')) {
        pgData.pg = selectPg(e.target.classList);
    }

    if(e.target.id == 'orderBtn') {
        if(pgData.pg == "") {
            alert('결제 수단을 선택해주세요.');
        } else {
            const pgObj = getPayDataFromServer(pgData.pg);
            pgData.channelKey = pgObj.channelKey;
            pgData.pay_method = pgObj.payMethod;
            pgData.merchant_uid = pgObj.merchantUid;

            pgData.amount = getTotalPrice();

            payWithIamport(pgObj);
            // pgData 초기화
            pgData = {
                channelKey: "",
                pg: "",
                pay_method: "",
                merchant_uid: "",
                name : "",
                amount : 0
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

function getTotalPrice() {
    const totalPrice = document.querySelector('[data-total-price="totalPrice"]').textContent;

    return parseInt(totalPrice);
}

async function getPayDataFromServer(selectedPg) {
    const url = `/payment/payout/prepare`;

    const config = {
        method: "post",
        headers: { "Content-Type": "application/json; charset=utf-8" },
        body: JSON.stringify({
            pg: selectedPg
        })
    }

    const response = await fetch(url, config);
    const result = await response.text();
    console.log("The result of getPayDataFromServer" + result);

    const pgDataObj = JSON.parse(result);
    return pgDataObj;
}

async function payWithIamport() {

    // 결제 api 사용 시 필요한 파라미터들
    const paymentData = {
        channelKey: pgData.channelKey,
        pg: pgData.pg, // pg provider
        pay_method: pgData.pay_method,
        merchant_uid: pgData.merchant_uid,
        name : "주문명: 결제 테스트",
        amount : pgData.amount,
        buyer_email : "qpfm111@naver.com",
        buyer_name : "박준희",
        buyer_tel : "01071882723",
        notice_url: pgData.notice_url,
        m_redirect_url: pgData.m_redirect_url
    }

    IMP.init('imp73014361'); // iamport_customer_verification_code의 값이다.
    IMP.request_pay(paymentData, function (impResponse) {
        console.log("Payment completed.");
        if(impResponse.success) {
            console.log("The result of the payment: " + JSON.stringify(impResponse));
            console.log("Start payment information verification.");
            // 결제 데이터를 DB에 저장하기 전에 검증 먼저 실행.
            // 결제 검증 1. 결제할 액수와 결제 결과로 반환된 결제 액수 동등성 비교.
            let verifyOneResult = false;
            // 결제 검증 2. 포트원의 "결제 단건 조회" API를 통해 imp_uid와 액수 비교.
            let verifyTwoResult = false;

            let verifyResult = checkFlags(paymentData.amount, impResponse.paid_amount, impResponse);

        (impResponse);
        } else if (impResponse.error_code != null) {
            alert("Fail -> code(" + impResponse.error_code + ") / Message(" + impResponse.error_msg + ")");
        }
    });
}

async function checkFlags(paymentDataAmount, impResPaidAmount, impResponse) {

    const result = {
        verify1: false,
        verify2: false
    }

    result.verify1 = await comparePayment(paymentDataAmount, impResPaidAmount);
    result.verify2 = await sendPaymentResultToServer(impResponse);
    
    if(result.verify1 && result.verify2) {
        await preserveOrdersToServer(impResponse);
        await preserveOrderDetailToServer(impResponse.merchant_uid);
        await preservePaymentToServer(impResponse);
        await removeCartToServer();
    }

    return result;
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

async function preserveOrdersToServer(impResponse) {
    const url = `/payment/payout/preserve-orders`;

    const mnoVal = document.getElementById('dataContainer').dataset.mno;
    const isPickupVal = document.getElementById('dataContainer').dataset.isPickup;

    const reqBody = {
        orno: impResponse.merchant_uid,
        mno: mnoVal,
        status: "",
        totalPrice: impResponse.paid_amount,
        isPickup: isPickupVal
    }
    reqBody.status = selectStatus(impResponse.status);

    const config = {
        method: "POST",
        headers: { "Content-Type": "application/json; charset=utf-8" },
        body: JSON.stringify(reqBody)
    }

    const response = await fetch(url, config);
    const result = await response.text();

    if(result == "1") {
        console.log("Preserve orders: Succeeded.");
    } else if (result == "0") {
        console.log("Preserve orders: Failed.");
    } else {
        console.log("Preserve orders: Unknown.");
    }
}

async function preserveOrderDetailToServer(respMerchantUid) {
    const url = `/payment/payout/preserve-order-detail`;

    const orderDetailArr = [];
    let index = document.querySelector('[data-list-total="listTotal"]').textContent;
    
    for(let i=0; i < parseInt(index); i++) {
        let orderDetail = {
            orno: respMerchantUid,
            prno: "",
            bookQty: "",
            price: ""
        }

        orderDetail.prno = document.querySelector(`[data-list-book-prno="${i}"]`).value;
        orderDetail.bookQty = document.querySelector(`[data-list-book-qty="${i}"]`).innerText;
        orderDetail.price = document.querySelector(`[data-list-book-price="${i}"]`).innerText;
        
        orderDetailArr.push(orderDetail);
    }

    const config = {
        method: "POST",
        headers: { "Content-Type": "application/json; charset=utf-8" },
        body: JSON.stringify(orderDetailArr)
    }

    const response = await fetch(url, config);
    const result = await response.text();
    if(result == "1") {
        console.log("Preserve order detail: Succeeded.");
    } else if (result == "0") {
        console.log("Preserve order detail: Failed.");
    } else {
        console.log("Preserve order detail: Unknown.");
    }
}

async function preservePaymentToServer(impResponse) {
    const url = `/payment/payout/preserve-payment`;
    const paymentData = {
        orno: impResponse.merchant_uid,
        measure: impResponse.pay_method,
        price: impResponse.paid_amount,
        status: "",
        // DB의 reg_at은 mapper에서 now()로 설정할 예정.
        cardName: impResponse.card_name,
        impUid: impResponse.imp_uid
    };

    paymentData.status = selectStatus(impResponse.status);
    const config = {
        method: "POST",
        headers: { "Content-Type": "application/json; charset=utf-8" },
        body: JSON.stringify(paymentData)
    };

    const response = await fetch(url, config);
    const result = await response.text();
    if(result == "1") {
        console.log("Preserve payment: Succeeded.");
    } else if (result == "0") {
        console.log("Preserve payment: Failed.");
    } else {
        console.log("Preserve payment: Unknown.");
    }
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

async function removeCartToServer() {
    const mnoVal = document.getElementById('dataContainer').dataset.mno;
    
    // 주문한 prno를 다 받아서 CartVO의 JSON으로 만들 것.
    const index = document.querySelector(`input[type="hidden"].data-storage-input`).dataset.listSize;
    const cartVoArr = [];

    for(let i = 0; i < parseInt(index); i++) {
        let cartVoObj = {
            mno: mnoVal,
            prno:"",
            bookQty: -1 // TODO: NOT NULL 컬럼이라 혹시 몰라 보냄. 빼도 이상 없으면 뺄 예정.
        };

        cartVoObj.prno = document.querySelector(`input[type="hidden"][data-payout="${i}"].data-storage-input`).value;
        // let prno = document.querySelector(`input[type="hidden"][data-payout="${i}"].data-storage-input`).value;
        // console.log("prno " + prno);

        cartVoArr.push(cartVoObj);
    }

    const url = '/payment/payout/remove-cart';

    const config = {
        method: "POST",
        headers: { "Content-Type": "application/json; charset=utf-8" },
        body: JSON.stringify(cartVoArr)
    };
    
    const response = await fetch(url, config);
    const result = await response.text();
    if(result == "1") {
        console.log("Remove cart: Succeeded.");
    } else if (result == "0") {
        console.log("Remove cart: Failed.");
    } else {
        console.log("Remove cart: Unknown.");
    }
}