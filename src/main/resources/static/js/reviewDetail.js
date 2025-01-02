console.log("리뷰 출력 JS");

// 리뷰 출력
function printReviewBox(prno, pageNo = 1, mno) {
    gerReviewFromServer(prno, pageNo, mno).then(result => {
        const reviewBox = document.getElementById('book_review_box');
        if (result.reviewVOList.length > 0) {
            reviewBox.innerHTML = "";
            for (reviewVO of result.reviewVOList) {

                //별점 이미지용 변환
                let score = reviewVO.rating;
                function roundToHalf(score) { return Math.floor(score * 2) / 2; }
                function formatClassName(score) { return score.toString().replace('.', '_'); }
                let roundedScore = roundToHalf(score);
                let className = formatClassName(roundedScore);


                let str = `<div class="review_item">
                           <div class="review_box">`
                if(reviewVO.mno == mno){
                    str += `<i class="ic ic_delete" data-rno=${reviewVO.rno}></i>`
                }
                str += `<div class="review_box_header">
                        <div class="review_member">
                        <div class="member_profile_photo"></div>
                        <span class="member_nike_name">${reviewVO.name}</span>
                        </div>
                        <div class="review_info">
                        <i class="ic ic_star star-${className}"></i>
                        <span class="review_date">${reviewVO.regAt.split(' ')[0]}</span>
                        </div></div>
                        <div class="review_content">`;

                if (reviewVO.fileAddr != null) {
                    str += `<img src="/upload/${reviewVO.fileAddr}" alt="">`;
                }

                str += `<p>${reviewVO.content}</p>
                        </div><div class="review_like">
                        <strong class="like_count" id="likeCnt_${reviewVO.rno}">${reviewVO.likeCnt}</strong>
                        <div class="panel">
                        <a href="javascript:void(0);" class="like1 ${reviewVO.isLike > 0 ? 'active' : ''}"}>
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 176.104 176.104">
                        <path class="likeBtn" data-rno=${reviewVO.rno} data-islike=${reviewVO.isLike} d="M150.383 18.301a49.633 49.633 0 0 0-24.033-6.187c-15.394 0-29.18 7.015-38.283 18.015-9.146-11-22.919-18.015-38.334-18.015-8.704 0-16.867 2.259-24.013 6.187C10.388 26.792 0 43.117 0 61.878 0 67.249.874 72.4 2.457 77.219c8.537 38.374 85.61 86.771 85.61 86.771s77.022-48.396 85.571-86.771a49.09 49.09 0 0 0 2.466-15.341c0-18.754-10.388-35.074-25.721-43.577z"/>
                        </svg></a></div></div></div></div>`;

                reviewBox.innerHTML += str;
            }

            // 페이지네이션 구역 재출력
            printPagingBox(result.pagingHandler.startPage, result.pagingHandler.endPage, pageNo);

            // 그리드 사이즈 조정
            resizeAllGridItems();
            allItems = document.getElementsByClassName("review_item");
            for (x = 0; x < allItems.length; x++) {
                imagesLoaded(allItems[x], resizeInstance);
            }
            const reviewItems = document.querySelectorAll(".review_item");
            // 반복 색상 배열
            const colors = ["#F2E1A9", "white", "#ABBC86"];
            // 각 review_item에 색상 설정
            reviewItems.forEach((item, index) => {
                const color = colors[index % colors.length]; // 순환적으로 색상 선택
                item.style.backgroundColor = color; // 배경색 설정
            });

        }

    });

}

// 리뷰 그리드 계산
function resizeGridItem(item) {
    grid = document.getElementsByClassName("grid")[0];
    rowHeight = parseInt(window.getComputedStyle(grid).getPropertyValue('grid-auto-rows'));
    rowGap = parseInt(window.getComputedStyle(grid).getPropertyValue('grid-row-gap'));
    rowSpan = Math.ceil((item.querySelector('.review_box').getBoundingClientRect().height + rowGap + 50) / (rowHeight + rowGap));
    item.style.gridRowEnd = "span " + rowSpan;
}

