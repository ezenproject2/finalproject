console.log("cart.js recognized.");

document.addEventListener('DOMContentLoaded', () => {
    const isCartEmpty = document.getElementById('dataContainer').dataset.isCartEmpty;
    const isCartEmptyBool = (isCartEmpty === "true");
    console.log("Is the cart empty: " + isCartEmptyBool);

    if(isCartEmptyBool) {
        // 장바구니가 비었을 때의 레이아웃 설정
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
        // calculateDeliveryFee();

        // 모든 itemBtn이 클릭되있게 함
        const selectItemBtns = document.getElementsByName('itemBtn');
    
        selectItemBtns.forEach(itemBtn => {
            itemBtn.checked = true;
        });
    }
})

// 수량 X 가격을 한 값 계산
function calculateQtyPrice() {
    console.log("calculateQtyPrice start.");

    const index = document.getElementById('listDataStorage').dataset.listSize;
    const numIndex = parseInt(index);

    for (let i=0; i < numIndex; i++) {
        let salePrice = document.querySelector(`span[data-cart="${i}"].sale-price`).dataset.salePrice;
        let bookQty = document.querySelector(`span[data-cart="${i}"].book-qty`).innerText;

        let qtyPrice = parseInt(salePrice) * parseInt(bookQty);

        document.querySelector(`strong[data-cart="${i}"].qty-price`).innerText = qtyPrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }

}

// 영수증에 들어갈 모든 값들 계산
function calculateReceipt() {
    // NOTE: 임시로 배송비는 3,000원 적용, 포인트는 일괄적으로 300P 적용.
    const index = document.getElementById('listDataStorage').dataset.listSize;

    let sumOriginalPrice = 0;
    let sumSalePrice = 0;

    for (let i = 0; i < index; i++) {
        let originalPrice = document.querySelector(`span[data-cart="${i}"].original-price`).dataset.originalPrice;
        let salePrice = document.querySelector(`span[data-cart="${i}"].sale-price`).dataset.salePrice;
        let bookQty = document.querySelector(`[data-cart="${i}"].book-qty`).innerText;

        sumOriginalPrice += parseInt(originalPrice) * parseInt(bookQty);
        sumSalePrice += parseInt(salePrice) * parseInt(bookQty);
    }

    // 전체 도서 원가 합계 데이터를 .total-original-price의 data-total-original-price에 저장
    let totalOriginalPriceEle = document.querySelector(`.total-original-price`);
    totalOriginalPriceEle.dataset.totalOriginalPrice = sumOriginalPrice;

    document.querySelector(`.total-original-price`).innerText = sumOriginalPrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    document.querySelector(`.total-discount-price`).innerText = "- " + `${sumOriginalPrice - sumSalePrice}`.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",") + "원";

    // 결제 예정 금액 설정
    let deliveryFee = 0;

    // 할인된 가격의 총합에 따라 배송비 설정
    if(20000 < sumSalePrice) {
        // 20,000 이상 결제 시 배송비 무료
    } else {
        deliveryFee = 3000;
    }

    // 배송비 설정 후 화면에 반영
    document.querySelector('.delivery-fee').dataset.deliveryFee = deliveryFee;
    document.querySelector('.delivery-fee').innerText = "+ " + deliveryFee.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",") + "원";

    // 배송비에 따라 최종 결제 예상 금액 설정
    document.querySelector(`.estimated-payment-amount`).innerText = `${sumSalePrice + deliveryFee}`.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    // NOTE: 포인트는 일괄적으로 300P 적용.
    document.querySelector(`.estimated-point-amount`).innerText = 300;
}

// function calculateDeliveryFee() {
//     let estimatedPriceVal = document.querySelector(`.estimated-payment-amount`).innerText;
    
//     // 금액에 찍힌 , 제거
//     estimatedPriceVal = estimatedPriceVal.replace(/,/g, "");

//     // 숫자로 변환
//     estimatedPriceVal = parseInt(estimatedPriceVal);

//     let deliveryFee = 0;
//     // 2만원 이상 구매 시 무료배송, 아니면 3천원 부과
//     if(20000 <= estimatedPriceVal) {
//     } else {
//         deliveryFee = 3000;
//     }

//     document.querySelector('.delivery-fee').dataset.deliveryFee = deliveryFee;
//     document.querySelector('.delivery-fee').innerText = "+ " + deliveryFee.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",") + "원";
// };

// 재고 + 버튼에 맞춰 가격 변동
const ascBtns = document.querySelectorAll('.asc-btn');
ascBtns.forEach(ascBtn => {
    ascBtn.addEventListener('click', () => {
        // 어느 order detail의 올리기 버튼인지 찾기
        let index = ascBtn.dataset.cart;
        console.log("asc index: " + index);
        
        // 도서 개수 +1
        let bookQty = document.querySelector(`.book-qty[data-cart="${index}"]`);
        let bookQtyVal = parseInt(bookQty.innerText);
        bookQty.innerText = bookQtyVal + 1;

        // 올라간 수량을 반영한 가격 띄우기
        calculateQtyPrice();
        calculateReceipt();
    })
});

// 재고 - 버튼에 맞춰 가격 변동
const descBtns = document.querySelectorAll('.desc-btn');
descBtns.forEach(descBtn => {
    descBtn.addEventListener('click', () => {
        // 어느 order detail의 내리기 버튼인지 찾기
        let index = descBtn.dataset.cart;
        console.log("desc index: " + index);

        // 도서 개수 -1
        let bookQty = document.querySelector(`.book-qty[data-cart="${index}"]`);
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

// x 아이콘을 누르면 장바구니 내역에서 삭제됨
document.addEventListener('click', (e) => {
    if(e.target.classList.contains('ic_delete')) {
        let index = e.target.dataset.cart;

        let mnoVal = document.querySelector(`.mno[data-cart="${index}"]`).value;
        let prnoVal = document.querySelector(`.prno[data-cart="${index}"]`).value;
    
        applyDeletion(mnoVal, prnoVal);
    }
})


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
    const url = '/payment/provide-cart-list';
    const config = {
        method: "post",
        headers: { "Content-Type": "application/json; charset=utf-8" },
        body: JSON.stringify(cartDtoArray)
    }

    const result = await fetch(url, config);
    const textResult = await result.text();
    if(textResult == "1") {
        console.log("sendCartVoArrayToServer: Succeeded.");
        window.location.href = "/payment/payout"; // GET 요청 생성
    } else {
        console.log("sendCartVoArrayToServer: Failed.");
    }
}

// x 버튼 누르면 순서대로 서버에서 cart의 데이터를 없애고 /cart를 다시 불러오는 함수
async function applyDeletion(mnoVal, prnoVal) {
    try {
        await deleteCartToServer(mnoVal, prnoVal);
        window.location.href = "/payment/cart?mno=" + mnoVal;
    } catch (error) {
        console.error("Error during applying deletion. Content", error);
    }   
}

// cart 테이블에서 선택된 cart 데이터 삭제
async function deleteCartToServer(mnoVal, prnoVal) {
    
    const url = "/payment/cart/delete";

    const cartData = {
        mno: mnoVal,
        prno: prnoVal
    }

    const config = {
        method: "POST",
        headers: { "Content-Type": "application/json; charset=utf-8" },
        body: JSON.stringify(cartData)
    };
    
    const response = await fetch(url, config);
    const result = await response.text();
    if(result == "1") {
        console.log("Delete cart: Succeeded.");
    } else if (result == "0") {
        console.log("Delete cart: Failed.");
    } else {
        console.log("Delete cart: Unknown.");
    }
}