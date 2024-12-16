console.log("paymentDisplay.js recognized.");

const mno = document.getElementById("dataContainer").getAttribute("data-mno");
console.log("The mno in paymentDisplay.js: " + mno);

document.addEventListener("DOMContentLoaded", () => {
    // getDefaultAddress(mno);
    // displayMemberAddress();
});


async function getDefaultAddress(paramMno) {
    const url = "/payment/payout/default-address";
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

function displayMemberAddress() {
    // 띄울 element를 document.getElement든 .querySelector든 해서 가져와서
    // const 커스텀-어트리뷰트 = 요소.dataset.커스텀camelCase; 로 커스텀에 접근한 후
    // 요소.textContent = 커스텀-어트리뷰트; 로 띄운다.
}