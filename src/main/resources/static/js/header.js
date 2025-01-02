document.addEventListener('DOMContentLoaded', function () {
    const btnNav = document.querySelector('.btn_nav');
    const iconMenu = btnNav.querySelector('.ic_menu');
    const iconClose = btnNav.querySelector('.ic_menu_close');
    const header = document.querySelector('header'); // 헤더 요소 선택

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
            header.style.zIndex = '100';
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

    //----[알림]-------------------------------------------------------------------------------------------------------------
    const eventSource = new EventSource('/notification/subscribe/' + mnoData);

    let unreadCount = 0; // 읽지 않은 알림 개수를 저장할 변수
    let dataList = []; // 알림 목록을 저장할 배열

    // (차민주) SSE로 알림 수신
    eventSource.onmessage = function (event) {
        if(mnoData != -1){
            // console.log("데이터 수신 받음");
            const notification = JSON.parse(event.data);
            
            // 기존 알림 목록에 이미 해당 nfno가 있는지 확인
            if (!dataList.some(item => item.nfno === notification.nfno)) {
                dataList.push(notification); // 중복되지 않으면 알림 추가
                dataList.sort((a, b) => b.nfno - a.nfno); // 추가 후 nfno 순으로 정렬 (최신알림이 위로 올라가도록)
            }

            unreadCount = dataList.filter(notification => notification.status === 'unread').length;
            // console.log(unreadCount);
            // console.log(dataList);
            // 알림박스에 데이터 추가하는 메서드 제작
            updateNotificationBox();
            document.getElementById("count_bell").innerText = unreadCount;
            if(unreadCount <= 0){
                document.getElementById("count_bell").style.display = 'none';
            }else{
                document.getElementById("count_bell").style.display = 'inline';
            }
        }
    };

    // (차민주) 알림창 내용 추가하는 메서드
    function updateNotificationBox() {
        const notificationBox = document.getElementById('notification_contents');
        notificationBox.innerHTML = '';
    
        dataList.forEach(notificationVO => {
            // 읽음/안읽음 처리
            let read = notificationVO.status;
    
            // 날짜 형식 전환
            let date = new Date(notificationVO.regAt);
            let month = date.getMonth() + 1;
            let day = date.getDate();
            let dateStr = `${month}월 ${day}일`;
    
            // 알림 타입 클래스명 지정
            let typeClassName = "";
            let customHref = "";
            if (notificationVO.type == "주문") {
                typeClassName = "ic_noti_delivery";
                customHref = "/mypage/order-list";
            } else if (notificationVO.type == "픽업") {
                typeClassName = "ic_noti_pickup";
                customHref = "/mypage/order-list";
            } else if (notificationVO.type == "쿠폰") {
                typeClassName = "ic_noti_coupon";
                customHref = "/mypage/coupon";
            } else if (notificationVO.type == "회원") {
                typeClassName = "ic_noti_grade";
                customHref = "/mypage/main";
            } else if (notificationVO.type == "리뷰") {
                typeClassName = "ic_noti_review";
                customHref = "/product/detail?isbn=" + notificationVO.isbn;
            }
    
            // a 태그 생성
            const link = document.createElement('a');
            link.href = customHref;  // 링크 주소
            link.classList.add('notification_content');
            link.classList.add(read);  // '읽음' 상태 클래스 추가
            link.dataset.nfno = notificationVO.nfno; // nfno를 dataset에 추가
    
            // 헤더 부분 생성
            const headerDiv = document.createElement('div');
            headerDiv.classList.add('notification_content_header');
    
            const titleWrapSpan = document.createElement('span');
            titleWrapSpan.classList.add('noti_title_wrap');
            
            const icon = document.createElement('i');
            icon.classList.add('ic', typeClassName);  // 아이콘 클래스 추가
            titleWrapSpan.appendChild(icon);
            
            const titleSpan = document.createElement('span');
            titleSpan.classList.add('noti_title');
            titleSpan.textContent = notificationVO.type; // 알림 타입
    
            titleWrapSpan.appendChild(titleSpan);
    
            const dateSpan = document.createElement('span');
            dateSpan.classList.add('noti_date');
            dateSpan.textContent = dateStr; // 날짜
    
            headerDiv.appendChild(titleWrapSpan);
            headerDiv.appendChild(dateSpan);
    
            // 본문 부분 생성
            const contentDiv = document.createElement('div');
            contentDiv.classList.add('notification_content_content');
    
            const messageSpan = document.createElement('span');
            messageSpan.textContent = notificationVO.message; // 알림 메시지
    
            contentDiv.appendChild(messageSpan);
    
            // 각 div들을 a 태그에 append
            link.appendChild(headerDiv);
            link.appendChild(contentDiv);
    
            // 생성된 a 태그를 notificationBox에 추가
            notificationBox.appendChild(link);
    
            // 이벤트 리스너 추가
            link.addEventListener('click', function(event) {
                event.preventDefault(); // 기본 동작인 페이지 이동을 막음
    
                // '읽음' 처리
                if(link.classList.contains('unread')){
                    markAsReadToServer(link.dataset.nfno).then(result =>{
                        if(result == "1"){
                            link.classList.remove('unread');
                            link.classList.add('read');
                        }
                    });
                }
    
                // 페이지 이동
                window.location.href = link.href; // 이 링크의 href로 페이지 이동
            });
        });
    }
    
    // 알림창 관련
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

    bell.addEventListener('click', () => {
        if(mnoData == -1){return;}
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

    // 알림창 관련
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


    // 장바구니 아이콘 관련하여 준희가 넣은 코드.
    const cartIcon = document.querySelector('.ic_cart');
    if (cartIcon.dataset.authorize != "anonymousUser") {
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

async function markAsReadToServer(nfno) {
    try{
        const url = "/notification/update/" + nfno;
        const resp = await fetch(url);
        const result = await resp.text();
        return result;
    } catch (error) {
        console.log(error);
    }
}

