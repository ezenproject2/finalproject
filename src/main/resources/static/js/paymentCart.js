console.log("cart.js recognized.");

document.addEventListener('DOMContentLoaded', () => {
    const isCartEmpty = document.getElementById('dataContainer').dataset.isCartEmpty;
    const isCartEmptyBool = (isCartEmpty === "true");
    console.log("Is the cart empty: " + isCartEmptyBool);

    if(isCartEmptyBool) {
        // TODO: 장바구니가 비었을 때의 레이아웃을 띄울 것.
        document.querySelector('.cart_list_table').style = 'display: none';
        document.querySelector('.cart_list_none').style = "display: block";
        document.getElementById('orderBtn').disabled = true;
        document.getElementById('pickupBtn').disabled = true;
    } else {
        // 레이아웃 설정
        document.querySelector('.cart_list_table').style = "display: block";
        document.querySelector('.cart_list_none').style = "display: none";
        document.getElementById('orderBtn').disabled = false;
        document.getElementById('pickupBtn').disabled = false;

        // 화면 로딩 시 페이지에 보여질 값들 계산
        calculateQtyPrice();
        calculateReceipt();

        // 모든 itemBtn이 클릭되있게 함
        const selectItemBtns = document.getElementsByName('itemBtn');
    
        selectItemBtns.forEach(itemBtn => {
            itemBtn.checked = true;
        });
    }
})

function calculateQtyPrice() {
    console.log("calculateQtyPrice start.");

    const index = document.getElementById('listDataStorage').dataset.listSize;
    const numIndex = parseInt(index);

    for (let i=0; i < numIndex; i++) {
        let salePrice = document.querySelector(`span[data-cart="${i}"].sale-price`).innerText;
        let bookQty = document.querySelector(`span[data-cart="${i}"].book-qty`).innerText;

        let qtyPrice = parseInt(salePrice) * parseInt(bookQty);

        document.querySelector(`strong[data-cart="${i}"].qty-price`).innerText = qtyPrice.toString();
    }

}

function calculateReceipt() {
    // NOTE: 임시로 배송비는 3,000원 적용, 포인트는 일괄적으로 300P 적용.
    const index = document.getElementById('listDataStorage').dataset.listSize;

    let sumOriginalPrice = 0;
    let sumSalePrice = 0;

    for (let i = 0; i < index; i++) {
        let originalPrice = document.querySelector(`span[data-cart="${i}"].original-price`).innerText;
        let salePrice = document.querySelector(`span[data-cart="${i}"].sale-price`).innerText;
        let bookQty = document.querySelector(`[data-cart="${i}"].book-qty`).innerText;

        sumOriginalPrice += parseInt(originalPrice) * parseInt(bookQty);
        sumSalePrice += parseInt(salePrice) * parseInt(bookQty);
    }

    document.querySelector(`.total-original-price`).innerText = sumOriginalPrice;
    document.querySelector(`.total-discount-price`).innerText = "- " + `${sumOriginalPrice - sumSalePrice}`;

    let deliverFeeEle = document.querySelector(`.delivery-fee`);
    deliverFeeEle.innerText = 3000;

    document.querySelector(`.estimated-payment-amount`).innerText = (sumSalePrice + parseInt(deliverFeeEle.innerText));
    document.querySelector(`.estimated-point-amount`).innerText = 300;
}

// 재고 + - 버튼에 맞춰 가격 변동
const ascBtns = document.querySelectorAll('.asc-btn');
ascBtns.forEach(ascBtn => {
    ascBtn.addEventListener('click', () => {
        // 어느 order detail의 올리기 버튼인지 찾기
        let index = ascBtn.dataset.cart;
        console.log("asc index: " + index);
        
        // 도서 개수 +1
        let bookQty = document.querySelector(`span.book-qty[data-cart="${index}"]`);
        let bookQtyVal = parseInt(bookQty.innerText);
        bookQty.innerText = bookQtyVal + 1;

        // 올라간 수량을 반영한 가격 띄우기
        calculateQtyPrice();
        calculateReceipt();
    })
});

