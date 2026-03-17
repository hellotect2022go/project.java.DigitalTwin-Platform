import { SvgIcons } from "@/components/common/Icon";

export const menuData = [
  {
    label: "사용자 관리",
    abbr: "사용자",
    items: [
      { title: "사용자 등록", icon: <SvgIcons.FileCheck/>, path: "/users/register" },
      { title: "목록 조회", icon: <SvgIcons.ClipboardSearch/>, path: "/users/list" },
      { title: "정보 수정", icon: <SvgIcons.Clipboard/>, path: "/users/edit" },
      { title: "비밀번호 초기화", icon: <SvgIcons.LockPassword/>, path: "/users/password-reset" },
    ]
  },
  {
    label: "권한 관리",
    abbr: "권한",
    items: [
      { title: "사용자 그룹 관리", icon: <SvgIcons.Users/>, path: "/auth/groups" },
      { title: "그룹별 메뉴 권한 설정", icon: <SvgIcons.Widget/>, path: "/auth/menu-permission" },
      { title: "그룹별 기능 권한 설정", icon: <SvgIcons.Tunning/>, path: "/auth/feature-permission" },
    ]
  },
  {
    label: "메뉴 관리",
    abbr: "메뉴",
    items: [
      { title: "표시 설정", icon: <SvgIcons.Setting/>, path: "/menu/display" },
      { title: "정보 수정", icon: <SvgIcons.WidgetAdd/>, path: "/menu/edit" },
    ]
  },
  {
    label: "이벤트 관리",
    abbr: "이벤트",
    items: [
      { title: "유형 관리", icon: <SvgIcons.Siren/>, path: "/event/type" },
      { title: "명칭 관리", icon: <SvgIcons.CheckList/>, path: "/event/name" },
      { title: "임계값 설정", icon: <SvgIcons.Layers/>, path: "/event/threshold" },
      { title: "알림 설정", icon: <SvgIcons.Bell/>, path: "/event/notification" },
    ]
  },
  {
    label: "로그 관리",
    abbr: "로그",
    items: [
      { title: "접속 로그", icon: <SvgIcons.SidebarCode/>, path: "/log/access" },
      { title: "사용 로그", icon: <SvgIcons.Code/>, path: "/log/usage" },
      { title: "운영 통계", icon: <SvgIcons.PieChart/>, path: "/log/statistics" },
    ]
  },
  {
    label: "시스템 관리",
    abbr: "시스템",
    items: [
      { title: "서버 관리 콘솔", icon: <SvgIcons.Server/>, path: "/system/server" },
      { title: "로그 보관 기간", icon: <SvgIcons.Folder/>, path: "/system/log-retention" },
    ]
  }
];