function resizeAllGridItems() {
    allItems = document.getElementsByClassName("review_item");
    for (x = 0; x < allItems.length; x++) {
        resizeGridItem(allItems[x]);
    }
}

function resizeInstance(instance) {
    item = instance.elements[0];
    resizeGridItem(item);
}

window.addEventListener("resize", resizeAllGridItems);

// 좋아요 버튼
//https://codepen.io/nodws/pen/qZLBrd?editors=1011
//https://codepen.io/akm2/pen/rHIsa

// 리뷰 페이지네이션 버튼 출력
function printPagingBox(startPage, endPage, pageNo) {
    const pagingBox = document.getElementById("pagenation");
    pagingBox.innerHTML = "";
    let str = `<a data-page=${startPage - 1} class="pagingBtn"><i class="ic ic_carousel_left_white"></i></a>`;
    str += `<ul class="pagenation_num">`;
    for (let i = startPage; i <= endPage; i++) {
        if(pageNo == i){
            str += `<li class="present"><a data-page=${i} class="pagingBtn">${i}</a></li>`;
        }else{
            str += `<li><a data-page=${i} class="pagingBtn">${i}</a></li>`;
        }
    }
    str += `<a data-page=${endPage + 1} class="pagingBtn"><i class="ic ic_carousel_right_white"></i></a>`;
    pagingBox.innerHTML += str;
}

document.addEventListener("click", (e) => {

    // 페이지네이션 버튼 눌렀을 때 리뷰 출력 변환
    if (e.target.classList.contains("pagingBtn")) {
        let pageNo = e.target.dataset.page;
        printReviewBox(prno, pageNo, mno);

        const book_detail_page_title = document.getElementById("book_detail_page_title");
        book_detail_page_title.focus();
    }

    if (e.target.classList.contains("likeBtn")) {
        const closestAnchor = e.target.closest('a');

        if (e.target.dataset.islike == 1) {
            // 이미 좋아요 > 취소
            if(mno == -1){
                alert("로그인 후 이용 가능합니다.");
                return;
            }
            cancelToServer(e.target.dataset.rno, mno).then(result => {
                console.log(result)
                if (result != -1) {
                    console.log("좋아요 취소 성공!");
                    e.target.dataset.islike = 0;
                    closestAnchor.classList.remove('active');

                    document.getElementById(`likeCnt_${e.target.dataset.rno}`).innerText = result;
                } else {
                    console.log("좋아요 취소 실패!");
                }
            });
        } else if (e.target.dataset.islike == 0) {
            // 좋아요 안함 > 함
            if(mno == -1){
                alert("로그인 후 이용 가능합니다.");
                return;
            }
            doLikeToServer(e.target.dataset.rno, mno).then(result => {
                console.log(result)
                if (result != -1) {
                    console.log("좋아요 성공!");
                    e.target.dataset.islike = 1;
                    closestAnchor.classList.add('active');

                    document.getElementById(`likeCnt_${e.target.dataset.rno}`).innerText = result;
                } else {
                    console.log("좋아요 실패!");
                }
            });
        }
    }

    if(e.target.classList.contains("ic_delete")){
        let rno = e.target.dataset.rno;

        deleteReviewToServer(rno).then(result => {
            if(result == "1"){
                alert("리뷰가 삭제되었습니다.");
                location.reload();
            }else{
                alert("리뷰 삭제 과정중 오류가 발생했습니다.")
            }

        });
    }
});

// (비동기) 리뷰 목록 요청
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
        const url = "/review/doLike/" + rno + "/" + mno;
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
        const url = "/review/cancel/" + rno + "/" + mno;
        const resp = await fetch(url);
        const result = await resp.text();
        return result;
    } catch (error) {
        console.log(error);
    }
}

// (비동기) 리뷰 삭제
async function deleteReviewToServer(rno) {
    try {
        const url = "/review/delete/" + rno;
        const resp = await fetch(url);
        const result = await resp.text();
        return result;
    } catch (error) {
        console.log(error);
    }
}

