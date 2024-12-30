console.log("orderListDisplay.js recognized.");

document.addEventListener('DOMContentLoaded', () => {
    const isOrderEmpty = document.getElementById('isOrderEmptyInput').value;
    const isOrderEmptyBool = (isOrderEmpty === "true");
    console.log("Is the cart empty: " + isOrderEmptyBool);

    if(isOrderEmptyBool) {
        // 주문 내역이 비었을 때 레이아웃 설정
        document.querySelector('.my_orders_box_none').style = "display: block";
        document.querySelector('.my_orders_box_wrap').style = "display: none";
    } else {
        // 레이아웃 설정
        document.querySelector('.my_orders_box_none').style = "display: none";
        document.querySelector('.my_orders_box_wrap').style = "display: block";

        // 첫 로딩 시 실행될 함수
        let outerSize = document.getElementById('outerSizeInput').value;
        outerSize = parseInt(outerSize);

        trimOrderDate(outerSize);
        showStatusInKorean(outerSize);
    }
})

// LocalDateTime인 orderDate의 T를 공백으로 치환.
function trimOrderDate(outerSize) {
    for(let i=0; i < outerSize; i++) {
        let orderDate = document.querySelector(`.order-date[data-outer-index="${i}"]`).innerText;
        orderDate = orderDate.replace(/T/, ' ');
        console.log("The trimmed order date: ", orderDate);
    }
}

// order_detail의 영문 status를 화면에 한글 status로 바꿔 띄움.
function showStatusInKorean(outerSize) {
    for(let i=0; i < outerSize; i++) {
        let innerSize = document.querySelector(`.inner-data-container[data-outer-index="${i}"]`).dataset.innerSize;
        innerSize = parseInt(innerSize);

        for(let j=0; j < innerSize; j++) {
            let status = document.querySelector(`.order-detail-status[data-inner-index="${j}"]`).innerText;
            let statusVal = "";

            switch (status.innerText) {
                case "refunded":
                    statusVal = "환불됨";
                    break;
                case "completed":
                    statusVal = "주문 완료";
                    break;
                default:
                    statusVal = "Unknown";
                    break;
            }

            status = statusVal;
        }
    }
}