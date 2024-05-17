export type Article = {
    id: string;
    publishUserId: string;
    publishUserName: string;
    avatar: string;
    title: string;
    content: string;
    agreeNumber: number;
    commentNumber: number;
    createTime: string;
    medias: string[];
}
