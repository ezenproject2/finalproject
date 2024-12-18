// 페이지 로드 시 버튼에 이벤트 리스너 추가
document.getElementById("sendButton").addEventListener("click", sendMessageToServer);

// (비동기) 질문을 서버로 보내고 챗봇 답변 받기
async function sendMessageToServer() {
    const userMessage = document.getElementById("userMessage").value; // 사용자 입력 가져오기
    if (!userMessage.trim()) {
        alert("질문을 입력해주세요.");
        return;
    }

    try {
        const url = "/chatbot"; // 서버 엔드포인트 (예: Spring Controller에서 처리할 URL)

        const response = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ message: userMessage }) // 사용자 메시지를 JSON 형태로 서버로 전송
        });

        const result = await response.json(); // 서버 응답 받기 (챗봇의 답변)
        displayResponse(result.answer); // 챗봇의 답변을 화면에 표시
    } catch (error) {
        console.log("Error:", error); // 에러 처리
    }
}

// 서버에서 받은 챗봇의 답변을 화면에 표시
function displayResponse(answer) {
    const chatBox = document.getElementById("chatBox"); // 채팅 박스 요소
    const botMessage = document.createElement("div"); // 챗봇 메시지 컨테이너 생성
    botMessage.classList.add("bot-message"); // 스타일 클래스 추가
    botMessage.textContent = answer; // 답변 텍스트 삽입
    chatBox.appendChild(botMessage); // 채팅 박스에 답변 추가
}