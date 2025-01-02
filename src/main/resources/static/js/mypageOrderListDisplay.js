console.log("mypageOrderListDisplay.js recognized.");

document.addEventListener('DOMContentLoaded', () => {
    const isOrderEmpty = document.getElementById('isOrderEmptyInput').value;
    const isOrderEmptyBool = (isOrderEmpty === "true");
    console.log("Is the cart empty: " + isOrderEmptyBool);

    if(isOrderEmptyBool) {
        // 주문 내역이 비었을 때 레이아웃 설정
        // document.querySelector('.my_orders_box_none').style = "display: block";
        document.querySelector('.my_orders_box_wrap').style = "display: none";
    } else {
        // 레이아웃 설정
        document.querySelector('.my_orders_box_none').style = "display: none";
        // document.querySelector('.my_orders_box_wrap').style = "display: block";

        // 첫 로딩 시 실행될 함수
        let outerSize = document.getElementById('outerSizeInput').value;
        outerSize = parseInt(outerSize);
        console.log("The outer size: ", outerSize);

        trimOrderDate(outerSize);
        displayOrderStatusList(outerSize);
        showStatusInKorean(outerSize);
    }
})

// LocalDateTime인 orderDate의 T를 공백으로 치환.
function trimOrderDate(outerSize) {
    for(let i=0; i < outerSize; i++) {
        let orderDate = document.querySelector(`.order-date[data-outer-index="${i}"]`);
        let orderDateVal = orderDate.innerText.replace(/T/, ' ');
        orderDate.innerText = orderDateVal;
        // console.log("The trimmed order date: ", orderDate.innerText);
    }
}

// my_orders_nav 안의 주문 상태 내역 값을 채움
function displayOrderStatusList(outerSize) {
    let preparing = 0;
    let shipping = 0;
    let completed = 0;
    let canceled = 0;
    let refunded = 0;

    for(let i=0; i < outerSize; i++) {
        let innerSize = document.querySelector(`.inner-data-container[data-outer-index="${i}"]`).dataset.innerSize;
        innerSize = parseInt(innerSize);
        console.log("The inner size: ", innerSize);

        for(let j=0; j < innerSize; j++) {
            let status = document.querySelector(`.inner-data-container[data-outer-index="${i}"] .order-detail-status[data-inner-index="${j}"]`);

            switch (status.innerText) {
                case "preparing":
                    preparing += 1;
                    break;
                case "shipping":
                    shipping += 1;
                    break;
                case "completed":
                    completed += 1;
                    break;
                case "canceled":
                    canceled += 1;
                    break;
                case "refunded":
                    refunded += 1;
                    break;
                default:
                    break;
            }
        }
    }

    // console.log("refunded", refunded);
    // console.log("refunded" + refunded);
    document.querySelector('.status-preparing').innerText = preparing;
    document.querySelector('.status-shipping').innerText = shipping;
    document.querySelector('.status-completed').innerText = completed;
    document.querySelector('.status-canceled').innerText = canceled;
    document.querySelector('.status-refunded').innerText = refunded;
};

// order_detail의 영문 status를 화면에 한글 status로 바꿔 띄움.
function showStatusInKorean(outerSize) {
    for(let i=0; i < outerSize; i++) {
        let innerSize = document.querySelector(`.inner-data-container[data-outer-index="${i}"]`).dataset.innerSize;
        innerSize = parseInt(innerSize);
        console.log("The inner size: ", innerSize);

        for(let j=0; j < innerSize; j++) {
            let status = document.querySelector(`.inner-data-container[data-outer-index="${i}"] .order-detail-status[data-inner-index="${j}"]`);
            console.log("The raw status: ", status.innerText);
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

            status.innerText = statusVal;
        }
    }
}