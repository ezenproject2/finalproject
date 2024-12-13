
console.log("리뷰 출력 JS");

// 리뷰 출력
function printReviewBox(prno, pageNo=1, mno) {
    gerReviewFromServer(prno, pageNo, mno).then(result =>{
        const reviewBox = document.getElementById('reviewBox');
        if(result.reviewVOList.length>0){
            reviewBox.innerHTML = "";

            for(reviewVO of result.reviewVOList){

                let str = `<div>`;
                str += `<p>${reviewVO.name} | ${reviewVO.regAt} | ${reviewVO.rating} </p>`;
                str += `<button type="button" data-rno=${reviewVO.rno} data-islike=${reviewVO.isLike} class="likeBtn">${reviewVO.isLike>0? '좋아요 후' : '좋아요 전'}</button>`;
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
        printReviewBox(prno, pageNo, mno);
    }

    if(e.target.classList.contains("likeBtn")){
        if(e.target.dataset.islike == 1){
            // 이미 좋아요 > 취소
            cancelToServer(e.target.dataset.rno, mno).then(result=>{
                if(result == "1"){
                    console.log("좋아요 취소 성공!");
                    e.target.dataset.islike = 0;
                    e.target.innerText = "좋아요 전"
                }else{
                    console.log("좋아요 취소 실패!");
                }
            });
        }else if(e.target.dataset.islike == 0){
            // 좋아요 안함 > 함
            doLikeToServer(e.target.dataset.rno, mno).then(result=>{
                if(result == "1"){
                    console.log("좋아요 성공!");
                    e.target.dataset.islike = 1;
                    e.target.innerText = "좋아요 후"
                }else{
                    console.log("좋아요 실패!");
                }
            });
        }
    }

});

// (비동기) 리뷰 데이터 요청
async function gerReviewFromServer(prno, pageNo, mno) {
    try {
        const url = "/review/list/" + prno + "/" + pageNo + "/" + mno;
        const resp = await fetch(url);
        const result = await resp.json();
        return result;
    } catch (error) {
        console.log(error);
    }
}

// (비동기) 좋아요 ㄱㄱ
async function doLikeToServer(rno, mno) {
    try {
        const url = "/review/doLike/" + rno +  "/" + mno;
        const resp = await fetch(url);
        const result = await resp.text();
        return result;
    } catch (error) {
        console.log(error);
    }
}

// (비동기) 좋아요 취소
async function cancelToServer(rno, mno) {
    try {
        const url = "/review/cancel/" + rno +  "/" + mno;
        const resp = await fetch(url);
        const result = await resp.text();
        return result;
    } catch (error) {
        console.log(error);
    }
}

