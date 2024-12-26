console.log("paymentPayoutBusiness.js recognized.");

// 결제 api에 쓰일 파라미터를 보관하는 전역 객체
// pg: payment gateway. 결제 대행사라고 생각하면 됨. toss, naver pay 등.
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

// 배송지 등록 버튼에 클릭 이벤트 부여.
document.addEventListener('click', (e) => {

    if(e.target.id == "registerAddrBtn") {
        // 배송지 정보를 모두 채웠는지 확인
        const addrInputs = document.querySelectorAll('.address-input');
        let isAllFilled = true;
        // console.log(addrInputs);

        addrInputs.forEach(input => {
            // console.log("The input: ");
            // console.log(input);
            if(!input.classList.contains('address-name') && input.value.trim() === '') {
                isAllFilled = false;
            } 
        })

        console.log("isAllFilled: ", isAllFilled);

        if (isAllFilled) {
            registerDefaultAddrFlag();
        } else {
            alert("배송지 이름을 제외한 모든 배송지 정보를 채워주세요.");
        }
    }
})

// 모든 pay-btn 클래스가 있는 버튼들에 이벤트 부여. pgData의 pg를 설정함.
const payBtnGroup = document.querySelectorAll('.pay-btn');
payBtnGroup.forEach(payBtn => {
    payBtn.addEventListener('click', () => {
        pgData.pg = selectPg(payBtn.classList);
        console.log("The selected pg is: " + pgData.pg);
    })
})

// pay-btn 클래스가 있는 버튼 아래의 span들에 이벤트 부여. pgData의 pg를 설정함.
const spanUnderPayBtn = document.querySelectorAll('.pay-btn span');
spanUnderPayBtn.forEach(span => {
    span.addEventListener('click', () => {
        let payBtnClassList = span.parentElement.classList;
        pgData.pg = selectPg(payBtnClassList);
        console.log("The selected pg is: " + pgData.pg);
    })
})

// 주문 버튼 클릭 시 pg를 골랐는지 + 기본 배송지가 null인지 판별, 조건 통과 시 결제 진행
// 픽업을 가리는 조건도 추가됨.
document.getElementById('orderBtn').addEventListener('click', () => {
    const isDefaultAddrNull = document.getElementById('dataContainer').dataset.isDefaultAddrNull;
    const isPickup = document.getElementById('dataContainer').dataset.isPickup;


    if(pgData.pg == "") {
        alert('결제 수단을 선택해주세요.');
    } else if (isPickup == "N" && isDefaultAddrNull == "true") {
        alert("기본 배송지를 입력해주세요.");
    } else {
        const pgObj = getPayDataFromServer(pgData.pg);
        pgData.channelKey = pgObj.channelKey;
        pgData.pay_method = pgObj.payMethod;
        // pgData.merchant_uid = pgObj.merchantUid;
        pgData.merchant_uid = document.querySelector('.list-data-storage').dataset.merchantUid;
        console.log("The merchant uid: ", document.querySelector('.list-data-storage').dataset.merchantUid);

        pgData.amount = getTotalPrice();
        console.log("The total amount is: " + pgData.amount);

        payWithIamport(pgObj);
        // 결제 완료 후 pgData 초기화
        pgData = {
            channelKey: "",
            pg: "",
            pay_method: "",
            merchant_uid: "",
            name : "",
            amount : 0
        };
    }
})


// pay-btn을 누르면 그에 따라 pdData의 pg를 설정함
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

// 화면에 표시된 총 액수를 가져옴
function getTotalPrice() {
    const totalPrice = document.querySelector('.total-price').dataset.totalPrice;

    return parseInt(totalPrice);
}

