        const menuItems = document.querySelectorAll(".coupon_list_menu > ul > li");
        const underline = document.createElement("div");
        underline.classList.add("underline");
        document.querySelector(".coupon_list_menu > ul").appendChild(underline);

        const availableCoupon = document.querySelector(".available_coupon");
        const disappearCoupon = document.querySelector(".disappear_coupon");

        // 초기 위치 설정 (첫 번째 li 밑에 underline 배치)
        function updateUnderline(index) {
            underline.style.transform = `translateX(${index * 100}%)`;
        }

        // 초기 상태 설정
        function updateView() {
            if (menuItems[0].classList.contains("select")) {
            availableCoupon.style.display = "block"; // 사용 가능 쿠폰 보이기
            disappearCoupon.style.display = "none"; // 만료된 쿠폰 숨기기
            } else if (menuItems[1].classList.contains("select")) {
            availableCoupon.style.display = "none"; // 사용 가능 쿠폰 숨기기
            disappearCoupon.style.display = "block"; // 만료된 쿠폰 보이기
            }
        }

        // 초기 활성화 상태
        menuItems.forEach((item, index) => {
            item.addEventListener("click", () => {
                document.querySelector(".select").classList.remove("select"); // 기존 'select' 클래스 제거
                item.classList.add("select"); // 클릭된 요소에 'select' 클래스 추가
                updateUnderline(index); // 밑줄 위치 업데이트
                updateView();
            });
        });

        // 초기 밑줄 위치
        updateUnderline(0);
        updateView();