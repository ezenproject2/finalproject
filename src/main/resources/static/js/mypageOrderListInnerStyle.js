document.addEventListener("DOMContentLoaded", () => {
    const openModalBtns = document.querySelectorAll('.review_btn'); // 버튼들 모두 선택
    const closeModalBtn = document.getElementById("closeModal");
    const modalOverlay = document.querySelector('.modal_overlay');

    console.log(openModalBtns);

    // 모든 버튼에 클릭 이벤트 추가
    openModalBtns.forEach((btn, index) => {
        console.log(`Button ${index + 1} 이벤트 리스너 추가됨`); // 각 버튼에 리스너 추가 확인
        btn.addEventListener("click", () => {
            console.log(`Button ${index + 1} 클릭됨`);
            modalOverlay.style.display = "block"; // 모달 보이게
        });
    });

    // 추후 각 리뷰 버튼에 책 정보 data 추가해서 getAttribute로 갖고 와야 함

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

    // 별점
    const stars = document.querySelectorAll('.star');
    const ratingValue = document.querySelector('.rating-value');
    const rateStars = document.querySelector('.rate_stars'); // 별점 영역

    let currentHoverIndex = -1; // 현재 호버 중인 별의 인덱스
    let isHalfHover = false; // 현재 반쪽 여부
    let selectedRating = 0; // ** 선택된 별점 값 **

    stars.forEach((star, index) => {
        // 별점 호버 이벤트
        star.addEventListener('mousemove', (e) => {
            const rect = star.getBoundingClientRect();
            const offsetX = e.clientX - rect.left;
            const isHalf = offsetX < rect.width / 2; // 반쪽 여부 확인

            // 중복 호출 방지
            if (currentHoverIndex === index && isHalfHover === isHalf) return;

            currentHoverIndex = index; // 현재 호버 중인 별 업데이트
            isHalfHover = isHalf; // 현재 반쪽 여부 업데이트

            // 모든 별 초기화
            stars.forEach((s, i) => {
                const gradientId = `starGradient${i + 1}`; // 각 별의 고유 ID
                const gradient = document.querySelector(`#${gradientId} stop:nth-child(2)`); // 회색 부분
                const firstStop = document.querySelector(`#${gradientId} stop:nth-child(1)`); // 노란 부분

                if (i < index) {
                    firstStop.setAttribute('offset', '100%'); // 이전 별은 전체 노란색
                    gradient.setAttribute('offset', '100%');
                } else if (i === index && isHalf) {
                    firstStop.setAttribute('offset', '50%'); // 현재 별은 반쪽 노란색
                    gradient.setAttribute('offset', '50%');
                } else if (i === index && !isHalf) {
                    firstStop.setAttribute('offset', '100%'); // 현재 별은 전체 노란색
                    gradient.setAttribute('offset', '100%');
                } else {
                    firstStop.setAttribute('offset', '0%'); // 나머지는 회색
                    gradient.setAttribute('offset', '0%');
                }
            });

            // 현재 별점 표시
            const hoverRating = isHalf ? index + 0.5 : index + 1;
            ratingValue.textContent = `${hoverRating}`;
        });

        // 별점 클릭 이벤트
        star.addEventListener('click', (e) => {
            const rect = star.getBoundingClientRect();
            const offsetX = e.clientX - rect.left;
            const isHalf = offsetX < rect.width / 2;
            const clickedRating = isHalf ? index + 0.5 : index + 1;

            // 같은 점수를 클릭하면 0점으로 초기화
            if (selectedRating === clickedRating) {
                selectedRating = 0;
                updateStars(-1, false);
                ratingValue.textContent = '0';
            } else {
                // 선택된 별점 값 저장
                selectedRating = clickedRating;
                ratingValue.textContent = `${selectedRating}`;

                // 별점 확정
                updateStars(index, isHalf);
            }
        });

        // 별점에서 마우스를 뺐을 때 초기화
        star.addEventListener('mouseleave', () => {
            if (selectedRating === 0) {
                // 별점 선택이 없을 때만 초기화
                stars.forEach((s, i) => {
                    const gradientId = `starGradient${i + 1}`;
                    const gradient = document.querySelector(`#${gradientId} stop:nth-child(2)`);
                    const firstStop = document.querySelector(`#${gradientId} stop:nth-child(1)`);

                    // 모든 별을 회색으로
                    firstStop.setAttribute('offset', '0%');
                    gradient.setAttribute('offset', '0%');
                });

                ratingValue.textContent = '0'; // 점수 초기화
            }

            currentHoverIndex = -1; // 호버 상태 초기화
            isHalfHover = false; // 반쪽 여부 초기화
        });
    });

    // `rate_stars` 영역 밖으로 나갔을 때 초기화
    document.addEventListener('mousemove', (e) => {
        const rect = rateStars.getBoundingClientRect(); // 별점 영역
        const isOutside =
            e.clientX < rect.left ||
            e.clientX > rect.right ||
            e.clientY < rect.top ||
            e.clientY > rect.bottom;

        if (isOutside && selectedRating === 0 && !rateStars.contains(e.target)) {
            stars.forEach((s, i) => {
                const gradientId = `starGradient${i + 1}`;
                const gradient = document.querySelector(`#${gradientId} stop:nth-child(2)`);
                const firstStop = document.querySelector(`#${gradientId} stop:nth-child(1)`);

                // 모든 별을 회색으로
                firstStop.setAttribute('offset', '0%');
                gradient.setAttribute('offset', '0%');
            });

            ratingValue.textContent = '0'; // 점수 초기화
            currentHoverIndex = -1; // 상태 초기화
            isHalfHover = false;
        }
    });

    // 리뷰 글자 수 세기
    const contentTextarea = document.querySelector("#review");
    const textCountSpan = document.querySelector(".text_count span");

    // 글자 수 업데이트 함수
    contentTextarea.addEventListener("input", () => {
        const currentLength = contentTextarea.value.length; // 현재 글자 수
        const maxLength = contentTextarea.getAttribute("maxlength"); // 최대 글자 수
        textCountSpan.textContent = `${currentLength}/${maxLength}`; // 글자 수 업데이트
    });
});