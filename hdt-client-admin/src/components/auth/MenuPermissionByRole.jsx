import { useMemo, useState } from "react";
import styled from "styled-components";

/**
 * 관리자 사이드 메뉴 트리 (권한 설정용 id)
 * 실제 라우트와 맞추고, API는 roleId + menuKey 저장 형태로 맞추면 됨.
 */
const MENU_TREE = [
  {
    sectionId: "user",
    sectionLabel: "사용자 관리",
    items: [{ menuId: "manage_user", label: "사용자 관리", path: "/manage/user" }],
  },
  {
    sectionId: "auth",
    sectionLabel: "권한 관리",
    items: [
      { menuId: "manage_group", label: "사용자 그룹 관리", path: "/manage/group" },
      { menuId: "menu_permission", label: "그룹별 메뉴 권한 설정", path: "/auth/menu-permission" },
      { menuId: "feature_permission", label: "그룹별 기능 권한 설정", path: "/auth/feature-permission" },
    ],
  },
  {
    sectionId: "menu",
    sectionLabel: "메뉴 관리",
    items: [
      { menuId: "menu_display", label: "표시 설정", path: "/menu/display" },
      { menuId: "menu_edit", label: "정보 수정", path: "/menu/edit" },
    ],
  },
  {
    sectionId: "event",
    sectionLabel: "이벤트 관리",
    items: [
      { menuId: "event_type", label: "유형 관리", path: "/event/type" },
      { menuId: "event_name", label: "명칭 관리", path: "/event/name" },
      { menuId: "event_threshold", label: "임계값 설정", path: "/event/threshold" },
      { menuId: "event_notification", label: "알림 설정", path: "/event/notification" },
    ],
  },
  {
    sectionId: "log",
    sectionLabel: "로그 관리",
    items: [
      { menuId: "log_access", label: "접속 로그", path: "/log/access" },
      { menuId: "log_usage", label: "사용 로그", path: "/log/usage" },
      { menuId: "log_stats", label: "운영 통계", path: "/log/statistics" },
    ],
  },
  {
    sectionId: "system",
    sectionLabel: "시스템 관리",
    items: [
      { menuId: "system_server", label: "서버 관리 콘솔", path: "/system/server" },
      { menuId: "system_retention", label: "로그 보관 기간", path: "/system/log-retention" },
    ],
  },
];

const ROLE_OPTIONS = [
  { roleId: 1, roleName: "최종관리자" },
  { roleId: 2, roleName: "운영자" },
  { roleId: 3, roleName: "조회자" },
];

/** 더미: roleId → Set<menuId> 또는 Record<menuId, boolean> */
function buildInitialPermissions() {
  const allIds = MENU_TREE.flatMap((s) => s.items.map((i) => i.menuId));
  const full = Object.fromEntries(allIds.map((id) => [id, true]));
  return {
    1: { ...full },
    2: {
      ...Object.fromEntries(allIds.map((id) => [id, false])),
      manage_user: true,
      manage_group: true,
      menu_permission: true,
      log_access: true,
      log_usage: true,
    },
    3: {
      ...Object.fromEntries(allIds.map((id) => [id, false])),
      manage_user: true,
      log_access: true,
      log_stats: true,
    },
  };
}

