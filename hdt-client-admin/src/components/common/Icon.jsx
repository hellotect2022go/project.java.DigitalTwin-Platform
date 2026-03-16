import HanaLogo from '@/assets/icons/hana-logo.svg?react'
import UserLogo from '@/assets/icons/user-logo.svg?react'
import Search from '@/assets/icons/search.svg?react'
import Logout from '@/assets/icons/logout.svg?react'

export const SvgIcons = {
    Hana: (props) => <HanaLogo {...props}/>,
    User: (props) => <UserLogo {...props}/>,
    Search: (props) => <Search {...props}/>,
    Logout: (props) => <Logout {...props}/>
}