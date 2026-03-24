import privateApi from "./api";

/**
 * @typedef {{
 *   menuName: string;
 *   menuCode: string;
 *   sortOrder: number;
 *   categoryId: number;
 * }} MenuCreateRequest
 */

/**
 * @typedef {{
 *   menuId: number;
 *   menuName: string;
 *   menuCode: string;
 *   sortOrder: number;
 *   categoryId: number | null;
 *   active?: boolean;
 * }} MenuRow
 */

export const MENU_ALL_QUERY_KEY = ["menu", "all"];

/**
 * POST /api/menu — 카테고리 기반 메뉴 생성
 * @param {MenuCreateRequest} body
 * @returns {Promise<unknown>}
 */
export async function createMenuFromCategory(body) {
  const { data } = await privateApi.post("/menu", body);
  return data;
}

/**
 * GET /api/menu/all — 메뉴 전체 (표시·삭제용 menuId 조회)
 * @returns {Promise<MenuRow[]>}
 */
export async function fetchAllMenus() {
  const { data } = await privateApi.get("/menu/all");
  if (!Array.isArray(data)) return [];
  return data.map((m) => ({
    menuId: m.menuId,
    menuName: m.menuName,
    menuCode: m.menuCode,
    sortOrder: m.sortOrder,
    categoryId: m.categoryId ?? null,
    active: m.active,
  }));
}

/**
 * DELETE /api/menu/{menuId}
 * @param {number} menuId
 * @returns {Promise<unknown>}
 */
export async function deleteMenu(menuId) {
  const { data } = await privateApi.delete(`/menu/${menuId}`);
  return data;
}

/**
 * PUT /api/menu/sort-orders — 메뉴 정렬 순서 일괄 수정
 * @param {{ menuId: number; sortOrder: number }[]} items
 * @returns {Promise<unknown>}
 */
export async function updateMenuSortOrders(items) {
  console.log('items',items)
  const { data } = await privateApi.put("/menu/sort-orders", items);
  return data;
}
