console.log("paymentPayoutDisplay.js recognized.");

const mno = document.getElementById("dataContainer").getAttribute("data-mno");
// console.log("The mno in paymentDisplay.js: " + mno); // mno 값 나오는 것 확인

document.addEventListener('DOMContentLoaded', () => {
    // 화면 로딩 시 최종 결제 금액을 화면에 띄움.
    jehoInitializeTotalOriginalPrice();
    jehoInitializeTotalDiscountAmount();
    jehoCalculateTotalPrice();
    // jehoCalculateDeliveryFee();
})

// 사용자의 입력으로 .point_input의 value가 변화하면 즉시 포인트 액수를 저장하고 ,를 찍음
document.querySelector('.point_input').addEventListener('input', (e) => {
    // console.log("The target: ", e.target);
    let pointVal = e.target.value;

    // 콤마가 찍혀 있다면 제거하기
    pointVal = pointVal.replace(/,/g, "");

    // 포인트 액수를 데이터로 저장
    e.target.dataset.pointAmount = pointVal;

    // 영수증의 "포인트 사용"에 반영
    document.querySelector('.discount-point').dataset.discountPoint = pointVal;
    document.querySelector('.discount-point').innerText = pointVal.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",") + "원";

    // 포인트 액수를 계산하여 영수증 액수 갱신
    jehoCalculateTotalPrice();

    // 포인트 액수에 다시 , 찍어서 .point_input 안에 넣기
    e.target.value = pointVal.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
})



// 도서들의 전체 원가 합계 설정
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

// 총 결제 금액 설정
function jehoCalculateTotalPrice() {
    const index = document.querySelector(`[data-list-total="listTotal"]`).innerText;
    let indexNum = parseInt(index);
    // console.log("The index: " + indexNum);

    let totalPrice = 0;
    for (let i = 0; i < indexNum; i++) {
        let salePrice = document.querySelector(`[data-payout="${i}"].book-sale-price`).dataset.salePrice;
        let bookQty = document.querySelector(`[data-payout="${i}"].book-qty`).innerText;

        // console.log("the salePrice: " + salePrice);
        // console.log("the book qty: " + bookQty);

        totalPrice += parseInt(salePrice) * parseInt(bookQty);
        // console.log("The total price: " + totalPrice);
    }
    // console.log("totalPrice" + totalPrice);

    // 포인트, 쿠폰, 배송비 반영
    totalPrice = applyPointCoupon(totalPrice);

    // 배송비 설정
    let deliveryFee = 0;
    if(20000 < totalPrice) {
        // 2만 원 이상 구매 시 배송비 무료
    } else {
        deliveryFee = 3000;
    }

    // 결정된 배송비를 화면에 반영
    document.querySelector('.delivery-fee').dataset.deliveryFee = deliveryFee;
    document.querySelector('.delivery-fee').innerText = "+ " + deliveryFee.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",") + "원";

    totalPrice - deliveryFee;

    document.querySelector(`.total-price`).dataset.totalPrice = totalPrice;
    document.querySelector(`.total-price`).innerText = totalPrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

// 최종 결제 금액에 포인트, 쿠폰, 배송비 반영
function applyPointCoupon(totalPrice) {
    let pointVal = document.querySelector('.discount-point').dataset.discountPoint;
    let couponVal = document.querySelector('.discount-coupon').dataset.discountCoupon;
    // let deliveryFeeVal = document.querySelector('.delivery-fee').dataset.deliveryFee;
    console.log("The pointVal from applyPointCoupon: ", pointVal);

    // 포인트의 값이 없으면("") 포인트를 0으로 할당
    if(pointVal == "") {
        pointVal = 0;
    } else {
        pointVal = parseInt(pointVal);
        console.log("The result of parseInt: ", pointVal);
    }

    couponVal = parseInt(couponVal);
    // deliveryFeeVal = parseInt(deliveryFeeVal);

    totalPrice = (totalPrice - pointVal - couponVal);
    console.log("total price: ", totalPrice);

    return totalPrice;
}