document.addEventListener('DOMContentLoaded', function () {
    const btnNav = document.querySelector('.btn_nav');
    const iconMenu = btnNav.querySelector('.ic_menu');
    const iconClose = btnNav.querySelector('.ic_menu_close');
    const header = document.querySelector('header'); // 헤더 요소 선택

    const modal = document.querySelector('.modal_main_menu');
    const overlay = document.createElement('div'); // 오버레이 생성
    overlay.classList.add('overlay'); // 오버레이에 클래스 추가
    header.appendChild(overlay);

    const openModal = () => {
        iconMenu.style.display = 'none';
        iconClose.style.display = 'block';
        modal.style.display = 'flex';
        overlay.style.display = 'block';

        setTimeout(() => {
            modal.style.opacity = '1';
            header.style.zIndex = '100';
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
            header.style.zIndex = '99';
        }, 300);
    };

    btnNav.addEventListener('click', function () {
        const isMenuVisible = iconMenu.style.display !== 'none';
        if (isMenuVisible) {
            openModal();
        } else {
            closeModal();
        }
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
        console.log("Scroll position updated:", lastScrollY); // 디버깅용

        // requestAnimationFrame이 진행 중이 아닐 때만 실행
        if (!ticking) {
            window.requestAnimationFrame(function () {
                console.log("Handling scroll for:", lastScrollY); // 디버깅용
                handleScroll(lastScrollY); // 스크롤 위치에 따른 처리
                ticking = false; // requestAnimationFrame 완료 후 플래그 해제
            });
            ticking = true; // requestAnimationFrame이 진행 중임을 표시
        }
    });

    // 스크롤 위치에 따른 클래스 처리 함수
    const container = document.querySelector('.container'); // 스크롤 영역인 div

    function handleScroll(scrollY) {
        if (!header) {
            console.error('헤더 요소를 찾을 수 없음');
            return;
        }
        console.log('스크롤 위치:', scrollY); // 스크롤 값 확인
        console.log('현재 헤더 클래스:', header.className); // 현재 클래스 확인

        if (scrollY > 250) {
            header.classList.remove("default-header");
            header.classList.add("sticky-header");
        } else {
            header.classList.remove("sticky-header");
            header.classList.add("default-header");
        }
    }

    if (container) {
        container.addEventListener("scroll", function () {
            const scrollY = container.scrollTop; // container의 스크롤 위치
            console.log('스크롤 이벤트 트리거됨'); // 스크롤 이벤트 확인용
            handleScroll(scrollY);
        });
    } else {
        console.error('container 요소를 찾을 수 없음');
    }


    const bell = document.querySelector('.user_menu_item.bell');
    const notificationList = document.querySelector('.notification_list');

    // 알림 리스트 토글 함수
    const toggleNotificationList = () => {
        const isVisible = notificationList.style.display === 'block';
        if (isVisible) {
            notificationList.style.display = 'none'; // 숨기기
            header.style.zIndex = ''; // z-index 클래스 제거
        } else {
            notificationList.style.display = 'block'; // 보이기
            header.style.zIndex = '100'; // z-index 클래스 추가
        }
    };

    // bell 클릭 이벤트
    bell.addEventListener('click', (event) => {
        event.stopPropagation(); // 이벤트 전파 차단
        toggleNotificationList();
    });

    // 바깥 클릭 시 알림 리스트 닫기
    const closeNotificationList = (event) => {
        // notification_list나 bell 내부에서 클릭된 경우 닫지 않음
        if (!notificationList.contains(event.target) && !bell.contains(event.target)) {
            notificationList.style.display = 'none';
            header.style.zIndex = '';
        }
    };

    // notification_list 내부에서 클릭 시 닫지 않도록 설정
    notificationList.addEventListener('click', (event) => {
        event.stopPropagation(); // 이벤트 전파 차단
    });

    // 바깥 클릭 이벤트
    document.addEventListener('click', closeNotificationList);

    const notificationItems = document.querySelectorAll('.notification_menu');

    // notification_list_content 안의 콘텐츠 가져오기
    const notificationContent = document.querySelector('.news_content');
    const benefitContent = document.querySelector('.benefit_content');

    // 각 아이템에 클릭 이벤트 추가
    notificationItems.forEach(item => {
        item.addEventListener('click', () => {
            // 모든 아이템에서 select 클래스 제거
            notificationItems.forEach(i => {
                i.classList.remove('select');
            });

            // 클릭된 아이템에 select 클래스 추가
            item.classList.add('select');

            // 클릭된 아이템에 따라 콘텐츠 보이기/숨기기
            if (item.classList.contains('news')) {
                notificationContent.style.display = 'block'; // 알림 보이기
                benefitContent.style.display = 'none';       // 혜택 숨기기
            } else if (item.classList.contains('benefit')) {
                notificationContent.style.display = 'none';  // 알림 숨기기
                benefitContent.style.display = 'block';      // 혜택 보이기
            }
        });
    });
});