console.log("mypageOrderListBusiness.js recognized.");

// NOTE: 한꺼번에 환불하는 게 가능한 경우를 염두하고 짠 코드.
// 맨 위에 nav bar를 만들어서 거기에 환불하기 버튼을 뒀었음.
// document.querySelector('.refund-btn').addEventListener('click', () => {
//     let outerListSize = document.querySelector('.outer-data-container').dataset.outerListSize;

//     for (let outer=0; outer<outerListSize; outer++) {
//         let innerListSize = document.querySelector(`.cart_list[data-order-list="${i}"] .inner-data-container`).dataset.innerListSize;

//         for(let inner=0; inner<innerListSize; inner++) {
//             let singleItemBtn = document.querySelector(`.cart_list[data-order-list="${outer}"] .single-item-btn[data-order-detail="${inner}"]`);

//             if(singleItemBtn.checked == true) {
//                 //
//             }
//         }
//     }
// })

const refundBtns = document.querySelectorAll('.refund_btn');
refundBtns.forEach(refundBtn => {
    refundBtn.addEventListener('click', (e) => {
        let innerIndexVal = e.target.dataset.innerIndex;
        let outerIndexVal = e.target.closest('.inner-data-container').dataset.outerIndex;

        let odno = document.querySelector(`.inner-data-container[data-outer-index="${outerIndexVal}"] #odnoInput[data-inner-index="${innerIndexVal}"]`).value;
        let orno = document.querySelector(`.inner-data-container[data-outer-index="${outerIndexVal}"] #ornoInput[data-inner-index="${innerIndexVal}"]`).value;
        let qtyPrice= document.querySelector(`.inner-data-container[data-outer-index="${outerIndexVal}"] .order-detail-price[data-inner-index="${innerIndexVal}"]`).innerText.replace(/,/g, "");

        console.log("odno: " + odno);
        console.log("orno: " + orno);
        console.log("qtyPrice: " + qtyPrice);

        processRefund(odno, orno, qtyPrice);
    })
})

async function processRefund(odnoVal, ornoVal, qtyPriceVal) {

    await sendRefundDataToServer(odnoVal, ornoVal, qtyPriceVal);
    alert("환불이 완료되었습니다.");
    window.location.href = "/mypage/order-list";
}

async function sendRefundDataToServer(odnoVal, ornoVal, qtyPriceVal) {
    const url = '/mypage/order-list/refund';

    const refundData = {
        odno: odnoVal,
        orno: ornoVal,
        qtyPrice: qtyPriceVal
    };

    const config = {
        method: "POST",
        headers: { "Content-Type": "application/json; charset=utf-8" },
        body: JSON.stringify(refundData)
    };

    const response = await fetch(url, config);
    const result = await response.text();
    if(result == "1") {
        console.log("Send refund data to server: Succeeded.");
    } else if (result == "0") {
        console.log("Send refund data to server: Failed.");
    } else {
        console.log("Send refund data to server: Unknown.");
    }
}