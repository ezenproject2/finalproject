const locationHeader = document.getElementById('locationHeader');
const toggle = document.querySelector('.toggle')

toggle.addEventListener('click', () => {
    locationHeader.classList.toggle('active')
    console.log(locationHeader.classList);
})

// 사용자의 위치를 가져오는 함수
function getLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function (position) {
            var lat = position.coords.latitude;
            var lon = position.coords.longitude;

            // 현재 위치의 지도 로딩
            loadMap(lat, lon);

            // 사용자 위치에서 가까운 매장 5개 찾기
            showNearestStores(lat, lon);
        }, function (error) {
            // 위치 파악 실패시 본점 위치로 자동 세팅
            console.log("위치 정보를 가져올 수 없음! error 0");
            loadMap(37.50125763153371, 127.02508935309022);
        });
    } else {
        alert("위치 정보를 가져올 수 없음! error 1");
        loadMap(37.50125763153371, 127.02508935309022);
    }
}

// 오프라인 매장 위치 리스트
const locations = [
    { name: "강남점", lat: 37.50125763153371, lng: 127.02508935309022 },
    { name: "종로점", lat: 37.56969036845602, lng: 126.98517712707628 },
    { name: "신촌점", lat: 37.556900686688294, lng: 126.94151936299673 },
    { name: "노원점", lat: 37.655791346407995, lng: 127.06233468092012 },
    { name: "상봉점", lat: 37.59728312068519, lng: 127.08776975160595 },
    { name: "송파점", lat: 37.49384662482695, lng: 127.12050404389352 },
    { name: "인천직영점", lat: 37.45044363731795, lng: 126.70282182285416 },
    { name: "안양점", lat: 37.401868948346, lng: 126.91924648760003 },
    { name: "의정부점", lat: 37.401868948346, lng: 126.91924648760003 },
    { name: "구리점", lat: 37.60505705653705, lng: 127.14064324568844 },
    { name: "일산점", lat: 37.65305102746592, lng: 126.77660982233597 },
    { name: "안산점", lat: 37.308438093312574, lng: 126.85093208791548 },
    { name: "성남분당점", lat: 37.35035614966523, lng: 127.11016728620048 },
    { name: "성남모란점", lat: 37.43275737409808, lng: 127.12967108377303 },
    { name: "김포점", lat: 37.64469308033463, lng: 126.66712240505267 },
    { name: "하남점", lat: 37.56257062943442, lng: 127.1919092642279 },
    { name: "천안점", lat: 36.8198107252224, lng: 127.15849073511612 },
    { name: "전주점", lat: 35.84025192123691, lng: 127.1324586087236 },
    { name: "서초점", lat: 37.50201459572809, lng: 127.02452131577509 }
];

// 두 지점 간의 거리 계산 (단위: km)
function calculateDistance(lat1, lon1, lat2, lon2) {
    const R = 6371; // 지구의 반지름 (단위: km)
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLon = (lon2 - lon1) * Math.PI / 180;
    const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    const distance = R * c; // 결과 값은 km
    return distance;
}

// 가까운 매장 리스트 출력
function showNearestStores(userLat, userLon) {
    const distances = locations.map(location => {
        const distance = calculateDistance(userLat, userLon, location.lat, location.lng);
        return { name: location.name, distance: distance, lat: location.lat, lng: location.lng };
    });

    // 거리 순으로 정렬
    distances.sort((a, b) => a.distance - b.distance);

    // 5개 가까운 매장만 추출
    const nearestStores = distances.slice(0, 3);

    // 리스트 표시
    const storeList = document.getElementById('store-list');
    storeList.innerHTML = "";
    nearestStores.forEach(store => {
        let str = ``;
        str += `<li class="nearestBtn" data-name="${store.name}">`
        str += `<p>`;
        str += `<svg xmlns="http://www.w3.org/2000/svg" width="25" height="25" fill="#CDC49E" class="bi-geo-fill" viewBox="0 0 16 16">
                    <path fill-rule="evenodd" d="M4 4a4 4 0 1 1 4.5 3.969V13.5a.5.5 0 0 1-1 0V7.97A4 4 0 0 1 4 3.999zm2.493 8.574a.5.5 0 0 1-.411.575c-.712.118-1.28.295-1.655.493a1.3 1.3 0 0 0-.37.265.3.3 0 0 0-.057.09V14l.002.008.016.033a.6.6 0 0 0 .145.15c.165.13.435.27.813.395.751.25 1.82.414 3.024.414s2.273-.163 3.024-.414c.378-.126.648-.265.813-.395a.6.6 0 0 0 .146-.15l.015-.033L12 14v-.004a.3.3 0 0 0-.057-.09 1.3 1.3 0 0 0-.37-.264c-.376-.198-.943-.375-1.655-.493a.5.5 0 1 1 .164-.986c.77.127 1.452.328 1.957.594C12.5 13 13 13.4 13 14c0 .426-.26.752-.544.977-.29.228-.68.413-1.116.558-.878.293-2.059.465-3.34.465s-2.462-.172-3.34-.465c-.436-.145-.826-.33-1.116-.558C3.26 14.752 3 14.426 3 14c0-.599.5-1 .961-1.243.505-.266 1.187-.467 1.957-.594a.5.5 0 0 1 .575.411"/>
                    </svg>`;
        str += `<span class="nearest_store_name nearestBtn" data-name="${store.name}">${store.name}</span>`;
        str += `</p>
                    <p>${store.distance.toFixed(2)}km</p>
                    </li>`;

        storeList.innerHTML += str;
    });
}

