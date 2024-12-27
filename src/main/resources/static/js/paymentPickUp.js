document.getElementById("pickUpInfo").style.display = 'none';
document.getElementById("orderBtn").disabled = true;
let osno = 0;

// 체크박스에서 중복 선택 방지
const checkboxes = document.querySelectorAll('.cart_check');

checkboxes.forEach(checkbox => {
  checkbox.addEventListener('change', (e)=>{
    // 하나의 체크박스를 선택하면 나머지는 선택 해제
    checkboxes.forEach(box => {
      if (box !== e.target) {
        box.checked = false;
      }
    });
    const selectedStore = storeList.filter(store => store.osno === parseInt(e.target.dataset.osno));
    console.log(selectedStore);
    document.getElementById("nameEl").innerText = `매장명: ${selectedStore[0].name}`;
    document.getElementById("addrEl").innerText = `주소: ${selectedStore[0].address}`;
    document.getElementById("hourEl").innerText = `영업시간: ${selectedStore[0].hours}`;
    osno = parseInt(selectedStore[0].osno);

    document.getElementById("pickUpInfo").style.display = 'block';
    document.getElementById("orderBtn").disabled = false;

  });
});

document.getElementById("orderBtn").addEventListener("click", ()=>{
  console.log(osno);

  window.location.href = "/payment/payout/" + osno;
});