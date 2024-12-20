
console.log("SSE JS 연결");

const eventSource = new EventSource('/notification/stream/1');  // 예시로 mno = 1

eventSource.onmessage = function(event) {
    const notification = JSON.parse(event.data);
    console.log('New Notification:', notification);
    // 알림을 화면에 표시하는 로직 추가
};