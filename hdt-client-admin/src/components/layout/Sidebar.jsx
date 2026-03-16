import { useState } from "react";
import styled from "styled-components";
import { SvgIcons } from "../common/Icon";

const Sidebar = () => {
    const [isCollapsed, setIsCollapsed] = useState(false);
    const toggleSidebar = () => setIsCollapsed(!isCollapsed); // 사이드바 확장/축소

    return (
        <SidebarContainer $isCollapsed={isCollapsed}>
            {/* 상단 로고 및 토글 버튼 영역 */}
            <LogoSection onClick={toggleSidebar}>
                <SvgIcons.Hana width={24} height={24}/>
                {!isCollapsed && (
                    <LogoTextWrapper>
                        <LogoText>하나드림타운</LogoText>
                        <AdminText>관리자</AdminText>
                    </LogoTextWrapper>
                )}
            </LogoSection>

            {/* 2. 프로필 영역 */}
            <ProfileSection $isCollapsed={isCollapsed}>
                {isCollapsed ? (
                <CollapsedProfileIcon>
                    <SvgIcons.User width={24} height={24}/>
                </CollapsedProfileIcon>
                ) : (
                <>
                    <UserInfo>
                        <NameText>
                            김하나 <span>과장</span>
                        </NameText>
                        <TeamBadge>유지보수팀</TeamBadge>
                    </UserInfo>
                    <SubInfo>61073 / H204837</SubInfo>
                </>
                )}
                <Divider />
            </ProfileSection>

        </SidebarContainer>
    )

}


const SidebarContainer = styled.aside`
  width: ${props => (props.$isCollapsed ? '80px' : '260px')};
  background-color: #111d2c; // 이미지의 짙은 네이비 톤
  color: #fff;
  height: 100vh;
  transition: width 0.3s ease; // 부드러운 확장/축소 애니메이션
  display: flex;
  flex-direction: column;
  overflow-x: hidden;
`;

const LogoSection = styled.div`
  padding: 20px 24px;
  display: flex;
  align-items: center;
  cursor: pointer;
  min-height: 64px;
  
  &:hover {
    background-color: rgba(255, 255, 255, 0.05);
  }
`;

const LogoTextWrapper = styled.div`
  display: flex;
  align-items: center;
  margin-left: 12px;
  gap: 8px;
`;

const LogoText = styled.span`
  font-family: 'HanaFont', sans-serif; // 전용 폰트 적용 위치
  font-size: 19px;
  font-weight: 400;
  color: #ffffff;
  white-space: nowrap;
`;

const AdminText = styled.span`
  font-size: 16px;
  font-weight: 400;
  color: #ffffff;
  opacity: 0.9;
  white-space: nowrap;
`;

const ProfileSection = styled.div`
  padding: ${props => (props.$isCollapsed ? '20px 0' : '24px')};
  display: flex;
  flex-direction: column;
  align-items: ${props => (props.$isCollapsed ? 'center' : 'flex-start')};
`;

const CollapsedProfileIcon = styled.div`
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #a6adb4;
`;

const UserInfo = styled.div`
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
`;

const NameText = styled.div`
  font-size: 20px;
  font-weight: 700;
  span {
    font-size: 16px;
    font-weight: 400;
    margin-left: 6px;
  }
`;

const TeamBadge = styled.span`
  background-color: rgba(0, 209, 178, 0.1);
  color: #00d1b2;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
`;

const SubInfo = styled.div`
  font-size: 14px;
  color: #a6adb4;
`;

const Divider = styled.div`
  width: 100%;
  height: 1px;
  background-color: rgba(255, 255, 255, 0.1);
  margin-top: 20px;
`;

export default Sidebar;