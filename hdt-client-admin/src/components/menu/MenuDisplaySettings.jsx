import { useEffect, useMemo, useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import styled from "styled-components";
import {
  categoryFetchAPI,
  DEVICE_CATEGORY_QUERY_KEY,
  flattenDeviceCategoryTree,
  sortByDisplayOrder,
} from "@/services/deviceService";
import {
  createMenuFromCategory,
  deleteMenu,
  fetchAllMenus,
  MENU_ALL_QUERY_KEY,
  updateMenuSortOrders,
} from "@/services/menuService";

/**
 * 메뉴 표시 설정 — 대·중·소 1:1 3열 + 메뉴에는 categoryId(숫자) 단일 매핑
 * 장비 카테고리: GET /device/device-categories
 */

function getPathLabel(flatList, categoryId) {
  const row = flatList.find((c) => c.categoryId === categoryId);
  if (!row) return "";
  return row.fullPath?.replace(/\s*>\s*/g, " › ") ?? row.categoryName ?? "";
}

const MenuDisplaySettings = () => {
  const queryClient = useQueryClient();

  // 카테고리 조회 
  const {
    data: categoryTree,
    isLoading,
    isError,
    error,
    refetch,
    isFetching,
  } = useQuery({
    queryKey: DEVICE_CATEGORY_QUERY_KEY,
    queryFn: async () => {
      const res = await categoryFetchAPI();
      if (!res?.success) {
        throw new Error(res?.message || "장비 카테고리 조회에 실패했습니다.");
      }
      return res.data ?? [];
    },
  });

  const flat = useMemo(() => flattenDeviceCategoryTree(categoryTree),
    [categoryTree]
  );

  const [selectedMajorId, setSelectedMajorId] = useState(null);
  const [selectedMidId, setSelectedMidId] = useState(null);
  const [selectedSmallId, setSelectedSmallId] = useState(null);

  // 대분류
  const majors = useMemo(() => {
    return flat
      .filter((c) => c.parentId == null && c.active)
      .sort(sortByDisplayOrder);
  }, [flat]);

  //중분류
  const mids = useMemo(() => {
    if (selectedMajorId == null) return [];
    return flat
      .filter((c) => c.parentId === selectedMajorId && c.active)
      .sort(sortByDisplayOrder);
  }, [flat, selectedMajorId]);

  // 소분류
  const smalls = useMemo(() => {
    if (selectedMidId == null) return [];
    return flat
      .filter((c) => c.parentId === selectedMidId && c.active)
      .sort(sortByDisplayOrder);
  }, [flat, selectedMidId]);


  // 메뉴 조회 
  const { data: menus = [], isLoading: menusLoading } = useQuery({
    queryKey: MENU_ALL_QUERY_KEY,
    queryFn: fetchAllMenus,
  });

  const sortedMenus = useMemo(() => {
    return [...menus].sort(
      (a, b) => (a.sortOrder ?? 0) - (b.sortOrder ?? 0)
    );
  }, [menus]);

  /** 서버 목록과 동기화된 편집용 행 (↑↓ 이동 후 일괄 저장) */
  const [localMenuRows, setLocalMenuRows] = useState([]);

  useEffect(() => {
    const ordered = [...menus].sort(
      (a, b) => (a.sortOrder ?? 0) - (b.sortOrder ?? 0)
    );
    setLocalMenuRows(ordered.map((m) => ({ ...m })));
  }, [menus]);

  const isOrderDirty = useMemo(() => {
    if (localMenuRows.length !== sortedMenus.length) return false;
    return localMenuRows.some(
      (r, i) => r.menuId !== sortedMenus[i]?.menuId
    );
  }, [localMenuRows, sortedMenus]);

  const moveMenuRow = (index, direction) => {
    setLocalMenuRows((prev) => {
      const j = direction === "up" ? index - 1 : index + 1;
      if (j < 0 || j >= prev.length) return prev;
      const next = [...prev];
      [next[index], next[j]] = [next[j], next[index]];
      return next.map((r, i) => ({ ...r, sortOrder: i + 1 }));
    });
  };

  const resetLocalOrder = () => {
    setLocalMenuRows(sortedMenus.map((m) => ({ ...m })));
  };

  const nextSortOrder = useMemo(() => {
    if (!menus.length) return 1;
    return Math.max(...menus.map((m) => m.sortOrder ?? 0)) + 1;
  }, [menus]);

  const createMenuMutation = useMutation({
    mutationFn: ({ menuName, menuCode, sortOrder, categoryId }) =>
      createMenuFromCategory({ menuName, menuCode, sortOrder, categoryId }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: MENU_ALL_QUERY_KEY });
    },
    onError: (err) => {
      const msg =
        err?.response?.data?.message ??
        err?.response?.data?.error ??
        err?.message ??
        "메뉴 생성에 실패했습니다.";
      window.alert(typeof msg === "string" ? msg : JSON.stringify(msg));
    },
  });

  const deleteMenuMutation = useMutation({
    mutationFn: (menuId) => deleteMenu(menuId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: MENU_ALL_QUERY_KEY });
    },
    onError: (err) => {
      const msg =
        err?.response?.data?.message ??
        err?.response?.data?.error ??
        err?.message ??
        "메뉴 삭제에 실패했습니다.";
      window.alert(typeof msg === "string" ? msg : JSON.stringify(msg));
    },
  });

  const sortOrdersMutation = useMutation({
    mutationFn: (payload) => updateMenuSortOrders(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: MENU_ALL_QUERY_KEY });
    },
    onError: (err) => {
      const msg =
        err?.response?.data?.message ??
        err?.response?.data?.error ??
        err?.message ??
        "순서 저장에 실패했습니다.";
      window.alert(typeof msg === "string" ? msg : JSON.stringify(msg));
    },
  });

  const handleSaveSortOrders = () => {
    if (!isOrderDirty) return;
    const payload = localMenuRows.map((r, i) => ({
      menuId: r.menuId,
      sortOrder: i + 1,
    }));
    sortOrdersMutation.mutate(payload);
  };

  useEffect(() => {
    if (majors.length === 0) return;
    setSelectedMajorId((prev) => {
      if (prev != null && majors.some((m) => m.categoryId === prev)) return prev;
      return majors[0].categoryId;
    });
  }, [majors]);

  const activeCategoryId =
    selectedSmallId ?? selectedMidId ?? selectedMajorId ?? null;

  const activeLabel = useMemo(() => {
    if (activeCategoryId == null) return "";
    return getPathLabel(flat, activeCategoryId);
  }, [flat, activeCategoryId]);

  const pickMajor = (id) => {
    setSelectedMajorId(id);
    setSelectedMidId(null);
    setSelectedSmallId(null);
  };

  const pickMid = (id) => {
    setSelectedMidId(id);
    setSelectedSmallId(null);
  };

  const pickSmall = (id) => {
    setSelectedSmallId(id);
  };

  const addToMenu = () => {
    if (activeCategoryId == null) {
      window.alert("대·중·소 중 하나를 선택해 주세요.");
      return;
    }
    if (
      menus.some(
        (m) => Number(m.categoryId) === Number(activeCategoryId)
      )
    ) {
      window.alert("이미 메뉴에 등록된 categoryId 입니다.");
      return;
    }
    const cat = flat.find((c) => c.categoryId === activeCategoryId);
    const menuName = cat?.categoryName ?? activeLabel;
    const menuCode = `MENU_CAT_${activeCategoryId}`;
    const sortOrder = nextSortOrder;

    createMenuMutation.mutate({
      menuName,
      menuCode,
      sortOrder,
      categoryId: activeCategoryId,
    });
  };

  const handleDeleteMenu = (menuId) => {
    if (!window.confirm("이 메뉴를 서버에서 삭제할까요?")) return;
    deleteMenuMutation.mutate(menuId);
  };

  if (isLoading && !categoryTree) {
    return (
      <Wrap>
        <StatusBox>장비 카테고리를 불러오는 중…</StatusBox>
      </Wrap>
    );
  }

  if (isError) {
    return (
      <Wrap>
        <ErrorBox>
          <p>{error?.message ?? "카테고리를 불러오지 못했습니다."}</p>
          <RetryBtn type="button" onClick={() => refetch()}>
            다시 시도
          </RetryBtn>
        </ErrorBox>
      </Wrap>
    );
  }

  return (
    <Wrap>
      {isFetching && !isLoading ? (
        <FetchHint>동기화 중…</FetchHint>
      ) : null}

      <ThreeCol>
        <Pane>
          <PaneTitle>대분류</PaneTitle>
          <ListBox>
            {majors.length === 0 ? (
              <EmptyHint>등록된 대분류가 없습니다.</EmptyHint>
            ) : (
              majors.map((m) => (
                <ListItem
                  key={m.categoryId}
                  type="button"
                  $active={m.categoryId === selectedMajorId}
                  onClick={() => pickMajor(m.categoryId)}
                >
                  {m.categoryName}
                </ListItem>
              ))
            )}
          </ListBox>
          <PaneFoot>장비 카테고리는 API에서 조회합니다.</PaneFoot>
        </Pane>
        
        {/* 중분류 */}
        <Pane>
          <PaneTitle>중분류</PaneTitle>
          <ListBox>
            {mids.length === 0 ? (
              <EmptyHint>대분류를 선택하세요.</EmptyHint>
            ) : (
              mids.map((m) => (
                <ListItem
                  key={m.categoryId}
                  type="button"
                  $active={m.categoryId === selectedMidId}
                  onClick={() => pickMid(m.categoryId)}
                >
                  {m.categoryName}
                </ListItem>
              ))
            )}
          </ListBox>
          <PaneFoot>장비 카테고리는 API에서 조회합니다.</PaneFoot>
        </Pane>

        {/* 소분류 */}
        <Pane>
          <PaneTitle>소분류</PaneTitle>
          <ListBox>
            {smalls.length === 0 ? (
              <EmptyHint>중분류를 선택하세요.</EmptyHint>
            ) : (
              smalls.map((s) => (
                <ListItem
                  key={s.categoryId}
                  type="button"
                  $active={s.categoryId === selectedSmallId}
                  onClick={() => pickSmall(s.categoryId)}
                >
                  {s.categoryName}
                </ListItem>
              ))
            )}
          </ListBox>
          <PaneFoot>장비 카테고리는 API에서 조회합니다.</PaneFoot>
        </Pane>
      </ThreeCol>

      <AddBar>
        <AddInfo>
          <span>선택 categoryId</span>
          <code>{activeCategoryId ?? "—"}</code>
          <span className="path">{activeLabel || "—"}</span>
        </AddInfo>
        <ArrowBtn
          type="button"
          onClick={addToMenu}
          disabled={
            activeCategoryId == null ||
            createMenuMutation.isPending ||
            menusLoading
          }
          title="선택한 categoryId로 서버에 메뉴 생성"
        >
          {createMenuMutation.isPending ? "생성 중…" : "메뉴에 추가 →"}
        </ArrowBtn>
      </AddBar>

      <MenuSection>
        <MenuSectionHeader>
          <MenuTitle>
            메뉴 목록
            {menusLoading ? (
              <MenuTitleHint> (불러오는 중…)</MenuTitleHint>
            ) : null}
            {isOrderDirty ? <DirtyBadge>순서 변경됨</DirtyBadge> : null}
          </MenuTitle>
          <SortToolbar>
            <ToolBtn
              type="button"
              onClick={resetLocalOrder}
              disabled={!isOrderDirty || sortOrdersMutation.isPending}
            >
              순서 초기화
            </ToolBtn>
            <ToolBtn
              type="button"
              $primary
              onClick={handleSaveSortOrders}
              disabled={
                !isOrderDirty ||
                sortOrdersMutation.isPending ||
                localMenuRows.length === 0
              }
            >
              {sortOrdersMutation.isPending ? "저장 중…" : "순서 저장"}
            </ToolBtn>
          </SortToolbar>
        </MenuSectionHeader>
        <MenuTableWrap>
          <MenuTable>
            <thead>
              <tr>
                <th style={{ width: 88 }}>이동</th>
                <th>menuId</th>
                <th>menuName</th>
                <th>menuCode</th>
                <th>categoryId</th>
                <th>경로</th>
                <th>sortOrder</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {localMenuRows.length === 0 ? (
                <tr>
                  <td colSpan={8} style={{ textAlign: "center", color: "#9ca3af" }}>
                    {menusLoading
                      ? "메뉴 목록을 불러오는 중입니다."
                      : "등록된 메뉴가 없습니다. 대·중·소를 선택한 뒤「메뉴에 추가」를 누르세요."}
                  </td>
                </tr>
              ) : (
                localMenuRows.map((r, index) => (
                  <tr key={r.menuId}>
                    <td>
                      <OrderBtnGroup>
                        <OrderBtn
                          type="button"
                          aria-label="위로"
                          onClick={() => moveMenuRow(index, "up")}
                          disabled={
                            index === 0 ||
                            sortOrdersMutation.isPending ||
                            deleteMenuMutation.isPending
                          }
                        >
                          ↑
                        </OrderBtn>
                        <OrderBtn
                          type="button"
                          aria-label="아래로"
                          onClick={() => moveMenuRow(index, "down")}
                          disabled={
                            index >= localMenuRows.length - 1 ||
                            sortOrdersMutation.isPending ||
                            deleteMenuMutation.isPending
                          }
                        >
                          ↓
                        </OrderBtn>
                      </OrderBtnGroup>
                    </td>
                    <td>{r.menuId}</td>
                    <td>{r.menuName}</td>
                    <td>
                      <code>{r.menuCode}</code>
                    </td>
                    <td>
                      <code>{r.categoryId ?? "—"}</code>
                    </td>
                    <td>
                      {r.categoryId != null
                        ? getPathLabel(flat, r.categoryId)
                        : "—"}
                    </td>
                    <td>{r.sortOrder}</td>
                    <td>
                      <RemoveBtn
                        type="button"
                        onClick={() => handleDeleteMenu(r.menuId)}
                        disabled={deleteMenuMutation.isPending}
                      >
                        삭제
                      </RemoveBtn>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </MenuTable>
        </MenuTableWrap>
        <FootNote>
          생성: <code>POST /api/menu</code> · 삭제:{" "}
          <code>DELETE /api/menu/{"{menuId}"}</code> · 목록:{" "}
          <code>GET /api/menu/all</code> · 순서 일괄:{" "}
          <code>PUT /api/menu/sort-orders</code> (body:{" "}
          <code>[ {"{"} menuId, sortOrder {"}"}, … ]</code>)
        </FootNote>
      </MenuSection>
    </Wrap>
  );
};

const Wrap = styled.div`
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 8px 0 24px;
`;

const StatusBox = styled.div`
  padding: 48px 24px;
  text-align: center;
  font-size: 14px;
  color: #64748b;
`;

const ErrorBox = styled.div`
  padding: 24px;
  text-align: center;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 8px;
  color: #991b1b;
  p {
    margin: 0 0 12px 0;
  }
`;

const RetryBtn = styled.button`
  padding: 8px 16px;
  font-size: 13px;
  font-weight: 600;
  color: #fff;
  background: #4a6380;
  border: none;
  border-radius: 6px;
  cursor: pointer;
`;

const FetchHint = styled.div`
  font-size: 12px;
  color: #64748b;
  text-align: right;
`;

const ThreeCol = styled.div`
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  align-items: stretch;
  @media (max-width: 900px) {
    grid-template-columns: 1fr;
  }
`;

const Pane = styled.div`
  display: flex;
  flex-direction: column;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: #fff;
  overflow: hidden;
  min-height: 320px;
`;

const PaneTitle = styled.div`
  padding: 10px 12px;
  font-size: 13px;
  font-weight: 700;
  color: #111d2c;
  background: #e8ecf1;
  border-bottom: 1px solid #d1d5db;
`;

const PaneFoot = styled.div`
  padding: 8px 10px;
  font-size: 11px;
  color: #94a3b8;
  border-top: 1px solid #e5e7eb;
  background: #fafafa;
`;

const ListBox = styled.div`
  flex: 1;
  min-height: 240px;
  max-height: 360px;
  overflow-y: auto;
  padding: 6px;
`;

const ListItem = styled.button`
  display: block;
  width: 100%;
  text-align: left;
  padding: 8px 10px;
  margin-bottom: 4px;
  font-size: 14px;
  border: 1px solid transparent;
  border-radius: 4px;
  background: ${(p) => (p.$active ? "rgba(74, 99, 128, 0.15)" : "transparent")};
  color: ${(p) => (p.$active ? "#1e3a5f" : "#374151")};
  font-weight: ${(p) => (p.$active ? 600 : 400)};
  cursor: pointer;
  &:hover {
    background: ${(p) => (p.$active ? "rgba(74, 99, 128, 0.2)" : "#f3f4f6")};
  }
`;

const EmptyHint = styled.div`
  padding: 24px 12px;
  text-align: center;
  font-size: 13px;
  color: #9ca3af;
`;

const AddBar = styled.div`
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 14px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
`;

const AddInfo = styled.div`
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  color: #475569;
  span.path {
    color: #111d2c;
    font-weight: 500;
  }
  code {
    font-size: 13px;
    color: #0d47a1;
    background: #fff;
    padding: 2px 8px;
    border-radius: 4px;
    border: 1px solid #e2e8f0;
  }
`;

const ArrowBtn = styled.button`
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 700;
  color: #fff;
  background: #4a6380;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.12);
  white-space: nowrap;
  &:hover:not(:disabled) {
    filter: brightness(1.06);
  }
  &:disabled {
    opacity: 0.45;
    cursor: not-allowed;
  }
`;

const MenuSection = styled.section`
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  background: #fafafa;
`;

const MenuSectionHeader = styled.div`
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
`;

const MenuTitle = styled.h3`
  margin: 0;
  font-size: 15px;
  font-weight: 700;
  color: #111d2c;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
`;

const MenuTitleHint = styled.span`
  font-size: 13px;
  font-weight: 500;
  color: #94a3b8;
`;

const DirtyBadge = styled.span`
  font-size: 11px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 999px;
  background: #fef3c7;
  color: #92400e;
`;

const SortToolbar = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
`;

const ToolBtn = styled.button`
  padding: 8px 14px;
  font-size: 13px;
  font-weight: 600;
  border-radius: 6px;
  border: 1px solid ${(p) => (p.$primary ? "#4a6380" : "#cbd5e1")};
  background: ${(p) => (p.$primary ? "#4a6380" : "#fff")};
  color: ${(p) => (p.$primary ? "#fff" : "#334155")};
  cursor: pointer;
  &:hover:not(:disabled) {
    filter: brightness(1.03);
  }
  &:disabled {
    opacity: 0.45;
    cursor: not-allowed;
  }
`;

const OrderBtnGroup = styled.div`
  display: flex;
  gap: 4px;
`;

const OrderBtn = styled.button`
  width: 32px;
  height: 28px;
  padding: 0;
  font-size: 14px;
  line-height: 1;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  background: #fff;
  color: #334155;
  cursor: pointer;
  &:hover:not(:disabled) {
    background: #f1f5f9;
  }
  &:disabled {
    opacity: 0.35;
    cursor: not-allowed;
  }
`;

const MenuTableWrap = styled.div`
  overflow-x: auto;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
`;

const MenuTable = styled.table`
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
  th,
  td {
    padding: 8px 10px;
    border-bottom: 1px solid #f3f4f6;
    text-align: left;
  }
  th {
    background: #f9fafb;
    font-weight: 600;
    color: #374151;
  }
  code {
    font-size: 12px;
    color: #0d47a1;
  }
`;

const RemoveBtn = styled.button`
  padding: 4px 10px;
  font-size: 12px;
  color: #b91c1c;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 4px;
  cursor: pointer;
  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
`;

const FootNote = styled.p`
  margin: 12px 0 0 0;
  font-size: 12px;
  color: #6b7280;
  line-height: 1.5;
  code {
    font-size: 11px;
    background: #f1f5f9;
    padding: 1px 4px;
    border-radius: 3px;
  }
`;

export default MenuDisplaySettings;
