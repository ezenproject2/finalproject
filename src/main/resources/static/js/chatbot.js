// 채팅방이 나타나도록 하는 버튼
// document.getElementById('chatBtn').addEventListener('click', function() {
//     document.getElementById('chatBox').style.display = 'flex';
// });
//
// // 채팅방이 사라지도록 하는 버튼
// document.getElementById('closeChat').addEventListener('click', function() {
//     document.getElementById('chatBox').style.display = 'none';
// });
//
// // 전송 버튼을 눌렀을 때 서버와 연결하여 반응함
// document.getElementById("sendButton").addEventListener("click", sendMessageToServer);

// // 사용자가 보낸 질문을 화면에 표시
// function displayRequest(question) {
//     const chatContent = document.querySelector('.chat-content'); // 채팅 내용 영역
//     const userMessage = document.createElement("div"); // 사용자의 메시지 컨테이너 생성
//     userMessage.classList.add("user-message"); // 스타일 클래스 추가
//     userMessage.innerHTML = question; // 사용자의 질문을 삽입 // ** 여기 라인에 테스트 문구 넣으면 됩니다.
//     chatContent.appendChild(userMessage); // 채팅 내용에 추가
//     chatContent.scrollTop = chatContent.scrollHeight; // 스크롤을 맨 아래로
// }
//
// // 서버에서 받은 챗봇의 답변을 화면에 표시
// function displayResponse(answer) {
//     const chatContent = document.querySelector('.chat-content'); // 채팅 내용 영역
//     const botMessage = document.createElement("div"); // 챗봇의 메시지 컨테이너 생성
//     botMessage.classList.add("bot-message"); // 스타일 클래스 추가
//     botMessage.innerHTML = answer; // 챗봇의 답변을 삽입 // ** 여기 라인에 테스트 문구 넣으면 됩니다.
//     chatContent.appendChild(botMessage); // 채팅 내용에 추가
//     chatContent.scrollTop = chatContent.scrollHeight; // 스크롤을 맨 아래로
// }
const toggleChatBox = (isOpen) => { // 공통 함수: 채팅 박스 올리고 내리기
    const chatBtn = document.getElementById('chatBtn');
    const chatBox = document.querySelector(".chat-box");

    if (isOpen) {
        chatBtn.classList.add('open'); // open 클래스 추가
        chatBtn.style.animation = "none"; // 애니메이션 제거
        chatBox.style.bottom = "100px"; // 채팅 박스 올리기
    } else {
        chatBtn.classList.remove('open'); // open 클래스 제거
        chatBtn.style.animation = "bounce 1.7s infinite ease-in-out"; // 애니메이션 활성화
        chatBox.style.bottom = "-500px"; // 채팅 박스 내리기
    }
};
// closeChat 버튼 클릭 시
document.getElementById("closeChat").addEventListener("click", () => { toggleChatBox(false); }); // 항상 닫기
// 부엉이 버튼 클릭 시
document.getElementById("chatBtn").addEventListener("click", () => {
    const isOpen = !document.getElementById('chatBtn').classList.contains('open');
    toggleChatBox(isOpen); // 상태에 따라 열거나 닫음
});
// 전송 버튼을 눌렀을 때 서버와 연결하여 반응함
document.getElementById("sendButton").addEventListener("click", sendMessageToServer);

// input창 내의 글자 수에 따른 sendButton 속성 변경
const userMessage = document.getElementById('userMessage');
const sendButton = document.getElementById('sendButton');

// 입력 필드에서 글자 수 확인
userMessage.addEventListener('input', () => {
    if (userMessage.value.trim().length > 0) {
        // 한 글자 이상 입력된 경우 버튼 활성화
        sendButton.style.backgroundColor = '#E7B343';
        sendButton.disabled = false; // 버튼 활성화
    } else {
        // 글자가 없는 경우 버튼 비활성화
        sendButton.style.backgroundColor = '#e6cd9a';
        sendButton.disabled = true; // 버튼 비활성화
    }
});

