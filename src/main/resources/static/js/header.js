document.addEventListener('DOMContentLoaded', function () {
    const btnNav = document.querySelector('.btn_nav');
    const iconMenu = btnNav.querySelector('.ic_menu');
    const iconClose = btnNav.querySelector('.ic_menu_close');

    const modal = document.querySelector('.modal_main_menu');
    const body = document.querySelector('header');
    const overlay = document.createElement('div'); // 오버레이 생성
    overlay.classList.add('overlay'); // 오버레이에 클래스 추가
    body.appendChild(overlay);

    const openModal = () => {
        iconMenu.style.display = 'none';
        iconClose.style.display = 'block';
        modal.style.display = 'flex';
        overlay.style.display = 'block';

        setTimeout(() => {
            modal.style.opacity = '1';
            overlay.style.opacity = '1';
        }, 10);
    };

    const closeModal = () => {
        iconMenu.style.display = 'block';
        iconClose.style.display = 'none';
        modal.style.opacity = '0';
        overlay.style.opacity = '0';

        setTimeout(() => {
            modal.style.display = 'none';
            overlay.style.display = 'none';
        }, 300);
    };

    btnNav.addEventListener('click', function () {
        const isMenuVisible = iconMenu.style.display !== 'none';
        if (isMenuVisible) { openModal(); }
        else { closeModal(); }
    });

    // 오버레이 클릭 시 모달 닫기
    overlay.addEventListener('click', closeModal);

    // 모달 내부 클릭 시 이벤트 전파 차단
    modal.addEventListener('click', function (e) {
        e.stopPropagation(); // 이벤트가 부모 요소로 전달되지 않도록 차단
    });

    const menuItems = document.querySelectorAll('.category1_list li');
    const contents = document.querySelectorAll('.menu-content');

    menuItems.forEach((item) => {
        item.addEventListener('click', function () {
            contents.forEach((content) => content.classList.remove('active')); // 모든 콘텐츠 숨김
            menuItems.forEach((menuItem) => menuItem.classList.remove('active')); // 모든 메뉴 항목에서 active 클래스 제거

            // 해당 카테고리 콘텐츠만 보이기
            const category = item.getAttribute('data-category');
            const targetContent = document.getElementById(category);
            if (targetContent) {
                targetContent.classList.add('active');
            }

            // 클릭된 메뉴 항목에 active 클래스 추가
            item.classList.add('active');
        });
    });

    const menuList = document.querySelector('.category1_list'); // 메뉴 리스트
    const nextButton = document.querySelector('.ic_next_area'); // 다음 버튼
    const prevButton = document.querySelector('.ic_prev_area'); // 이전 버튼

    const slideWidth = 1000; // 한 번 이동하는 거리
    let currentPosition = 0; // 현재 위치 값

    // 다음 버튼 클릭 이벤트
    nextButton.addEventListener('click', () => {
        const maxMove = -(menuList.scrollWidth - menuList.parentElement.offsetWidth); // 최대 이동 가능한 값
        if (currentPosition > maxMove) {
            currentPosition -= slideWidth;
            if (currentPosition < maxMove) currentPosition = maxMove; // 범위 초과 방지
            menuList.style.transform = `translateX(${currentPosition}px)`;
            menuList.style.transition = 'transform 1s ease-in-out'; // 부드러운 애니메이션
        }
    });

    // 이전 버튼 클릭 이벤트
    prevButton.addEventListener('click', () => {
        if (currentPosition < 0) {
            currentPosition += slideWidth;
            if (currentPosition > 0) currentPosition = 0; // 범위 초과 방지
            menuList.style.transform = `translateX(${currentPosition}px)`;
            menuList.style.transition = 'transform 1s ease-in-out'; // 부드러운 애니메이션
        }
    });
          
    let lastScrollY = 0; // 마지막 스크롤 위치
    let ticking = false; // 애니메이션 프레임 동작 여부 플래그

    // 스크롤 이벤트 추가
    window.addEventListener("scroll", function () {
    lastScrollY = window.scrollY; // 현재 스크롤 위치 저장

    // requestAnimationFrame이 진행 중이 아닐 때만 실행
    if (!ticking) {
        window.requestAnimationFrame(function () {
        handleScroll(lastScrollY); // 스크롤 위치에 따른 처리
        ticking = false; // requestAnimationFrame 완료 후 플래그 해제
        });
        ticking = true; // requestAnimationFrame이 진행 중임을 표시
    }
    });

    // 스크롤 위치에 따른 클래스 처리 함수
    function handleScroll(scrollY) {
    const header = document.querySelector("header"); // 헤더 요소 선택
    if (!header) return; // 헤더 요소가 없을 경우 종료

    if (scrollY > 100) {
        header.classList.remove("default-header");
        header.classList.add("sticky-header");
    } else {
        header.classList.remove("sticky-header");
        header.classList.add("default-header");
    }
    }


    // 장바구니 아이콘 관련하여 준희가 넣은 코드.
    const cartIcon = document.querySelector('.ic_cart');
    if(cartIcon.dataset.authorize != "anonymousUser") {
        let mno = document.querySelector('.ic_cart').dataset.mno;
        console.log("mno", mno);

        displayCartAmount(mno);
    }
});

// 장바구니 아이콘 관련하여 준희가 넣은 비동기 코드.
async function displayCartAmount(mno) {
    try {
        await getCartAmountFromServer(mno).then(result => {
            document.querySelector('.cart_item_count').innerText = result;
        })
    } catch (error) {
        console.log("Error during displayCartAmount.", error);
    }
}

// 장바구니 아이콘 관련하여 준희가 넣은 비동기 코드.
async function getCartAmountFromServer(mnoVal) {
    const url = "/payment/header-cart";

    const config = {
        method: "POST",
        headers: { "Content-Type": "application/json; charset=utf-8" },
        body: JSON.stringify(mnoVal)
    }

    const response = await fetch(url, config);
    const result = await response.text();
    return result;
}
