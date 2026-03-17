import axios from "axios";

// 환경변수를 통한 base_url 설정
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';
//const API_BASE_URL  = "http://192.168.10.218:8083/api"

// 1. 인증이 필요 없는 공통 인스턴스 (로그인, 회원가입 등)
export const publicApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
        'Content-Type' : 'application/json'
    }
});

// 2. 인증이 필요한 인스턴스 (내 정보, 게시글 작성 등)
const privateApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
        'Content-Type' : 'application/json'
    }
});

// privateApi에만 인터셉터 적용
privateApi.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 응답 인터셉터: 401 에러 발생 시 토큰 갱신 시도
privateApi.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // 401 에러(인증 만료)가 발생하고 재시도하지 않은 요청인 경우
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem('refreshToken');
        // Refresh Token으로 새로운 Access Token 요청
        const res = await axios.post(`${API_BASE_URL}/auth/refresh`, { refreshToken });
        
        const { accessToken } = res.data;
        localStorage.setItem('accessToken', accessToken);

        // 새 토큰으로 기존 요청 재시도
        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        // Refresh Token도 만료된 경우 로그아웃 처리 등 후속 조치
        localStorage.clear();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  }
);

export default privateApi;

// ========== 필요한 API (한글 정리) ==========
// 아래 URL 입력 장소에 연동할 API가 필요합니다.
//
// [사용자 관리]
// - 사용자 등록: 사용자 등록 API (POST)
// - 목록 조회: 사용자 목록 조회 API (GET)
// - 정보 수정: 사용자 정보 수정 API (GET/PUT)
// - 비밀번호 초기화: 비밀번호 초기화 API (POST)
//
// [권한 관리]
// - 사용자 그룹 관리: 그룹 목록/등록/수정/삭제 API
// - 그룹별 메뉴 권한 설정: 그룹별 메뉴 권한 조회/저장 API
// - 그룹별 기능 권한 설정: 그룹별 기능 권한 조회/저장 API
//
// [메뉴 관리]
// - 표시 설정: 메뉴 표시 설정 조회/저장 API
// - 정보 수정: 메뉴 정보 조회/수정 API
//
// [이벤트 관리]
// - 유형 관리: 이벤트 유형 목록/등록/수정/삭제 API
// - 명칭 관리: 이벤트 명칭 목록/등록/수정/삭제 API
// - 임계값 설정: 임계값 조회/저장 API
// - 알림 설정: 알림 설정 조회/저장 API
//
// [로그 관리]
// - 접속 로그: 접속 로그 목록 조회 API
// - 사용 로그: 사용 로그 목록 조회 API
// - 운영 통계: 운영 통계 조회 API
//
// [시스템 관리]
// - 서버 관리 콘솔: 서버 상태/관리 API
// - 로그 보관 기간: 로그 보관 기간 조회/저장 API