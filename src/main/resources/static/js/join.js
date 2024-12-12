document.addEventListener("DOMContentLoaded", () => {
    // 입력 필드 DOM 요소
    const idInput = document.getElementById("id");
    const pwInput = document.getElementById("pw");
    const pwCheckInput = document.getElementById("pwCheck");
    const numberInput = document.getElementById("number");
    const emailInput = document.getElementById("email");
    const nameInput = document.getElementById("name");
    const postcodeInput = document.getElementById("postcode");
    const homeAddressInput = document.getElementById("homeAddress");
    const detailAddressInput = document.getElementById("detailAddress");
    const nickNameInput = document.getElementById("nickName");
    const joinBtn = document.querySelector(".join_btn");
  
    // 주소 입력 필드
    const addressInputs = [
      postcodeInput,
      homeAddressInput,
      detailAddressInput,
    ];
  
    // 전체 입력 필드
    const inputs = [
      idInput,
      pwInput,
      pwCheckInput,
      numberInput,
      emailInput,
      nameInput,
      ...addressInputs,
      nickNameInput,
    ];
  
    // 비밀번호 보기/숨기기 버튼
    const pwViewButtons = document.querySelectorAll(".btn_view");
  
    // 체크박스 관련 DOM 요소
    const allCheckbox = document.querySelector(".input_check.all");
    const checkboxes = document.querySelectorAll(".essential_check");
    const requiredCheckboxes = document.querySelectorAll(
      ".join_terms_item input[type='checkbox']:not(.all):not(:last-child)"
    );
    const termsButtons = document.querySelectorAll(".join_terms_ex");
  
    // 유효성 검사 정규식
    const validateId = (value) => /^[a-z0-9_-]{5,20}$/.test(value);
    const validatePw = (value) =>
      /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,16}$/.test(value);
    const validateNumber = (value) => /^\d{10,11}$/.test(value);
    const validateEmail = (value) => value.includes("@");
  
    // 에러 메시지 표시
    const showError = (input, message) => {
      const formData = input.closest(".form_data");
      const errorSpan = formData.querySelector(".error_message");
      if (formData && errorSpan) {
        const formItem = formData.querySelector(".form_item");
        formItem.classList.add("error");
        errorSpan.innerHTML = `<i class="ic ic_error"></i> ${message}`;
        errorSpan.style.display = "inline-flex";
      }
    };
  
    // 에러 메시지 제거
    const clearError = (input) => {
      const formData = input.closest(".form_data");
      const errorSpan = formData.querySelector(".error_message");
      if (formData && errorSpan) {
        const formItem = formData.querySelector(".form_item");
        formItem.classList.remove("error");
        errorSpan.style.display = "none";
      }
    };
  
    // 비밀번호 보기/숨기기 기능
    pwViewButtons.forEach((button) => {
      button.addEventListener("click", () => {
        const input = button.previousElementSibling; // 이전 형제 요소(input)
        const icon = button.querySelector("i");
        const isPasswordVisible = input.type === "text";
  
        if (isPasswordVisible) {
          input.type = "password"; // input 타입 변경
          icon.className = "ic ic_view hide"; // 클래스 업데이트
        } else {
          input.type = "text";
          icon.className = "ic ic_view"; // 클래스 업데이트
        }
      });
    });
  
    // 비밀번호와 비밀번호 확인 일치 여부 검사
    const checkPasswordMatch = () => {
      if (pwInput.value !== pwCheckInput.value) {
        showError(pwCheckInput, ""); //showError(pwCheckInput, "비밀번호가 일치하지 않습니다.");
      } else {
        clearError(pwCheckInput);
      }
    };
  
    // 모든 약관 동의 체크박스 동작
    if (allCheckbox) {
      allCheckbox.addEventListener("change", () => {
        const isChecked = allCheckbox.checked;
        checkboxes.forEach((checkbox) => {
          checkbox.checked = isChecked;
        });
      });
    } else {
      console.error("allCheckbox 요소를 찾을 수 없습니다.");
    }
  
    // 개별 약관 버튼 클릭 시 모달창 열기
    termsButtons.forEach((button, index) => {
      button.addEventListener("click", () => {
        // 모달창 열기, window.name에 index 값을 전달
        const modalWindow = window.open(
          "join_terms.html",
          "약관 내용",
          `width=600,height=600,scrollbars=yes`
        );
  
        if (modalWindow) {
          modalWindow.name = index; // window.name에 인덱스 값 저장
        } else {
          console.error("모달창을 열지 못했습니다.");
        }
      });
    });
  
    // 외부에서 체크박스 업데이트를 위한 함수
    window.confirmCheckbox = (index) => {
      if (typeof index === "number") {
        checkboxes[index].checked = true;
      } else {
        console.error("올바른 인덱스 값이 전달되지 않았습니다.");
      }
    };
  
    // 약관 동의 여부 확인
    const validateTerms = () => {
      for (const checkbox of requiredCheckboxes) {
        if (!checkbox.checked) {
          alert("필수 약관에 동의해 주세요.");
          checkbox.focus(); // 체크되지 않은 약관에 포커스
          return false;
        }
      }
      return true;
    };
  
    // 버튼 상태 업데이트
    const updateJoinButtonState = () => {
      const areAllFilled = inputs.every((input) => input.value.trim() !== "");
      const isIdValid = validateId(idInput.value);
      const isPwValid = validatePw(pwInput.value);
      const isNumberValid = validateNumber(numberInput.value);
      const isEmailValid = validateEmail(emailInput.value);
  
      if (areAllFilled && isIdValid && isPwValid && isNumberValid && isEmailValid) {
        joinBtn.style.backgroundColor = "#497248";
        joinBtn.disabled = false; // 버튼 활성화
      } else {
        joinBtn.style.backgroundColor = "#4972487c";
        joinBtn.disabled = true; // 버튼 비활성화
      }
    };
  
    // 버튼 클릭 시 약관 검증 및 폼 제출 처리
    joinBtn.addEventListener("click", (event) => {
      if (!validateTerms()) {
        event.preventDefault(); // 폼 제출 막기
      }
    });
  
    // 개별 입력 필드 유효성 검사
    inputs.forEach((input) => {
      input.addEventListener("blur", () => {
        if (input.value.trim() === "") {
          showError(input, "필수 정보입니다.");
        } else {
          clearError(input);
          if (input === idInput && !validateId(idInput.value)) {
            showError(idInput, "5~20자의 영문 소문자, 숫자와 특수기호(_),(-)만 사용 가능합니다.");
          } else if (input === pwInput && !validatePw(pwInput.value)) {
            showError(pwInput, "8~16자의 영문 대/소문자, 숫자, 특수문자를 사용해 주세요.");
          } else if (input === numberInput && !validateNumber(numberInput.value)) {
            showError(numberInput, "숫자만 입력해 주세요.");
          } else if (input === emailInput && !validateEmail(emailInput.value)) {
            showError(emailInput, "이메일에 '@'를 포함하여 작성해 주세요.");
          }
        }
        updateJoinButtonState();
      });
    });
  
    // 비밀번호 확인창 blur 이벤트 추가
    pwCheckInput.addEventListener("blur", checkPasswordMatch);
  
    // 입력 필드 포커스 시 에러 제거
    inputs.forEach((input) => {
      input.addEventListener("focus", () => clearError(input));
    });
  });  