import privateApi from "./api";

/**
 * @typedef {{
 *   categoryId: number;
 *   parentId: number | null;
 *   depth: number;
 *   categoryName: string;
 *   description?: string | null;
 *   displayOrder?: number;
 *   active?: boolean;
 *   fullPath: string;
 *   isRoot?: boolean;
 *   isLeaf?: boolean;
 *   children?: DeviceCategoryNode[];
 * }} DeviceCategoryNode
 */

/**
 * 장비 카테고리 트리 응답 (서버 래퍼)
 * @typedef {{
 *   success: boolean;
 *   message: string;
 *   data: DeviceCategoryNode[];
 *   errorCode: string | null;
 * }} DeviceCategoryListResponse
 */

/**
 * @typedef {{
 *   categoryId: number;
 *   parentId: number | null;
 *   categoryName: string;
 *   fullPath: string;
 *   depth: number;
 *   displayOrder: number;
 *   active: boolean;
 * }} DeviceCategoryFlat
 */

export const DEVICE_CATEGORY_QUERY_KEY = ["device", "device-categories"];

/**
 * GET /device/device-categories — 장비 카테고리 목록(트리)
 * @returns {Promise<DeviceCategoryListResponse>}
 */
export async function categoryFetchAPI() {
  const { data } = await privateApi.get("/device/device-categories");
  return data;
}

/**
 * 중첩 children 트리를 평탄 리스트로 변환 (대·중·소 컬럼 필터용)
 * @param {DeviceCategoryNode[] | undefined | null} nodes
 * @returns {DeviceCategoryFlat[]}
 */
export function flattenDeviceCategoryTree(nodes) {
  /** @type {DeviceCategoryFlat[]} */
  const out = [];
  const walk = (list) => {
    if (!list?.length) return;
    for (const n of list) {
      out.push({
        categoryId: n.categoryId,
        parentId: n.parentId,
        categoryName: n.categoryName,
        fullPath: n.fullPath,
        depth: n.depth,
        displayOrder: n.displayOrder ?? 0,
        active: n.active !== false,
      });
      if (n.children?.length) walk(n.children);
    }
  };
  walk(nodes);
  return out;
}

export function sortByDisplayOrder(a, b) {
  return (a.displayOrder ?? 0) - (b.displayOrder ?? 0);
}
