console.log("공지사항 등록 js in!!");

var noticeBox = new FroalaEditor('#noticeBox', {
  // 한글 패치
  language: 'ko',

  toolbarButtons: {
    'moreText': {
      'buttons': ['bold', 'italic', 'underline', 'strikeThrough', 'subscript', 'superscript', 'fontFamily', 'fontSize', 'textColor', 'backgroundColor', 'inlineClass', 'inlineStyle', 'clearFormatting']
    },
    'moreParagraph': {
      'buttons': ['alignLeft', 'alignCenter', 'formatOLSimple', 'alignRight', 'alignJustify', 'formatOL', 'formatUL', 'paragraphFormat', 'paragraphStyle', 'lineHeight', 'outdent', 'indent', 'quote']
    },
    'moreRich': {
      'buttons': ['insertLink', 'insertImage', 'emoticons', 'insertTable', 'fontAwesome', 'specialCharacters', 'embedly', 'insertHR']
    }
  },

  height: 450,   // 높이 설정 (px 단위)
  width: 1280, // 너비 설정 (백분율로 설정 가능)

  // 이미지 업로드 처리
  imageUploadURL: '/notice/upload',
  imageUploadMethod: 'POST',
  imageUploadParam: 'file',

  events: {
    // 이미지 삭제 시 실행되는 함수
    'image.removed': function (image) {
      console.log("이미지 삭제 진행 : " + image[0].currentSrc);
      let url = image[0].currentSrc
      let path = url.split("upload/")[1];

      console.log(path);
      deleteTempFile(path).then(result => {
        if (result == "1") {
        } else {
          console.log("이미지 삭제 작업을 진행 중 오류가 발생했습니다. ");
        }
      });
    }
  }

});

document.getElementById("regBtn").addEventListener('click', () => {
  const title = document.getElementById('title')
  const mno = document.getElementById('mno')

  let formData = {
    content: noticeBox.el.innerHTML,
    title: title.value,
    mno: parseInt(mno.value)
  }

  saveNotice(formData).then(result => {
    if (result == "1") {
      location.href = "/notice/list";
    } else {
      alert("삭제 작업 중 오류가 발생했습니다.")
    }
  })
})

async function saveNotice(formData) {
  console.log("저장을 시작합니다.");

  const url = "/notice/register";
  const config = {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json; charset=utf-8'
    },
    body: JSON.stringify(formData)
  };

  const resp = await fetch(url, config);
  const result = await resp.text();
  return result;
}

async function deleteTempFile(path) {

  const regex = /([0-9a-fA-F-]{36})/;  
  const match = path.match(regex);

  if (match) {
    const uuid = match[0]; 

    const url = "/notice/deleteFile?uuid=" + uuid;
    const resp = await fetch(url);
    const result = await resp.text();
    return result;
  }

}