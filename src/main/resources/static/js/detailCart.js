console.log("detailCart.js recognized.");

// "장바구니" 버튼에 이벤트를 부여하여 /payment/cart로 이동.
document.querySelector('.shopping_basket_btn').addEventListener('click', () => {
    // CartVO: mno, prno, bookQty

    // 최종 금액 가져오기
    // const totalPrice = document.getElementById('totalPrice');
    // const totalPriceVal = totalPrice.childNodes[0].nodeValue.trim();

    // console.log("The total price: ");
    // console.log(totalPriceVal);

    const mno = document.getElementById('cartMno').value;
    console.log("mno: " + mno);

    const prno = document.getElementById('prnoEl').value;
    console.log("prno: " + prno);

    const bookQty = document.getElementById('number').innerText;
    console.log("bookQty: " + bookQty);

    if(mno == "-1") {
        alert("로그인 먼저 해주세요.");
    } else {
        storeCartVoToServer(mno, prno, bookQty);
    }
});

// "바로구매" 버튼에 이벤트를 부여하여 /payment/payout으로 이동.
document.querySelector('.purchase_btn').addEventListener('click', () => {

    const mnoVal = document.getElementById('cartMno').value;

    if(mnoVal == "-1") {
        alert("로그인 먼저 해주세요.");
    } else {
        const prnoVal = document.getElementById('prnoEl').value;
        const bookQtyVal = document.getElementById('number').innerText;
    
        processBuyNow(mnoVal, prnoVal, bookQtyVal);
    }
})


async function storeCartVoToServer(mnoVal, prnoVal, bookQtyVal) {
    const url = "/payment/cart/insert";

    const cartData = {
        mno:mnoVal,
        prno: prnoVal,
        bookQty: bookQtyVal
    }

    const config = {
        method: "POST",
        headers: { "Content-Type": "application/json; charset=utf-8" },
        body: JSON.stringify(cartData)
    }

    const response = await fetch(url, config);
    const result = await response.text();

    if(result == "1") {
        console.log("Insert cart: Succeeded.");
        alert("장바구니에 담겼습니다.");
    } else if(result == "2") {
        alert("이미 해당 도서가 장바구니에 있어서 개수를 추가하였습니다.");
    } else if (result == "0") {
        console.log("Insert cart: Failed.");
        alert("서버 에러: 실패했습니다.");
    } else {
        console.log("Insert cart: Unknown.");
        alert("알 수 없는 오류: 실패했습니다.");
    }
}

// 
async function processBuyNow(mnoVal, prnoVal, bookQtyVal) {

    await prepareForBuyNow(mnoVal, prnoVal, bookQtyVal);
    window.location.href = "/payment/payout/0";
}

// 서버에 cart의 정보를 전달하여 Cart에 데이터를 입력하도록 요청함
async function prepareForBuyNow(mnoVal, prnoVal, bookQtyVal) {
    const url = "/payment/buy-now";

    const cartData = {
        mno: mnoVal,
        prno: prnoVal,
        bookQty: bookQtyVal
    }

    const config = {
        method: "POST",
        headers: { "Content-Type": "application/json; charset=utf-8" },
        body: JSON.stringify(cartData)
    }

    const response = await fetch(url, config);
    const result = await response.text();

    if(result == "1") {
        console.log("Prepare for buy-now: Succeeded.");
    } else if (result == "0") {
        console.log("Prepare for buy-now: Failed.");
    } else {
        console.log("Prepare for buy-now: Unknown.");
    }
}