const descBtns = document.querySelectorAll('.desc-btn');
descBtns.forEach(descBtn => {
    descBtn.addEventListener('click', () => {
        // 어느 order detail의 내리기 버튼인지 찾기
        let index = descBtn.dataset.cart;
        console.log("desc index: " + index);

        // 도서 개수 -1
        let bookQty = document.querySelector(`span.book-qty[data-cart="${index}"]`);
        let bookQtyVal = parseInt(bookQty.innerText);

        if((bookQtyVal - 1) == 0) {
            bookQty.innerText == 1;
        } else {
            bookQty.innerText = bookQtyVal - 1;
        }

        // 내려간 수량을 반영한 가격 띄우기
        calculateQtyPrice();
        calculateReceipt();
    })
})

// #allItemBtn을 누르면 모두 선택됨
document.getElementById('allItemBtn').addEventListener('click', (e) => {
    console.log("e.target.checked: " + e.target.checked);

    const selectItemBtns = document.getElementsByName('itemBtn');

    selectItemBtns.forEach((itemBtn) => {
        itemBtn.checked = e.target.checked;
    });
});

// 모든 <input name="itemBtn">이 checked = false면 주문 버튼 비활성화
const itemBtns = document.querySelectorAll('input[name="itemBtn"]');
const singleItemBtns = document.querySelectorAll('input[name="itemBtn"].single-item-btn');
itemBtns.forEach(itemBtn => {

    itemBtn.addEventListener('click', () => {

        let areAllUnchecked = Array.from(singleItemBtns).every(singleBtn => !singleBtn.checked);
        if(areAllUnchecked) {
            document.getElementById('orderBtn').disabled = true;
        } else {
            document.getElementById('orderBtn').disabled = false;
        }
    })
});

// 주문 버튼 누르면 CartDTO와 매핑할 JSON 생성
document.getElementById('orderBtn').addEventListener('click', () => {
    console.log("orderBtn clicked.");
    let cartDtoArray = createCartDtoArray();
    console.log(" >>> CartDtoArray: " + cartDtoArray);

    sendCartVoArrayToServer(cartDtoArray);
});

// 클릭된 class="single-item-btn"들을 선택
function createCartDtoArray() {
    const singleItemBtns = document.querySelectorAll('input[type="checkbox"].single-item-btn:checked');
    const cartDtoArray = [];

    if(singleItemBtns == null) {
    console.log("The singleItemBtns is null.");
    } else {
        singleItemBtns.forEach(singleBtn => {
            const dataCart = singleBtn.dataset.cart;
            console.log("The value of data-cart: " + dataCart);
            let cartVoJson = getDataForCartDto(dataCart);
            cartDtoArray.push(cartVoJson);
        })
    }

    return cartDtoArray;
}

// single-item-btn의 data-cart 값을 바탕으로 mno, prno, bootQty 값 추출
function getDataForCartDto(singleItemBtnDataCart) {
    const cartVoJson = {
        mno:"",
        prno:"",
        bookQty:""
    };

    const mnoVal = document.querySelector(`input[data-cart="${singleItemBtnDataCart}"].mno`).value;
    const prnoVal = document.querySelector(`input[data-cart="${singleItemBtnDataCart}"].prno`).value;
    const bookQtyVal = document.querySelector(`span[data-cart="${singleItemBtnDataCart}"].book-qty`).innerText;

    // console.log("The mno of <input type='hidden' class='mno'>: " + mnoVal);
    // console.log("The prno of <input type='hidden' class='prno'>: " + prnoVal);
    // console.log("The bookQty of <input type='text' class='bookQty'>: " + bookQtyVal);

    cartVoJson.mno = mnoVal;
    cartVoJson.prno = prnoVal;
    cartVoJson.bookQty = bookQtyVal;

    return cartVoJson;
}

async function sendCartVoArrayToServer(cartDtoArray) {
    console.log(" >>> sendCartVoArrayToServer start.");
    const url = '/payment/get-cart-list';
    const config = {
        method: "post",
        headers: { "Content-Type": "application/json; charset=utf-8" },
        body: JSON.stringify(cartDtoArray)
    }

    const result = await fetch(url, config);
    const textResult = await result.text();
    if(textResult == "1") {
        console.log("sendCartVoArrayToServer: Succeeded.");
        window.location.href = "/payment/payout"; // 이게 GET요청을 보내는 거라고 함.
    } else {
        console.log("sendCartVoArrayToServer: Failed.");
    }
}