const MenuPermissionByRole = () => {
  const [roleId, setRoleId] = useState(1);
  const [permissionsByRole, setPermissionsByRole] = useState(() => buildInitialPermissions());

  const current = useMemo(
    () => permissionsByRole[roleId] ?? {},
    [permissionsByRole, roleId]
  );

  const toggleMenu = (menuId) => {
    setPermissionsByRole((prev) => ({
      ...prev,
      [roleId]: {
        ...prev[roleId],
        [menuId]: !prev[roleId]?.[menuId],
      },
    }));
  };

  const setSectionAll = (section, value) => {
    const ids = section.items.map((i) => i.menuId);
    setPermissionsByRole((prev) => {
      const next = { ...prev[roleId] };
      ids.forEach((id) => {
        next[id] = value;
      });
      return { ...prev, [roleId]: next };
    });
  };

  const handleSave = () => {
    // 필요 API: PUT /auth/roles/{roleId}/menu-permissions body: { menuId: boolean } 또는 menuIds[]
    console.log("저장", { roleId, permissions: current });
    window.alert("저장 요청이 전송되었습니다. (목업)");
  };

  const selectedRoleName = ROLE_OPTIONS.find((r) => r.roleId === roleId)?.roleName ?? "";

  return (
    <Wrap>
      <Toolbar>
        <ToolbarLeft>
          <FieldLabel htmlFor="role-select">그룹(역할)</FieldLabel>
          <RoleSelect
            id="role-select"
            value={roleId}
            onChange={(e) => setRoleId(Number(e.target.value))}
          >
            {ROLE_OPTIONS.map((r) => (
              <option key={r.roleId} value={r.roleId}>
                {r.roleName}
              </option>
            ))}
          </RoleSelect>
          <RoleHint>
            선택한 역할에 대해 사이드 메뉴 접근을 허용/차단합니다.
          </RoleHint>
        </ToolbarLeft>
        <SaveButton type="button" onClick={handleSave}>
          저장
        </SaveButton>
      </Toolbar>

      <Panel>
        <PanelTitle>메뉴 접근 권한 — {selectedRoleName}</PanelTitle>
        <ScrollArea>
          {MENU_TREE.map((section) => {
            const sectionIds = section.items.map((i) => i.menuId);
            const allOn = sectionIds.every((id) => current[id]);
            const someOn = sectionIds.some((id) => current[id]);
            return (
              <Section key={section.sectionId}>
                <SectionHeader>
                  <SectionTitle>{section.sectionLabel}</SectionTitle>
                  <SectionActions>
                    <MiniLink type="button" onClick={() => setSectionAll(section, true)}>
                      전체 허용
                    </MiniLink>
                    <MiniLink type="button" onClick={() => setSectionAll(section, false)}>
                      전체 해제
                    </MiniLink>
                    <Badge $active={allOn}>{allOn ? "전체 허용" : someOn ? "일부" : "없음"}</Badge>
                  </SectionActions>
                </SectionHeader>
                <MenuTable>
                  <thead>
                    <tr>
                      <Th $narrow>허용</Th>
                      <Th>메뉴명</Th>
                      <Th $path>경로</Th>
                    </tr>
                  </thead>
                  <tbody>
                    {section.items.map((item) => (
                      <tr key={item.menuId}>
                        <Td $narrow>
                          <CheckLabel>
                            <input
                              type="checkbox"
                              checked={!!current[item.menuId]}
                              onChange={() => toggleMenu(item.menuId)}
                            />
                          </CheckLabel>
                        </Td>
                        <Td>{item.label}</Td>
                        <Td $muted>{item.path}</Td>
                      </tr>
                    ))}
                  </tbody>
                </MenuTable>
              </Section>
            );
          })}
        </ScrollArea>
      </Panel>

      <FootNote>
        필요 API 예: GET /auth/roles/&#123;roleId&#125;/menu-permissions, PUT 동일 경로로 맵 저장
      </FootNote>
    </Wrap>
  );
};

const Wrap = styled.div`
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 0;
`;

const Toolbar = styled.div`
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: flex-end;
  gap: 12px;
  padding: 16px 20px;
  background: #fff;
  border: 1px solid #e1e2e5;
  border-radius: 8px;
`;

const ToolbarLeft = styled.div`
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
`;

const FieldLabel = styled.label`
  font-size: 14px;
  font-weight: 600;
  color: #374151;
`;

const RoleSelect = styled.select`
  min-width: 200px;
  padding: 8px 12px;
  font-size: 14px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  background: #fff;
`;

const RoleHint = styled.span`
  font-size: 13px;
  color: #6b7280;
  @media (max-width: 768px) {
    width: 100%;
  }
`;

const SaveButton = styled.button`
  padding: 10px 28px;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  background: #4a6380;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  &:hover {
    background: #3d5370;
  }
`;

const Panel = styled.div`
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
`;

const PanelTitle = styled.h2`
  margin: 0;
  padding: 14px 18px;
  font-size: 15px;
  font-weight: 700;
  color: #111d2c;
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
`;

const ScrollArea = styled.div`
  flex: 1;
  overflow-y: auto;
  padding: 12px 16px 20px;
  max-height: calc(100vh - 280px);
`;

const Section = styled.div`
  margin-bottom: 20px;
  &:last-child {
    margin-bottom: 0;
  }
`;

const SectionHeader = styled.div`
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
`;

const SectionTitle = styled.h3`
  margin: 0;
  font-size: 14px;
  font-weight: 700;
  color: #1976d2;
`;

const SectionActions = styled.div`
  display: flex;
  align-items: center;
  gap: 10px;
`;

const MiniLink = styled.button`
  padding: 0;
  border: none;
  background: none;
  font-size: 12px;
  color: #4a6380;
  cursor: pointer;
  text-decoration: underline;
  &:hover {
    color: #2c3e50;
  }
`;

const Badge = styled.span`
  font-size: 11px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 999px;
  background: ${(p) => (p.$active ? "#dcfce7" : "#f3f4f6")};
  color: ${(p) => (p.$active ? "#166534" : "#6b7280")};
`;

const MenuTable = styled.table`
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  overflow: hidden;
`;

const Th = styled.th`
  text-align: left;
  padding: 8px 12px;
  background: #f3f4f6;
  font-weight: 600;
  color: #374151;
  border-bottom: 1px solid #e5e7eb;
  ${(p) => p.$narrow && "width: 64px; text-align: center;"}
  ${(p) => p.$path && "width: 40%; color: #6b7280; font-weight: 500;"}
`;

const Td = styled.td`
  padding: 8px 12px;
  border-bottom: 1px solid #f3f4f6;
  color: #111827;
  ${(p) => p.$narrow && "text-align: center; vertical-align: middle;"}
  ${(p) => p.$muted && "font-size: 12px; color: #6b7280; font-family: monospace;"}
`;

const CheckLabel = styled.label`
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  input {
    width: 18px;
    height: 18px;
    accent-color: #4a6380;
  }
`;

const FootNote = styled.p`
  margin: 0;
  font-size: 12px;
  color: #9ca3af;
`;

export default MenuPermissionByRole;
