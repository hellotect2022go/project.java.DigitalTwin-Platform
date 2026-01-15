import { defineConfig } from 'vite'

export default defineConfig({
  server: {
    // 외부 접속 허용 (0.0.0.0으로 설정하면 모든 네트워크 인터페이스를 엽니다)
    host: '0.0.0.0', 
    // 원하는 포트 번호 설정 (기본은 5173)
    port: 3000,
    // 포트가 이미 사용 중일 경우 바로 다음 포트로 넘어갈지 여부
    strictPort: true,
  }
})