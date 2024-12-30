
console.log("리뷰 등록 JS");

// 첨부 파일 규칙
const regExp = new RegExp("\.(exe|jar|msi|dll|sh|bat)$");
const maxSize = 1024 * 1024 * 20;

// 첨부 파일 검증
function fileValidation(fileName, fileSize){
    if(regExp.test(fileName)){
        return 0;
    }else if(fileSize > maxSize){
        return 0;
    }else{
        return 1;
    }
}

document.addEventListener('change', (e)=>{
    if(e.target.id == 'fileEl'){
        const fileObj = document.getElementById('fileEl').files;
        console.log(fileObj);

        let isOk = fileValidation(fileObj[0].name, fileObj[0].size);

        if(isOk == 0){
            document.getElementById('regBtn').disabled = true;
        }
    }
});

document.getElementById("regBtn").addEventListener("click", ()=>{
    const contentEl = document.getElementById("review");
    const ratingEl = document.getElementById("ratingEl");
    const prnoEl = document.getElementById("prnoEl");
    const mnoEl = document.getElementById("mnoEl");

    if(contentEl.value == "" || contentEl.value == null){
        alert("빈 값을 입력할 수 없음.");
        return;
    }else if(ratingEl.value == "" || ratingEl.value == null){
        alert("빈 값을 입력할 수 없음.");
        return;
    }else if(isNaN(parseFloat(ratingEl.value))){
        alert("별점은 숫자만 가능함! \n 이 부분은 어차피 나중에 별점 누르는 걸로 바꾸면 해결됨.");
        return;
    }

    const formData = new FormData();
    formData.append("content", contentEl.value);
    formData.append("rating", ratingEl.value);
    formData.append("prno", prnoEl.value);
    formData.append("mno", mnoEl.value);


    const fileObj = document.getElementById('fileEl').files;
    if (fileObj) {
        formData.append("file", fileObj[0]);   
    }

    console.log(formData);

    regReviewToServer(formData).then(result => {
        if (result === "1") {
            console.log("댓글 입력 성공!");
            location.reload();
        } else {
            console.log("댓글 입력 실패!");
        }
    });

})

// (비동기) 리뷰 작성
async function regReviewToServer(formData) {
    try {
        const url = "/review/register"
        const config = {
            method : 'post',
            body : formData
        };
        const resp = await fetch(url, config);
        const result = await resp.text();
        return result;
    } catch (error) {
        console.log(error);
    }
}