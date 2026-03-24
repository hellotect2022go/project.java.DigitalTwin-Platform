import AdminPageTemplate from "@/components/common/AdminPageTemplate";
import MenuPermissionByRole from "@/components/auth/MenuPermissionByRole";

const AuthMenuPermissionPage = () => {
  return (
    <AdminPageTemplate
      title="그룹별 메뉴 권한 설정"
      description="역할(그룹)별로 사이드 메뉴 접근을 허용하거나 제한합니다."
    >
      <MenuPermissionByRole />
    </AdminPageTemplate>
  );
};

export default AuthMenuPermissionPage;
