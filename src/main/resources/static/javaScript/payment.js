let pg = "";
let payMethod = "";

document.addEventListener('click', (e) => {
    console.log(e.target);
    let target = e.target;

    if(target.classList.contains('pay-btn')) {
        console.log("pay-btn clicked!");
        identifyPayBtn(target);
    }
})

function identifyPayBtn(target) {
    console.log(target);
}


async function payment() {
    IMP.init('imp73014361');
    IMP.request_pay(
        {
            channelKey : "channel-key-a95bda2d-2a2f-43fb-8e53-7ba806a637f4",
            pg: "kakaopay",
            pay_method: "card",
            merchant_uid : "ORD20241206-0000011",
            name : "Amusing Ourselves to Death",
            amount : 100,
            buyer_email : "qpfm111@naver.com",
            buyer_name : "박준희",
            buyer_tel : "01071882723",
            // 웹훅 설정을 위한 url
            notice_url: "http://localhost:8087/payment/notice"
        },
        (response) => {
            if(response.success) {
                alert("Success -> imp_uid : " + response.imp_uid + "/ merchant_uid(orderKey): " + response.merchant_uid);
                const notified = getPaymentResult;
            } else if(response.error_code != null) {
                alert("Fail -> code(" + response.error_code + ") / Message(" + response.error_msg + ")");
            }
        }
    );
}

async function getPaymentResult() {
    const url = `http://localhost:8087/payment/complete`;
    const config = {
        method: "POST",
        headers: { "Content-Type": "application/json"},
        // imp_uid와 merchant_uid, 주문 정보를 서버에 전달
        body: JSON.stringify({
            imp_uid: response.imp_uid,
            merchant_uid: response.merchant_uid
        })
    };

    const notified = await fetch(url, config);
    return notified;
}