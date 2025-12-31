import './style.css'
import {Client} from '@stomp/stompjs'

let stompClient = null;

// 버튼 엘리먼트 가져오기 편의 함수
const getBtn = (id) => document.getElementById(id);

function setConnected(connected) {
    // 연결되면 connect 버튼은 비활성화, disconnect 버튼은 활성화
    getBtn('connectBtn').disabled = connected;
    getBtn('disconnectBtn').disabled = !connected;
    
    // 입력창도 연결 중에는 수정 못 하게 막으면 더 좋습니다.
    document.getElementById('url').disabled = connected;

    if (!connected) {
      document.getElementById("channelBox").innerHTML=null
    }
}

function connect() {
  var url = document.getElementById('url').value 
  
  if (stompClient) {
    stompClient.deactivate();
  }

  stompClient = new Client({
    brokerURL:url,
    onConnect: (frame) => {
        console.log('Connected: ' + frame);
        setConnected(true); // 버튼 비활성화 처리
    },
    onDisconnect: () => {
        console.log('연결이 끊어졌습니다.');
        setConnected(false); // 버튼 다시 활성화 처리
    },
    onWebSocketClose: () => {
        console.log('WebSocket 연결 종료');
        setConnected(false);
    },
    onStompError: (frame) => {
        console.error('STOMP 에러: ' + frame.headers['message']);
        setConnected(false);
    }
  })

  stompClient.activate();
}

function disconnect() {
  if (stompClient) {
    stompClient.deactivate();
  }
}

function subscribeGroup(btn) {
    // 3. 서버에 연결 시도
    const val = btn.parentElement.querySelector('input').value;
    stompClient.subscribe(val,channelResponse)
    
    var box = document.getElementById('channelBox')
    var div = document.createElement('div')
    div.id = `box-${val}`;
    div.innerHTML = `
        <strong>채널: ${val}</strong> 
    `;
    div.style=`
    width: auto;
    min-width: 400px;
    height: 500px; 
    border: 1px solid #ccc; 
    overflow-y: auto;    /* 내용이 많아지면 스크롤 생성 */
    display: flex;       /* 플렉스 박스 사용 */
    flex-direction: column; /* 위에서 아래로 수직 정렬 */
    gap: 10px;           /* 메시지 사이의 간격 */
    padding: 10px;
    margin:10px;
    `
    box.append(div)
    
}

function channelResponse(response) {
    console.log("받은 메시지: " + response.body);
    // 1. 메시지가 온 목적지(채널명)를 확인합니다.
    const dest = response.headers.destination;
    const targetBox = document.getElementById('box-' + dest);
    
    if (targetBox) {
        // 3. 찾은 박스 안에 새로운 메시지 추가
        const msgDiv = document.createElement('div');
        msgDiv.textContent = `> ${response.body}`;
        msgDiv.style=
        `
        border-bottom:1px solid #ddd;
        `
        
        targetBox.appendChild(msgDiv);
        
        // 4. 스크롤 최하단 유지
        targetBox.scrollTop = targetBox.scrollHeight;
    }
}



window.connect = connect;
window.disconnect = disconnect;
window.subscribeGroup = subscribeGroup;

