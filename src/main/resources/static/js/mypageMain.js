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

    const colours = [
        "#52aca2", "#bada55", "#999999", "#78d663", "#f1ab4f", "#f04f6b", "#ffda4f",
        "#55cdfc", "#aa7d42", "#ced5df", "#f7850a", "#935357", "#b190a5", "#90b19c",
        "#fdbdb8", "#ffe4e1", "#666666", "#93786f", "#f6c9b9", "#ecc2bf", "#aa5e40",
        "#aa5e40", "#408caa", "#489cbd", "#bada55", "#062542", "#998888", "#cdcdd2",
        "#cbc2bf", "#e7d5c9", "#e9d7c7", "#a2697d", "#86516b", "#dbdcff", "#feffa3",
        "#00b8ff", "#ff9966", "#a52a5e", "#008080", "#000000", "#01f6f1", "#ffc0cb"
    ];

    const contentColors = ["#555", "#fff"];

    // content 값을 위한 배열
    const contentOptions = ["", "-", "--", "---", "----", "-----", "------", "-------", "--------"];

    // 99개의 책을 랜덤 스타일로 생성
    const books = document.querySelectorAll('.book1');
    books.forEach((book, i) => {
      // 랜덤 색상 선택
      const randomColor = colours[Math.floor(Math.random() * colours.length)];

      // 랜덤 높이와 너비
      const randomHeight = Math.floor(Math.random() * 30) + 40; // 40px ~ 70px
      const randomWidth = Math.floor(Math.random() * 4) + 7; // 7px ~ 10px

      // 애니메이션 지연 시간
      const animationDelay = i * 0.002;

      // 랜덤 content 값
      const randomContent = contentOptions[Math.floor(Math.random() * contentOptions.length)];
      const randomContentColor = contentColors[Math.floor(Math.random() * contentColors.length)];

      // 스타일 적용
      book.style.backgroundColor = randomColor;
      book.style.height = `${randomHeight}px`;
      book.style.width = `${randomWidth}px`;
      book.style.animationDelay = `${animationDelay}s`;

      // ::before의 content 값 동적으로 설정
      const style = document.createElement('style');
      style.innerHTML = `
        .book1:nth-child(${i + 1})::before {
          content: "${randomContent}";
          color: ${randomContentColor};
        }
      `;
      document.head.appendChild(style);
    });

});