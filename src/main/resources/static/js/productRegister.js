
console.log("상품 등록 페이지 JS");

const isbnEl = document.getElementById("isbnEl");
const titleEl = document.getElementById("titleEl");
const authorEl = document.getElementById("authorEl");
const discountEl = document.getElementById("discountEl");
const publisherEl = document.getElementById("publisherEl");
const pubdateEl = document.getElementById("pubdateEl");
const descriptionEl = document.getElementById("descriptionEl");
const primaryCtgEl = document.getElementById("primaryCtgEl");
const secondaryCtgEl = document.getElementById("secondaryCtgEl");
const stockEl = document.getElementById("stockEl");
const discountRateEl = document.getElementById("discountRateEl");
const linkEl = document.getElementById("linkEl");
const imageEl = document.getElementById("imageEl");

// 검색하면 bookVO 채워지도록 함
document.getElementById("searchBtn").addEventListener("click", ()=>{
    document.getElementById("regBtn").classList.add('disabled'); 
    document.getElementById("regBtn").disabled = true;

    const searchEl = document.getElementById("searchEl");
    const printEl = document.getElementById("printEl");

    searchBookByAPI(searchEl.value).then(result => {
        if(result != null){
            printEl.innerText = "미등록 확인 ✔️";
            if(result.isValid == 0){
                document.getElementById("isbnEl").value = result.isbn;
                document.getElementById("titleEl").value = result.title;
                document.getElementById("linkEl").value = result.link;
                document.getElementById("imageEl").value = result.image;
                document.getElementById("authorEl").value = result.author;
                document.getElementById("discountEl").value = result.discount;
                document.getElementById("publisherEl").value = result.publisher;
                document.getElementById("pubdateEl").value = result.pubdate;
                document.getElementById("descriptionEl").value = result.description;
            }else if (result.isValid == 2){
                printEl.innerText = "검색어를 정확히 입력해주세요!";
            }else{
                printEl.innerHTML = `이미 등록된 상품입니다. 해당상품 페이지 이동<a href="/product/detail?isbn=${result.isbn}">🔍</a>`
            }
        }else{
            printEl.innerText = "검색 결과가 없습니다.";
        }
    })

});

// 여러개의 input 태그의 변화 감지 하고 저장 버튼 막기
const inputs = document.querySelectorAll('input');
inputs.forEach(function(input) {
    input.addEventListener('change', function(event) {
        document.getElementById("regBtn").classList.add('disabled'); 
        document.getElementById("regBtn").disabled = true;
    });
});

// 적합 검사가 되어야만 저장 버튼이 활성화
document.getElementById("checkBtn").addEventListener("click", ()=>{
    // 모든 필드의 값이 유효한지 체크
    const isValid = [
        isbnEl, titleEl, authorEl, discountEl, publisherEl, pubdateEl, descriptionEl,
        primaryCtgEl, secondaryCtgEl, linkEl, imageEl
    ].every(input => input.value !== "" && input.value !== null) && 
    !isNaN(stockEl.value) && stockEl.value !== "" && stockEl.value !== null && stockEl.value > 0 &&
    !isNaN(discountRateEl.value) && discountRateEl.value !== "" && discountRateEl.value !== null && discountRateEl.value >= 0 && discountRateEl.value <= 100;

    // isbn 존재 여부
    if(isbnEl.value == null || isbnEl.value == ""){
        alert("isbn을 입력해주세요.")
        document.getElementById("regBtn").classList.add('disabled'); 
        document.getElementById("regBtn").disabled = true;
        return;
    }

    // isbn 중복 여부
    isbnDuplicateCheck(isbnEl.value).then(result => {
        if(result === "1"){
            alert("같은 도서의 중복 등록은 불가능합니다.");
            document.getElementById("regBtn").classList.add('disabled'); 
            document.getElementById("regBtn").disabled = true;
            return;
        }
    });
    
    // input 태그 채움 확인
    if (isValid) {
        alert("등록 가능합니다.")
        document.getElementById("regBtn").classList.remove('disabled'); 
        document.getElementById("regBtn").disabled = false;
    } else {
        alert("등록 조건을 충족하지 못했습니다. \n1) 값 누락 2) 재고와(0이상) 할인율(0~100)은 숫자만 가능");
        document.getElementById("regBtn").classList.add('disabled'); 
        document.getElementById("regBtn").disabled = true;
    }


});


// (비동기) isbn을 통해 중복검사
async function isbnDuplicateCheck(isbn) {
    try {
        const url = "/product/duplCheck/" + isbn
        const resp = await fetch(url);
        const result = await resp.text();
        return result;
    } catch (error) {
        console.log(error);
    }
}

// (비동기) 키워드를 통해 네이버 API에서 데이터 검색
async function searchBookByAPI(keyword) {
    try {
        const url = "/product/search/" + keyword
        const resp = await fetch(url);
        const result = await resp.json();
        return result;
    } catch (error) {
        console.log(error);
    }
}

