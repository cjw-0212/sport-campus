<template>
  <page-meta :page-style="'overflow:'+(show?'hidden':'visible')"></page-meta>
  <scroll-view scroll-y lower-threshold="20"
               @scrolltolower="scrollLower"
               refresher-enabled
               :refresher-triggered="trigger"
               @refresherrefresh="refresh"
               :show-scrollbar="false"
               :enable-back-to-top="true"
               style="height: 100vh">
    <template v-for="article in articleList" :key="article.id">
      <Dynamic
          :name="article.publishUserName"
          :avatar="article.avatar"
          :content="`<h4><i><b>${article.title}</b></i></h4><br>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp`+article.content"
          :img-list="article.medias"
          :like-number="article.agreeNumber"
          :chat-number="article.commentNumber"
          :publish-time="Number(article.createTime)"
          @clickUser="clickUser"
          @clickThumbsup="clickThumbsup(article.id)"
          @clickChat="clickChat(article.id,article.publishUserId,article.commentNumber)"
          @clickShare="clickShare"
          :is-like="isLikeGetIndex(article.id)"
      >
      </Dynamic>
    </template>
  </scroll-view>
  <uni-popup :safe-area="false" ref="popup" type="bottom" border-radius="10px 10px 0 0" background-color="white"
             @change="change">
    <view>
      <Comment :articleId="showCommentArticleId"
               :article-publish-user-id="showCommentArticlePublishUserId"
               :comment-total="commentCount"></Comment>
    </view>
  </uni-popup>
  <uni-load-more :status="status"></uni-load-more>
  <uni-fab
     horizontal="right"
     @fabClick="fabClick"
  ></uni-fab>
</template>

<script setup lang="ts">
import {onMounted, ref} from "vue";
import type {Article} from "@/types/article";
import {
  requestArticleAgree,
  requestArticleAgreeData,
  requestArticleDisagree,
  requestArticlePage
} from "@/api/request/article";
import Dynamic from "@/components/article_item/Dynamic.vue";
import Comment from "@/components/comment/Comment.vue";

const trigger = ref(false)
const popup = ref()
const showCommentArticleId = ref<string>()
const commentCount = ref<number>()
const showCommentArticlePublishUserId = ref<string>()
const show = ref(false)
const articleList = ref<Article[]>([])
const pageParams = ref({
  currentPage: 1,
  pageSize: 5,
})

const status = ref('more')
const refresh = () => {
  trigger.value = true;
  pageParams.value.currentPage = 1
  articleList.value = []
  getArticleData()
  setTimeout(() => {
    trigger.value = false;
  }, 1000);
}
const scrollLower = () => {
  status.value = "loading"
  pageParams.value.currentPage++
  setTimeout(getArticleData, 1000)
}
const title = ref<string>('')
const agreeArticleIds = ref<string[]>()
const isLikeGetIndex = (articleId: string) => {
  return agreeArticleIds.value?.includes(articleId)
}
const getArticleData = async () => {
  const res = await requestArticlePage(pageParams.value.currentPage, pageParams.value.pageSize, title.value)
  if (res.data.records.length != 0) {
    articleList.value = articleList.value?.concat(res.data.records)
    status.value = "more"
  } else {
    status.value = "no-more"
  }
}
const fabClick=()=>{
  uni.navigateTo({
    url:'/pages/articlePublish/articlePublish'
  })
}
const getArticleAgreeData = async () => {
  //获取用户是否点赞数据
  const res1 = await requestArticleAgreeData()
  agreeArticleIds.value = res1.data
}
const clickChat = (articleId: string, publishUserId: string, commentNumber: number) => {
  showCommentArticleId.value = articleId
  showCommentArticlePublishUserId.value = publishUserId
  commentCount.value = commentNumber
  popup.value.open()
}
const change = (e: any) => {
  show.value = e.show
}
const clickUser = () => {
  console.log("clickUser")
}
const clickThumbsup = async (articleId: string) => {
  let isLike = agreeArticleIds.value?.includes(articleId)
  if (isLike) {
    //取消点赞
    const res = await requestArticleDisagree(articleId)
    if (res.code === 1) {
      agreeArticleIds.value = agreeArticleIds.value?.filter(item => item != articleId)
      changeArticleAgreeNumber(articleId, -1)
    }
  } else {
    //点赞
    const res = await requestArticleAgree(articleId)
    if (res.code === 1) {
      agreeArticleIds.value?.push(articleId)
      changeArticleAgreeNumber(articleId, 1)
    }
  }
}
const changeArticleAgreeNumber = (articleId: string, num: number) => {
  articleList.value?.forEach(article => {
    if (article.id === articleId) {
      if (article.agreeNumber == 0 && num == -1) {
        article.agreeNumber = 0;
      } else {
        article.agreeNumber += num
      }
    }
  })
}

const clickShare = () => {
  console.log("clickShare")
}
onMounted(() => {
  getArticleData()
  getArticleAgreeData()
})
</script>

<style scoped lang="scss">

</style>
