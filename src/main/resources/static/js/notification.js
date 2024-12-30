console.log("SSE JS 연결");

// let unreadCount = 0; // 읽지 않은 알림 개수를 저장할 변수
// let dataList = []; // 알림 목록을 저장할 배열

// // SSE 연결 설정
// const eventSource = new EventSource('/notification/subscribe/' + 1);

// // SSE로 알림 수신
// eventSource.onmessage = function (event) {
//     const notification = JSON.parse(event.data);
//     console.log('New Notification:', notification);

//     // 기존 알림 목록에 이미 해당 nfno가 있는지 확인
//     if (!dataList.some(item => item.nfno === notification.nfno)) {
//         dataList.push(notification); // 중복되지 않으면 알림 추가
//     }

//     const currentDate = new Date();
//     console.log(currentDate);
//     console.log(dataList);

// };

// // 알림창 관련
// const bell = document.querySelector('.user_menu_item.bell');
// const notificationList = document.querySelector('.notification_list');

// // 알림 리스트 토글 함수
// const toggleNotificationList = () => {
//     const isVisible = notificationList.style.display === 'block';
//     if (isVisible) {
//         notificationList.style.display = 'none'; // 숨기기
//         header.style.zIndex = ''; // z-index 클래스 제거
//     } else {
//         notificationList.style.display = 'block'; // 보이기
//         header.style.zIndex = '100'; // z-index 클래스 추가
//     }
// };

// bell.addEventListener('click', () => {
//     toggleNotificationList();
// });

// // 바깥 클릭 시 알림 리스트 닫기
// const closeNotificationList = (event) => {
//     // notification_list나 bell 내부에서 클릭된 경우 닫지 않음
//     if (!notificationList.contains(event.target) && !bell.contains(event.target)) {
//         notificationList.style.display = 'none';
//         header.style.zIndex = '';
//     }
// };

// // notification_list 내부에서 클릭 시 닫지 않도록 설정
// notificationList.addEventListener('click', (event) => {
//     event.stopPropagation(); // 이벤트 전파 차단
// });

// // 바깥 클릭 이벤트
// document.addEventListener('click', closeNotificationList);


// // 알림창 관련
// const notificationItems = document.querySelectorAll('.notification_menu');

// // notification_list_content 안의 콘텐츠 가져오기
// const notificationContent = document.querySelector('.news_content');
// const benefitContent = document.querySelector('.benefit_content');

// // 각 아이템에 클릭 이벤트 추가
// notificationItems.forEach(item => {
//     item.addEventListener('click', () => {
//         // 모든 아이템에서 select 클래스 제거
//         notificationItems.forEach(i => {
//             i.classList.remove('select');
//         });

//         // 클릭된 아이템에 select 클래스 추가
//         item.classList.add('select');

//         // 클릭된 아이템에 따라 콘텐츠 보이기/숨기기
//         if (item.classList.contains('news')) {
//             notificationContent.style.display = 'block'; // 알림 보이기
//             benefitContent.style.display = 'none';       // 혜택 숨기기
//         } else if (item.classList.contains('benefit')) {
//             notificationContent.style.display = 'none';  // 알림 숨기기
//             benefitContent.style.display = 'block';      // 혜택 보이기
//         }
//     });
// });





//--------------------------------------------------------------------------------
// 알림 아이콘 클릭 시 드롭다운 열기
// const notificationIcon = document.getElementById('notification-icon');
// const notificationDropdown = document.getElementById('notification-dropdown');

// notificationIcon.addEventListener('click', () => {
//     notificationDropdown.classList.toggle('show'); // 드롭다운 토글
// });

// 드롭다운에 알림 목록 업데이트
// function updateNotificationDropdown() {
//     const dropdownContent = document.getElementById('notification-dropdown-content');
//     dropdownContent.innerHTML = ''; // 기존 내용 지우기

//     notificationList.forEach(notification => {
//         const notificationElement = document.createElement('div');
//         notificationElement.classList.add('notification-item');
//         notificationElement.textContent = notification.message;

//         // 알림 상태에 따라 스타일 변경
//         if (notification.status === 'UNREAD') {
//             notificationElement.classList.add('unread'); // 읽지 않은 알림 강조
//             notificationElement.addEventListener('click', () => {
//                 markAsRead(notification.nfno); // 클릭하면 읽은 상태로 변경
//             });
//         } else {
//             notificationElement.classList.add('read'); // 읽은 알림
//         }

//         dropdownContent.appendChild(notificationElement);
//     });
// }

// 알림 상태를 읽음으로 변경하는 함수
function markAsRead(nfno) {
    fetch(`/notification/update/${nfno}`, {
        method: 'POST'
    }).then(response => {
        // 읽음 처리 후, 상태 업데이트
        const notification = notificationList.find(n => n.nfno === nfno);
        if (notification) {
            notification.status = 'READ'; // 상태 변경
            unreadCount--;
            updateUnreadCount();
            updateNotificationDropdown();
        }
    });
}

// // 읽지 않은 알림 개수 업데이트
// function updateUnreadCount() {
//     unreadCount = notificationList.filter(notification => notification.status === 'UNREAD').length;
//     const unreadCountElement = document.getElementById('unread-count');
//     unreadCountElement.textContent = unreadCount > 0 ? unreadCount : ''; // 0일 경우 빈 문자열로 처리
// }

// document.getElementById("def").addEventListener("click", () => {
//     let text = document.getElementById("abc").value;

//     insert(3, text);
// })

// async function insert(mno, text) {
//     try {
//         const url = "/review/insertTest/" + mno + "/" + text
//         const resp = await fetch(url);
//         const result = await resp.text();
//         return result;
//     } catch (error) {
//         console.log(error);
//     }
// }