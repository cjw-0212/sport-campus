<template>
  <up-sticky>
    <view class="content">
      <!-- titleArr: 选择项数组 dropArr: 下拉项数组 二维数组 @finishDropClick: 下拉筛选完成事件-->
      <DropDownMenu :titleArr="titleArr" :dropArr="dropArr" @finishDropClick="finishClick"></DropDownMenu>
    </view>
  </up-sticky>
  <div>
    <scroll-view scroll-y lower-threshold="20"
                 @scrolltolower="scrollLower"
                 :refresher-triggered="trigger"
                 refresher-enabled
                 @refresherrefresh="refresh"
                 :show-scrollbar="false"
                 :enable-back-to-top="true"
                 style="height: 100vh">
      <view class="activity-wrap">
        <activity-listitem v-for="row in activityList" :key="row.id" :cover="row.photo"
                           :title="row.title" :totalPerson="row.totalNum" :status="row.isFinish===1"
                           :count="row.joinNum"
                           :id="row.id" :publish-user-id="row.publishUserId"
                           :is-card="row.isCard" :is-finish="row.isFinish"
        />
      </view>
    </scroll-view>
    <uni-load-more :status="status"></uni-load-more>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref} from "vue";
import type {Activity} from "@/types/activity";
import {requestActivityListByUserId} from "@/api/request/activity";
import ActivityListitem from "@/components/activity/activity-listitem.vue";
import DropDownMenu from "@/components/user_activity/DropDownMenu.vue";
import {onShow} from "@dcloudio/uni-app";

const titleArr = ['分类', '状态']
const dropArr = [
  [
    {
      text: '我的发布',
      value: 0
    },
    {
      text: '我的加入',
      value: 1
    }
  ],
  [
    {
      text: '进行中',
      value: 0
    },
    {
      text: '已结束',
      value: 1
    }
  ]
]
const finishClick = (data: any) => {
  category.value = data[0] == "" ? 0 : data[0]
  isFinish.value = data[1] == "" ? 0 : data[1]
  console.log(category.value, isFinish.value)
  refresh()
}
const trigger = ref(false)
const category = ref(0)
const isFinish = ref(0)

const activityList = ref<Activity[]>()
const pageParams = ref({
  currentPage: 1,
  pageSize: 5,
})
const status = ref('more')
const refresh = () => {
  trigger.value = true;
  pageParams.value.currentPage = 1
  activityList.value = []
  getActivityData()
  setTimeout(() => {
    trigger.value = false;
  }, 1000);
}
const scrollLower = () => {
  status.value = "loading"
  pageParams.value.currentPage++
  setTimeout(getActivityData, 1000)
}
const getActivityData = async () => {
  const res = await requestActivityListByUserId(category.value, isFinish.value,
      pageParams.value.currentPage, pageParams.value.pageSize)
  if (res.data.records.length != 0) {
    activityList.value = activityList.value?.concat(res.data.records)
    status.value = "more"
  } else {
    status.value = "no-more"
  }
}
onMounted(async () => {
  const res = await requestActivityListByUserId(category.value, isFinish.value,
      pageParams.value.currentPage, pageParams.value.pageSize)
  activityList.value = res.data.records
})
onShow(() => {
  refresh();
})
</script>

<style scoped>
.content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
</style>
