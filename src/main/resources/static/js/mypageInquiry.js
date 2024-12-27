document.addEventListener('DOMContentLoaded', function () {
    // 셀렉트 박스
    const selectBox = document.querySelector(".inquiry_type_select_box");
    const select = document.querySelector(".inquiry_type");
    const arrowIcon = selectBox.querySelector(".inquiry_type_select_box .ic");

    // 상태 관리 - 포커스
    select.addEventListener("focus", () => {
        selectBox.setAttribute("data-state", "open");
        arrowIcon.classList.remove("ic_arrow_down");
        arrowIcon.classList.add("ic_arrow_up");
    });

    // 상태 관리 - 포커스 아웃
    select.addEventListener("blur", () => {
        selectBox.setAttribute("data-state", "closed");
        arrowIcon.classList.remove("ic_arrow_up");
        arrowIcon.classList.add("ic_arrow_down");
    });

    // 옵션 선택 시 포커스 해제
    select.addEventListener("change", () => {
        select.blur(); // 선택 후 포커스 해제
    });

    // 글자 수 세기
    const contentTextarea = document.querySelector("#content");
    const textCountSpan = document.querySelector(".text_count span");

    // 글자 수 업데이트 함수
    contentTextarea.addEventListener("input", () => {
        const currentLength = contentTextarea.value.length; // 현재 글자 수
        const maxLength = contentTextarea.getAttribute("maxlength"); // 최대 글자 수
        textCountSpan.textContent = `${currentLength}/${maxLength}`; // 글자 수 업데이트
    });

});