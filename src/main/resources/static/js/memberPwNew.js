document.addEventListener("DOMContentLoaded", () => {
    const pwInput = document.getElementById("pw");
    const pwCheckInput = document.getElementById("pwCheck");
    const errorMessage = document.getElementById("errorMessage");
    const sameMessage = document.getElementById("same");

    const checkPasswordMatch = () => {
        if (pwInput.value !== pwCheckInput.value) {
            sameMessage.style.display = "block";
            sameMessage.textContent = "비밀번호가 일치하지 않습니다.";
            errorMessage.textContent = "";
        } else {
            sameMessage.style.display = "none";
            errorMessage.textContent = "";
        }
    };

    pwInput.addEventListener("input", checkPasswordMatch);
    pwCheckInput.addEventListener("input", checkPasswordMatch);

    const submitButton = document.getElementById("check");
    submitButton.addEventListener("click", (e) => {
        if (pwInput.value !== pwCheckInput.value) {
            e.preventDefault();
            alert("비밀번호가 일치하지 않습니다.");
        }
    });
});
