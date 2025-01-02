const todayDateElement = document.querySelector(".today_date");
if (todayDateElement) {
    const today = new Date();
    const year = String(today.getFullYear()).slice(2); // 연도 뒤 2자리
    const month = String(today.getMonth() + 1).padStart(2, "0"); // 월 (2자리)
    const date = String(today.getDate()).padStart(2, "0"); // 일 (2자리)
    // 날짜 형식: 24.01.01
    const formattedDate = `${year}.${month}.${date}`;
    // span에 날짜 삽입
    todayDateElement.textContent = formattedDate;
}
// Swiper 초기화
var swiper = new Swiper(".mySwiper", {
    autoplay: { delay: 5500, },// 자동 재생 시간 설정
    pagination: {
        el: ".swiper-pagination",
        clickable: true,
    },
    navigation: {
        nextEl: ".swiper-button-next",
        prevEl: ".swiper-button-prev",
    },
    loop: true, // 슬라이드 반복
    on: {
        slideChangeTransitionStart: function () {
            // 현재 활성 슬라이드 가져오기
            const activeSlide = document.querySelector(".swiper-slide-active");
            if (activeSlide.classList.contains("slide1")) {
                var tl = gsap.timeline({ defaults: { duration: 1.5, ease: "power2.inOut" } });
                tl.fromTo(".slide1-img",
                    { x: 0, y: 100, opacity: 0.5 }, // 초기 상태
                    { x: 0, y: -100, opacity: 1}, 0 // 동시에 실행 시작
                )
                tl.fromTo(".title_text",
                    { opacity: 0, x: -1000 },
                    { opacity: 1, x: 0}, 0
                )
                tl.fromTo(".sub_text",
                    { opacity: 0},
                    { opacity: 1}, 1.1
                )
                tl.fromTo(".content_text",
                    { opacity: 0},
                    { opacity: 1}, 1.1
                );
            }
            if (activeSlide.classList.contains("slide2")) {
                var tl = gsap.timeline({ defaults: { duration: 2, ease: "circ.inOut" } }); // 지속 시간 0.5초로 조정
                // circ 애니메이션
                tl.fromTo(".circ",
                    { x:"10%", y:"-50%", scale: 0.5, opacity: 0 }, // 초기 상태
                    { x:"10%", y:"-50%", scale: 1, opacity: 1 }, 0.1 // 최종 상태
                )
                tl.fromTo(".circ1",
                    { scale: 0.5, opacity: 0 }, // 초기 상태
                    { scale: 1, opacity: 1 }, 0.1 // 최종 상태
                )
                tl.fromTo(".circ2",
                    { scale: 0.5, opacity: 0 }, // 초기 상태
                    { scale: 1, opacity: 1 }, 0.1 // 최종 상태
                )
                tl.fromTo(".circ3",
                    { scale: 0.5, opacity: 0 }, // 초기 상태
                    { scale: 1, opacity: 1 }, 0.1 // 최종 상태
                )
                tl.fromTo(".circ4",
                    { scale: 0.5, opacity: 0 }, // 초기 상태
                    { scale: 1, opacity: 1 }, 0.1 // 최종 상태
                )
                tl.fromTo(".text_area > p",
                    {opacity: 0},
                    {opacity: 1}, 0
                )
                tl.fromTo(".main_kid",
                    { scale: 0.3, opacity: 0 }, // 초기 상태
                    { scale: 1, opacity: 1 }, 0.1 // 최종 상태
                );
            }
            if (activeSlide.classList.contains("slide3")) {
                var tl = gsap.timeline({ defaults: { duration: 1.5, ease: "bounce.inOut" } });
                // .text_area > p 개별 텍스트 애니메이션
                tl.fromTo(".text_area",
                    { opacity: 0, y: 1000 },
                    { opacity: 1, y: 0}, 0
                )
                tl.fromTo(".review",
                    { opacity: 0, y: 1000 },
                    { opacity: 1, y: 0}, 0
                )
                tl.fromTo(".girl",
                    { opacity: 0, y: 1000 },
                    { opacity: 1, y: 0}, 0
                )
                tl.fromTo(".man",
                    { opacity: 0, y: 1000 },
                    { opacity: 1, y: 0}, 0
                )
                tl.fromTo(".trophy",
                    { opacity: 0, y: -1000 },
                    { opacity: 1, y: 0}, 0.7
                );
            }
            if (activeSlide.classList.contains("slide4")) {
                var tl = gsap.timeline({ defaults: { duration: 1.5, ease: "power2.out" } });
                // 애니메이션 추가
                tl.fromTo(".slide4 .center",
                    { x:"-50%", y:"-50%", scale: 1, opacity: 0 },
                    { opacity: 1 }, 0 )
                // book 애니메이션 (공통 처리)
                tl.fromTo(".slide4 .book", // 모든 책을 선택
                    { x: 0, y: 1000, opacity: 0.3 }, // 초기 상태
                    { opacity: 1, // 공통 최종 상태
                        y: (i) => {
                            switch (i) {
                                case 0: return 10; // 첫 번째 요소
                                case 1: return 70;  // 두 번째 요소
                                case 2: return 120; // 세 번째 요소
                                case 3: return 165; // 세 번째 요소
                                case 4: return 210; // 세 번째 요소
                                default: return 0;  // 나머지 요소
                            }
                        },
                        stagger: 0.3 // 동시에 실행
                    }, 0 // 타임라인에서 동일 시작 시간
                );
            } else {
                // 다른 슬라이드로 전환 시 초기화
                gsap.set(".slide4 .center", {opacity: 0 });
            }
        },
    },
});
// 첫 번째 슬라이드 애니메이션 수동 실행
document.addEventListener("DOMContentLoaded", function () {
    const firstSlide = document.querySelector(".swiper-slide-active");
    // 타임라인 객체 생성
    var tl = gsap.timeline({ defaults: { duration: 1.5, ease: "power2.inOut" } });
    if (firstSlide && firstSlide.classList.contains("slide1")) {
        tl.fromTo(".slide1-img",
            { x: 0, y: 100, opacity: 0.5 }, // 초기 상태
            { x: 0, y: -100, opacity: 1}, 0 // 동시에 실행 시작
        )
        tl.fromTo(".title_text",
            { opacity: 0, x: -1000 },
            { opacity: 1, x: 0}, 0
        )
        tl.fromTo(".sub_text",
            { opacity: 0},
            { opacity: 1}, 1.1
        )
        tl.fromTo(".content_text",
            { opacity: 0},
            { opacity: 1}, 1.1
        );
    }
});
const books = document.querySelectorAll(".daily_pick_slide_book");
const btn = document.querySelector(".slide_btn");
const leftBookImg = document.querySelector(".daily_pick_book > img");
const leftBookTitle = document.querySelector(".daily_pick_title");
const leftBookInfo = document.querySelector(".daily_pick_info");
const leftBookLink = document.querySelector(".daily_pick_left > a");
// 초기 위치 설정 (12시, 3시, 6시, 9시 방향)
const positions = [
    { top: "0%", left: "50%", transform: "translate(-50%, -0%)" }, // book_1
    { top: "50%", right: "0%", transform: "translate(0%, -50%)" }, // book_2
    { bottom: "0%", left: "50%", transform: "translate(-50%, 0%)" }, // book_3
    { top: "50%", left: "0%", transform: "translate(-0%, -50%)" }, // book_4
];
// 책 정보 배열 (title, info, link)
const bookDetails = [
    {
        title: "우리의 싸움은 아직 시작도 하지 않았다",
        info: "기후 위기, 불평등, 반민주주의, 차별과 혐오\u22EF<br>위기의 시대를 돌파하려는 이들이 알아야 할 저항의 문법<br>그리고 그들에게 전하는 용기와 희망의 메세지",
        link: "https://example.com/book1",
    },
    {
        title: "어떻게 살 것인가",
        info: "힐링에서 스탠딩으로!<br>멘붕 사회에 해독제로 쓰일 책!<br>정치인에서 자유인으로 돌아와 내놓은 첫 번째 책",
        link: "https://example.com/book2",
    },
    {
        title: "하이큐!! 17 - 재능과 센스",
        info: "&lsquo;재능은 꽃피우는 것, 센스는 갈고 닦는 것.&rsquo;<br>봄철 대회 1차 예선을 통과하고 대표 결정전에 진출한 카라스노.<br>1회전 상대는 '놀이'가 모토인 자유분방한 팀 조젠지고!<br>임기응변으로 대처하며 틀에 얽매이지 않은 공격을 해오는 상대에게<br>카라스노고 선수들은 고전을 면치 못하는데...?!",
        link: "https://example.com/book3",
    },
    {
        title: "리액트를 다루는 기술",
        info: "본문과 소스 전면 업그레이드!<br>기본기를 꼼꼼하게!<br>실전에서 효과적으로 활용하는 방법까지 알차게 배우자!",
        link: "https://example.com/book4",
    },
];
let currentPositions = [...positions]; // 현재 위치 상태
// 위치 업데이트 함수
function updatePositions() {
    books.forEach((book, index) => {
        book.classList.remove("select"); // 모든 책에서 select 클래스 제거
        const pos = currentPositions[index];
        // 책의 위치를 갱신
        book.style.top = pos.top || "auto";
        book.style.bottom = pos.bottom || "auto";
        book.style.left = pos.left || "auto";
        book.style.right = pos.right || "auto";
        book.style.transform = pos.transform;
        // 9시 방향의 책에 select 클래스 추가
        if (pos.left === "0%" && pos.top === "50%") {
            book.classList.add("select");
            // 선택된 책의 데이터를 왼쪽에 반영
            const details = bookDetails[index];
            leftBookImg.src = book.querySelector("img").src;
            leftBookTitle.textContent = details.title;
            leftBookInfo.innerHTML = details.info.replace(/<br>/g, "<br>");
            leftBookLink.href = details.link;
        }
    });
}
// 버튼 클릭 시 위치를 시계 방향으로 회전
let rotationAngle = 0; // 초기 회전 각도
btn.addEventListener("click", () => {
    // 회전 각도를 45도씩 증가
    rotationAngle += 45;
    // .daily_pick_slide에 스타일 적용
    const slide = document.querySelector(".daily_pick_slide");
    slide.style.setProperty("--rotation-angle", `${rotationAngle}deg`);
    // 위치 업데이트
    currentPositions.push(currentPositions.shift());
    updatePositions();
});
// 초기 위치 적용
updatePositions();
var swiper = new Swiper(".my-carousel__swiper", {
    loop: true,
    grabCursor: false,
    allowTouchMove: false,
    centeredSlides: true,
    centeredSlidesBounds: true,
    navigation: {
        nextEl: ".my-carousel__control--next",
        prevEl: ".my-carousel__control--prev"
    },
    slidesPerView: 3,
    effect: "creative",
    creativeEffect: {
        perspective: true,
        limitProgress: 3,
        prev: {
            translate: ["-90%", "20%", -100],
            rotate: [0, 0, -20],
            origin: "bottom"
        },
        next: {
            translate: ["90%", "20%", -100],
            rotate: [0, 0, 20],
            origin: "bottom"
        }
    }
});
const reviewTexts = document.querySelectorAll(".review-text"); // 모든 review-text 요소 선택
const maxChars = 225; // 최대 글자 수
reviewTexts.forEach((reviewText) => {
    if (reviewText.textContent.length > maxChars) {
        const truncatedText = reviewText.textContent.slice(0, maxChars).trim(); // 210글자까지 자름
        reviewText.textContent = truncatedText; // 자른 텍스트로 대체
        reviewText.classList.add("truncated"); // 가상요소를 추가하기 위한 클래스 추가
    }
});
// GSAP와 ScrollTrigger 활성화
gsap.registerPlugin(ScrollTrigger);
// 모든 .section 요소를 순회하며 애니메이션 적용
gsap.utils.toArray(".section").forEach((section) => {
    // 초기 상태 설정
    gsap.set(section, { opacity: 0 });
    // ScrollTrigger 애니메이션 설정
    gsap.to(section, {
        opacity: 1, // 투명도 1로 변경
        duration: 0.7, // 애니메이션 지속 시간
        scrollTrigger: {
            trigger: section, // 현재 섹션을 트리거로 설정
            start: "top 90%", // 섹션 상단이 화면의 80% 위치에 도달하면 시작
            end: "top 70%", // 섹션 상단이 화면의 20% 위치에 도달하면 끝
            scrub: 1, // 부드러운 스크러빙
            //markers: true, // 개발가이드선 (디버깅용, 확인 후 제거 가능)
            toggleActions: "play none none none", // 스크롤 방향에 따라 동작
            scroller: ".container",
        },
    });
});
var swiper = new Swiper(".notice_swiper", {
    direction: "vertical",
    loop:true,
    autoplay: { delay: 3000},
});
var swiper = new Swiper(".new_book_swiper", {
    direction: "vertical",
    loop:true,
    effect: "fade",
    pagination: {
        el: ".swiper-pagination",
        clickable: true,
    },
    autoplay: { delay: 4000},
});
