import {http, submitFile} from "@/api/request";
import {useUserStore} from "@/stores/user";
import type {Activity} from "@/types/activity";
import type {Page} from "@/types/page";
import type {User} from "@/types/user";

const userStore = useUserStore()
export const requestPublishActivityText = (title: string, content: string, number: number, longitude: number,
                                           latitude: number, address: string, name: string, startTime: string) => {
    return http<string>({
        url: "/activity/publish/text",
        method: "POST",
        header: {
            'content-type': 'application/json'
        },
        data: {
            "publishUserId": userStore.user!.id,
            "totalNum": number,
            "title": title,
            "content": content,
            "longitude": longitude,
            "latitude": latitude,
            "address": address,
            "addressName": name,
            "startTime": startTime
        }
    })
}
export const requestPublishActivityMedia = (activityId: string, filePath: string) => {
    return submitFile<void>({
        url: `/activity/publish/media/${activityId}`,
        name: "file",
        filePath: filePath
    })
}

export const requestActivityList = (currentPage: number, pageSize: number, isFinish: number) => {
    return http<Page<Activity[]>>({
        url: `/activity/page/${userStore.user!.id}/${isFinish}`,
        method: "GET",
        data: {
            "currentPage": currentPage,
            "pageSize": pageSize
        }
    })
}

export const requestActivityInfoById = (activityId: string) => {
    return http<Activity>({
        url: `/activity/info/${activityId}`,
        method: "GET"
    })
}

export const requestJoinActivity = (activityId: string) => {
    return http<void>({
        url: `/activity/join/${userStore.user!.id}/${activityId}`,
        method: "POST"
    })
}

export const requestIsJoin = (activityId: string) => {
    return http<number>({
        url: `/activityUser/isJoin/${userStore.user!.id}/${activityId}`,
        method: "GET"
    })
}

export const requestJoinUserList = (activityId: string, currentPage: number, pageSize: number) => {
    return http<User[]>({
        url: `/activityUser/joinUser/${activityId}`,
        method: "GET",
        data: {
            "currentPage": currentPage,
            "pageSize": pageSize
        }
    })
}

export const requestActivityListByUserId = (category: number, status: number,
                                            currentPage: number, pageSize: number) => {
    return http<Page<Activity[]>>({
        url: `/activity/${userStore.user!.id}/${category}/${status}`,
        method: "GET",
        data: {
            "currentPage": currentPage,
            "pageSize": pageSize
        }
    })
}

export const requestOpenCard = (activityId: string, longitude: number, latitude: number) => {
    return http<void>({
        url: `/activity/${activityId}/openCard`,
        method: "PUT",
        header: {
            'content-type': 'application/x-www-form-urlencoded'
        },
        data: {
            "longitude": longitude,
            "latitude": latitude
        }
    })
}

export const requestFinishActivity = (activityId: string) => {
    return http<void>({
        url: `/activity/${activityId}/finish`,
        method: "PUT"
    })
}

export const requestIsCard = (activityId: string) => {
    return http<number>({
        url: `/card/${activityId}/${userStore.user!.id}`,
        method: "GET"
    })
}

export const requestDoCard = (activityId: string, longitude: number, latitude: number) => {
    return http<void>({
        url: `/card/${activityId}/${userStore.user!.id}`,
        method: "POST",
        header: {
            'content-type': 'application/x-www-form-urlencoded'
        },
        data: {
            "longitude": longitude,
            "latitude": latitude
        }
    })
}

export const requestDeleteActivity = (id: string) => {
    return http<void>({
        url: `/activity/${id}`,
        method: "DELETE"
    })
}
