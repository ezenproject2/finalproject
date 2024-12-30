document.addEventListener("DOMContentLoaded", () => {
    Sticker.init('.sticker');

    // 책 이미지 움직임 이벤트
    const wrapper = document.querySelector('.book_thum_wrap');
    const book = document.querySelector('.book-items');

    wrapper.addEventListener('mousemove', (e) => {
        const rect = wrapper.getBoundingClientRect();
        const x = e.clientX - rect.left;
        const y = e.clientY - rect.top;

        const rotateX = ((y / rect.height) - 0.5) * -20;
        const rotateY = ((x / rect.width) - 0.5) * 20;

        book.style.transform = `rotateX(${rotateX}deg) rotateY(${rotateY}deg)`;
    });

    wrapper.addEventListener('mouseleave', () => {
        book.style.transform = 'rotateX(0deg) rotateY(0deg)';
    });

    // 무이자 할부 클릭 이벤트
    // 모든 .book_point 요소를 선택
    const bookPoints = document.querySelectorAll(".book_point");

    bookPoints.forEach((bookPoint) => {
        const modal = bookPoint.querySelector(".book_point_modal"); // 해당 부모 안의 모달
        const toggleIcon = bookPoint.querySelector(".ic_carousel_down_white"); // 해당 부모 안의 아이콘

        if (!modal || !toggleIcon) {
            console.error("모달 또는 아이콘 요소를 찾을 수 없습니다.");
            return;
        }

        // 무이자 할부 클릭 이벤트
        bookPoint.addEventListener("click", function () {
            // 모달창 열기/닫기
            modal.style.display = modal.style.display === "block" ? "none" : "block";

            // 아이콘 클래스 변경
            if (modal.style.display === "block") {
                toggleIcon.classList.replace("ic_carousel_down_white", "ic_carousel_up_white");
            } else {
                toggleIcon.classList.replace("ic_carousel_up_white", "ic_carousel_down_white");
            }
        });

        // 모달 닫기 이벤트
        const closeButton = modal.querySelector(".ic_delete");
        if (closeButton) {
            closeButton.addEventListener("click", function (event) {
                event.stopPropagation(); // 클릭 이벤트 전파 방지
                modal.style.display = "none";
                toggleIcon.classList.replace("ic_carousel_up_white", "ic_carousel_down_white");
            });
        }
    });

    // 책 이미지 전체보기
    const fullViewBtn = document.querySelector(".full_view_btn");
    const modal = document.getElementById("imageModal");
    const fullImage = document.getElementById("fullImage");
    const thumbnail = document.querySelector(".thumbnail");

    // 전체보기 버튼 클릭 이벤트
    fullViewBtn.addEventListener("click", () => {
        fullImage.src = thumbnail.src; // 원본 이미지 가져오기
        modal.style.display = "flex"; // 모달창 보이기
    });

    // 모달 영역 클릭 시 닫기
    modal.addEventListener("click", (e) => {
        if (e.target === modal) { // 모달 배경 클릭 시에만 닫힘
            modal.style.display = "none";
        }
    });

    // 모달 초기 상태를 강제로 display: none으로 설정 (안전장치)
    modal.style.display = "none";

    // 매장 재고 버튼
    const openModalBtn = document.getElementById("openModal");
    const closeModalBtn = document.getElementById("closeModal");
    const modalOverlay = document.getElementById("modalOverlay");

    // 모달 열기
    openModalBtn.addEventListener("click", () => {
        // 매장 재고 업데이트하는 함수 추가
        updateOfflineStock();

        modalOverlay.style.display = "block";
    });

    // 매장 재고 업데이트 함수
    function updateOfflineStock() {

        getOfflineStockFromServer(prno).then(result => {
            if (result != null) {
                result.forEach(data => {
                    const td = document.querySelector(`.td_stock[data-osno="${data.osno}"]`);

                    if (td) {
                        // 일치하는 td 요소가 있으면, 그 안의 텍스트를 stock 값으로 변경
                        td.innerText = data.stock;
                    }
                });
            }
        })
    }

    // (비동기요청) 오프라인 매장 재고 확인
    async function getOfflineStockFromServer(prno) {
        try {
            const url = "/offline/getStock/" + prno;
            const resp = await fetch(url);
            const result = await resp.json();
            return result;
        } catch (error) {
            console.log(error);
        }
    }

    // 모달 닫기
    closeModalBtn.addEventListener("click", () => {
        modalOverlay.style.display = "none";
    });

    // 배경 클릭 시 모달 닫기
    modalOverlay.addEventListener("click", (e) => {
        if (e.target === modalOverlay) {
            modalOverlay.style.display = "none";
        }
    });

    // +, - 버튼(하단의 구매창)
    const plusBtn = document.querySelector('.plus_btn');
    const minusBtn = document.querySelector('.minus_btn');
    const number = document.getElementById('number');
    let value = 1; // 초기값 설정

    // + 버튼 클릭 시
    plusBtn.addEventListener('click', () => {
        value++;
        number.textContent = value;
        updateTotalPrice();
    });

    // - 버튼 클릭 시
    minusBtn.addEventListener('click', () => {
        if (value > 1) {
            value--;
            number.textContent = value;
            updateTotalPrice();
        }
    });

    // 위쪽 카테고리 업데이트를 위한 데이터
    const secondaryCategories = {
        "소설": ["세계 각국 소설", "한국소설", "고전/문학", "장르소설", "테마문학", "비평/창작/이론", "신화/전설/설화", "희곡/시나리오"],
        "시/에세이": ["한국시", "외국시", "그림/사진 에세이", "독서 에세이", "명상 에세이", "성공 에세이", "여행 에세이", "연애/사랑 에세이", "명사/연예인 에세이", "명언/잠언록", "음식/요리 에세이", "예술 에세이", "한국 에세이", "외국 에세이"],
        "경제/경영": ["경제", "경영", "마케팅/세일즈", "재테크/투자", "CEO/비즈니스"],
        "자기계발": ["대화/협상", "성공/처세", "시간관리", "자기능력계발", "인간관계", "취업"],
        "인문": ["인문일반", "심리", "철학", "언어학/기호학", "종교학/신화학"],
        "역사": ["역사학/이론/비평", "세계사", "서양사", "동양사", "한국사", "주제별 역사/문화"],
        "사회/정치": ["정치/외교", "행정", "국방/군사", "법", "사회학", "사회복지", "언론/미디어", "여성학", "교육학"],
        "자연/과학": ["공학일반/산업공학", "기계/전기/전자", "농수산/축산", "도시/토목/건설", "물리학", "생물학", "수학", "천문/지구과학", "화학", "과학"],
        "예술/대중문화": ["예술일반/예술사", "미술", "음악", "건축", "만화/애니메이션", "사진", "연극/공연/영화", "TV/라디오"],
        "종교": ["개신교", "천주교(가톨릭)", "불교", "종교일반", "기타종교"],
        "유아/어린이": ["유아놀이책", "유아그림책", "유아학습", "어린이영어", "어린이 문학", "학습/학습만화", "어린이 교양", "어린이 세트"],
        "가정/요리": ["결혼/가족", "임신/출산", "자녀교육", "인테리어/살림", "요리", "육아"],
        "여행": ["국내여행", "해외여행", "테마여행", "지도"],
        "언어": ["국어", "영어", "일본어", "중국어", "기타외국어", "한자사전", "기타 국가 사전", "백과/전문사전"],
        "컴퓨터/IT": ["그래픽/멀티미디어", "오피스활용도서", "웹사이트", "컴퓨터 입문/활용", "게임", "OS/데이터베이스", "프로그래밍 언어", "네트워크/보안", "컴퓨터공학"],
        "청소년": ["학습법/진학 가이드", "청소년 경제/자기계발", "청소년 과학", "청소년 문학", "청소년 예술", "청소년 인문/사회", "논술/면접대비"],
        "수험서/자격증": ["취업/상식/적성검사", "공무원", "고등고시/전문직", "검정고시", "교원임용고시", "경제/금융/회계/물류", "공인중개/주택관리", "국가자격/전문사무", "편입/대학원", "독학사", "컴퓨터수험서", "보건/위생/의학"],
        "만화": ["공포/추리", "교양만화", "드라마", "성인만화", "명랑/코믹만화", "순정만화", "스포츠만화", "액션/무협만화", "웹툰/카툰에세이", "학원만화", "일본어판 만화", "영문판 만화", "SF/판타지", "기타만화"],
        "잡지": ["문예/교양지", "자연/공학", "컴퓨터/게임/그래픽", "어학/고시/교육", "연예/영화/만화", "여행/취미/스포츠", "외국잡지", "여성/남성/패션", "요리/건강", "리빙/육아", "경제/시사", "종교", "예술/사진/건축"],
        "건강/취미": ["건강", "취미/레저"]
    };

    // 카테고리 업데이트
    const secondaryList = document.querySelector(".secondary_list");
    function updateList(listElement, items, category) {
        console.log(primaryCtgData);
        listElement.innerHTML = items
            .map(item => {
                // item (2차 카테고리)과 category (1차 카테고리) 함께 링크 생성
                return `<li><a href="/product/list?pageNo=1&primaryCtg=${category}&secondaryCtg=${item}">${item}</a></li>`;
            })
            .join("");
    }
    updateList(secondaryList, secondaryCategories[primaryCtgData] || ["항목 없음"], primaryCtgData);

    // 날짜 출력 형식 변경 함수
    // 데이터 변환 함수
    function formatDate(pubdate) {
        if (!pubdate || pubdate.length !== 8) return "날짜 형식 오류";

        const year = pubdate.substring(0, 4);
        const month = pubdate.substring(4, 6);
        const day = pubdate.substring(6, 8);

        return `${year}년 ${parseInt(month, 10)}월 ${parseInt(day, 10)}일`;
    }

    // 데이터 가져오기 및 변환
    const pubdate = document.getElementById("book_publication_date").innerText;

    // 변환된 데이터 삽입
    document.getElementById("book_publication_date").innerText = formatDate(pubdate);

    // 별점 이미지 업데이트---------------------------------------------------
    function updateStarImgSet(){
        // 서버에서 전달된 점수를 가져오기 (data-score에서 가져옴)
        let score = parseFloat(document.getElementById('review_avg_star_1').dataset.score);
        // 0.5단위로 반 내림 함수
        function roundToHalf(score) {
            return Math.floor(score * 2) / 2;
        }
        
        // 소수점을 _로 바꾸는 함수
        function formatClassName(score) {
            return score.toString().replace('.', '_');
        }
        
        // 0.5단위로 반 내림하여 클래스명과 텍스트 값 계산
        let roundedScore = roundToHalf(score);
        let className = formatClassName(roundedScore);
        
        // 기존 클래스를 보존하고, star- 클래스만 추가
        let iconElement1 = document.getElementById('review_avg_star_1');
        let iconElement2 = document.getElementById('review_avg_star_2');
        iconElement1.classList.add('star-' + className);  // 새로운 클래스 추가
        iconElement2.classList.add('star-' + className);  // 새로운 클래스 추가
    }
    
    updateStarImgSet();
    //------------------------------------------------------------------------

    // 별점 게이지 넓이 계산
    const rateElements = document.querySelectorAll(".rate");

    rateElements.forEach((rate) => {
        const scoreElement = rate.querySelector(".score_number");
        const gaugeElement = rate.querySelector(".gauge");

        // data-value 속성에서 점수 가져오기
        const score = parseInt(scoreElement.getAttribute("data-value"), 10);

        // 점수에 따른 width 계산
        let widthPercentage = 0;
        if (score == 0) widthPercentage = 0;
        else if (score <= 10) widthPercentage = 10;
        else if (score <= 19) widthPercentage = 20;
        else if (score <= 29) widthPercentage = 30;
        else if (score <= 39) widthPercentage = 40;
        else if (score <= 49) widthPercentage = 50;
        else if (score <= 59) widthPercentage = 60;
        else if (score <= 69) widthPercentage = 70;
        else if (score <= 79) widthPercentage = 80;
        else if (score <= 89) widthPercentage = 90;
        else if (score <= 100) widthPercentage = 100;

        // width 적용
        const gaugeWidth = (330 * widthPercentage) / 100;
        gaugeElement.style.width = `${gaugeWidth}px`;
    });

});

function updateTotalPrice() {
    let number = document.getElementById("number");
    let totalPrice = document.getElementById("totalPrice");
    let result = parseInt(number.innerText) * realPrice;
    result = result.toLocaleString();
    totalPrice.innerHTML = `${result}<span class="won">원</span>`;
}