import { SvgIcons } from "@/components/common/Icon";

export const menuData = [
  {
    label: "사용자 관리",
    abbr: "사용자",
    items: [
      { title: "사용자 등록", icon: <SvgIcons.User/>, path: "/users/register" },
      { title: "목록 조회", icon: <SvgIcons.User/>, path: "/users/list" },
      { title: "정보 수정", icon: <SvgIcons.User/>, path: "/users/edit" },
    ]
  },
  {
    label: "권한 관리",
    abbr: "권한", 
    items: [
      { title: "사용자 그룹 관리", icon: <SvgIcons.User/>, path: "/auth/groups" },
      { title: "그룹별 메뉴 권한", icon: <SvgIcons.User/>, path: "/auth/menu" },
    ]
  }
];