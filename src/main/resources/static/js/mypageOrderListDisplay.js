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

// order_detail의 영문 status를 화면에 한글 status로 바꿔 띄움.
function showStatusInKr() {
    let outerSize = document.querySelector('')
}