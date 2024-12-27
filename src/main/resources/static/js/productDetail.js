document.addEventListener("DOMContentLoaded", () => {
    Sticker.init('.sticker');

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
        const closeButton = modal.querySelector(".book_benefits_modal .ic_delete");
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

    // 별점 게이지 넓이 계산
    const rateElements = document.querySelectorAll(".rate");

    rateElements.forEach((rate) => {
        const scoreElement = rate.querySelector(".score_number");
        const gaugeElement = rate.querySelector(".gauge");

        // data-value 속성에서 점수 가져오기
        const score = parseInt(scoreElement.getAttribute("data-value"), 10);

        // 점수에 따른 width 계산
        let widthPercentage = 0;
        if (score == 0) widthPercentage = 0;
        else if (score <= 10) widthPercentage = 10;
        else if (score <= 19) widthPercentage = 20;
        else if (score <= 29) widthPercentage = 30;
        else if (score <= 39) widthPercentage = 40;
        else if (score <= 49) widthPercentage = 50;
        else if (score <= 59) widthPercentage = 60;
        else if (score <= 69) widthPercentage = 70;
        else if (score <= 79) widthPercentage = 80;
        else if (score <= 89) widthPercentage = 90;
        else if (score <= 100) widthPercentage = 100;

        // width 적용
        const gaugeWidth = (330 * widthPercentage) / 100;
        gaugeElement.style.width = `${gaugeWidth}px`;
    });

    // 리뷰 그리드 계산
    function resizeGridItem(item) {
        grid = document.getElementsByClassName("grid")[0];
        rowHeight = parseInt(window.getComputedStyle(grid).getPropertyValue('grid-auto-rows'));
        rowGap = parseInt(window.getComputedStyle(grid).getPropertyValue('grid-row-gap'));
        rowSpan = Math.ceil((item.querySelector('.review_box').getBoundingClientRect().height + rowGap+50) / (rowHeight + rowGap));
        item.style.gridRowEnd = "span " + rowSpan;

        console.log(Math.ceil((item.querySelector('.review_box').getBoundingClientRect().height + rowGap+50))); // 여기에서 높이 확인
    }

    function resizeAllGridItems() {
        allItems = document.getElementsByClassName("review_item");
        for (x = 0; x < allItems.length; x++) {
            resizeGridItem(allItems[x]);
        }
    }

    function resizeInstance(instance) {
        item = instance.elements[0];
        resizeGridItem(item);
    }

    window.onload = resizeAllGridItems();
    window.addEventListener("resize", resizeAllGridItems);

    allItems = document.getElementsByClassName("review_item");
    for (x = 0; x < allItems.length; x++) {
        imagesLoaded(allItems[x], resizeInstance);
    }

    // 리뷰 클릭 시 크기 조정 계산
    const reviewItems = document.querySelectorAll(".review_item");

    // 반복 색상 배열
    const colors = ["#F2E1A9", "white", "#ABBC86"];

    // 각 review_item에 색상 설정
    reviewItems.forEach((item, index) => {
        const color = colors[index % colors.length]; // 순환적으로 색상 선택
        item.style.backgroundColor = color; // 배경색 설정
    });

    // 좋아요 버튼
    //https://codepen.io/nodws/pen/qZLBrd?editors=1011
    //https://codepen.io/akm2/pen/rHIsa

    // 모든 .panel 요소 선택
    var panels = document.querySelectorAll('.panel');

    // 각 .panel 요소에 클릭 이벤트 바인딩
    panels.forEach(function(panel) {
        panel.addEventListener('click', function() {
            // .panel 안의 a.like1 요소 선택
            var likeAnchor = panel.querySelector('a.like1');
            if (likeAnchor) {
                // 클릭된 요소에 active 클래스 토글
                likeAnchor.classList.toggle('active');
            }
        });
    });
});