console.log("addressListBusiness.js recognized.");

document.addEventListener('click', (e) => {
    // console.log("The target: ", e.target);
    let isDefault = document.querySelector('.set-default-input').checked;
    // console.log("isDefault: ", isDefault);

    // <input> 대신에 그 위의 <i>를 선택해도 input의 checked를 바뀌게 하는 코드
    if(e.target.classList.contains('ic_checkbox_off')) {
        isDefault = !isDefault;
        // console.log("isDefault: ", isDefault);
    }

    // "새 배송지 등록" 버튼 클릭 시
    if(e.target.classList.contains('register-addr-btn')) {
        // is-register-or-modify를 1로 설정
        document.querySelector('.modal_overlay').dataset.isRegisterOrModify = 1;
        console.log("new address btn clicked.");
    }

    // 모달의 "확인"버튼 클릭 시
    if(e.target.classList.contains('addr-form-btn')) {

        // TODO: 기본 배송지 체크박스가 클릭되어 있으면 hidden-set-default-input을 form에서 뺌? 어쨌든 제외함.

        let isRegisterOrModify = document.querySelector('.modal_overlay').dataset.isRegisterOrModify;

        if(isRegisterOrModify == "1") { // 새 배송지를 등록하는 경우
            let isAllFilled = validateAddr();

            if (isAllFilled) {
                // form 태그로 컨트롤러에 요청을 보내서 요건 충족 시 수행할 동작 X
            } else {
                alert("배송지 이름을 제외한 모든 배송지 정보를 채워주세요.");
                e.preventDefault(); // submit 이벤트 방지
            }
        } else if (isRegisterOrModify == "2") { // 특정 배송지를 수정하는 경우
            
            // index를 가져와서 어떤 배송지인지 판별
            let index = document.querySelector('.modal_overlay').dataset.indexForModify;
            console.log("Index: ", index);
            index = parseInt(index);
            
            let isAllFilled = validateAddr();
        
            if (isAllFilled) {
                e.preventDefault(); // submit 못하게 방지. 수정이라 경로를 다르게 함.
                let adnoVal = document.querySelector(`.addr-adno-input[data-addr-index="${index}"]`).value;

                // 비동기 함수로 데이터 수정
                modifyAddr(adnoVal);
            } else {
                alert("배송지 이름을 제외한 모든 배송지 정보를 채워주세요.");
                e.preventDefault(); // submit 이벤트 방지
            }
        }
    }

})

// 수정 버튼 클릭 시
const modifyBtns = document.querySelectorAll('.modify-btn');
modifyBtns.forEach(modifyBtn => {
    // is-register-or-modify를 2로 설정
    modifyBtn.addEventListener('click', (e) => {
        // let index = e.target.dataset.addrIndex;
        // index = parseInt(index);
        // let isDefaultMark = document.querySelector(`.is-default-mark[data-addr-index="${index}"]`).innerText;

        // if(isDefaultMark == "기본 배송지") {
        //     alert("먼저 기본 배송지를 해제해주세요.");
        //     e.preventDefault(); // 모달 창 열리지 않게 방지
        //     return;
        // } else if (isDefaultMark == "일반 배송지") {
        //     // 통과
        // }

        document.querySelector('.modal_overlay').dataset.isRegisterOrModify = 2;
        console.log("modify address btn clicked.");

        // 모달 창에서 수정 버튼의 index를 확인할 수 있는 값 할당
        document.querySelector('.modal_overlay').dataset.indexForModify = e.target.dataset.addrIndex;
    })
})

// 삭제 버튼을 누르면 해당 index에 해당하는 adno를 가져온 뒤 비동기 함수에 보내는 이벤트
const deleteBtns = document.querySelectorAll('.delete-addr-btn');
deleteBtns.forEach(deleteBtn => {
    deleteBtn.addEventListener('click', (e) => {
        const index = e.target.dataset.addrIndex;
        console.log("The index: ", index);

        const adnoVal = document.querySelector(`.addr-adno-input[data-addr-index="${index}"]`).value;
        console.log("The adnoVal: ", adnoVal);

        deleteAddrToServer(adnoVal);
    })
})

// 필요한 배송지 정보를 모두 채웠는지 확인
function validateAddr() {
    const addrInputs = document.querySelectorAll('.address-input');
    let isAllFilled = true;
    // console.log(addrInputs);

    addrInputs.forEach(input => {
        // console.log("The input: ");
        // console.log(input);
        if(!input.classList.contains('addr-name-input') && input.value.trim() === '') {
            isAllFilled = false;
        } 
    })

    console.log("isAllFilled: ", isAllFilled);
    return isAllFilled;
}

// 선택된 배송지 DB에서 삭제
async function deleteAddrToServer(adnoVal) {
    const url = "/mypage/address-list/delete";

    const config = {
        method: "POST",
        headers: { "Content-Type": "application/json; charset=utf-8" },
        body: JSON.stringify(adnoVal)
    };
    
    const response = await fetch(url, config);
    const result = await response.text();
    if(result == "1") {
        console.log("Delete addr: Succeeded.");
        window.location.href = "/mypage/address-list";
    } else if (result == "0") {
        console.log("Delete addr: Failed.");
    } else {
        console.log("Delete addr: Unknown.");
    }
}

// 선택된 배송지 수정
async function modifyAddr(adnoData) {
    const mnoVal = document.querySelector('.addr-mno-input').value;
    const adnoVal = adnoData;
    const addrNameVal = document.querySelector('.addr-name-input').value;
    const recNameVal = document.querySelector('.addr-rec-name-input').value;
    const recPhoneVal = document.querySelector('.addr-rec-phone-input').value;
    const addrCodeVal = document.querySelector('.addr-postcode-input').value;
    const addrVal = document.querySelector('.addr-input').value;
    const addrDetailVal = document.querySelector('.addr-detail-input').value;
    let isDefaultVal = "";

    // 기본 배송지 등록 input이 checked 되어 있으면 Y, 아니면 N
    const isDefaultChecked = document.querySelector('.set-default-input').checked;
    if(isDefaultChecked) {
        isDefaultVal = "Y";
    } else {
        isDefaultVal = "N";
    }

    const url = "/mypage/address-list/modify";

    const config = {
        method: "POST",
        headers: { "Content-Type": "application/json; charset=utf-8" },
        body: JSON.stringify({
            mno: mnoVal,
            adno: adnoVal,
            addrName: addrNameVal,
            recName: recNameVal,
            recPhone: recPhoneVal,
            addrCode: addrCodeVal,
            addr: addrVal,
            addrDetail: addrDetailVal,
            isDefault: isDefaultVal
        })
    };
    
    const response = await fetch(url, config);
    const result = await response.text();
    if(result == "1") {
        console.log("Delete addr: Succeeded.");
        alert("수정 완료되었습니다.");
        window.location.href = "/mypage/address-list";
    } else if (result == "0") {
        console.log("Delete addr: Failed.");
    } else {
        console.log("Delete addr: Unknown.");
    }
}