// 지도 이동 (매장 클릭 시)
function moveToStore(name, lat, lon) {
    const moveLatLon = new kakao.maps.LatLng(lat, lon);

    map.panTo(moveLatLon);

    getStoreInfoFromServer(name).then(result => {
        if (result != null) {
            const div = document.getElementById("location_info");
            div.innerHTML = "";
            let str = `<h2>${result.name}</h2>
                       <p>
                       <span class="criterion">매장주소</span>`;
            str += `<span>${result.address}</span>
                        </p>
                        <p><span class="criterion">영업시간</span>`;
            str += `<span>${result.hours}</span></p>
                        <p><span class="criterion">휴점</span>`;
            str += `<span>${result.holiday}</span></p>`;
            str += `<a href="tel:${result.phone}" class="tel">
                        <i class="ic ic_call"></i><span>매장문의</span>
                        <span>${result.phone}</span></a>`;
            div.innerHTML = str;
        }
    });
}

// 현재 위치로 돌아가기
function moveToCurrentLocation() {
    getLocation();
}

// 지도 로딩 함수
function loadMap(lat, lon) {
    const container = document.getElementById("map");

    // 지도 중심 위치 잡기
    const options = {
        center: new kakao.maps.LatLng(lat, lon),
        level: 3 // 지도 확대 수준
    };
    map = new kakao.maps.Map(container, options);

    // 오프라인 매장 마커 표시
    locations.forEach(function (location) {
        const position = new kakao.maps.LatLng(location.lat, location.lng);
        const marker = new kakao.maps.Marker({
            position: position,
            title: location.name
        });
        marker.setMap(map);

        // 이름 표시
        const infowindow = new kakao.maps.InfoWindow({
            content: `<div style="padding:5px;">${location.name}</div>`
        });

        kakao.maps.event.addListener(marker, 'click', function () {
            infowindow.open(map, marker);
        });
    });
}

// 페이지 로드 시 위치 가져오기
window.onload = getLocation;

// (비동기) 오프라인 매장 정보 가져오기
async function getStoreInfoFromServer(name) {
    try {
        const url = "/offline/getDetail/" + name
        const resp = await fetch(url);
        const result = await resp.json();
        console.log(result);
        return result;
    } catch (error) {
        console.log(error);
    }
}

document.addEventListener("click", (e) => {
    if (e.target.classList.contains("ctgBtn")) {
        const storeName = e.target.dataset.name;
        console.log(storeName);
        const store = locations.find(location => location.name === storeName);
        console.log(store);
        if (store) {
            moveToStore(store.name, store.lat, store.lng);
        } else {
            console.log("매장을 찾을 수 없습니다.");
        }
    }

    if (e.target.classList.contains("nearestBtn")) {
        const storeName = e.target.dataset.name;
        const store = locations.find(location => location.name === storeName);
        if (store) {
            moveToStore(store.name, store.lat, store.lng);
        } else {
            console.log("매장을 찾을 수 없습니다.");
        }
    }

});

