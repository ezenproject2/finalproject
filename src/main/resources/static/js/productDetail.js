document.addEventListener("DOMContentLoaded", () => {
    // 책 이미지 움직임 이벤트
    const wrapper = document.querySelector('.book_thum_wrap');
    const book = document.querySelector('.book-items');

    wrapper.addEventListener('mousemove', (e) => {
        const rect = wrapper.getBoundingClientRect();
        const x = e.clientX - rect.left;
        const y = e.clientY - rect.top;

        const rotateX = ((y / rect.height) - 0.5) * -20;
        const rotateY = ((x / rect.width) - 0.5) * 20;

        book.style.transform = `rotateX(${rotateX}deg) rotateY(${rotateY}deg)`;
    });

    wrapper.addEventListener('mouseleave', () => {
        book.style.transform = 'rotateX(0deg) rotateY(0deg)';
    });

    // 무이자 할부 클릭 이벤트
    // 모든 .book_point 요소를 선택
    const bookPoints = document.querySelectorAll(".book_point");

    bookPoints.forEach((bookPoint) => {
        const modal = bookPoint.querySelector(".book_point_modal"); // 해당 부모 안의 모달
        const toggleIcon = bookPoint.querySelector(".ic_carousel_down_white"); // 해당 부모 안의 아이콘

        if (!modal || !toggleIcon) {
            console.error("모달 또는 아이콘 요소를 찾을 수 없습니다.");
            return;
        }

        // 무이자 할부 클릭 이벤트
        bookPoint.addEventListener("click", function () {
            // 모달창 열기/닫기
            modal.style.display = modal.style.display === "block" ? "none" : "block";

            // 아이콘 클래스 변경
            if (modal.style.display === "block") {
                toggleIcon.classList.replace("ic_carousel_down_white", "ic_carousel_up_white");
            } else {
                toggleIcon.classList.replace("ic_carousel_up_white", "ic_carousel_down_white");
            }
        });

        // 모달 닫기 이벤트
        const closeButton = modal.querySelector(".ic_delete");
        if (closeButton) {
            closeButton.addEventListener("click", function (event) {
                event.stopPropagation(); // 클릭 이벤트 전파 방지
                modal.style.display = "none";
                toggleIcon.classList.replace("ic_carousel_up_white", "ic_carousel_down_white");
            });
        }
    });

    // 책 이미지 전체보기
    const fullViewBtn = document.querySelector(".full_view_btn");
    const modal = document.getElementById("imageModal");
    const fullImage = document.getElementById("fullImage");
    const thumbnail = document.querySelector(".thumbnail");

    // 전체보기 버튼 클릭 이벤트
    fullViewBtn.addEventListener("click", () => {
        fullImage.src = thumbnail.src; // 원본 이미지 가져오기
        modal.style.display = "flex"; // 모달창 보이기
    });

    // 모달 영역 클릭 시 닫기
    modal.addEventListener("click", (e) => {
        if (e.target === modal) { // 모달 배경 클릭 시에만 닫힘
            modal.style.display = "none";
        }
    });

    // 모달 초기 상태를 강제로 display: none으로 설정 (안전장치)
    modal.style.display = "none";

    // 매장 재고 버튼
    const openModalBtn = document.getElementById("openModal");
    const closeModalBtn = document.getElementById("closeModal");
    const modalOverlay = document.getElementById("modalOverlay");

    // 모달 열기
    openModalBtn.addEventListener("click", () => {
        modalOverlay.style.display = "block";
    });

    // 모달 닫기
    closeModalBtn.addEventListener("click", () => {
        modalOverlay.style.display = "none";
    });

    // 배경 클릭 시 모달 닫기
    modalOverlay.addEventListener("click", (e) => {
        if (e.target === modalOverlay) {
            modalOverlay.style.display = "none";
        }
    });

    // +, - 버튼(하단의 구매창)
    const plusBtn = document.querySelector('.plus_btn');
    const minusBtn = document.querySelector('.minus_btn');
    const number = document.getElementById('number');
    let value = 1; // 초기값 설정

    // + 버튼 클릭 시
    plusBtn.addEventListener('click', () => {
        value++;
        number.textContent = value;
    });

    // - 버튼 클릭 시
    minusBtn.addEventListener('click', () => {
        if (value > 1) {
            value--;
            number.textContent = value;
        }
    });
});