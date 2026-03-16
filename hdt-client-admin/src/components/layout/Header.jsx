import styled from "styled-components";
import { SvgIcons } from "../common/Icon";

const Header = () => {
    return (
    <HeaderContainer>
      {/* 왼쪽: 탭 영역 */}
      <TabArea>
        <TabItem>사용자탭 ×</TabItem>
        <TabItem $active>기본정보 ×</TabItem>
        <TabItem>사용자탭 ×</TabItem>
      </TabArea>

      {/* 오른쪽: 시스템 정보 영역 */}
      <SystemArea>
        <SearchWrapper>
          <input type="text" placeholder="통합검색" />
          <SvgIcons.Search width={13} height={13}/>
        </SearchWrapper>
        
        <InfoDivider />
        
        <StatusInfo>
          <span>2026.02.23</span>
          <span>14:03</span>
          <span>💧 10%</span>
          <span>☀️ 맑음 19°C</span>
        </StatusInfo>

        <LogoutButton onClick={()=>alert("로그아웃 할거임? 아직은 alert 로 곧 modal 로 대체할 예정임")}>
          {/* <FiLogOut /> 로그아웃 */}
          <SvgIcons.Logout width={24} height={24}/>
          로그아웃
        </LogoutButton>
      </SystemArea>
    </HeaderContainer>
    )
}


const HeaderContainer = styled.header`
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 50px;
  background-color: #ffffff;
  border-bottom: 1px solid #e0e0e0;
  padding: 0 20px;
`;

const TabArea = styled.div`
  display: flex;
  gap: 4px;
`;

const TabItem = styled.div`
  padding: 6px 12px;
  font-size: 13px;
  background-color: ${props => props.$active ? '#4a69bd' : '#f1f2f6'};
  color: ${props => props.$active ? '#ffffff' : '#595959'};
  border-radius: 4px 4px 0 0;
  cursor: pointer;
  white-space: nowrap;
`;

const SystemArea = styled.div`
  display: flex;
  align-items: center;
  gap: 20px;
  color: #595959;
  font-size: 14px;
`;

const SearchWrapper = styled.div`
  position: relative;
  input {
    padding: 4px 30px 4px 10px;
    border: 1px solid #dcdde1;
    border-radius: 4px;
    outline: none;
  }
  svg {
    position: absolute;
    right: 8px;
    top: 50%;
    transform: translateY(-50%);
    color: #a6adb4;
  }
`;

const StatusInfo = styled.div`
  display: flex;
  gap: 15px;
  font-weight: 500;
`;

const InfoDivider = styled.div`
  width: 1px;
  height: 16px;
  background-color: #dcdde1;
`;

const LogoutButton = styled.button`
  display: flex;
  align-items: center;
  gap: 6px;
  background: none;
  border: none;
  color: #595959;
  cursor: pointer;
  font-weight: 600;
  
  &:hover { color: #2f3542; }
`;

export default Header;