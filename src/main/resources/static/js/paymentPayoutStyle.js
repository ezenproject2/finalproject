document.addEventListener('DOMContentLoaded', function () {
    // 배송 메세지 셀렉트 박스
    const selectElement = document.querySelector('.request_select');
    const parentElement = document.querySelector('.shipping_request_select');
    const arrowIcon = document.querySelector('.shipping_request_select .ic_arrow_down');

    let isOpen = false; // 셀렉트 박스가 열렸는지 상태를 저장


    function toggleSelectBox() { // 셀렉트 박스 열림/닫힘 상태 확인
        isOpen = !isOpen;

        if (isOpen) {
            // 셀렉트 박스 열림
            parentElement.style.border = '1.5px solid #717171';
            arrowIcon.classList.remove('ic_arrow_down');
            arrowIcon.classList.add('ic_arrow_up');
        } else {
            // 셀렉트 박스 닫힘
            parentElement.style.border = '1.5px solid #c6c6c6';
            arrowIcon.classList.remove('ic_arrow_up');
            arrowIcon.classList.add('ic_arrow_down');
        }
    }

    // 셀렉트 박스 클릭 이벤트
    selectElement.addEventListener('click', toggleSelectBox);

    // 다른 곳 클릭 시 자동으로 닫히게 처리
    document.addEventListener('click', (event) => {
        if (!parentElement.contains(event.target)) {
            isOpen = false;
            parentElement.style.border = '1.5px solid #c6c6c6';
            arrowIcon.classList.remove('ic_arrow_up');
            arrowIcon.classList.add('ic_arrow_down');
        }
    });

    // 배송 메세지 글자 수 세기
    const textarea = document.querySelector('.request_text');
    const textContainer = document.querySelector('.shipping_request_text');
    const textCount = document.querySelector('.text_count span');
    const maxLength = textarea.getAttribute('maxlength'); // 최대 글자 수 가져오기

    // select 값 변경 이벤트
    selectElement.addEventListener('change', function () {
        if (this.value === '직접 입력') {
            textContainer.style.display = 'block'; // 보이게 하기
        } else {
            textContainer.style.display = 'none'; // 숨기기
        }
    });

    // 글자 수 업데이트 및 보더라인 처리
textarea.addEventListener('input', () => {
    const currentLength = textarea.value.length; // 현재 입력된 글자 수

    // 글자 수 업데이트
    textCount.innerHTML = `<span style="color: #497248; font-weight: 700;">${currentLength}</span>/${maxLength}`;

    // 입력 중 보더라인 변경
    textContainer.style.border = '1.5px solid #497248';
});

    // textarea 포커스 이벤트
    textarea.addEventListener('focus', () => {
        textContainer.style.border = '1.5px solid #717171'; // 포커스 시 보더라인 변경
    });

    // textarea 포커스 아웃 이벤트
    textarea.addEventListener('blur', () => {
        const currentLength = textarea.value.length; // 현재 글자 수 확인

        if (currentLength > 0) {
            textContainer.style.border = '1.5px solid #717171'; // 글자가 있을 때
        } else {
            textContainer.style.border = '1.5px solid #c6c6c6'; // 글자가 없을 때
        }
    });

    // select 값 변경 이벤트
    selectElement.addEventListener('change', function () {
        if (this.value === '직접 입력') {
            textContainer.style.display = 'block'; // 보이게 하기
        } else {
            textContainer.style.display = 'none'; // 숨기기
        }
    });

    // 아이콘 클릭 시 리스트 숨기기
    const arrowIcons = document.querySelectorAll('.order_title_wrap .ic');

    arrowIcons.forEach(icon => {
        icon.addEventListener('click', function () {
            const parentWrap = this.closest('.order_table_wrap'); // 최상위 공통 부모 찾기
            const orderItem = parentWrap.querySelector('.order_item'); // 해당 부모 내 order_item 찾기

            if (orderItem.classList.contains('active')) {
                // 숨길 때
                orderItem.style.height = `${orderItem.scrollHeight}px`; // 현재 높이 설정
                setTimeout(() => {
                    orderItem.style.height = '0'; // 높이를 0으로 축소
                }, 10); // transition 시작
                orderItem.style.overflow = 'hidden'; // 내용 숨기기
                orderItem.classList.remove('active');
                this.classList.remove('ic_arrow_up');
                this.classList.add('ic_arrow_down');
            } else {
                // 보일 때
                orderItem.classList.add('active');
                orderItem.style.display = 'block'; // display를 먼저 block으로 설정
                orderItem.style.height = '0'; // 초기 높이 설정
                setTimeout(() => {
                    orderItem.style.height = `${orderItem.scrollHeight}px`; // 내용물 높이로 확장
                }, 10);
                this.classList.remove('ic_arrow_down');
                this.classList.add('ic_arrow_up');

                // 애니메이션 종료 후 height 초기화 (내용물에 따라 유연하게 대응)
                orderItem.addEventListener(
                    'transitionend',
                    () => {
                        orderItem.style.height = 'auto';
                    },
                    { once: true }
                );
            }
        });
    });

    const payButtons = document.querySelectorAll('.pay-btn');
    const selectCreditCard = document.querySelector('.select_credit_card');
    const selectBox = document.querySelector('.select_credit_card_box');
    const selectElement1 = document.querySelector('.select');
    const arrowIcon1 = document.querySelector('.cardIcon');

    // 1. credit-card-btn 버튼을 눌렀을 때 select_credit_card 보이기
    payButtons.forEach((button) => {
        button.addEventListener('click', function () {
            // 다른 버튼의 선택 스타일 초기화
            payButtons.forEach((btn) => btn.classList.remove('pushed'));

            if (this.classList.contains('credit-card-btn')) {
                selectCreditCard.style.display = 'block'; // select_credit_card 보이기
            } else {
                selectCreditCard.style.display = 'none'; // 숨기기
            }

            // 현재 버튼 스타일 활성화
            this.classList.add('pushed');
        });
    });

    // 3. 셀렉트 외 영역 클릭 시 select_credit_card 숨기기
    document.addEventListener('click', (e) => {
        const isInsidePayBtnContainer = e.target.closest('.pay_btn_container');
        const isInsideSelectCard = e.target.closest('.select_credit_card');

        if (!isInsidePayBtnContainer && !isInsideSelectCard) {
            selectCreditCard.style.display = 'none'; // select_credit_card 숨기기
            payButtons.forEach((btn) => btn.classList.remove('pushed')); // 모든 버튼 스타일 초기화
        }
    });
});

document.addEventListener('scroll', () => {
    const cartSidebar = document.querySelector('.cart_sidebar');
    const asideContainer = document.querySelector('.aside_container');

    const sidebarRect = cartSidebar.getBoundingClientRect();
    const asideRect = asideContainer.getBoundingClientRect();

    // 부모 높이보다 아래로 내려가는 것을 제한
    if (asideRect.bottom > sidebarRect.bottom) {
        asideContainer.style.position = 'absolute';
        asideContainer.style.bottom = '0';
    } else {
        asideContainer.style.position = 'sticky';
        asideContainer.style.top = '50px';
    }
});