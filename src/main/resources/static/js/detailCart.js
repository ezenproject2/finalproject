console.log("detailCart.js recognized.");

document.addEventListener('click', (e) => {
    let targetClassList = e.target.classList;
    let pathString = "";

    // 장바구니, 바로구매, 바로픽업 버튼 클릭 시 로그인이 되어 있지 않으면 로그인 페이지로 보냄
    const mnoVal = document.getElementById('cartMno').value;
    if(
        (['shopping_basket_btn', 'purchase_btn', 'pick_up_btn'].some(className => targetClassList.contains(className)))
        && mnoVal == "-1") {
        window.location.href = "/member/login";
        return;
    }

    const prnoVal = document.getElementById('prnoEl').value;
    const bookQtyVal = document.getElementById('number').innerText;

    if(targetClassList.contains('shopping_basket_btn')) { // 장바구니 버튼 클릭 시
        storeCartVoToServer(mnoVal, prnoVal, bookQtyVal);

    } else if (targetClassList.contains('purchase_btn')) { // 바로구매 버튼 클릭 시
        pathString = "buyNow";
        processSinglePurchase(mnoVal, prnoVal, bookQtyVal, pathString);

    } else if (targetClassList.contains('pick_up_btn')) { // 바로픽업 버튼 클릭 시
        pathString = "pickUpNow";
        processSinglePurchase(mnoVal, prnoVal, bookQtyVal, pathString);
    }
})

// "장바구니" 버튼에 이벤트를 부여하여 /payment/cart로 이동.
// document.querySelector('.shopping_basket_btn').addEventListener('click', () => {
//     // CartVO: mno, prno, bookQty

//     const mno = document.getElementById('cartMno').value;
//     console.log("mno: " + mno);

//     const prno = document.getElementById('prnoEl').value;
//     console.log("prno: " + prno);

//     const bookQty = document.getElementById('number').innerText;
//     console.log("bookQty: " + bookQty);
// });

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
async function processSinglePurchase(mnoVal, prnoVal, bookQtyVal, pathString) {

    if(pathString == "buyNow") {
        await prepareSinglePurchase(mnoVal, prnoVal, bookQtyVal);
        window.location.href = "/payment/payout/0";

    } else if (pathString == "pickUpNow") {
        await prepareSinglePurchase(mnoVal, prnoVal, bookQtyVal);
        window.location.href = "/payment/pickUp";

    }
}

// 서버에 cart의 정보를 전달하여 Cart에 데이터를 입력하도록 요청함
async function prepareSinglePurchase(mnoVal, prnoVal, bookQtyVal) {
    const url = `/payment/single-purchase`;

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
        console.log("Prepare single purchase: Succeeded.");
    } else if (result == "0") {
        console.log("Prepare single purchase: Failed.");
    } else {
        console.log("Prepare single purchase: Unknown.");
    }
}