
console.log("리뷰 출력 JS");

// 리뷰 출력
function printReviewBox(prno, pageNo=1) {
    gerReviewFromServer(prno, pageNo).then(result =>{
        const reviewBox = document.getElementById('reviewBox');
        if(result.reviewVOList.length>0){
            reviewBox.innerHTML = "";

            for(reviewVO of result.reviewVOList){

                // 각 리뷰객체의 좋아요 여부
                let isLike = 0;
                isLikeFromServer(reviewVO.rno, mno).then(result =>{
                    if(result=="1"){isLike=1}else{isLike=0}
                });

                let str = `<div>`;
                str += `<p>${reviewVO.name} | ${reviewVO.regAt} | ${reviewVO.rating} </p>`;
                if(isLike == 0){
                    str += `<button type="button" data-rno=${reviewVO.rno} class="likeBtn">좋아요 전</button>`;
                }else if(isLike == 1){
                    str += `<button type="button" data-rno=${reviewVO.rno} class="likeBtn">좋아요 후</button>`;
                }
                str += `<p>${reviewVO.content}</p>`;
                str += `</div><hr>`;
                reviewBox.innerHTML += str;
            }

            printPagingBox(result.pagingHandler.startPage, result.pagingHandler.endPage);            
        }

    });
}

// 리뷰 페이지네이션 버튼 출력
function printPagingBox(startPage, endPage) {
    const pagingBox = document.getElementById("pagingBox");
    pagingBox.innerHTML = "";

    let str = `<ul>`;
    str += `<li><a data-page=${startPage-1} class="pagingBtn">이전</a></li>`;
    for(let i=startPage; i<=endPage; i++){
        str += `<li><a data-page=${i} class="pagingBtn">${i}</a></li>`;
    }
    str += `<li><a data-page=${endPage+1} class="pagingBtn">다음</a></li>`;
    str += `</ul>`;

    pagingBox.innerHTML += str;
    
}

document.addEventListener("click", (e)=>{
    // 페이지네이션 버튼 눌렀을 때 리뷰 출력 변환
    if(e.target.classList.contains("pagingBtn")){
        let pageNo = e.target.dataset.page;
        printReviewBox(prno, pageNo);
    }
});

// (비동기) 리뷰 데이터 요청
async function gerReviewFromServer(prno, pageNo) {
    try {
        const url = "/review/list/" + prno + "/" + pageNo;
        const resp = await fetch(url);
        const result = await resp.json();
        return result;
    } catch (error) {
        console.log(error);
    }
}

// (비동기) 리뷰 좋아요 판단
// 비동기 요청 : 좋아요 상태 가져오기
async function isLikeFromServer(rno, mno) {
    try {
        const url = "/review/isLike/" + rno + "/" + mno;
        const resp = await fetch(url);
        const result = await resp.text();
        return result;
    } catch (error) {
        console.log(error)
    }
}