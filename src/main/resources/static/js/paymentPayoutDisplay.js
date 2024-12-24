console.log("paymentPayoutDisplay.js recognized.");

const mno = document.getElementById("dataContainer").getAttribute("data-mno");
// console.log("The mno in paymentDisplay.js: " + mno); // mno 값 나오는 것 확인

document.addEventListener('DOMContentLoaded', () => {
    // 화면 로딩 시 최종 결제 금액을 화면에 띄움.
    jehoInitializeTotalOriginalPrice();
    jehoInitializeTotalDiscountAmount();
    jehoInitializeTotalPrice();
})

// 도서들의 전체 원가 합계 첫 설정
function jehoInitializeTotalOriginalPrice() {
    const index = document.querySelector(`[data-list-total="listTotal"]`).innerText;
    let indexNum = parseInt(index);
    console.log("The index: " + indexNum);

    let totalPrice = 0;
    for (let i = 0; i < indexNum; i++) {
        let originalPrice = document.querySelector(`[data-payout="${i}"].book_original_price`).dataset.bookOriginalPrice;
        let bookQty = document.querySelector(`[data-payout="${i}"].book-qty`).innerText;

        // console.log("the originalPrice: " + originalPrice);
        // console.log("the book qty: " + bookQty);

        totalPrice += parseInt(originalPrice) * parseInt(bookQty);
        // console.log("The total price: " + totalPrice);
    }
    console.log("totalPrice" + totalPrice);
    
    document.querySelector(`.total-original-price`).dataset.totalOriginalPrice = totalPrice;
    document.querySelector(`.total-original-price`).innerText = totalPrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

// 총 할인받은 금액 설정
function jehoInitializeTotalDiscountAmount() {
    const index = document.querySelector(`[data-list-total="listTotal"]`).innerText;
    let indexNum = parseInt(index);
    console.log("The index: " + indexNum);

    let totalAmount = 0;
    for (let i = 0; i < indexNum; i++) {
        let salePrice = document.querySelector(`[data-payout="${i}"].book-sale-price`).dataset.salePrice;
        let originalPrice = document.querySelector(`[data-payout="${i}"].book_original_price`).dataset.bookOriginalPrice;
        let bookQty = document.querySelector(`[data-payout="${i}"].book-qty`).innerText;

        // console.log("the salePrice: " + salePrice);
        // console.log("the originalPrice: " + originalPrice);
        // console.log("the book qty: " + bookQty);

        let discountAmount = (parseInt(originalPrice) - parseInt(salePrice)) * parseInt(bookQty);

        totalAmount += discountAmount;
        // console.log("The total price: " + totalAmount);
    }
    console.log("totalAmount" + totalAmount);

    document.querySelector(`.discount-amount`).dataset.discountAmount = totalAmount;
    document.querySelector(`.discount-amount`).innerText = '- ' + totalAmount.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

// 총 결제 금액 첫 설정
function jehoInitializeTotalPrice() {
    const index = document.querySelector(`[data-list-total="listTotal"]`).innerText;
    let indexNum = parseInt(index);
    console.log("The index: " + indexNum);

    let totalPrice = 0;
    for (let i = 0; i < indexNum; i++) {
        let salePrice = document.querySelector(`[data-payout="${i}"].book-sale-price`).dataset.salePrice;
        let bookQty = document.querySelector(`[data-payout="${i}"].book-qty`).innerText;

        // console.log("the salePrice: " + salePrice);
        // console.log("the book qty: " + bookQty);

        totalPrice += parseInt(salePrice) * parseInt(bookQty);
        // console.log("The total price: " + totalPrice);
    }
    console.log("totalPrice" + totalPrice);

    document.querySelector(`.total-price`).dataset.totalPrice = totalPrice;
    document.querySelector(`.total-price`).innerText = totalPrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}