//// 사용자의 위치를 가져오는 함수
//function getLocation() {
//    if (navigator.geolocation) {
//        navigator.geolocation.getCurrentPosition(function (position) {
//            var lat = position.coords.latitude;
//            var lon = position.coords.longitude;
//
//            // 현재 위치의 지도 로딩
//            loadMap(lat, lon);
//
//            // 사용자 위치에서 가까운 매장 5개 찾기
//            showNearestStores(lat, lon);
//        }, function (error) {
//            // 위치 파악 실패시 본점 위치로 자동 세팅
//            console.log("위치 정보를 가져올 수 없음! error 0");
//            loadMap(37.50125763153371, 127.02508935309022);
//        });
//    } else {
//        alert("위치 정보를 가져올 수 없음! error 1");
//        loadMap(37.50125763153371, 127.02508935309022);
//    }
//}
//
//// 오프라인 매장 위치 리스트
//const locations = [
//    { name: "강남점", lat: 37.50125763153371, lng: 127.02508935309022 },
//    { name: "종로점", lat: 37.56969036845602, lng: 126.98517712707628 },
//    { name: "신촌점", lat: 37.556900686688294, lng: 126.94151936299673 },
//    { name: "노원점", lat: 37.655791346407995, lng: 127.06233468092012 },
//    { name: "상봉점", lat: 37.59728312068519, lng: 127.08776975160595 },
//    { name: "송파점", lat: 37.49384662482695, lng: 127.12050404389352 },
//    { name: "인천직영점", lat: 37.45044363731795, lng: 126.70282182285416 },
//    { name: "안양점", lat: 37.401868948346, lng: 126.91924648760003 },
//    { name: "의정부캠퍼스점", lat: 37.401868948346, lng: 126.91924648760003 },
//    { name: "구리점", lat: 37.60505705653705, lng: 127.14064324568844 },
//    { name: "일산점", lat: 37.65305102746592, lng: 126.77660982233597 },
//    { name: "안산점", lat: 37.308438093312574, lng: 126.85093208791548 },
//    { name: "성남분당캠퍼스점", lat: 37.35035614966523, lng: 127.11016728620048 },
//    { name: "성남모란캠퍼스점", lat: 37.43275737409808, lng: 127.12967108377303 },
//    { name: "김포캠퍼스점", lat: 37.64469308033463, lng: 126.66712240505267 },
//    { name: "하남미사캠퍼스점", lat: 37.56257062943442, lng: 127.1919092642279 },
//    { name: "천안캠퍼스점", lat: 36.8198107252224, lng: 127.15849073511612 },
//    { name: "전주점", lat: 35.84025192123691, lng: 127.1324586087236 },
//    { name: "이젠IT캠퍼스점", lat: 37.50201459572809, lng: 127.02452131577509 }
//];
//
//// 두 지점 간의 거리 계산 (단위: km)
//function calculateDistance(lat1, lon1, lat2, lon2) {
//    const R = 6371; // 지구의 반지름 (단위: km)
//    const dLat = (lat2 - lat1) * Math.PI / 180;
//    const dLon = (lon2 - lon1) * Math.PI / 180;
//    const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
//    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//    const distance = R * c; // 결과 값은 km
//    return distance;
//}
//
//// 가까운 매장 리스트 출력
//function showNearestStores(userLat, userLon) {
//    const distances = locations.map(location => {
//        const distance = calculateDistance(userLat, userLon, location.lat, location.lng);
//        return { name: location.name, distance: distance, lat: location.lat, lng: location.lng };
//    });
//
//    // 거리 순으로 정렬
//    distances.sort((a, b) => a.distance - b.distance);
//
//    // 5개 가까운 매장만 추출
//    const nearestStores = distances.slice(0, 5);
//
//    // 리스트 표시
//    const storeList = document.getElementById('store-list');
//    storeList.innerHTML = "";
//    nearestStores.forEach(store => {
//        const li = document.createElement('li');
//        li.textContent = `${store.name} - ${store.distance.toFixed(2)} km`;
//        li.onclick = () => {
//            moveToStore(store.name, store.lat, store.lng);
//        };
//        storeList.appendChild(li);
//    });
//}
//
//// 지도 이동 (매장 클릭 시)
//function moveToStore(name, lat, lon) {
//    const moveLatLon = new kakao.maps.LatLng(lat, lon);
//
//    map.panTo(moveLatLon);
//
//    getStoreInfoFromServer(name).then(result=>{
//        if(result != null){
//            const div = document.getElementById("storeBox");
//            div.innerHTML = "";
//            let str = `<h3>${result.name}</h3>`;
//            str += `<p>${result.address}</p>`;
//            str += `<p>${result.hours}</p>`;
//            str += `<p>${result.holiday}</p>`;
//            str += `<p>${result.phone}</p>`;
//            div.innerHTML += str;
//        }
//    });
//
//
//}
//
//// 현재 위치로 돌아가기
//function moveToCurrentLocation() {
//    getLocation();
//}
//
//// 지도 로딩 함수
//function loadMap(lat, lon) {
//    const container = document.getElementById("map");
//
//    // 지도 중심 위치 잡기
//    const options = {
//        center: new kakao.maps.LatLng(lat, lon),
//        level: 3 // 지도 확대 수준
//    };
//    map = new kakao.maps.Map(container, options);
//
//    // 오프라인 매장 마커 표시
//    locations.forEach(function(location) {
//        const position = new kakao.maps.LatLng(location.lat, location.lng);
//        const marker = new kakao.maps.Marker({
//            position: position,
//            title: location.name
//        });
//        marker.setMap(map);
//
//        // 이름 표시
//        const infowindow = new kakao.maps.InfoWindow({
//            content: `<div style="padding:5px;">${location.name}</div>`
//        });
//
//        kakao.maps.event.addListener(marker, 'click', function() {
//            infowindow.open(map, marker);
//        });
//    });
//}
//
//// 페이지 로드 시 위치 가져오기
//window.onload = getLocation;
//
//// (비동기) 오프라인 매장 정보 가져오기
//async function getStoreInfoFromServer(name) {
//    try {
//        const url = "/offline/getDetail/" + name
//        const resp = await fetch(url);
//        const result = await resp.json();
//        console.log(result);
//        return result;
//    } catch (error) {
//        console.log(error);
//    }
//}
//
//document.addEventListener("click", (e)=>{
//    if(e.target.classList.contains("ctgBtn")){
//        const storeName = e.target.dataset.name;
//        const store = locations.find(location => location.name === storeName);
//
//        if (store) {
//            moveToStore(store.name, store.lat, store.lng);
//        } else {
//            console.log("매장을 찾을 수 없습니다.");
//        }
//    }
//})