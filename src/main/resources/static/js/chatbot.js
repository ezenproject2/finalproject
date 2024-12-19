document.getElementById('chatBtn').addEventListener('click', function() {
    document.getElementById('chatBox').style.display = 'flex';
});

document.getElementById('closeChat').addEventListener('click', function() {
    document.getElementById('chatBox').style.display = 'none';
});

// 페이지 로드 시 버튼에 이벤트 리스너 추가
document.getElementById("sendButton").addEventListener("click", sendMessageToServer);


// (비동기) 질문을 서버로 보내고 챗봇 답변 받기
async function sendMessageToServer() {
    let classify = document.getElementById("sendButton").dataset.classify;

    const userMessage = document.getElementById("userMessage").value; // 사용자 입력 가져오기
    if (!userMessage.trim()) {
        alert("질문을 입력해주세요.");
        return;
    }

    displayRequest(userMessage);

    const qusData = {
        message: userMessage,
        userId: "user1111", //나중에 연결할것
        type: "text",
        classify: classify
    }

    document.getElementById("userMessage").value = "";

    RequestGPTFromServer(qusData).then(result => {

        // 챗봇의 답변을 화면에 표시
        displayResponse(result.answer);
        document.getElementById("sendButton").dataset.classify = result.classify;

        if (result.classify === "c-type") {
            // "가까운 매장 찾기"를 위한 메서드
            getLocationInfo();
        }

    });
}

// 오프라인 매장 위치 리스트
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

// 가까운 매장 찾기 함수 
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

// 가까운 매장 찾아서 매장명을 가지고 서버에 요청, 답변까지
function getLocationInfo() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            (position) => {
                const { latitude, longitude } = position.coords;
                console.log(`현재 위치: 위도 ${latitude}, 경도 ${longitude}`);
                // 위치값을 통해 가까운 매장 찾아오기 

                let nearestStore = null;
                let minDistance = Infinity;

                // 각 매장과의 거리 계산
                locations.forEach(store => {
                    const distance = calculateDistance(latitude, longitude, store.lat, store.lng);
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestStore = store;
                    }
                });

                // 가장 가까운 매장 출력
                if (nearestStore) {
                    console.log(`가장 가까운 매장은 ${nearestStore.name}입니다. 거리: ${minDistance.toFixed(2)} km`);

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
                console.error("위치 정보를 가져올 수 없습니다.", error);
            }
        );
    } else {
        alert("위치 정보 사용이 불가능한 브라우저입니다.");
    }
}

// // 서버에서 받은 챗봇의 답변을 화면에 표시
// function displayResponse(answer) {
//     const chatBox = document.getElementById("chatBox"); // 채팅 박스 요소
//     const botMessage = document.createElement("div"); // 챗봇 메시지 컨테이너 생성
//     botMessage.classList.add("bot-message"); // 스타일 클래스 추가
//     botMessage.innerHTML = answer; // HTML을 포함한 답변을 삽입 (textContent -> innerHTML로 변경)
//     chatBox.appendChild(botMessage); // 채팅 박스에 답변 추가
// }

// // 사용자가 보낸 질문을 화면에 표시
// function displayRequest(question) {
//     const chatBox = document.getElementById("chatBox"); // 채팅 박스 요소
//     const botMessage = document.createElement("div"); // 챗봇 메시지 컨테이너 생성
//     botMessage.classList.add("bot-message"); // 스타일 클래스 추가
//     botMessage.innerHTML = question;
//     chatBox.appendChild(botMessage); // 채팅 박스에 답변 추가
// }

// 사용자가 보낸 질문을 화면에 표시
function displayRequest(question) {
    const chatContent = document.querySelector('.chat-content'); // 채팅 내용 영역
    const userMessage = document.createElement("div"); // 사용자의 메시지 컨테이너 생성
    userMessage.classList.add("user-message"); // 스타일 클래스 추가
    userMessage.innerHTML = question; // 사용자의 질문을 삽입
    chatContent.appendChild(userMessage); // 채팅 내용에 추가
    chatContent.scrollTop = chatContent.scrollHeight; // 스크롤을 맨 아래로
}

// 서버에서 받은 챗봇의 답변을 화면에 표시
function displayResponse(answer) {
    const chatContent = document.querySelector('.chat-content'); // 채팅 내용 영역
    const botMessage = document.createElement("div"); // 챗봇의 메시지 컨테이너 생성
    botMessage.classList.add("bot-message"); // 스타일 클래스 추가
    botMessage.innerHTML = answer; // 챗봇의 답변을 삽입
    chatContent.appendChild(botMessage); // 채팅 내용에 추가
    chatContent.scrollTop = chatContent.scrollHeight; // 스크롤을 맨 아래로
}

// (비동기) 질문을 보내고 답변을 받는 메서드
async function RequestGPTFromServer(qusData) {
    try {
        const url = "/chatbot"; // 서버 엔드포인트 (예: Spring Controller에서 처리할 URL)

        const response = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(qusData) // 사용자 메시지를 JSON 형태로 서버로 전송
        });

        const result = await response.json(); // 서버 응답 받기 (챗봇의 답변)
        return result;

    } catch (error) {
        console.log("Error:", error); // 에러 처리
    }
}