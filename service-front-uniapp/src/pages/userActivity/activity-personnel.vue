<template>
  <view class="container">
    <scroll-view scroll-y lower-threshold="20"
                 @scrolltolower="scrollLower"
                 refresher-enabled
                 :refresher-triggered="trigger"
                 @refresherrefresh="refresh"
                 :show-scrollbar="false"
                 :enable-back-to-top="true"
                 style="height: 100vh">
      <view>
        共{{ total }}人参与活动
      </view>
      <view class="list-wrap">
        <view class="list-item-wrap" v-for="item in personList" :key="item.id">
          <view @click="goUserInfo(item.id)">
            <up-avatar bg-color="#f1f1f1" :src="item.avatar" ></up-avatar>
          </view>
          <view class="list-item-info-wrap">
            <text>{{ item.name || "未知" }}<text style="color: #2979ff">({{item.id}})</text></text>
            <text>{{ timestampFormat(Number(item.createTime)) }} 加入</text>
          </view>
          <up-tag text="已打卡" size="mini" type="success" shape="circle" v-if="item.isUserCard===1"></up-tag>
          <up-tag text="未打卡" size="mini" type="error" shape="circle" v-else></up-tag>
        </view>
      </view>
    </scroll-view>
  </view>
  <uni-load-more :status="status"></uni-load-more>
</template>

<script setup>
import {onLoad} from "@dcloudio/uni-app";
import {ref} from 'vue'
import {requestJoinUserList} from "@/api/request/activity";

const publishUserId = ref()
const total = ref(0)
const status = ref('more')
const trigger = ref(false)
const activityId = ref()
const personList = ref([])
const pageParams = ref({
  currentPage: 1,
  pageSize: 10,
})
const getActivityPersonList = async () => {
  const res = await requestJoinUserList(activityId.value, pageParams.value.currentPage, pageParams.value.pageSize)
  total.value = res.data.total == null ? null : res.data.total
  if (res.data.records.length !== 0) {
    personList.value = personList.value?.concat(res.data.records)
    status.value = "more"
  } else {
    status.value = "no-more"
  }
}
const goUserInfo = (id) => {
  uni.navigateTo({
    url: `/pages/userInfo/userInfo?id=${id}`
  })
}
const scrollLower = () => {
  status.value = "loading"
  pageParams.value.currentPage++
  setTimeout(getActivityPersonList, 1000)
}
const refresh = () => {
  trigger.value = true;
  pageParams.value.currentPage = 1
  personList.value = []
  getActivityPersonList()
  setTimeout(() => {
    trigger.value = false;
  }, 1000);
}
const timestampFormat = (timestamp) => {
  if (!timestamp) return '';

  function zeroize(num) {
    return (String(num).length === 1 ? '0' : '') + num;
  }

  let curTimestamp = parseInt(new Date().getTime() / 1000); //当前时间戳
  let timestampDiff = curTimestamp - timestamp; // 参数时间戳与当前时间戳相差秒数

  let curDate = new Date(curTimestamp * 1000); // 当前时间日期对象
  let tmDate = new Date(timestamp * 1000);  // 参数时间戳转换成的日期对象

  let Y = tmDate.getFullYear(), m = tmDate.getMonth() + 1, d = tmDate.getDate();
  let H = tmDate.getHours(), i = tmDate.getMinutes(), s = tmDate.getSeconds();

  if (timestampDiff < 60) { // 一分钟以内
    return "刚刚";
  } else if (timestampDiff < 3600) { // 一小时前之内
    return Math.floor(timestampDiff / 60) + "分钟前";
  } else if (curDate.getFullYear() === Y && curDate.getMonth() + 1 === m && curDate.getDate() === d) {
    return '今天' + zeroize(H) + ':' + zeroize(i);
  } else {
    let newDate = new Date((curTimestamp - 86400) * 1000); // 参数中的时间戳加一天转换成的日期对象
    if (newDate.getFullYear() === Y && newDate.getMonth() + 1 === m && newDate.getDate() === d) {
      return '昨天' + zeroize(H) + ':' + zeroize(i);
    } else if (curDate.getFullYear() === Y) {
      return zeroize(m) + '月' + zeroize(d) + '日 ' + zeroize(H) + ':' + zeroize(i);
    } else {
      return Y + '年' + zeroize(m) + '月' + zeroize(d) + '日 ' + zeroize(H) + ':' + zeroize(i);
    }
  }
}

onLoad((options) => {
  activityId.value = options.activityId
  publishUserId.value = options.publishUserId
  getActivityPersonList()
  console.log(publishUserId.value)
})

</script>

<style lang="scss">
.container {
  width: 100%;
  min-height: 100vh;
  padding: 20rpx;
  background-color: #f4f4f4;
  box-sizing: border-box;

  .list-wrap {
    background: #fff;
    border-radius: 10rpx;
    margin-top: 10rpx;

    .list-item-wrap {
      display: flex;
      padding: 20rpx;

      .list-item-info-wrap {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: center;
        margin-left: 20rpx;
        color: #333;
        font-size: 24rpx;
      }
    }
  }
}
</style>
