
console.log("상품 등록 페이지 JS");

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
                document.getElementById("titleEl").value = result.title;
                document.getElementById("linkEl").value = result.link;
                document.getElementById("imageEl").value = result.image;
                document.getElementById("authorEl").value = result.author;
                document.getElementById("discountEl").value = result.discount;
                document.getElementById("publisherEl").value = result.publisher;
                document.getElementById("pubdateEl").value = result.pubdate;
                document.getElementById("descriptionEl").value = result.description;
      
                document.getElementById("regBtn").disabled = false; 
            }else{
                printEl.innerText += "\n 이미 등록"
            }
        }else{
            printEl.innerText = "검색 결과가 없습니다.";
        }
    })

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

