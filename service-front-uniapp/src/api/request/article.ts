import {http, submitFile} from "@/api/request";
import type {Article} from "@/types/article";
import type {Page} from "@/types/page";
import {useUserStore} from "@/stores/user";

const userStore = useUserStore()
export const requestArticlePage = (currentPage: number, pageSize: number, title?: string) => {
    return http<Page<Article[]>>({
        url: "/article/page",
        method: "GET",
        data: {
            currentPage: currentPage,
            pageSize: pageSize,
            title: title
        }
    })
}

export const requestArticleAgree = (articleId: string) => {
    return http<void>({
        url: `/article/agree/${articleId}/${userStore.user!.id}`,
        method: "PUT"
    })
}

export const requestArticleDisagree = (articleId: string) => {
    return http<void>({
        url: `/article/disagree/${articleId}/${userStore.user!.id}`,
        method: "PUT"
    })
}

export const requestArticleAgreeData = () => {
    return http<string[]>({
        url: `/article/agree_data/${userStore.user!.id}`,
        method: "GET",
    })
}

export const requestPublishArticleText = ( title: string, content: string) => {
    return http<string>({
        url: "/article/publish/text",
        method:"POST",
        header: {
            'content-type': 'application/json'
        },
        data:{
            "publishUserId": userStore.user!.id,
            "publishUserName": userStore.user!.name,
            "title": title,
            "content": content
        }
    })
}

export const requestPublishArticleMedia=(articleId:string,filePath:string)=>{
    return submitFile<void>({
        url:`/article/publish/media/${articleId}`,
        name:"file",
        filePath:filePath
    })
}

