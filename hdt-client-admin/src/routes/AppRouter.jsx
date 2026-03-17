import { BrowserRouter, Route, Routes, Navigate } from "react-router-dom"
import PrivateRoute from "./PrivateRoute"
import LoginPage from "@/pages/LoginPage"
import MainLayout from "@/components/layout/MainLayout"
import TestPage from "@/pages/TestPage"
// 사용자 관리
import UserRegisterPage from "@/pages/users/UserRegisterPage"
import UserListPage from "@/pages/users/UserListPage"
import UserEditPage from "@/pages/users/UserEditPage"
import UserPasswordResetPage from "@/pages/users/UserPasswordResetPage"
// 권한 관리
import AuthGroupsPage from "@/pages/auth/AuthGroupsPage"
import AuthMenuPermissionPage from "@/pages/auth/AuthMenuPermissionPage"
import AuthFeaturePermissionPage from "@/pages/auth/AuthFeaturePermissionPage"
// 메뉴 관리
import MenuDisplayPage from "@/pages/menu/MenuDisplayPage"
import MenuEditPage from "@/pages/menu/MenuEditPage"
// 이벤트 관리
import EventTypePage from "@/pages/event/EventTypePage"
import EventNamePage from "@/pages/event/EventNamePage"
import EventThresholdPage from "@/pages/event/EventThresholdPage"
import EventNotificationPage from "@/pages/event/EventNotificationPage"
// 로그 관리
import LogAccessPage from "@/pages/log/LogAccessPage"
import LogUsagePage from "@/pages/log/LogUsagePage"
import LogStatisticsPage from "@/pages/log/LogStatisticsPage"
// 시스템 관리
import SystemServerPage from "@/pages/system/SystemServerPage"
import SystemLogRetentionPage from "@/pages/system/SystemLogRetentionPage"

const AppRouter = () => {
    return(
        <BrowserRouter basename="/admin">
            <Routes>
                {/* 공용 페이지 */}
                <Route path="/login" element={<LoginPage/>}/>

                {/* 로그인 후 토큰 발급받아서 사용 */}
                <Route element={<PrivateRoute/>}>
                    <Route element={<MainLayout/>}>
                        <Route path="/" element={<Navigate to="/users/register" replace />}/>
                        <Route path="/test" element={<TestPage/>}/>
                        {/* 사용자 관리 */}
                        <Route path="/users/register" element={<UserRegisterPage/>}/>
                        <Route path="/users/list" element={<UserListPage/>}/>
                        <Route path="/users/edit" element={<UserEditPage/>}/>
                        <Route path="/users/password-reset" element={<UserPasswordResetPage/>}/>
                        {/* 권한 관리 */}
                        <Route path="/auth/groups" element={<AuthGroupsPage/>}/>
                        <Route path="/auth/menu-permission" element={<AuthMenuPermissionPage/>}/>
                        <Route path="/auth/feature-permission" element={<AuthFeaturePermissionPage/>}/>
                        {/* 메뉴 관리 */}
                        <Route path="/menu/display" element={<MenuDisplayPage/>}/>
                        <Route path="/menu/edit" element={<MenuEditPage/>}/>
                        {/* 이벤트 관리 */}
                        <Route path="/event/type" element={<EventTypePage/>}/>
                        <Route path="/event/name" element={<EventNamePage/>}/>
                        <Route path="/event/threshold" element={<EventThresholdPage/>}/>
                        <Route path="/event/notification" element={<EventNotificationPage/>}/>
                        {/* 로그 관리 */}
                        <Route path="/log/access" element={<LogAccessPage/>}/>
                        <Route path="/log/usage" element={<LogUsagePage/>}/>
                        <Route path="/log/statistics" element={<LogStatisticsPage/>}/>
                        {/* 시스템 관리 */}
                        <Route path="/system/server" element={<SystemServerPage/>}/>
                        <Route path="/system/log-retention" element={<SystemLogRetentionPage/>}/>
                    </Route>
                </Route>
            </Routes>
        </BrowserRouter>
    )
}

export default AppRouter;