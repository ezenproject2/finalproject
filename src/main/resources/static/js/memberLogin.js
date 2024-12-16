document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".input_item").forEach((item) => {
      const input = item.querySelector("input");
      const deleteButton = item.querySelector(".btn_delete");
      const viewButton = item.querySelector(".btn_view");

      // 1. input_item이 포커스 되었을 때 focus 클래스 추가
      input.addEventListener("focus", () => {
        item.classList.add("focus");
      });

      // input에서 포커스 해제 시 focus 클래스 제거
      input.addEventListener("blur", () => {
        item.classList.remove("focus");
      });

      // 2. input창 내에 글자 입력 시 on 클래스 추가
      input.addEventListener("input", () => {
        if (input.value.trim().length > 0) {
          item.classList.add("on");
          deleteButton.style.display = "inline-block"; // 삭제 버튼 보이기
          if (viewButton) viewButton.style.display = "inline-block"; // 보기 버튼 보이기
        } else {
          item.classList.remove("on");
          deleteButton.style.display = "none"; // 삭제 버튼 숨기기
          if (viewButton) viewButton.style.display = "none"; // 보기 버튼 숨기기
        }
      });

      // 2-1. 초기화 버튼 클릭 시 input 내용 삭제 및 on 클래스 제거
      deleteButton.addEventListener("click", () => {
        input.value = ""; // 내용 삭제
        item.classList.remove("on"); // on 클래스 제거
        deleteButton.style.display = "none"; // 삭제 버튼 숨기기
        if (viewButton) viewButton.style.display = "none"; // 보기 버튼 숨기기
      });
    });

    // 3. 비밀번호 보기 버튼 처리
    const pwViewButton = document.querySelector("#pw_view");
    const pwInput = document.querySelector("#pw");

    if (pwViewButton && pwInput) {
      pwViewButton.addEventListener("click", () => {
        const icon = pwViewButton.querySelector("i");
        const isPasswordVisible = pwInput.type === "text";

        if (isPasswordVisible) {
          pwInput.type = "password"; // input 타입 변경
          icon.className = "ic ic_view hide"; // 클래스 업데이트
        } else {
          pwInput.type = "text";
          icon.className = "ic ic_view"; // 클래스 업데이트
        }
      });
    }


    // -----------------------------------------------------------------
    // 서버쪽
    document.getElementById('loginForm').addEventListener('submit', function(event) {
        const loginId = document.getElementById('id').value;
        const password = document.getElementById('pw').value;
        let errorMessage = '';

        if (!loginId) {
            errorMessage = '아이디 또는 이메일을 입력해 주세요.';
        } else if (!password) {
            errorMessage = '비밀번호를 입력해 주세요.';
        }

        if (errorMessage) {
            event.preventDefault();  // Prevent form submission
            document.getElementById('err_common').style.display = 'block';  // Show error message
            document.getElementById('err_common').querySelector('span').innerText = errorMessage;
        } else {
            document.getElementById('err_common').style.display = 'none';  // Hide error message if everything is valid
        }
    });

  });