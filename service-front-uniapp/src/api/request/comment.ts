import {http} from "@/api/request";
import type {Comment} from "@/types/comment";
import {useUserStore} from "@/stores/user";

const userStore=useUserStore()
export const requestPublishParentComment = (params: any) => {
    return http<string>({
        url: "/comment/publish/parent",
        method: "POST",
        header: {
            'content-type': 'application/json'
        },
        data: {
            articleId: params.articleId,
            content: params.content,
            publishUserId: params.publishUserId,
            publishUserName: params.publishUserName,
            publishUserAvatar: params.publishUserAvatar
        }
    })
}

export const requestPublishSubComment = (params: any) => {
    return http<string>({
        url: '/comment/publish/sub',
        method: "POST",
        header: {
            'content-type': 'application/json'
        },
        data: {
            parentCommentId: params.parentCommentId,
            articleId: params.articleId,
            content: params.content,
            publishUserId: params.publishUserId,
            publishUserName: params.publishUserName,
            publishUserAvatar: params.publishUserAvatar,
            replyUserName: params.replyUserName,
            replyCommentId: params.replyCommentId
        }
    })
}

export const requestCommentListById = (articleId: string) => {
    return http<Comment[]>({
        url: `/comment/list/${articleId}`,
        method: "GET"
    })
}

export const requestDeleteComment = (commentIds: string[],articleId:string) => {
    console.log(commentIds)
    return http<void>({
        url: `/comment/${articleId}`,
        method: "DELETE",
        header: {
            'content-type': 'application/x-www-form-urlencoded'
        },
        data: {
            ids:commentIds
        }
    })
}

export const requestCommentAgree=(commentId:string)=>{
    return http<void>({
        url: `/comment/agree/${commentId}/${userStore.user!.id}`,
        method: "PUT"
    })
}

export const requestCommentDisagree=(commentId:string)=>{
    return http<void>({
        url: `/comment/disagree/${commentId}/${userStore.user!.id}`,
        method: "PUT"
    })
}

export const requestCommentAgreeData=()=>{
    return http<string[]>({
        url: `/comment/agree_data/${userStore.user!.id}`,
        method: "GET",
    })
}
