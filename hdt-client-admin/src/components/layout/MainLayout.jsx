import { Outlet } from "react-router-dom";
import Header from "./Header";
import Sidebar from "./Sidebar";
import styled from "styled-components";

const MainLayout = () => {
    return (
        <LayoutWrapper>
            <Sidebar/>
            <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
                <Header/>
                <ContentArea>
                    <Outlet/>
                </ContentArea>
            </div>
        </LayoutWrapper>
    )
}



const LayoutWrapper = styled.div`
  display: flex;
  min-height: 100vh;
`;

const ContentArea = styled.main`
  flex: 1;
  padding: 20px;
  background-color: var(--bg-color);
`;

export default MainLayout;