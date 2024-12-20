document.addEventListener("DOMContentLoaded", () => {
    const switchBtn = document.querySelector(".switch-btn");
    const circle = document.querySelector(".circle");
    const leftLabel = document.querySelector(".label.left");
    const rightLabel = document.querySelector(".label.right");

    let isRight = false; // 스위치 상태

    // 초기 클래스 설정
    leftLabel.classList.add("active");

    // 스위치 버튼 클릭 이벤트
    switchBtn.addEventListener("click", () => {
        if (isRight) {
            // 왼쪽으로 이동
            circle.style.left = "4px";
            leftLabel.classList.add("active");
            rightLabel.classList.remove("active");
        } else {
            // 오른쪽으로 이동
            circle.style.left = "52px";
            rightLabel.classList.add("active");
            leftLabel.classList.remove("active");
        }
        isRight = !isRight; // 상태 변경
    });

    // 배너 슬라이드
    // const slide = document.querySelector(".banner_slide");
    // const leftBtn = document.querySelector(".ic_left");
    // const rightBtn = document.querySelector(".ic_right");
    // const bannerItems = document.querySelectorAll(".banner_item");

    // const itemWidth = bannerItems[0].offsetWidth + 25; // 아이템 너비 + margin-right(25px)
    // const totalItems = bannerItems.length;
    // const maxIndex = totalItems - 3; // 한 번에 보이는 아이템 수(3)를 제외한 최대 이동 인덱스

    // let currentIndex = 0; // 현재 슬라이드 위치

    // // 오른쪽 버튼 클릭
    // rightBtn.addEventListener("click", () => {
    //     if (currentIndex < maxIndex) {
    //         currentIndex++;
    //         updateSlidePosition();
    //     }
    // });

    // // 왼쪽 버튼 클릭
    // leftBtn.addEventListener("click", () => {
    //     if (currentIndex > 0) {
    //         currentIndex--;
    //         updateSlidePosition();
    //     }
    // });

    // // 슬라이드 위치 업데이트
    // function updateSlidePosition() {
    //     const translateX = -(currentIndex * itemWidth); // 아이템 1개의 너비만큼 이동
    //     slide.style.transform = `translateX(${translateX}px)`;
    // }
    const slide = document.querySelector(".banner_slide");
    const leftBtn = document.querySelector(".ic_left");
    const rightBtn = document.querySelector(".ic_right");
    const bannerItems = document.querySelectorAll(".banner_item");

    let itemWidth = bannerItems[0].offsetWidth + 25; // 초기값 설정
    const totalItems = bannerItems.length;
    let maxIndex = totalItems - 3; // 한 번에 보이는 아이템 수(3)

    let currentIndex = 0;

    // 슬라이드 위치 업데이트
    function updateSlidePosition() {
        const translateX = -(currentIndex * itemWidth);
        slide.style.transform = `translateX(${translateX}px)`;
    }

    // 오른쪽 버튼 클릭
    rightBtn.addEventListener("click", () => {
        if (currentIndex < maxIndex) {
            currentIndex++;
            updateSlidePosition();
        }
    });

    // 왼쪽 버튼 클릭
    leftBtn.addEventListener("click", () => {
        if (currentIndex > 0) {
            currentIndex--;
            updateSlidePosition();
        }
    });

    // 화면 리사이즈 이벤트: 아이템 너비 재계산
    window.addEventListener("resize", () => {
        itemWidth = bannerItems[0].offsetWidth + 25; // 리사이즈 시 재계산
        maxIndex = totalItems - 3; // 필요하면 maxIndex도 재조정
        updateSlidePosition(); // 현재 위치 반영
    });

});