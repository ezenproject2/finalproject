
console.log("상품 등록 페이지 JS");

// bookVO 미리 준비
let bookVO = {};
document.getElementById("regBtn").disabled = true; 

// 검색하면 bookVO 채워지도록 함
document.getElementById("searchBtn").addEventListener("click", ()=>{
    document.getElementById("regBtn").disabled = true; 

    const searchEl = document.getElementById("searchEl");
    const printEl = document.getElementById("printEl");

    searchBookByAPI(searchEl.value).then(result => {
        if(result != null){
            printEl.innerText = result.title + "/" + result.author + "/" + result.publisher;
            if(result.isValid == 0){
                printEl.innerText += "\n 미등록";
                document.getElementById("isbnEl").value = result.isbn;
                bookVO = {
                    isbn: result.isbn,
                    title: result.title,
                    link: result.link,
                    image : result.image,
                    author: result.author,
                    discount: result.discount,
                    publisher: result.publisher,
                    pubdate: result.pubdate,
                    description: result.description
                };
                document.getElementById("regBtn").disabled = false; 
            }else{
                printEl.innerText += "\n 이미 등록"
            }
        }else{
            printEl.innerText = "검색 결과가 없습니다.";
        }
    })

});

document.getElementById("regBtn").addEventListener("click", ()=>{
    const productVO = {
        isbn : document.getElementById("isbnEl").value,
        stock : document.getElementById("stockEl").value,
        discountRate : document.getElementById("discountRateEl").value,
        profileLink : document.getElementById("profileLinkEl").value,
        detailLink : document.getElementById("detailLinkEl").value,
        primaryCtg : document.getElementById("primaryCtgEl").value,
        secondaryCtg : document.getElementById("secondaryCtgEl").value
    }

    const bookProductDTO = {
        bookVO : bookVO,
        productVO : productVO
    }

    registerToServer(bookProductDTO).then(result =>{
        if(result === "1"){
            console.log("등록 완료!");
        }else{
            console.log("등록 실패!")
        }
    });
});

async function searchBookByAPI(keyword) {
    try {
        const url = "/product/search/" + keyword
        const resp = await fetch(url);
        const result = await resp.json();
        console.log(result);
        return result;
    } catch (error) {
        console.log(error);
    }
}

async function registerToServer(bookProductDTO) {
    try {
        const url = "/product/register"
        const config = {
            method : 'post',
            headers : {
                'Content-Type' : 'application/json; charset=utf-8'
            },
            body : JSON.stringify(bookProductDTO)
        };
        const resp = await fetch(url, config);
        const result = await resp.text();
        console.log(result);
        return result;
    } catch (error) {
        console.log(error);
    }
}