sendButton.disabled = true; // 초기 상태 설정 (비활성화)

// 사용자가 보낸 질문을 화면에 표시
function displayRequest(question) {
    const chatContent = document.querySelector('.chat-content'); // 채팅 내용 영역

    // 사용자 메시지 그룹 컨테이너 생성
    const userMessageGroup = document.createElement("div");
    userMessageGroup.classList.add("user-message-group");

    // 사용자 메시지 생성
    const userMessage = document.createElement("div");
    userMessage.classList.add("user-message", "message");
    userMessage.innerHTML = `${question} <span class="bot-message-time">${getCurrentTime()}</span>`; // 시간 추가

    userMessageGroup.appendChild(userMessage); // 메시지를 그룹에 추가
    chatContent.appendChild(userMessageGroup); // 그룹을 채팅 내용에 추가
    chatContent.scrollTop = chatContent.scrollHeight; // 스크롤을 맨 아래로
}

// 서버에서 받은 챗봇의 답변을 화면에 표시
function displayResponse(answer) {
    const chatContent = document.querySelector('.chat-content'); // 채팅 내용 영역

    // 챗봇 메시지 그룹 컨테이너 생성
    const botMessageGroup = document.createElement("div");
    botMessageGroup.classList.add("bot-message-group");

    // 챗봇 메시지 생성
    const botMessage = document.createElement("div");
    botMessage.classList.add("bot-message", "message");
    botMessage.innerHTML = `${answer} <span class="bot-message-time">${getCurrentTime()}</span>`; // 시간 추가

    botMessageGroup.appendChild(botMessage); // 메시지를 그룹에 추가
    chatContent.appendChild(botMessageGroup); // 그룹을 채팅 내용에 추가
    chatContent.scrollTop = chatContent.scrollHeight; // 스크롤을 맨 아래로
}

// 현재 시간을 가져오는 함수
function getCurrentTime() {
    const now = new Date();
    return now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}

// 화면을 위한 동작은 여기 윗 부분을 가지고 만들면 됩니다. 자유로운 변경 가능!
// ----------------------------------------------------------------------------------------------------------------------------------
// 아래는 서버와 연결되어 답변을 가져오는 과정이므로 VSCode 작업할 때 가져가지 않아도 됨.
// 대신 위쪽에 question과 answer는 임의로 값을 입력하고 함수가 실행되는 것도 테스트용 버튼을 만들던지 해야할것!

// (비동기) 질문을 서버로 보내고 챗봇 답변 받기
async function sendMessageToServer() {
    let classify = document.getElementById("sendButton").dataset.classify;

    // 1. 사용자가 입력한 텍스트를 가져온다.
    const userMessage = document.getElementById("userMessage").value;
    if (!userMessage.trim()) {
        alert("질문을 입력해주세요.");
        return;
    }

    // 2. 사용자가 입력한 텍스트를 화면에 출력한다. (input 태그 초기화도 같이 해줌)
    displayRequest(userMessage);
    document.getElementById("userMessage").value = "";

    const qusData = {
        message: userMessage,
        userId: "user1111", // 이 후에 mno 연결하면 적용
        type: "text",
        classify: classify
    }

    // 3. 비동기 요청을 통해 서버로 질문을 보내고 답변을 받아온다.
    RequestGPTFromServer(qusData).then(result => {

        // 4. 챗봇의 답변을 화면에 출력한다.
        displayResponse(result.answer);
        document.getElementById("sendButton").dataset.classify = result.classify;

        // Response의 classify로 c-type이 온다면, "가까운 매장 찾기" 로직 실행 (아래 함수 있음)
        if (result.classify === "c-type") {getLocationInfo();}

    });
}

