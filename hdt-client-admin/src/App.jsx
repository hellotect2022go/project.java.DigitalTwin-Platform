import { useEffect } from "react";

function App() {
  useEffect(() => {
    document.documentElement.setAttribute('data-theme', 'classic');
    // 만약 테마에 따라 폰트를 바꾸고 싶다면 CSS에서 처리하는 게 정석입니다.
  }, []);

  return (
    <div 
      style={{fontFamily:"NotoSansKR"}}
      >
      HELLO
    </div>
  )
}

export default App
