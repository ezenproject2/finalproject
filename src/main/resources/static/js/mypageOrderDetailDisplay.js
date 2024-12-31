console.log("mypageOrderDetailDisplay.js recognized.");

document.addEventListener('DOMContentLoaded', () => {
    displayPaymentInfo();
    showStatusInKorean();
})

function showStatusInKorean() {
    let orderSize = document.querySelector(`.order-data-container`).dataset.orderSize;
    orderSize = parseInt(orderSize);
    console.log("The order size: ", orderSize);

    for(let i=0; i < orderSize; i++) {
        let status = document.querySelector(`.order-detail-status[data-order-index="${i}"]`);
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

// 결제 정보에 입력되는 값을 계산하여 넣는 함수
function displayPaymentInfo() {
    let sumOriginalPrice = 0;
    let sumSalePrice = 0;
    let sumSaleAmount = 0;

    let orderSize = document.querySelector('.order-data-container').dataset.orderSize;
    orderSize = parseInt(orderSize);

    for (let i=0; i<orderSize; i++) {
        let originalPrice = document.querySelector(`.product-discount-input[data-order-index="${i}"]`).value;
        let detailPrice = document.querySelector(`.order-detail-price-input[data-order-index="${i}"]`).value;
        let bookQty = document.querySelector(`.book-qty-input[data-order-index="${i}"]`).value;
        
        originalPrice = parseInt(originalPrice);
        detailPrice = parseInt(detailPrice);
        bookQty = parseInt(bookQty);

        sumOriginalPrice += originalPrice * bookQty;
        sumSalePrice += detailPrice;
        sumSaleAmount += (originalPrice * bookQty) - detailPrice;
    }

    document.querySelector('.sum-sale-price').innerText = sumSalePrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    document.querySelector('.sum-original-price').innerText = sumOriginalPrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");

    let saleAmoutText = "- " + sumSaleAmount.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    document.querySelector('.sum-sale-amount').innerText = saleAmoutText;
}