console.log("paymentDisplay.js recognized.");

const mno = document.getElementById("dataContainer").getAttribute("data-mno");
console.log("The mno in paymentDisplay.js: " + mno);

document.addEventListener("DOMContentLoaded", () => {
    getDefaultAddress(mno);
    displayMemberAddress();
});

// TODO: 화면에 띄울 기본 배송지 객체 가져올 것.
// Java controller에서 받아야 함!
async function getDefaultAddress(paramMno) {
    const url = "/payment/default-address";
    const config = {
        method: "POST",
        headers: { "Content-Type": "application/json; charset=utf-8" },
        body: JSON.stringify({
            mno: paramMno
        })
    };

    const response = await fetch(url, config);
    const result = await response.text();
    console.log("The result of getDefaultAddress: " + result);
}

// TODO: 주소를 띄우기 위해 주소 객체를 파라미터로 가져와야 함.
function displayMemberAddress() {
    // 띄울 element를 document.getElement든 .querySelector든 해서 가져와서
    // const 커스텀-어트리뷰트 = 요소.dataset.커스텀camelCase; 로 커스텀에 접근한 후
    // 요소.textContent = 커스텀-어트리뷰트; 로 띄운다.
}