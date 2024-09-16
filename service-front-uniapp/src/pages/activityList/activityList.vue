<template>
  <view>
    <!--    <up-sticky bgColor="#fff">
          <view>
            <u-tabs-swiper ref="uTabs" :list="list" :current="current" @change="tabsChange" :is-scroll="false"
                           swiperWidth="750"></u-tabs-swiper>
          </view>
        </up-sticky>-->
    <!--    <swiper :current="swiperCurrent" @transition="transition" @animationfinish="animationfinish"
                class="swiper-wrap">-->
    <!--      <swiper-item class="swiper-item" v-for="(item, index) in tabs" :key="index">-->
    <up-loading-page :loading="loading"></up-loading-page>
    <scroll-view scroll-y lower-threshold="20"
                 @scrolltolower="scrollLower"
                 refresher-enabled
                 :refresher-triggered="trigger"
                 @refresherrefresh="refresh"
                 :show-scrollbar="false"
                 :enable-back-to-top="true"
                 style="height: 100vh"
                 v-if="!loading">
      <view class="activity-wrap">
        <activity-listitem v-for="row in activityList" :key="row.id" :cover="row.photo"
                           :title="row.title" :totalPerson="row.totalNum" :status="row.isFinish===1"
                           :count="row.joinNum"
                           :id="row.id" :publish-user-id="row.publishUserId"
                           :is-card="row.isCard" :is-finish="row.isFinish"
        />
      </view>
    </scroll-view>


    <!--        <scroll-view scroll-y style="height: 100vh;width: 100%;" @scrolltolower="onreachBottom">
              <view class="activity-wrap">
                <activity-listitem v-for="row in activityList" :key="row.id" :cover="row.cover"
                                   :title="row.title" :totalPerson="row.totalPerson" :status="row.status"
                                   :count="row.count"
                                   :id="row.id"
                />
              </view>
            </scroll-view>-->
    <!--      </swiper-item>-->
    <!--    </swiper>-->
  </view>
  <uni-load-more :status="status"></uni-load-more>
  <uni-fab
      horizontal="right"
      @fabClick="fabClick"
  ></uni-fab>
</template>

<script setup lang="ts">
import {onMounted, ref} from "vue";
import ActivityListitem from "@/components/activity/activity-listitem.vue";
import type {Activity} from "@/types/activity";
import {requestActivityList} from "@/api/request/activity";

const loading = ref(false)
const status = ref('more')
let activityList = ref<Activity[]>()
const trigger = ref(false)
const pageParams = ref({
  currentPage: 1,
  pageSize: 5,
})
const scrollLower = () => {
  status.value = "loading"
  pageParams.value.currentPage++
  setTimeout(getActivityList, 1000)
}
const refresh = () => {
  loading.value = true
  trigger.value = true;
  pageParams.value.currentPage = 1
  activityList.value = []
  getActivityList()
  setTimeout(() => {
    loading.value = false
    trigger.value = false;
  }, 1000);
}
// 因为内部的滑动机制限制，请将tabs组件和swiper组件的current用不同变量赋值
let current = ref(0) // tabs组件的current值，表示当前活动的tab选项
const getActivityList = async () => {
  const res = await requestActivityList(pageParams.value.currentPage, pageParams.value.pageSize, current.value)
  if (res.data.records.length != 0) {
    activityList.value = activityList.value?.concat(res.data.records)
    console.log("追加或刷新", activityList.value)
    status.value = "more"
  } else {
    status.value = "no-more"
  }
}
const fabClick = () => {
  uni.navigateTo({
    url: '/pages/activityPublish/activityPublish'
  })
}
onMounted(async () => {
  const res = await requestActivityList(pageParams.value.currentPage, pageParams.value.pageSize, current.value)
  activityList.value = res.data.records
  console.log("初始化", activityList.value)
})
</script>

<style scoped lang="scss">
.swiper-wrap {
  width: 100%;
  height: 100vh;
}

.activity-wrap {
  width: 100%;
  height: 100%;
  padding: 10px 20rpx;
  box-sizing: border-box;
}
</style>

