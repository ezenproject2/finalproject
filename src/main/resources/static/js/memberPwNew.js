document.addEventListener("DOMContentLoaded", () => {
    // 비밀번호와 비밀번호 확인 input 요소
    const pwInput = document.getElementById("pw");
    const pwCheckInput = document.getElementById("pwCheck");
    const errorMessage = document.getElementById("errorMessage");
    const sameMessage = document.getElementById("same");

    // 비밀번호 확인 일치 여부 체크 함수
    const checkPasswordMatch = () => {
        if (pwInput.value !== pwCheckInput.value) {
            sameMessage.style.display = "block"; // 불일치 시 메시지 표시
            sameMessage.textContent = "비밀번호가 일치하지 않습니다.";
            errorMessage.textContent = ""; // 비밀번호 불일치시 추가적인 메시지 처리
        } else {
            sameMessage.style.display = "none"; // 일치 시 메시지 숨기기
            errorMessage.textContent = ""; // 비밀번호가 일치하면 에러 메시지 제거
        }
    };

    // 비밀번호와 비밀번호 확인이 일치하는지 체크
    pwInput.addEventListener("input", checkPasswordMatch);
    pwCheckInput.addEventListener("input", checkPasswordMatch);

    // 비밀번호와 비밀번호 확인이 모두 입력되었는지 확인
    const submitButton = document.getElementById("check");
    submitButton.addEventListener("click", (e) => {
        if (pwInput.value !== pwCheckInput.value) {
            e.preventDefault(); // 비밀번호가 일치하지 않으면 폼 전송을 막음
            alert("비밀번호가 일치하지 않습니다.");
        }
    });
});