// 사용자의 기본 배송지를 서버에 등록함
async function registerDefaultAddrToServer(mnoVal) {
    const postcodeVal = document.getElementById('postcode').value;
    const addrVal = document.getElementById('homeAddress').value;
    const detailAddrVal = document.getElementById('detailAddress').value;
    const addrNameVal = document.getElementById('addressName').value;
    const recNameVal = document.getElementById('recName').value;
    const recPhoneVal = document.getElementById('recPhone').value;

    const url = "/payment/payout/register-address";

    const addrData = {
        mno: mnoVal,
        addrCode: postcodeVal,
        addr: addrVal,
        addrDetail: detailAddrVal,
        addrName: addrNameVal,
        recName: recNameVal,
        recPhone: recPhoneVal
    };

    const config = {
        method: "POST",
        headers: { "Content-Type": "application/json; charset=utf-8" },
        body: JSON.stringify(addrData)
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

// 서버에 기본 배송지를 등록한 후에 페이지 변경이 이뤄지도록 하는 함수.
async function registerDefaultAddrFlag() {
    const mnoVal = document.getElementById('dataContainer').dataset.mno;

    // 기본 배송지 서버에 저장하는 비동기 함수
    await registerDefaultAddrToServer(mnoVal);
    // /payment/payout으로 이동하기 전에 cartList를 준비하도록 요청하는 비동기 함수
    // await prepareCartListToServer(mnoVal);
    window.location.href = "/payment/payout/0";
    
}

// 서버에 mno를 보내 cartList를 생성할 수 있도록 하는 함수.
// async function prepareCartListToServer(mnoVal) {
//     const url = "/payment/prepare-cart-list";

//     const config = {
//         method: "post",
//         headers: { "Content-Type": "application/json; charset=utf-8" },
//         body: JSON.stringify(mnoVal)
//     }

//     const response = await fetch(url, config);
//     const result = await response.text();

//     if(result == "1") {
//         console.log("Preserve orders: Succeeded.");
//     } else if (result == "0") {
//         console.log("Preserve orders: Failed.");
//     } else {
//         console.log("Preserve orders: Unknown.");
//     }
// }

// 결제 수단이 있으면 서버로부터 channelKey, payMethod, merchantUid를 가져옴
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

// 결제 요청 api 사용
async function payWithIamport() {

    // 결제 api 사용 시 필요한 파라미터들
    const paymentData = {
        channelKey: pgData.channelKey,
        pg: pgData.pg, // pg provider
        pay_method: pgData.pay_method,
        merchant_uid: pgData.merchant_uid,
        name : "이젠문고 주문",
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

            let verifyOneResult = false;
            let verifyTwoResult = false;

            // let verifyResult = checkFlags(paymentData.amount, impResponse.paid_amount, impResponse);
            checkFlags(paymentData.amount, impResponse.paid_amount, impResponse);

        (impResponse);
        } else if (impResponse.error_code != null) {
            alert("Fail -> code(" + impResponse.error_code + ") / Message(" + impResponse.error_msg + ")");
        }
    });
}

// 결제 api를 성공적으로 수행했을 시 DB에 데이터 반영
async function checkFlags(paymentDataAmount, impResPaidAmount, impResponse) {

    const result = {
        verify1: false,
        verify2: false
    }

    // 결제 데이터를 DB에 저장하기 전에 검증 먼저 실행.
    // 결제 검증 1. 결제할 액수와 결제 결과로 반환된 결제 액수 동등성 비교.
    result.verify1 = await comparePayment(paymentDataAmount, impResPaidAmount);
    // 결제 검증 2. 포트원의 "결제 단건 조회" API를 통해 imp_uid와 액수 비교.
    result.verify2 = await sendPaymentResultToServer(impResponse);
    
    if(result.verify1 && result.verify2) {
        await preserveOrdersToServer(impResponse);
        await preserveOrderDetailToServer(impResponse);
        await preservePaymentToServer(impResponse);
        await removeCartToServer();

        // 픽업이면 pickupVO를 저장, 아니면 delivery에 저장.
        const isPickup = document.getElementById('dataContainer').dataset.isPickup;
        if(isPickup == "Y") {
            await preservePickupToServer();
        } else {
            await preserveDeliveryToServer(impResponse);   
        }
        alert("결제가 완료되었습니다.");
        window.location.href = "/payment/go-to-index";
    }

    return result;
}

// 1차 검증: api에서 가져온 금액과 결제해야 했을 금액 비교
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

// 2차 검증: 결제 데이터를 서버로 보내 "단건 조회" api로 결제 금액 다시 대조
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

// orders 테이블에 주문 정보 저장
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

