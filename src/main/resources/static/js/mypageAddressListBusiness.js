console.log("mypageAddressListBusiness.js recognized.");

document.addEventListener('DOMContentLoaded', () => {
    preventRegister();
})

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
        console.log("new address btn clicked.");
        // 모달 내부에 작성하다가 form 제출 없이 꺼버려서 남은 입력값들을 모두 비우기
        const addrInputs = document.querySelectorAll('.address-input');
        addrInputs.forEach(input => {
            // console.log("The input: ", input);
            input.value = "";
        })

        // is-register-or-modify를 1로 설정.
        document.querySelector('.modal_overlay').dataset.isRegisterOrModify = 1;
    }

    // 모달의 "확인"버튼 클릭 시
    if(e.target.classList.contains('addr-form-btn')) {

        // 기본 배송지 checkbox가 클릭되어 있다면 null 방지용으로 배치한 input type="hidden"을 제거
        const defaultInput = document.querySelector('.set-default-input');
        if(defaultInput.checked) {
            document.querySelector('.hidden-set-default-input').remove();
        }

        // 모달이 "새 배송지 등록"을 눌러서 뜬 건지 "수정"을 눌러서 뜬 건지 확인
        let isRegisterOrModify = document.querySelector('.modal_overlay').dataset.isRegisterOrModify;

        if(isRegisterOrModify == "1") { // 새 배송지를 등록하는 경우
            let isAllFilled = validateAddr();

            if (isAllFilled) {
                preventRegister();
                alert("등록 완료되었습니다.");
            } else {
                alert("배송지 이름을 제외한 모든 배송지 정보를 채워주세요.");
                e.preventDefault(); // submit 이벤트 방지
            }
        } else if (isRegisterOrModify == "2") { // 배송지를 수정하는 경우
            
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
    modifyBtn.addEventListener('click', (e) => {
        console.log("modify address btn clicked.");
        let index = e.target.dataset.addrIndex;

        // is-register-or-modify를 2로 설정
        document.querySelector('.modal_overlay').dataset.isRegisterOrModify = 2;

        // 모달 창에서 몇 번째 수정 버튼인지 확인할 수 있는 index 값 할당
        document.querySelector('.modal_overlay').dataset.indexForModify = index;

        // 수정하려는 배송지의 정보를 모달창에 배치
        displayAddrInfoInModal(index);

    })
})

// 삭제 버튼을 누르면 해당 index에 해당하는 adno를 가져온 뒤 비동기 함수에 보내는 이벤트
const deleteBtns = document.querySelectorAll('.delete-btn');
deleteBtns.forEach(deleteBtn => {
    deleteBtn.addEventListener('click', (e) => {
        const index = e.target.dataset.addrIndex;
        console.log("The index: ", index);

        const adnoVal = document.querySelector(`.addr-adno-input[data-addr-index="${index}"]`).value;
        console.log("The adnoVal: ", adnoVal);

        deleteAddrToServer(adnoVal);
        preventRegister();
    })
})

// 배송지 개수가 10개를 넘어가면 "새 배송지 등록" 버튼 비활성화
function preventRegister() {
    let addrListSize = document.querySelector('.address_table').dataset.listSize;
    addrListSize = parseInt(addrListSize);

    if(10 <= addrListSize) {
        document.querySelector('.register-addr-btn').disabled = true;
    } else {
        document.querySelector('.register-addr-btn').disabled = false;
    }
}

// "수정" 버튼 클릭 시 모달에 수정 대상인 배송지의 정보를 띄움
function displayAddrInfoInModal(index) {
    // 배송지명, 수취인 이름, 수취인 폰, 우편번호, 주소, 상세주소
    const addrInfo = document.querySelectorAll(`.addr-info[data-addr-index="${index}"]`);
    const addrInputs = document.querySelectorAll('.address-input');

    for (let i=0; i<addrInputs.length; i++) {
        addrInputs[i].value = addrInfo[i].innerText;
    }
}

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
        alert("삭제 완료되었습니다.");
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

    // 기본 배송지 등록 input이 checked 되어 있으면 isDefaultVal이 Y, 아니면 N
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