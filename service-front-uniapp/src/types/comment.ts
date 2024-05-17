export type Comment = {
	id: string;
	parentCommentId: string;
	articleId: string;
	content: string;
	publishUserId: string;
	publishUserName: string;
	publishUserAvatar: string;
	replyUserName: string;
	replyCommentId: string;
	agreeNumber: number;
	createTime: string;
	owner:boolean;
	is_like:boolean;
	children:[];
	childrenShow:[];
}