// order_detail 테이블에 주문 상세 데이터 저장
async function preserveOrderDetailToServer(impResponse) {
    const url = `/payment/payout/preserve-order-detail`;

    const orderDetailArr = [];
    let index = document.querySelector('[data-list-total="listTotal"]').textContent;
    
    for(let i=0; i < parseInt(index); i++) {
        let orderDetail = {
            orno: impResponse.merchant_uid,
            prno: "",
            bookQty: "",
            price: "",
            status: ""
        }

        // orderDetail.prno = document.querySelector(`[data-list-book-prno="${i}"]`).value;
        // orderDetail.bookQty = document.querySelector(`[data-list-book-qty="${i}"]`).innerText;
        // orderDetail.price = document.querySelector(`[data-list-book-price="${i}"]`).innerText;
        orderDetail.prno = document.querySelector(`[data-payout="${i}"].list-data-storage`).dataset.listBookPrno;
        orderDetail.bookQty = document.querySelector(`[data-payout="${i}"].book-qty`).innerText;
        let bookPriceVal = document.querySelector(`[data-payout="${i}"].book-price`).dataset.bookOriginalPrice;
        
        orderDetail.status = selectStatus(impResponse.status);

        // bookPriceVal이 숫자+원 임. "원"을 제거하고 숫자만 추출한 후에  문자열을 int로 전환.
        let onlyPriceVal = bookPriceVal.match(/\d+/);
        orderDetail.price = parseInt(onlyPriceVal);
        
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

// payment 테이블에 결제 데이터 저장
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

// cart 테이블에서 결제된 주문 내역 삭제
async function removeCartToServer() {
    const mnoVal = document.getElementById('dataContainer').dataset.mno;
    
    // 주문한 prno를 다 받아서 CartVO의 JSON으로 만들 것.
    const index = document.querySelector(`.list-data-storage`).dataset.listSize;
    const cartVoArr = [];

    for(let i = 0; i < parseInt(index); i++) {
        let cartVoObj = {
            mno: mnoVal,
            prno:""
        };

        cartVoObj.prno = document.querySelector(`[data-payout="${i}"].list-data-storage`).dataset.listBookPrno;

        // console.log("The cartVoObj: ");
        // console.log(cartVoObj);

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

// 결제가 끝나면 delivery 테이블에 배송지 정보를 저장함.
async function preserveDeliveryToServer(impResponse) {

    const recNameVal = document.querySelector('.default-addr-rec-name').innerText;
    const recPhoneVal = document.querySelector('.default-addr-rec-phone').innerText;
    const addrVal = document.querySelector('.address-info span').innerText;
    const addrCodeVal = document.querySelector('.address-info').dataset.addrPostcode;
    const addrDetailVal = document.querySelector('.address-info').dataset.addrDetail;

    const url = "/payment/payout/preserve-delivery";

    const deliveryData = {
        orno: impResponse.merchant_uid,
        recName: recNameVal,
        recPhone: recPhoneVal,
        addrCode: addrVal,
        addr: addrCodeVal,
        addrDetail: addrDetailVal,
        addrMemo: ""
    }

    deliveryData.addrMemo = getDeliveryMemo();

    const config = {
        method: "POST",
        headers: { "Content-Type": "application/json; charset=utf-8" },
        body: JSON.stringify(deliveryData)
    };
    
    const response = await fetch(url, config);
    const result = await response.text();
    if(result == "1") {
        console.log("Preserve delivery: Succeeded.");
    } else if (result == "0") {
        console.log("Preserve delivery: Failed.");
    } else {
        console.log("Preserve delivery: Unknown.");
    }
    
}

// 화면으로부터 배송 메모를 가져오는 함수.
function getDeliveryMemo() {
    const deliverySelect = document.getElementById('Request').value;
    let deliveryMemoVal = "";

    switch (deliverySelect) {
        case "문 앞에 놓아주세요.":
            deliveryMemoVal = "문 앞에 놓아주세요.";
            break;
        case "부재 시 경비실에 맡겨 주세요.":
            deliveryMemoVal = "부재 시 경비실에 맡겨 주세요.";
            break;
        case "부재 시 연락 주세요.":
            deliveryMemoVal = "부재 시 연락 주세요.";
            break;
        case "배송 전 연락 주세요.":
            deliveryMemoVal = "배송 전 연락 주세요.";
            break;
        case "직접 입력":
            const userMemo = document.querySelector('.shipping_request_text #userDeliveryMemo').value;
            deliveryMemoVal = userMemo;
            break;
        default:
            deliveryMemoVal = "메모 없음";
            break;
    }

    return deliveryMemoVal;
}

async function preservePickupToServer() {
    const osnoVal = document.querySelector('.pickup_table').dataset.pickupOsno;
    const statusVal = document.querySelector('.pickup_table').dataset.pickupStatus;
    const ornoVal = document.querySelector('.pickup_table').dataset.pickupOrno;

    const url = "/payment/payout/preserve-pickup";

    const config = {
        method: "POST",
        headers: { "Content-Type": "application/json; charset=utf-8" },
        body: JSON.stringify({
            osno: osnoVal,
            status: statusVal,
            orno: ornoVal
        })
    };
    
    const response = await fetch(url, config);
    const result = await response.text();
    if(result == "1") {
        console.log("Preserve pickup: Succeeded.");
    } else if (result == "0") {
        console.log("Preserve pickup: Failed.");
    } else {
        console.log("Preserve pickup: Unknown.");
    }
}