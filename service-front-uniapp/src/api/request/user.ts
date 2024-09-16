import {http, submitFile} from "@/api/request";
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

export const requestChangeAvatar = (filePath: string) => {
    return submitFile<string>({
        url: `/user/${userStore.user!.id}/avatar`,
        name: "avatar",
        filePath: filePath
    })
}

export const requestChangeInfo = (name: string, sex: number, birthday: string, intro: string) => {
    return http<void>({
        url: `/user/changeInfo`,
        method: "PUT",
        header: {
            'content-type': 'application/json'
        },
        data: {
            id: userStore.user!.id,
            name: name,
            sex: sex,
            birthday: birthday,
            intro: intro
        }
    })
}

export const requestUserInfoById = (id: string) => {
    return http<User>({
        url: `/user/info/${id}`,
        method: "GET"
    })
}
