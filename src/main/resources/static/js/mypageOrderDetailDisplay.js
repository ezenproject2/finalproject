console.log("mypageOrderDetailDisplay.js recognized.");

document.addEventListener('DOMContentLoaded', () => {
    displayPaymentInfo();
})

function displayPaymentInfo() {
    let sumOriginalPrice = 0;
    let sumSalePrice = 0;
    let sumSaleAmount = 0;

    let orderSize = document.querySelector('.order-data-container').dataset.orderSize;
    orderSize = parseInt(orderSize);

    for (let i=0; i<orderSize; i++) {
        let originalPrice = document.querySelector(`#productDiscountInput[data-order-index="${i}"]`).value;
        let salePrice = document.querySelector(`#orderDetailPriceInput[data-order-index="${i}"]`).value;
        originalPrice = parseInt(originalPrice);
        salePrice = parseInt(salePrice);

        sumOriginalPrice += originalPrice;
        sumSalePrice += salePrice;
        sumSaleAmount += (originalPrice - salePrice);
    }

    // .toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",")
    document.querySelector('.sum-sale-price').innerText = sumSalePrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    document.querySelector('.sum-original-price').innerText = sumOriginalPrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    document.querySelector('.sum-sale-amount').innerText = "- " + sumSaleAmount.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}