// "가까운 매장 찾기"를 위한 정보, 매장 위치
const locations = [
    { name: "강남점", lat: 37.50125763153371, lng: 127.02508935309022 },
    { name: "종로점", lat: 37.56969036845602, lng: 126.98517712707628 },
    { name: "신촌점", lat: 37.556900686688294, lng: 126.94151936299673 },
    { name: "노원점", lat: 37.655791346407995, lng: 127.06233468092012 },
    { name: "상봉점", lat: 37.59728312068519, lng: 127.08776975160595 },
    { name: "송파점", lat: 37.49384662482695, lng: 127.12050404389352 },
    { name: "인천직영점", lat: 37.45044363731795, lng: 126.70282182285416 },
    { name: "안양점", lat: 37.401868948346, lng: 126.91924648760003 },
    { name: "의정부캠퍼스점", lat: 37.401868948346, lng: 126.91924648760003 },
    { name: "구리점", lat: 37.60505705653705, lng: 127.14064324568844 },
    { name: "일산점", lat: 37.65305102746592, lng: 126.77660982233597 },
    { name: "안산점", lat: 37.308438093312574, lng: 126.85093208791548 },
    { name: "성남분당캠퍼스점", lat: 37.35035614966523, lng: 127.11016728620048 },
    { name: "성남모란캠퍼스점", lat: 37.43275737409808, lng: 127.12967108377303 },
    { name: "김포캠퍼스점", lat: 37.64469308033463, lng: 126.66712240505267 },
    { name: "하남미사캠퍼스점", lat: 37.56257062943442, lng: 127.1919092642279 },
    { name: "천안캠퍼스점", lat: 36.8198107252224, lng: 127.15849073511612 },
    { name: "전주점", lat: 35.84025192123691, lng: 127.1324586087236 },
    { name: "이젠IT캠퍼스점", lat: 37.50201459572809, lng: 127.02452131577509 }
];

// "가까운 매장 찾기"에서 거리 계산을 맡는 함수
function calculateDistance(lat1, lon1, lat2, lon2) {
    const R = 6371; // 지구 반지름 (단위: km)
    const dLat = ((lat2 - lat1) * Math.PI) / 180;
    const dLon = ((lon2 - lon1) * Math.PI) / 180;
    const a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos((lat1 * Math.PI) / 180) *
        Math.cos((lat2 * Math.PI) / 180) *
        Math.sin(dLon / 2) * Math.sin(dLon / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c; // 거리 (단위: km)
}

// "가까운 매장 찾기"에서 실제로 돌아갈 메서드
function getLocationInfo() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            (position) => {
                // 현재 사용자의 위치 가져오기
                const { latitude, longitude } = position.coords;
                
                // 각 매장과의 거리 계산
                let nearestStore = null;
                let minDistance = Infinity;

                locations.forEach(store => {
                    const distance = calculateDistance(latitude, longitude, store.lat, store.lng);
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestStore = store;
                    }
                });

                // 가장 가까운 매장 출력
                if (nearestStore) {
                    const qusData = {
                        message: nearestStore.name,
                        type: "text",
                        classify: "c-type"
                    }
                    RequestGPTFromServer(qusData).then(result => {
                        displayResponse(result.answer);
                        document.getElementById("sendButton").dataset.classify = result.classify;
                    });
                }
            },(error) => {
                console.log("위치 정보를 가져올 수 없습니다.", error);
                // 오류 발생 시 오류 메세지를 출력하고 a-type으로 돌아감
                displayErrorMessage();
            }
        );
    } else {
        alert("위치 정보 사용이 불가능한 브라우저입니다.");
        // 오류 발생 시 오류 메세지를 출력하고 a-type으로 돌아감
        displayErrorMessage();
    }
}

// 답변으로 오류 메세지를 출력하도록 하는 함수
function displayErrorMessage(){
    let str = "오류가 발생했습니다. 다른 질문 부탁드립니다.";
    displayResponse(str);
    document.getElementById("sendButton").dataset.classify = "a-type";
}

// (비동기) 질문을 보내고 답변을 받는 메서드
async function RequestGPTFromServer(qusData) {
    try {
        const url = "/chatbot";
        const response = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(qusData)
        });

        const result = await response.json();
        return result;

    } catch (error) {
        console.log("Error:", error);
        displayErrorMessage();
    }
}

