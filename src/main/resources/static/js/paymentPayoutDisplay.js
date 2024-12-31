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
    // console.log("The index: " + indexNum);

    let totalPrice = 0;
    for (let i = 0; i < indexNum; i++) {
        let originalPrice = document.querySelector(`[data-payout="${i}"].book_original_price`).dataset.bookOriginalPrice;
        let bookQty = document.querySelector(`[data-payout="${i}"].book-qty`).innerText;

        // console.log("the originalPrice: " + originalPrice);
        // console.log("the book qty: " + bookQty);

        totalPrice += parseInt(originalPrice) * parseInt(bookQty);
        // console.log("The total price: " + totalPrice);
    }
    // console.log("totalPrice" + totalPrice);

    document.querySelector(`.total-original-price`).dataset.totalOriginalPrice = totalPrice;
    document.querySelector(`.total-original-price`).innerText = totalPrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

// 총 할인받은 금액 설정
function jehoInitializeTotalDiscountAmount() {
    const index = document.querySelector(`[data-list-total="listTotal"]`).innerText;
    let indexNum = parseInt(index);
    // console.log("The index: " + indexNum);

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
    // console.log("totalAmount" + totalAmount);

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

// 최종 결제 금액에 포인트, 쿠폰 반영
function applyPointCoupon(totalPrice) {
    let pointVal = document.querySelector('.discount-point').dataset.discountPoint;
    let couponVal = document.querySelector('.discount-coupon').dataset.discountCoupon;
    // let deliveryFeeVal = document.querySelector('.delivery-fee').dataset.deliveryFee;
    // console.log("The pointVal from applyPointCoupon: ", pointVal);
    // console.log("The couponVal from applyPointCoupon: ", couponVal);

    // 포인트의 값이 없으면("") 포인트를 0으로 할당
    if(pointVal == "") {
        pointVal = 0;
    } else {
        pointVal = parseInt(pointVal);
//        console.log("The result of parseInt: ", pointVal);
    }

    couponVal = parseInt(couponVal);
    // deliveryFeeVal = parseInt(deliveryFeeVal);

    totalPrice = (totalPrice - pointVal - couponVal);
//    console.log("total price: ", totalPrice);

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

        document.querySelector('.point').innerText = `보유 : ${formatWithComma(fullPoints)} P`;
    }

    // 포인트 입력 시 유효성 검증
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

    // 전액 사용 버튼 클릭시 포인트 입력 필드에 보유 포인트 전액 입력
    function useFullPoints() {
        pointInput.value = maxPoints;  // 전액 사용

        document.querySelector('.discount-point').dataset.discountPoint = maxPoints;
        document.querySelector('.discount-point').innerText = formatWithComma(maxPoints) + " P";

        jehoCalculateTotalPrice();  // 총 결제 금액 갱신

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
                    updateTotalPrice();
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

    // 포인트 입력 시 유효성 검증
    pointInput.addEventListener("input", validatePointInput);

    // 전액 사용 버튼 클릭 이벤트
    document.querySelector('.benefit_btn').addEventListener('click', useFullPoints);
});

/*--쿠폰--*/
document.addEventListener("DOMContentLoaded", function () {
    const orderBtn = document.getElementById("orderBtn");
    const couponSelect = document.getElementById("coupon");  // 쿠폰 선택 드롭다운
    const totalOriginalPriceElement = document.querySelector('.total-original-price');
    const dataContainer = document.getElementById("dataContainer");
    const couponElement = document.querySelector('.coupon-discount'); // 쿠폰 할인 요소
    let couponDiscount = 0;  // 쿠폰 할인 금액
    let couponCno = 0;  // 쿠폰 번호

    // 도서들의 전체 원가 합계를 가져오는 함수
    function getTotalOriginalPrice() {
        const totalOriginalPrice = parseInt(totalOriginalPriceElement.textContent.replace(',', '').trim());
        return totalOriginalPrice;
    }

    // 전체 원가 합계에 맞춰 쿠폰 목록을 필터링하는 함수
    function filterCouponsByPrice() {
        const totalPrice = getTotalOriginalPrice();  // 도서들의 전체 원가 합계
        const couponOptions = couponSelect.querySelectorAll("option");

        couponOptions.forEach(option => {
            const minPrice = parseInt(option.getAttribute("data-min-price"));

            // 최소 금액보다 작은 쿠폰은 보이고, 그 외의 쿠폰은 숨김
            if (minPrice > totalPrice) {
                option.style.display = 'none';
            } else {
                option.style.display = 'block';
            }
        });
    }

    // 페이지 로딩 시에 쿠폰 필터링을 한 번 수행
    filterCouponsByPrice();

    // 쿠폰 선택 시 처리
    couponSelect.addEventListener("change", function () {
        const selectedOption = this.options[this.selectedIndex];
        couponCno = selectedOption.value;  // 선택된 쿠폰의 cno
        couponDiscount = parseInt(selectedOption.getAttribute('data-discount'));  // 선택된 쿠폰의 할인 금액

        console.log("선택한 쿠폰의 할인 금액: ", couponDiscount);

        // 쿠폰 할인 정보 업데이트
        if (couponCno !== "0") {  // 쿠폰이 선택된 경우에만
            document.querySelector('.discount-coupon').dataset.discountCoupon = couponDiscount;
            document.querySelector('.discount-coupon').innerText = "- " + formatWithComma(couponDiscount);
        } else {
            document.querySelector('.discount-coupon').dataset.discountCoupon = 0;
        }

        jehoCalculateTotalPrice();
    });


    // 결제 버튼 클릭 시 처리
    orderBtn.addEventListener("click", function () {

        if (couponCno === 0) {
            couponCno = null; // 쿠폰을 사용하지 않으면 null로 설정
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