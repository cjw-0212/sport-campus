import {http} from "@/api/request";
import type {User} from "@/types/user";
import {useUserStore} from "@/stores/user";

const userStore = useUserStore()
export const requestUserLogin = (name: string, password: string) => {
    return http<User>({
        url: '/user/login',
        method: "POST",
        header: {
            'content-type': 'application/json'
        },
        data: {
            name: name,
            password: password
        }
    })
}
export const requestUserRegister = (name: string, password: string) => {
    return http<void>({
        url: "/user/register",
        method: "POST",
        header: {
            'content-type': 'application/json'
        },
        data: {
            name: name,
            password: password
        }
    })
}

export const requestUserInfo = () => {
    return http<User>({
        url: `/user/info/${userStore.user!.id}`,
        method: "GET"
    })
}
