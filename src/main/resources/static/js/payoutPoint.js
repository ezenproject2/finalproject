document.addEventListener("DOMContentLoaded", function () {
    const orderBtn = document.getElementById("orderBtn");
    const pointInput = document.getElementById("pointInput");
    const maxPoints = parseInt(pointInput.getAttribute("data-max"));  // 보유 포인트
    console.log(maxPoints);
    const dataContainer = document.getElementById("dataContainer");
    const totalOriginalPriceElement = document.querySelector('.total-original-price');
    const totalPriceElement = document.querySelector('.total-price');
    const pointElement = document.querySelector(".point");

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
            alert("에러가 발생했습니다. 다시 시도해주세요.");
        });
    });

    function updatePaymentSummary(newPointsBalance) {
        const totalPrice = parseInt(totalOriginalPriceElement.textContent.replace(',', '').trim());
        const finalPrice = totalPrice - parseInt(pointInput.value);
        totalPriceElement.textContent = finalPrice.toLocaleString();

        pointElement.innerText = `보유 : ${newPointsBalance} P`;
    }

    // 포인트 입력 시 유효성 검증
    function validatePointInput() {
        let usedPoint = parseInt(pointInput.value);

        // 입력값이 NaN이거나 보유 포인트를 초과하면 최대 포인트로 설정
        if (isNaN(usedPoint) || usedPoint > maxPoints) {
            pointInput.value = maxPoints;  // 최대 포인트로 설정
        }

        updateTotalPrice();  // 총 결제 금액 갱신
    }

    // 전액 사용 버튼 클릭시 포인트 입력 필드에 보유 포인트 전액 입력
    function useFullPoints() {
        pointInput.value = maxPoints;  // 전액 사용
        updateTotalPrice();  // 총 결제 금액 갱신
    }

    function updateTotalPrice() {
        const usedPoints = parseInt(pointInput.value);

        if (isNaN(usedPoints)) {
            pointInput.value = 0;
            return;
        }

        const totalPrice = parseInt(totalOriginalPriceElement.textContent.replace(',', '').trim());
        const finalPrice = totalPrice - usedPoints;

        totalPriceElement.textContent = finalPrice.toLocaleString();
    }

    // 포인트 입력 시 유효성 검증
    pointInput.addEventListener("input", validatePointInput);

    // 전액 사용 버튼 클릭 이벤트
    document.querySelector('.benefit_btn').addEventListener('click', useFullPoints);
});
