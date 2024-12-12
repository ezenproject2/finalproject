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
    { name: "의정부캠퍼스점", lat: 37.401868948346, lng: 126.91924648760003 },
    { name: "구리점", lat: 37.60505705653705, lng: 127.14064324568844 },
    { name: "일산점", lat: 37.65305102746592, lng: 126.77660982233597 },
    { name: "안산점", lat: 37.308438093312574, lng: 126.85093208791548 },
    { name: "성남분당캠퍼스점", lat: 37.35035614966523, lng: 127.11016728620048 },
    { name: "성남모란캠퍼스점", lat: 37.43275737409808, lng: 127.12967108377303 },
    { name: "김포캠퍼스점", lat: 37.64469308033463, lng: 126.66712240505267 },
    { name: "하남미사캠퍼스점", lat: 37.56257062943442, lng: 127.1919092642279 },
    { name: "천안캠퍼스점", lat: 36.8198107252224, lng: 127.15849073511612 },
    { name: "전주점", lat: 35.84025192123691, lng: 127.1324586087236 },
    { name: "이젠IT캠퍼스점", lat: 37.50201459572809, lng: 127.02452131577509 }
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
    const nearestStores = distances.slice(0, 5);

    // 리스트 표시
    const storeList = document.getElementById('store-list');
    storeList.innerHTML = "";
    nearestStores.forEach(store => {
        const li = document.createElement('li');
        li.textContent = `${store.name} - ${store.distance.toFixed(2)} km`;
        li.onclick = () => {
            moveToStore(store.name, store.lat, store.lng);
        };
        storeList.appendChild(li);
    });
}

// 지도 이동 (매장 클릭 시)
function moveToStore(name, lat, lon) {
    const moveLatLon = new kakao.maps.LatLng(lat, lon);
    
    map.panTo(moveLatLon);
    
    getStoreInfoFromServer(name).then(result=>{
        if(result != null){
            const div = document.getElementById("storeBox");
            div.innerHTML = "";
            let str = `<h3>${result.name}</h3>`;
            str += `<p>${result.address}</p>`;
            str += `<p>${result.hours}</p>`;
            str += `<p>${result.holiday}</p>`;
            str += `<p>${result.phone}</p>`;
            div.innerHTML += str;
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
    locations.forEach(function(location) {
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

        kakao.maps.event.addListener(marker, 'click', function() {
            infowindow.open(map, marker);
        });
    });
}

// 페이지 로드 시 위치 가져오기
window.onload = getLocation;

// (비동기) 오프라인 매장 정보 가져오기
async function getStoreInfoFromServer(name) {
    try {
        const url = "/map/getDetail/" + name
        const resp = await fetch(url);
        const result = await resp.json();
        console.log(result);
        return result;
    } catch (error) {
        console.log(error);
    }
}

document.addEventListener("click", (e)=>{
    if(e.target.classList.contains("ctgBtn")){
        const storeName = e.target.dataset.name;
        const store = locations.find(location => location.name === storeName);
        
        if (store) {
            moveToStore(store.name, store.lat, store.lng);
        } else {
            console.log("매장을 찾을 수 없습니다.");
        }
    }
})