<template>
  <up-toast ref="uToastRef"></up-toast>
  <view>
    <u-image width="100%" height="250px" :src="activityInfo.photo"></u-image>

    <view class="activity-info-wrap">
      <text>{{ activityInfo.title || '-' }}</text>
      <text>
        时间：{{ activityInfo.startTime }}
      </text>
      <text>
        地点：{{ activityInfo.addressName }}
      </text>
      <text>
        内容：{{ activityInfo.content }}
      </text>
    </view>
    <view class="activity-desc-wrap">
      <text>

      </text>
    </view>
    <view class="btn-activity-wrap" v-if="publishUserId!==userStore.user.id">
      <u-empty v-if="isJoin" text="已加入该活动"></u-empty>
      <div v-else-if="!activityInfo.isFinish">
        <view v-if="!request" class="btn-activity" @click="show1=true">
          <view>加入活动</view>
        </view>
        <view v-else class="btn-activity">
          <up-loading-icon duration="2000"></up-loading-icon>
        </view>
      </div>
      <u-empty v-else text="活动已结束" mode="favor"></u-empty>
    </view>
    <view class="activity-info-personnel">
      <text>已有{{ activityInfo.joinNum || 0 }}/{{ activityInfo.totalNum || 0 }}人参与</text>
      <text class="text" @click="linkToList(activityInfo.id)" v-if="publishUserId===userStore.user.id">
        查看参与名单
      </text>
    </view>
  </view>
  <up-modal :show="show1" content='确认加入该活动' @confirm="joinActivityById" @cancel="show1=false"
            :showCancelButton="true" contentTextAlign="center"></up-modal>
  <up-modal :show="show2" content='将获取当前位置作为打卡定位中心点,确认开启打卡' @confirm="openCard"
            @cancel="show2=false"
            :showCancelButton="true" contentTextAlign="center"></up-modal>
  <up-modal :show="show3" content='确认结束活动' @confirm="finishActivity" @cancel="show3=false"
            :showCancelButton="true" contentTextAlign="center"></up-modal>
  <view style="margin: 20rpx;padding: 15rpx" v-show="publishUserId===userStore.user.id">
    <up-sticky offset-top="750rpx">
      <up-row customStyle="margin-bottom: 10px">
        <up-col span="6">
          <up-button type="primary" :text="isCard==1?'已开启打卡':'开启打卡'" style="margin: 20rpx;padding: 15rpx"
                     shape="circle" :disabled="isCard==1"
                     @click="show2=true"></up-button>
        </up-col>
        <up-col span="6">
          <up-button type="error" :text="isFinish==1?'已结束活动':'结束活动'" style="margin: 20rpx;padding: 15rpx"
                     shape="circle" :disabled="isFinish==1"
                     @click="show3=true"></up-button>
        </up-col>
      </up-row>
    </up-sticky>
  </view>
  <view style="margin: 20rpx;padding: 15rpx" v-show="publishUserId!==userStore.user.id&&isJoin===1">
    <up-sticky offset-top="750rpx">
      <up-button type="primary" :text="isUserCard===1?'已完成打卡':'打卡'" style="margin: 20rpx;padding: 15rpx"
                 shape="circle" :disabled="isUserCard===1" @click="doCard"
      ></up-button>
    </up-sticky>
  </view>
</template>

<script setup>
import {onLoad} from "@dcloudio/uni-app";
import {ref} from 'vue'
import {
  requestActivityInfoById,
  requestDoCard,
  requestFinishActivity,
  requestIsCard,
  requestIsJoin,
  requestJoinActivity,
  requestOpenCard
} from "@/api/request/activity";
import {useUserStore} from "@/stores/user";

const longitude = ref(0)
const latitude = ref(0)
const isUserCard = ref()
const isCard = ref()
const isFinish = ref()
const userStore = useUserStore()
let show1 = ref(false)
let show2 = ref(false)
let show3 = ref(false)
const uToastRef = ref()
const request = ref(false)
const isJoin = ref(0);
const activityId = ref()
const activityInfo = ref({})
const publishUserId = ref()
const getActivityDetailById = async (id) => {
  const res = await requestActivityInfoById(id)
  if (res.code === 1) {
    activityInfo.value = res.data
  }
}

