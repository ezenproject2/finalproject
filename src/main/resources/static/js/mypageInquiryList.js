document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll('.ic_arrow_down').forEach(icon => {
        icon.addEventListener('click', function () {
            const parentItem = this.closest('.inquriy_item_wrap');
            const contentWrap = parentItem.querySelector('.inquriy_item_content_wrap');

            // 활성화 토글
            if (contentWrap.classList.contains('active')) {
                contentWrap.classList.remove('active'); // 숨기기
                this.style.transform = 'rotate(0deg)'; // 아이콘 원래대로
            } else {
                contentWrap.classList.add('active'); // 보여주기
                this.style.transform = 'rotate(180deg)'; // 아이콘 회전
            }
        });
    });


    const menuItems = document.querySelectorAll(".list_menu_item");

    function setTapMenu() {
        const currentUrl = window.location.href;
        let tapStatus = null;

        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.has("status")) {
            tapStatus = urlParams.get("status");
        }

        menuItems.forEach(item => {
            const status = item.getAttribute("data-status");

            if (status === tapStatus || (tapStatus === null && !status)) {
                item.classList.add("tap");
            } else {
                item.classList.remove("tap");
            }
        });
    }

    setTapMenu();
})
