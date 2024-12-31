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

        // totalPrice = 도서 할인가 * 도서 개수를 누적하여 더한 값
        totalPrice += parseInt(salePrice) * parseInt(bookQty);
        // console.log("The total price: " + totalPrice);
    }
    // console.log("totalPrice" + totalPrice);

    // 포인트, 쿠폰, 배송비 반영
    totalPrice = applyPointCoupon(totalPrice);

    // 배송비 설정
    let deliveryFee = 0;
    if (totalPrice < 20000) {
            deliveryFee = 3000;
    }

    // 결정된 배송비를 화면에 반영
    document.querySelector('.delivery-fee').dataset.deliveryFee = deliveryFee;
    document.querySelector('.delivery-fee').innerText = "+ " + deliveryFee.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",") + "원";

    // 결제 예상 금액에 배송비를 더함
    totalPrice += deliveryFee;

    document.querySelector(`.total-price`).dataset.totalPrice = totalPrice;
    document.querySelector(`.total-price`).innerText = totalPrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

// 최종 결제 금액에 포인트, 쿠폰, 배송비 반영
function applyPointCoupon(totalPrice) {
    let pointVal = document.querySelector('.discount-point').dataset.discountPoint;
    let couponVal = document.querySelector('.discount-coupon').dataset.discountCoupon;
    // let deliveryFeeVal = document.querySelector('.delivery-fee').dataset.deliveryFee;
    console.log("The pointVal from applyPointCoupon: ", pointVal);
    console.log("The couponVal from applyPointCoupon: ", couponVal);

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
//// 사용자의 입력으로 .point_input의 value가 변화하면 즉시 포인트 액수를 저장하고 ,를 찍음
//document.querySelector('.point_input').addEventListener('input', (e) => {
//    // console.log("The target: ", e.target);
//    let pointVal = e.target.value;
//
//    // 콤마가 찍혀 있다면 제거하기
//    pointVal = pointVal.replace(/,/g, "");
//
//    /* yh-------------- */
//    const maxPoints = parseInt(document.querySelector('.point_input').getAttribute('data-max'));
//
//    if (isNaN(pointVal) || pointVal === "") {
//        pointVal = 0;
//    } else {
//        pointVal = parseInt(pointVal);
//    }
//
//    if (pointVal > maxPoints) {
//        pointVal = maxPoints;
//    }
//    /* ---------------- */
//
//    // 포인트 액수를 데이터로 저장
//    e.target.dataset.pointAmount = pointVal;
//
//    // 영수증의 "포인트 사용"에 반영
//    document.querySelector('.discount-point').dataset.discountPoint = pointVal;
//    document.querySelector('.discount-point').innerText = pointVal.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",") + " P";
//
//    // 포인트 액수를 계산하여 영수증 액수 갱신
//    jehoCalculateTotalPrice();
//
//    // 포인트 액수에 다시 , 찍어서 .point_input 안에 넣기
//    e.target.value = pointVal.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
//})

/* yh-------------- */
/*--포인트--*/
document.addEventListener("DOMContentLoaded", function () {
    const orderBtn = document.getElementById("orderBtn");
    const pointInput = document.getElementById("pointInput");
    const maxPoints = parseInt(pointInput.getAttribute("data-max"));  // 보유 포인트
    const dataContainer = document.getElementById("dataContainer");
    const totalOriginalPriceElement = document.querySelector('.total-original-price');
    const totalPriceElement = document.querySelector('.total-price');
    const pointElement = document.querySelector(".point");

    function updatePaymentSummary(newPointsBalance) {
        const totalPrice = parseInt(totalOriginalPriceElement.textContent.replace(',', '').trim());
        const finalPrice = totalPrice - parseInt(pointInput.value);
        totalPriceElement.textContent = finalPrice.toLocaleString();

        const fullPoints = maxPoints;

        pointElement.innerText = `보유 : ${formatWithComma(fullPoints)} P`;
    }

    function validatePointInput() {
        let usedPoint = parseInt(pointInput.value);

        if (isNaN(usedPoint) || pointInput.value === "") {
            usedPoint = 0;
            pointInput.value = usedPoint;
        }

        if (usedPoint > maxPoints) {
            usedPoint = maxPoints;
            pointInput.value = usedPoint;
        }

        document.querySelector('.discount-point').dataset.discountPoint = usedPoint;
        document.querySelector('.discount-point').innerText = formatWithComma(usedPoint) + " P";

        jehoCalculateTotalPrice();
    }

    function useFullPoints() {
        pointInput.value = maxPoints;

        document.querySelector('.discount-point').dataset.discountPoint = maxPoints;
        document.querySelector('.discount-point').innerText = formatWithComma(maxPoints) + " P";

        jehoCalculateTotalPrice();

        pointInput.value = formatWithComma(maxPoints);
    }

    orderBtn.addEventListener("click", function () {
        const usedPoints = parseInt(pointInput.value);

        if (usedPoints === 0) {
            return;
        }

        // 입력한 포인트가 보유 포인트를 초과하면 경고
        if (usedPoints > maxPoints) {
            alert("보유 포인트를 초과할 수 없습니다.");
            return;
        }

        const requestData = {
            usedPoints: usedPoints,
            mno: dataContainer.getAttribute("data-mno")
        };

        fetch("/payment/payout/use-points", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestData)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                updatePaymentSummary(data.newPointsBalance);
                jehoCalculateTotalPrice();
            } else {
                alert("포인트 사용에 실패했습니다.");
            }
        })
        .catch(error => {
            console.error("Error:", error);
            console.error("Stack trace:", error.stack);
            alert("에러가 발생했습니다. 다시 시도해주세요.(Point)");
        });
    });

    pointInput.addEventListener("input", validatePointInput);

    document.querySelector('.benefit_btn').addEventListener('click', useFullPoints);
});


