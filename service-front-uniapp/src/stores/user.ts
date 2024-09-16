import {defineStore} from "pinia";
import {ref} from "vue";
import type {User} from "@/types/user";

// @ts-ignore
export const useUserStore = defineStore(
    "user",
    () => {
        const token = ref<string>('')
        const user = ref<User>()
        const setUserInf = (value: User) => {
            user.value = value
        }
        const setToken = (value: string) => {
            token.value = value
        }
        const clear = () => {
            user.value = {
                isUserCard: 0,
                birthday: "", sex: 0, avatar: "", createTime: "", id: "", intro: "", isUsable: 0, name: ""}
            token.value = ''
        }
        return {
            token,
            user,
            setUserInf,
            setToken,
            clear
        }
    },
    {
        // @ts-ignore
        unistorage: true, // 开启后对 state 的数据读写都将持久化
    },
)
