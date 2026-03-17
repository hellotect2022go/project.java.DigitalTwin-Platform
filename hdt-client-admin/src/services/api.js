import axios from "axios";

// 환경변수를 통한 base_url 설정
const API_BASE_URL  = "http://192.168.10.218:8083/api"

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