/*--쿠폰--*/
document.addEventListener("DOMContentLoaded", function () {
    const orderBtn = document.getElementById("orderBtn");
    const couponSelect = document.getElementById("coupon");
    const totalOriginalPriceElement = document.querySelector('.total-original-price');
    const dataContainer = document.getElementById("dataContainer");
    const couponElement = document.querySelector('.coupon-discount');
    let couponDiscount = 0;
    let couponCno = 0;

    function getTotalOriginalPrice() {
        const totalOriginalPrice = parseInt(totalOriginalPriceElement.textContent.replace(',', '').trim());
        return totalOriginalPrice;
    }

    function filterCouponsByPrice() {
        const totalPrice = getTotalOriginalPrice();
        const couponOptions = couponSelect.querySelectorAll("option");

        couponOptions.forEach(option => {
            const minPrice = parseInt(option.getAttribute("data-min-price"));

            if (minPrice > totalPrice) {
                option.style.display = 'none';
            } else {
                option.style.display = 'block';
            }
        });
    }

    filterCouponsByPrice();

    couponSelect.addEventListener("change", function () {
        const selectedOption = this.options[this.selectedIndex];
        couponCno = selectedOption.value;
        couponDiscount = parseInt(selectedOption.getAttribute('data-discount'));

        console.log("선택한 쿠폰의 할인 금액: ", couponDiscount);

        if (couponCno !== "0") {
            document.querySelector('.discount-coupon').dataset.discountCoupon = couponDiscount;
            document.querySelector('.discount-coupon').innerText = "- " + formatWithComma(couponDiscount);
        } else {
            document.querySelector('.discount-coupon').dataset.discountCoupon = 0;
        }

        jehoCalculateTotalPrice();
    });

    orderBtn.addEventListener("click", function () {

        if (couponCno === 0) {
            couponCno = null;
        }

        const requestData = {
            mno: dataContainer.getAttribute("data-mno"),
            cno: couponCno
        };

        fetch("/payment/payout/use-coupon", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestData)
        })
        .then(response => response.json())
        .then(data => {
        console.log("쿠폰 서버 응답 데이터: ", data);
            if (data.success) {

            } else {
                alert("쿠폰 적용에 실패했습니다.");
            }

            jehoCalculateTotalPrice();
        })
        .catch(error => {
            console.error("Error:", error);
            alert("에러가 발생했습니다. 다시 시도해주세요.(Coupon)");
        });
    });
});

// 숫자에 콤마를 추가하는 함수
function formatWithComma(number) {
    return number.toLocaleString();
}
/* ---------------- */
