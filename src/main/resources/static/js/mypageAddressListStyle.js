console.log("mypageAddressListStyle.js recognized.");

document.addEventListener("DOMContentLoaded", () => {
    const openModalBtns = document.querySelectorAll('.registration_of_address_btn'); // 버튼들 모두 선택
    const closeModalBtn = document.getElementById("closeModal");
    const modalOverlay = document.querySelector('.modal_overlay');

    // 모든 버튼에 클릭 이벤트 추가
    openModalBtns.forEach((btn) => {
        btn.addEventListener("click", () => {
            modalOverlay.style.display = "block"; // 모달 보이게
        });
    });

    // 모달 닫기
    if (closeModalBtn) { // 닫기 버튼이 존재하는 경우만 추가
        closeModalBtn.addEventListener("click", () => {
            modalOverlay.style.display = "none"; // 모달 숨기기
        });
    }

    // 배경 클릭 시 모달 닫기
    modalOverlay.addEventListener("click", (e) => {
        if (e.target === modalOverlay) {
            modalOverlay.style.display = "none"; // 모달 숨기기
        }
    });

    const checkBox = document.querySelector('.check_box'); // 체크박스 선택
    const icon = document.querySelector('.defalut_address_check .ic'); // 아이콘 선택

    checkBox.addEventListener('change', () => {
        if (checkBox.checked) {
            icon.classList.remove('ic_checkbox_off'); // 기존 클래스 제거
            icon.classList.add('ic_checkbox_on'); // 새 클래스 추가
        } else {
            icon.classList.remove('ic_checkbox_on'); // 체크된 클래스 제거
            icon.classList.add('ic_checkbox_off'); // 원래 클래스 추가
        }
    });
});