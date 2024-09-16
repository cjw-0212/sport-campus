<template>
  <scroll-view :scroll-y="true" style="height: 70vh">
    <CComment
        ref="ccRef"
        v-model:userInfo="otherInfo"
        v-model:tableData="tableData"
        v-model:tableTotal="tableTotal"
        @likeFun="likeFun"
        @replyFun="replyFun"
        @deleteFun="deleteFun"
    ></CComment>
  </scroll-view>
  <view class="btn" @tap="openComment">发一条新评论</view>
</template>

<script setup lang="ts">
import {onMounted, ref, watch} from "vue";
import CComment from "@/components/comment/CommentTree.vue";
import {useUserStore} from "@/stores/user";
import {
  requestCommentAgree,
  requestCommentAgreeData,
  requestCommentDisagree,
  requestCommentListById,
  requestDeleteComment,
  requestPublishParentComment,
  requestPublishSubComment
} from "@/api/request/comment";
import type {Comment} from "@/types/comment";

const commentAgreeIds = ref<string[]>()
const userStore = useUserStore()
const articleId = ref<string>()
const articlePublishUserId = ref<string>()
let tableTotal = ref(); // 评论总数
let tableData = ref<Comment[]>(); // 评论表
let props = defineProps({
  articleId: {
    type: String
  },
  articlePublishUserId: {
    type: String
  },
  commentTotal: {
    type: Number
  }
})
watch(
    () => props.articleId,
    (newVal, oldVal) => {
      if (newVal != oldVal) {
        articleId.value = newVal
        getTableData()
      }
    }
)
watch(
    () => props.articlePublishUserId,
    (newVal, oldVal) => {
      if (newVal != oldVal) {
        articlePublishUserId.value = newVal
      }
    }
)
watch(() => props.commentTotal,
    (newVal, oldVal) => {
      if (newVal != oldVal) {
        tableTotal.value = newVal
      }
    })
let otherInfo = ref({
  userId: '',
  userName: '',
  userAvatar: '',
  articleId: articleId,
  articlePublishUserId: articlePublishUserId
});

const getAgreeData = async () => {
  const res = await requestCommentAgreeData()
  if (res.code == 1) {
    commentAgreeIds.value = res.data
  }
}
const ccRef = ref();
const getTableData = async () => {
  if (articleId.value != null) {
    const res = await requestCommentListById(articleId.value)
    tableData.value = res.data
    //对是否点赞赋值
    tableData.value?.forEach(item=>{
      if (commentAgreeIds.value?.includes(item.id)){
        item.is_like=true
      }
    })
  }
}
const openComment = () => {
  ccRef.value.newCommentFun();
}

// 点赞回调事件
const likeFun = async ({params}: any, callback: any) => {
  if (params.is_like == true) {
    //点赞
    const res = await requestCommentAgree(params.id)
    if (res.code != 1) {
      //当请求失败, 调用callback重置点赞效果;
      callback(res)
    }else {
      commentAgreeIds.value?.push(params.id)
    }
  } else {
    //取消点赞
    const res = await requestCommentDisagree(params.id)
    if (res.code != 1) {
      callback(res)
    }
  }
}

// 评论回调事件
const replyFun = async ({params}: any, callback: any) => {
  if (params.replyCommentId == null) {
    //发布一级评论
    const res = await requestPublishParentComment(params)
    if (res.code == 1) {
      //拿到后端返回评论id,删除需要
      const data = {id: res.data}
      //调用callback执行评论插入
      setTimeout(() => callback(data), 500);
    }
  } else {
    //发布回复评论
    const res = await requestPublishSubComment(params)
    if (res.code == 1) {
      const data = {id: res.data}
      setTimeout(() => callback(data), 500)
    }
  }
}


const deleteFun = async ({params, mode}: any, callback: any) => {
  const res = await requestDeleteComment(params,articleId.value!)
  if (res.code === 1) {
    // 当请求成功, 调用callback执行评论删除;目前为了展示效果, 直接执行callback
    setTimeout(() => callback(), 500);
  }
}

onMounted(() => {
  //从持久化仓库获取当前用户的id，姓名，头像地址
  otherInfo.value.userId = userStore.user!.id
  otherInfo.value.userAvatar = userStore.user!.avatar
  otherInfo.value.userName = userStore.user!.name
  //获取用户的点赞评论id集合
  getAgreeData()
})
</script>

<style lang="scss" scoped>
.btn {
  text-align: center;
  color: #fff;
  padding: 20rpx;
  margin: 20rpx;
  border-radius: 20rpx;
  background-color: #2979ff;
}
</style>
