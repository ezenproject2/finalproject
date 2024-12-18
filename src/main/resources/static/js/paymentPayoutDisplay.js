console.log("paymentDisplay.js recognized.");

const mno = document.getElementById("dataContainer").getAttribute("data-mno");
console.log("The mno in paymentDisplay.js: " + mno);


document.addEventListener('DOMContentLoaded', () => {
    initializeTotalPrice();
})

function initializeTotalPrice() {
    let index = document.querySelector('[data-list-total="listTotal"]').textContent;

    let sumPrice = 0;
    
    for(let i=0; i < parseInt(index); i++ ) {
        let price = document.querySelector(`[data-list-book-price="${i}"]`).innerText;
        sumPrice += parseInt(price);
    }

    document.querySelector('[data-total-price="totalPrice"]').innerText = sumPrice;
}