const doCard = () => {
  console.log('获取当前位置')
  uni.getLocation({
    isHighAccuracy: true,
    type: 'gcj02',
    success: (res) => {
      longitude.value = res.longitude
      latitude.value = res.latitude
      console.log(longitude.value, latitude.value)
      subDoCard()
    }
  })
}
const subDoCard = async () => {
  const res = await requestDoCard(activityId.value, longitude.value, latitude.value)
  if (res.code === 1) {
    isUserCard.value = 1
    uToastRef.value.show({
      type: 'success',
      message: "打卡成功",
      position: "top",
      duration: 1000
    })
  } else {
    uToastRef.value.show({
      type: 'error',
      message: res.message,
      position: "top",
      duration: 1000
    })
  }
}
const joinActivityById = async () => {
  show1.value = false
  request.value = true
  const res = await requestJoinActivity(activityId.value)
  if (res.code === 1) {
    setTimeout(() => {
      isJoin.value = 1
      request.value = false
      uToastRef.value.show({
        type: 'success',
        message: "加入活动成功",
        position: "top",
        duration: 1000
      })
    }, 1000)
  } else {
    request.value = false
    uToastRef.value.show({
      type: 'error',
      message: res.message,
      position: "top",
      duration: 1000
    })
  }
  console.log('加入活动')
}
const openCard = async () => {
  show2.value = false
  console.log('获取当前位置')
  uni.getLocation({
    isHighAccuracy: true,
    type: 'gcj02',
    success: (res) => {
      longitude.value = res.longitude
      latitude.value = res.latitude
      console.log(longitude.value, latitude.value)
      subOpenCard()
    }
  })
}

const subOpenCard = async () => {
  const res = await requestOpenCard(activityId.value, longitude.value, latitude.value)
  if (res.code === 1) {
    isCard.value = 1
    setTimeout(() => {
      uToastRef.value.show({
        type: 'success',
        message: "开启打卡成功",
        position: "top",
        duration: 1000
      })
    }, 1000)
  } else {
    request.value = false
    uToastRef.value.show({
      type: 'error',
      message: res.message,
      position: "top",
      duration: 1000
    })
  }
}
const finishActivity = async () => {
  show3.value = false
  const res = await requestFinishActivity(activityId.value)
  if (res.code === 1) {
    isFinish.value = 1
    setTimeout(() => {
      uToastRef.value.show({
        type: 'success',
        message: "结束活动成功",
        position: "top",
        duration: 1000
      })
    }, 1000)
  } else {
    request.value = false
    uToastRef.value.show({
      type: 'error',
      message: res.message,
      position: "top",
      duration: 1000
    })
  }
}
const linkToList = (activityId) => {
  uni.navigateTo({
    url: `activity-personnel?activityId=${activityId}&publishUserId=${publishUserId.value}`
  });
}
const judgeIsJoin = async () => {
  const res = await requestIsJoin(activityId.value)
  if (res.code === 1) {
    isJoin.value = res.data
  }
}
const getIsUserCard = async () => {
  const res = await requestIsCard(activityId.value)
  isUserCard.value = res.data
}
onLoad((options) => {
  activityId.value = options.id
  publishUserId.value = options.publishUserId
  isCard.value = options.isCard
  isFinish.value = options.isFinish
  getActivityDetailById(options.id)
  judgeIsJoin()
  getIsUserCard()
})
</script>

<style lang="scss" scoped>
.activity-info-wrap {
  display: inline-flex;
  flex-direction: column;
  width: 100%;

  color: #333;
  font-size: 24rpx;
  padding: 10rpx 30rpx;
  background-color: #efefef;
}

.activity-desc-wrap {
  display: flex;
  flex-direction: column;
  width: 100%;
  padding: 10rpx 30rpx;
  color: #999;
  font-size: 12px;
}

.activity-info-personnel {
  color: #333;
  font-size: 24rpx;
  text-align: center;
  margin-top: 30rpx;

  .text {
    color: #2979ff;
  }
}

.btn-activity-wrap {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  margin: 30rpx 0;

  .btn-activity {
    display: inline-flex;
    justify-content: center;
    align-items: center;
    color: #fff;
    width: 220rpx;
    height: 220rpx;
    border: 1rpx solid transparent;
    border-radius: 50%;
    background-color: #2b85e4;
    animation: wave 3s ease-out infinite;
    font-size: 30rpx;
  }
}


@keyframes wave {
  0% {
    transform: scale(0.8);
  }

  50% {
    transform: scale(1);
  }

  100% {

    transform: scale(0.8);
  }
}
</style>
