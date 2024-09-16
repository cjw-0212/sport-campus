import type {Result} from "@/types/result";
import {useUserStore} from "@/stores/user";

export const http = <T>(options: UniApp.RequestOptions) => {
    return new Promise<Result<T>>((resolve, reject) => {
        uni.request({
            ...options,
            //请求成功
            success(res) {
                if (res.statusCode >= 200 && res.statusCode <= 300) {
                    resolve(res.data as Result<T>);
                } else if (res.statusCode == 401) {
                    let userStore = useUserStore()
                    userStore.clear();
                    //未登录跳转登录页面
                    uni.navigateTo({
                        url: '/pages/login/login'
                    });
                    reject(res);
                } else {
                    uni.showToast({
                        icon: 'none',
                        title: (res.data as Result<T>).message || '请求错误'
                    });
                    reject(res);
                }
            },
            //响应失败
            fail(err) {
                uni.showToast({
                    icon: "none",
                    title: '服务异常'
                })
                reject(err)
            }
        })
    })
}

export const submitFile = <T>(options: UniApp.UploadFileOption) => {
    return new Promise<Result<T>>((resolve, reject) => {
        uni.uploadFile({
            ...options,
            //请求成功
            success(res) {
                if (res.statusCode >= 200 && res.statusCode <= 300) {
                    resolve(res.data as unknown as Result<T>);
                } else if (res.statusCode == 401) {
                    let userStore = useUserStore()
                    userStore.clear();
                    //未登录跳转登录页面
                    uni.navigateTo({
                        url: '/pages/login/login'
                    });
                    reject(res);
                } else {
                    uni.showToast({
                        icon: 'none',
                        title: (res.data as unknown as Result<T>).message || '请求错误'
                    });
                    reject(res);
                }
            },
            //响应失败
            fail(err) {
                uni.showToast({
                    icon: "none",
                    title: '网络错误，试试换个网络'
                })
                reject(err)
            }
        })
    })
}
