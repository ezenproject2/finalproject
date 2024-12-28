
console.log("SSE JS 연결");

let unreadCount = 0; // 읽지 않은 알림 개수를 저장할 변수
let notificationList = []; // 알림 목록을 저장할 배열

// SSE 연결 설정
const eventSource = new EventSource('/notification/subscribe/' + 1);

// SSE로 알림 수신
eventSource.onmessage = function(event) {
    const notification = JSON.parse(event.data);
    console.log('New Notification:', notification);

    // 기존 알림 목록에 이미 해당 nfno가 있는지 확인
    if (!notificationList.some(item => item.nfno === notification.nfno)) {
        notificationList.push(notification); // 중복되지 않으면 알림 추가
    }

    const currentDate = new Date();
    console.log(currentDate);
    console.log(notificationList);

    // 알림 개수를 갱신
    updateUnreadCount();
    // 드롭다운 업데이트
    updateNotificationDropdown();
};

// 알림 아이콘 클릭 시 드롭다운 열기
const notificationIcon = document.getElementById('notification-icon');
const notificationDropdown = document.getElementById('notification-dropdown');

notificationIcon.addEventListener('click', () => {
    notificationDropdown.classList.toggle('show'); // 드롭다운 토글
});

// 드롭다운에 알림 목록 업데이트
function updateNotificationDropdown() {
    const dropdownContent = document.getElementById('notification-dropdown-content');
    dropdownContent.innerHTML = ''; // 기존 내용 지우기

    notificationList.forEach(notification => {
        const notificationElement = document.createElement('div');
        notificationElement.classList.add('notification-item');
        notificationElement.textContent = notification.message;

        // 알림 상태에 따라 스타일 변경
        if (notification.status === 'UNREAD') {
            notificationElement.classList.add('unread'); // 읽지 않은 알림 강조
            notificationElement.addEventListener('click', () => {
                markAsRead(notification.nfno); // 클릭하면 읽은 상태로 변경
            });
        } else {
            notificationElement.classList.add('read'); // 읽은 알림
        }

        dropdownContent.appendChild(notificationElement);
    });
}

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

// 읽지 않은 알림 개수 업데이트
function updateUnreadCount() {
    unreadCount = notificationList.filter(notification => notification.status === 'UNREAD').length;
    const unreadCountElement = document.getElementById('unread-count');
    unreadCountElement.textContent = unreadCount > 0 ? unreadCount : ''; // 0일 경우 빈 문자열로 처리
}

document.getElementById("def").addEventListener("click", ()=>{
    let text = document.getElementById("abc").value;

    insert(3, text);
})

async function insert(mno, text) {
    try {
        const url = "/review/insertTest/" + mno + "/" + text
        const resp = await fetch(url);
        const result = await resp.text();
        return result;
    } catch (error) {
        console.log(error);
    }
}