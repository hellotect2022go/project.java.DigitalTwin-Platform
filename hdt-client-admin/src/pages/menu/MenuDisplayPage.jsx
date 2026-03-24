import AdminPageTemplate from "@/components/common/AdminPageTemplate";
import MenuDisplaySettings from "@/components/menu/MenuDisplaySettings";

const MenuDisplayPage = () => {
  return (
    <AdminPageTemplate
      title="표시 설정"
      description="대·중·소를 선택한 뒤 메뉴에 추가하면 categoryId 한 건으로 연결됩니다."
    >
      <MenuDisplaySettings />
    </AdminPageTemplate>
  );
};

export default MenuDisplayPage;
