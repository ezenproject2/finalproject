console.log("orderListDisplay.js recognized.");

document.addEventListener('DOMContentLoaded', () => {
    const isOrderEmpty = document.querySelector('.outer-data-container').dataset.isOrderEmpty;
    const isOrderEmptyBool = (isOrderEmpty === "true");
    console.log("Is the cart empty: " + isOrderEmptyBool);

    if(isOrderEmptyBool) {
        // 주문 내역이 비었을 때 레이아웃 설정
        document.querySelector('.cart_list').style = "display: none";
        document.querySelector('.cart_list_none').style = "display: block";
    } else {
        // 레이아웃 설정
        document.querySelector('.cart_list').style = "display: block";
        document.querySelector('.cart_list_none').style = "display: none";

        calculateQtyPrice();
    }
})

function calculateQtyPrice() {
    console.log("calculateQtyPrice start.");
    // NOTE: outerStat의 크기 만큼 반복하고, 그 안에서 innerStat을 반복해야 함.

    const outerIndex = document.querySelector(`.outer-data-container`).dataset.outerListSize;
    console.log("outerIndex: " + outerIndex);

    for(let outer=0; outer < parseInt(outerIndex); outer++) {
        let innerIndex = document.querySelector(`.cart_list[data-order-list="${outer}"] .inner-data-container`).dataset.innerListSize;
        console.log("innerIndex: " + innerIndex);

        for(let inner=0; inner < parseInt(innerIndex); inner++) {
            let salePrice = document.querySelector(`.cart_list[data-order-list="${outer}"] .sale-price[data-order-detail="${inner}"]`).innerText;
            let bookQty = document.querySelector(`.cart_list[data-order-list="${outer}"] .book-qty[data-order-detail="${inner}"]`).innerText;

            let qtyPriceVal = parseInt(salePrice) * parseInt(bookQty);

            let qtyPrice= document.querySelector(`.cart_list[data-order-list="${outer}"] .qty-price[data-order-detail="${inner}"]`);
            qtyPrice.innerText = qtyPriceVal;
        }

    }

}

// all-item-btn을 누르면 모두 선택됨
const allItemBtns = document.querySelectorAll('.all-item-btn');
allItemBtns.forEach(allItemBtn => {
    allItemBtn.addEventListener('click', (event) => {
        let outerIndex = allItemBtn.dataset.orderList;
        let innerIndex = document.querySelector(`.cart_list[data-order-list="${outerIndex}"] .inner-data-container`).dataset.innerListSize;

        for (let i=0; i<innerIndex; i++) {
            let singleItemBtn = document.querySelector(`.cart_list[data-order-list="${outerIndex}"] .single-item-btn[data-order-detail="${i}"]`);
            singleItemBtn.checked = event.target.checked;
        }

    })
})

// 재고 + 버튼에 맞춰 가격 변동
// NOTE: order-list는 주문 내역을 전시만 하는 역할이라 수량을 변동하면 안 됨. 주석처리함.
// const ascBtns = document.querySelectorAll('.asc-btn');
// ascBtns.forEach(ascBtn => {
//     ascBtn.addEventListener('click', () => {
//         // 클릭된 asc-btn의 innerIndex, outerIndex 찾기
//         let innerIndex = ascBtn.dataset.orderDetail;
//         let outerIndex = ascBtn.closest('.cart_list').getAttribute('data-order-list');

//         // index들을 기반으로 도서 개수 +1
//         let bookQty = document.querySelector(`.cart_list[data-order-list="${outerIndex}"] .book-qty[data-order-detail="${innerIndex}"]`);
//         let bookQtyVal = parseInt(bookQty.innerText);
//         bookQty.innerText = bookQtyVal + 1;

//         // 올라간 수량을 반영한 가격 띄우기
//         calculateQtyPrice();
//     })
// });

// 재고 - 버튼에 맞춰 가격 변동
// NOTE: order-list는 주문 내역을 전시만 하는 역할이라 수량을 변동하면 안 됨. 주석처리함.
// const descBtns = document.querySelectorAll('.desc-btn');
// descBtns.forEach(descBtn => {
//     descBtn.addEventListener('click', () => {
//         // 클릭된 desc-btn의 innerIndex, outerIndex 찾기
//         let innerIndex = descBtn.dataset.orderDetail;
//         let outerIndex = descBtn.closest('.cart_list').getAttribute('data-order-list');

//         // index들을 기반으로 도서 개수 -1
//         let bookQty = document.querySelector(`.cart_list[data-order-list="${outerIndex}"] .book-qty[data-order-detail="${innerIndex}"]`);
//         let bookQtyVal = parseInt(bookQty.innerText);

//         if((bookQtyVal - 1) == 0) {
//             bookQty.innerText == 1;
//         } else {
//             bookQty.innerText = bookQtyVal - 1;
//         }

//         // 내려간 수량을 반영한 가격 띄우기
//         calculateQtyPrice();
//     })
// })