// 부엉이 눈동자 위치 추적
const svg = document.querySelector('.owl'); // SVG 요소
const leftPath = document.querySelector('.left-eye'); // 왼쪽 눈 path
const rightPath = document.querySelector('.right-eye'); // 오른쪽 눈 path

// 경계 상자(Bounding Box)로 중심 좌표 계산
const leftBBox = leftPath.getBBox();
const leftEyeCenterX = leftBBox.x + leftBBox.width / 2;
const leftEyeCenterY = leftBBox.y + leftBBox.height / 2;

const rightBBox = rightPath.getBBox();
const rightEyeCenterX = rightBBox.x + rightBBox.width / 2;
const rightEyeCenterY = rightBBox.y + rightBBox.height / 2;

// 눈동자 요소 가져오기
const leftPupil = document.querySelector('.left-pupil');
const rightPupil = document.querySelector('.right-pupil');

let targetLeft = { x: leftEyeCenterX, y: leftEyeCenterY }; // 왼쪽 눈 목표 좌표
let targetRight = { x: rightEyeCenterX, y: rightEyeCenterY }; // 오른쪽 눈 목표 좌표

const updatePupilPosition = (pupil, currentX, currentY, targetX, targetY, eyeCenterX, eyeCenterY, maxDistance) => {
    const smoothingFactor = 0.2; // 부드러운 이동 비율
    const deltaX = targetX - eyeCenterX;
    const deltaY = targetY - eyeCenterY;

    // 목표 위치와 눈 중심 간의 거리 계산
    const distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

    // 최대 이동 거리 내로 제한
    const clampedDistance = Math.min(distance, maxDistance);

    // 제한된 거리와 각도를 기반으로 목표 좌표 조정
    const angle = Math.atan2(deltaY, deltaX);
    const adjustedTargetX = eyeCenterX + clampedDistance * Math.cos(angle);
    const adjustedTargetY = eyeCenterY + clampedDistance * Math.sin(angle);

    const nextX = currentX + (adjustedTargetX - currentX) * smoothingFactor;
    const nextY = currentY + (adjustedTargetY - currentY) * smoothingFactor;

    pupil.setAttribute('cx', nextX);
    pupil.setAttribute('cy', nextY);

    return { x: nextX, y: nextY }; // 다음 위치 반환
};

document.addEventListener('mousemove', (e) => {
    const point = svg.createSVGPoint();
    point.x = e.clientX;
    point.y = e.clientY;

    const svgPoint = point.matrixTransform(svg.getScreenCTM().inverse());

    targetLeft = { x: svgPoint.x, y: svgPoint.y }; // 왼쪽 눈 목표 위치
    targetRight = { x: svgPoint.x, y: svgPoint.y }; // 오른쪽 눈 목표 위치
});

const eyeRadius = 12; // 눈 반지름
const pupilRadius = 4.5; // 눈동자 반지름
const maxDistance = eyeRadius - pupilRadius; // 최대 이동 거리

let currentLeft = { x: leftEyeCenterX, y: leftEyeCenterY }; // 왼쪽 눈 현재 위치
let currentRight = { x: rightEyeCenterX, y: rightEyeCenterY }; // 오른쪽 눈 현재 위치

const animate = () => {
    // 왼쪽 눈 업데이트
    currentLeft = updatePupilPosition(
        leftPupil,
        currentLeft.x,
        currentLeft.y,
        targetLeft.x,
        targetLeft.y,
        leftEyeCenterX,
        leftEyeCenterY,
        maxDistance
    );

    // 오른쪽 눈 업데이트
    currentRight = updatePupilPosition(
        rightPupil,
        currentRight.x,
        currentRight.y,
        targetRight.x,
        targetRight.y,
        rightEyeCenterX,
        rightEyeCenterY,
        maxDistance
    );

    requestAnimationFrame(animate);
};
animate();

