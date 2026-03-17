import { SvgIcons } from "@/components/common/Icon";

export const menuData = [
  {
    label: "사용자 관리",
    abbr: "사용자",
    items: [
      { title: "사용자 등록", icon: <SvgIcons.FileCheck/>, path: "/users/register" },
      { title: "목록 조회", icon: <SvgIcons.ClipboardSearch/>, path: "/users/list" },
      { title: "정보 수정", icon: <SvgIcons.Clipboard/>, path: "/users/edit" },
      { title: "비밀번호 초기화", icon: <SvgIcons.LockPassword/>, path: "/users/edit" },
    ]
  },
  {
    label: "권한 관리",
    abbr: "권한", 
    items: [
      { title: "사용자 그룹 관리", icon: <SvgIcons.Users/>, path: "/auth/groups" },
      { title: "그룹별 메뉴 권한 설정", icon: <SvgIcons.Widget/>, path: "/auth/menu" },
      { title: "그룹별 기능 권한 설정", icon: <SvgIcons.Tunning/>, path: "/auth/menu" },
    ]
  },
  {
    label: "메뉴 관리",
    abbr: "메뉴", 
    items: [
      { title: "표시 설정", icon: <SvgIcons.Setting/>, path: "/auth/groups" },
      { title: "정보 수정", icon: <SvgIcons.WidgetAdd/>, path: "/auth/menu" },
    ]
  },
  {
    label: "이벤트 관리",
    abbr: "이벤트", 
    items: [
      { title: "유형 관리", icon: <SvgIcons.Siren/>, path: "/auth/groups" },
      { title: "명칭 관리", icon: <SvgIcons.CheckList/>, path: "/auth/menu" },
      { title: "임계값 설정", icon: <SvgIcons.Layers/>, path: "/auth/menu" },
      { title: "알림 설정", icon: <SvgIcons.Bell/>, path: "/auth/menu" },
    ]
  },
  {
    label: "로그 관리",
    abbr: "로그", 
    items: [
      { title: "접속 로그", icon: <SvgIcons.SidebarCode/>, path: "/auth/groups" },
      { title: "사용 로그", icon: <SvgIcons.Code/>, path: "/auth/menu" },
      { title: "운영 통계", icon: <SvgIcons.PieChart/>, path: "/auth/menu" },
    ]
  },
  {
    label: "시스템 관리",
    abbr: "시스템", 
    items: [
      { title: "서버 관리 콘솔", icon: <SvgIcons.Server/>, path: "/auth/groups" },
      { title: "로그 보관 기간", icon: <SvgIcons.Folder/>, path: "/auth/menu" },
    ]
  }
];