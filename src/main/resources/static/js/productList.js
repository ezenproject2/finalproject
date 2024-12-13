document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll('.breadcrumb_item').forEach(item => {
        const icon = item.querySelector('.ic_carousel_up_white'); // i 태그
        const subDepth = item.querySelector('.sub_depth'); // 모달창

        item.addEventListener('mouseenter', () => {
            // subDepth와 icon이 존재하는지 확인
            if (subDepth) { subDepth.style.display = 'block'; } // 모달창 표시
            if (icon) {
                icon.classList.remove('ic_carousel_up_white'); // 기존 클래스 제거
                icon.classList.add('ic_carousel_down_white'); // 새로운 클래스 추가
            }
        });

        item.addEventListener('mouseleave', () => {
            // subDepth와 icon이 존재하는지 확인
            if (subDepth) { subDepth.style.display = 'none'; } // 모달창 숨김
            if (icon) {
                icon.classList.remove('ic_carousel_down_white'); // 새로운 클래스 제거
                icon.classList.add('ic_carousel_up_white'); // 기존 클래스 추가
            }
        });
    });

    const primaryCtg = document.querySelector(".primary_ctg");
    const secondaryCtg = document.querySelector(".secondary_ctg");
    const asideTitle = document.querySelector(".aside_title");
    const secondaryList = document.querySelector(".secondary_list");
    const asideList = document.querySelector(".aside_list");
    const primaryOptions = document.querySelectorAll(".primary_option");;

    // 1차 카테고리별 2차 카테고리 데이터
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

    // 슬래시('/')를 기준으로 줄바꿈 처리 함수
    function formatAsideTitle() {
        if (asideTitle.textContent.includes('/')) {
            asideTitle.textContent = asideTitle.textContent.replace('/', '/\n');
        }
    }

    // 1차 카테고리 클릭 이벤트
    primaryOptions.forEach(option => {
        option.addEventListener("click", (event) => {
            // event.preventDefault();

            // 1차 카테고리 텍스트 가져오기
            const category = option.dataset.category;
            const categoryText = option.textContent.trim();

            // primary_ctg와 aside_title 업데이트
            primaryCtg.textContent = categoryText;
            asideTitle.textContent = categoryText;

            // 슬래시('/')를 기준으로 줄바꿈 처리
            formatAsideTitle();

            // 2차 카테고리 업데이트
            const subCategories = secondaryCategories[category] || ["항목 없음"];

            // secondary_ctg는 "전체"로 유지
            secondaryCtg.textContent = "전체";

            // aside_list에 해당 카테고리의 모든 옵션 표시
            updateList(asideList, subCategories, category);

            // secondary_list 초기화 (필요 시 설정)
            updateList(secondaryList, subCategories, category);
        });
    });

    // 2차 카테고리 클릭 이벤트
    secondaryList.addEventListener("click", (event) => {
        if (event.target.tagName === "A") {
            // event.preventDefault();
            secondaryCtg.textContent = event.target.textContent.trim();
        }
    });

    // 리스트 업데이트 함수
    function updateList(listElement, items, category) {
        listElement.innerHTML = items
            .map(item => {
                // item (2차 카테고리)과 category (1차 카테고리) 함께 링크 생성
                return `<li><a href="/product/list?pageNo=1&primaryCtg=${category}&secondaryCtg=${item}">${item}</a></li>`;
            })
            .join("");
    }

    // 1차 카테고리명(primary_ctg) 가져오기
    primaryOptions.forEach(option => {
        option.addEventListener("click", (event) => {
            // event.preventDefault();

            // data-category 값 가져오기
            const category = option.dataset.category;
            console.log(`선택된 카테고리: ${category}`); // "소설", "시/에세이" 등 출력
        });
    })


    // -------------------------------------------------------list_menu tap
    const listItems = document.querySelectorAll('.list_menu_item');
    listItems.forEach(listItem => {
        // listItems에서 각 listItem의 dataset.order이 변수로 가져온 order과 같다면 'tap' 클래스 추가로 변경
        // listItem.addEventListener('click', () => {
        //     // event.preventDefault(); // 기본 동작 막기, db 연결 후 제거해야 함
        //     listItems.forEach(i => i.classList.remove('tap'));
        //     listItem.classList.add('tap');
        // })

        // 이동 후 현재 order 상태에 따라 탭 클래스 추가하는 형식으로 바꿨어요!
        if (listItem.dataset.order === order) {
            listItem.classList.add('tap');
        } else {
            listItem.classList.remove('tap');
        }
    })

    document.querySelectorAll('.book_rating_wrap').forEach(function (bookRatingWrap) {
        // 서버에서 전달된 점수를 가져오기 (data-score에서 가져옴)
        let score = parseFloat(bookRatingWrap.querySelector('i').getAttribute('data-score'));

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
        let iconElement = bookRatingWrap.querySelector('.ic_star');
        iconElement.classList.add('star-' + className);  // 새로운 클래스 추가
    });

    if(primaryCtgData != null && primaryCtgData != ""){

        // primary_ctg와 aside_title 업데이트
        primaryCtg.textContent = primaryCtgData;
        asideTitle.textContent = primaryCtgData;
        // 슬래시('/')를 기준으로 줄바꿈 처리
        formatAsideTitle();
        updateList(asideList, secondaryCategories[primaryCtgData] || ["항목 없음"], primaryCtgData);
        updateList(secondaryList, secondaryCategories[primaryCtgData] || ["항목 없음"], primaryCtgData);
    }else{
        primaryCtg.textContent = "전체";
        asideTitle.textContent = "전체";

        // 1차 카테고리만 가져오기 (객체의 키들)
        const primaryCategories = Object.keys(secondaryCategories);
        console.log(primaryCategories);
        // primaryCategories를 사용하여 링크를 생성
        asideList.innerHTML = primaryCategories
            .map(category => {
                // 각 1차 카테고리에 대해 링크를 생성
                return `<li><a href="/product/list?pageNo=1&primaryCtg=${category}">${category}</a></li>`;
            })
            .join("");

    }

});

