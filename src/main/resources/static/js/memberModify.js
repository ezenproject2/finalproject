document.addEventListener("DOMContentLoaded", function () {
    const modifyMsg = /*[[${mod_msg}]]*/ '';
    const deleteMsg = /*[[${del_msg}]]*/ '';

    if (modifyMsg === "ok") {
        alert("수정이 완료되었습니다.");
    } else if (modifyMsg === "fail") {
        alert("수정 실패. 다시 시도해주세요.");
    }

    if (deleteMsg === "ok") {
        alert("회원 탈퇴가 완료되었습니다.");
    } else if (deleteMsg === "fail") {
        alert("회원 탈퇴 실패. 다시 시도해주세요.");
    }
});