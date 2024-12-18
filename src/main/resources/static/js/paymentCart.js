 console.log("cart.js recognized.");

 // #allItemBtn을 누르면 모두 선택됨됨
 document.getElementById('allItemBtn').addEventListener('click', (e) => {
     console.log("e.target.checked: " + e.target.checked);

     const selectItemBtns = document.getElementsByName('itemBtn');

     selectItemBtns.forEach((itemBtn) => {
         itemBtn.checked = e.target.checked;
     })
 })

 // 주문 버튼 누르면 CartDTO와 매핑할 JSON 생성
 document.getElementById('orderBtn').addEventListener('click', () => {
     console.log("orderBtn clicked.");
     let cartDtoArray = createCartDtoArray();
     console.log(" >>> CartDtoArray: " + cartDtoArray);
     sendCartVoArrayToServer(cartDtoArray);
 });

 // 클릭된 class="single-item-btn"들을 선택
 function createCartDtoArray() {
     const singleItemBtns = document.querySelectorAll('input[type="checkbox"].single-item-btn:checked');
     const cartDtoArray = [];

     if(singleItemBtns == null) {
        console.log("The singleItemBtns is null.");
    } else {
         singleItemBtns.forEach(singleBtn => {
            const dataCart = singleBtn.dataset.cart;
            console.log("The value of data-cart: " + dataCart);
            let cartDtoJson = getDataForCartDto(dataCart);
            cartDtoArray.push(cartDtoJson);
        })
    }

     return cartDtoArray;
 }

 // single-item-btn의 data-cart 값을 바탕으로 mno, prno, bootQty 값 추출
 function getDataForCartDto(singleItemBtnDataCart) {
     const cartDtoJson = {
         mno:"",
         prno:"",
         bookQty:""
     };

     const mnoVal = document.querySelector(`input[data-cart="${singleItemBtnDataCart}"].mno`).value;
     const prnoVal = document.querySelector(`input[data-cart="${singleItemBtnDataCart}"].prno`).value;
     const bookQtyVal = document.querySelector(`input[data-cart="${singleItemBtnDataCart}"].bookQty`).value;

     // console.log("The mno of <input type='hidden' class='mno'>: " + mnoVal);
     // console.log("The prno of <input type='hidden' class='prno'>: " + prnoVal);
     // console.log("The bookQty of <input type='text' class='bookQty'>: " + bookQtyVal);

     cartDtoJson.mno = mnoVal;
     cartDtoJson.prno = prnoVal;
     cartDtoJson.bookQty = bookQtyVal;

     return cartDtoJson;
 }

 async function sendCartVoArrayToServer(cartDtoArray) {
     console.log(" >>> sendCartVoArrayToServer start.");
     const url = '/payment/get-cart-list';
     const config = {
         method: "post",
         headers: { "Content-Type": "application/json; charset=utf-8" },
         body: JSON.stringify(cartDtoArray)
     }

     const result = await fetch(url, config);
     const textResult = await result.text();
     if(textResult == "1") {
         console.log("sendCartVoArrayToServer: Succeeded.");
         window.location.href = "/payment/payout"; // 이게 GET요청을 보내는 거라고 함.
     } else {
         console.log("sendCartVoArrayToServer: Failed.");
     }
 }

 // sendCartVoArrayToServer의 config 원본
 /*
 body: JSON.stringify({ "cartList": cartDtoArray })
 Spring Boot의 getCartList에서 뜨던 log: // cartList: {"cartList":[{"mno":"1","prno":"1","bookQty":"5"},{"mno":"1","prno":"2","bookQty":"1"}]}
 */