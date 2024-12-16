
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
    const contentEl = document.getElementById("contentEl");
    const ratingEl = document.getElementById("ratingEl");
    const prnoEl = document.getElementById("prnoEl");
    const mnoEl = document.getElementById("mnoEl");


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