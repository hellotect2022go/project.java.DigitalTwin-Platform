import { BrowserRouter, Route, Routes } from "react-router-dom"
import PrivateRoute from "./PrivateRoute"
import LoginPage from "@/pages/LoginPage"
import MainLayout from "@/components/layout/MainLayout"
import TestPage from "@/pages/TestPage"

const AppRouter = () => {
    return(
        <BrowserRouter>
            <Routes>
                {/* 공용 페이지 */}
                <Route path="/login" element={<LoginPage/>}/>
                
                {/* 로그인 후 토큰 발급받아서 사용 */}
                <Route element={<PrivateRoute/>}>
                    <Route element={<MainLayout/>}>
                        <Route path="/test" element={<TestPage/>}/>
                    </Route>
                </Route>

            </Routes>
        </BrowserRouter>
    )
}

export default AppRouter;