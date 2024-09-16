<template>
  <view class="user">
    <!-- 底图 - 开始 -->
    <image class="user-header-image" src="/static/myInfo_background.png"></image>
    <!-- 底图 - 结束 -->

    <!-- 用户信息 - 开始 -->
    <view class="user-info-box">
      <u-avatar :src="userInfo.avatar" shape="square"></u-avatar>
      <view class="user-info-right">
        <view class="user-nickname">{{ userInfo.name }}</view>
        <view class="user-phone" v-if="userInfo.phone">{{
            userInfo.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
          }}
        </view>
      </view>
    </view>
    <!-- 用户信息 - 结束 -->

    <!-- 列表菜单 - 开始 -->
    <view class="user-activity-menu">
      <view class="menu-item" @click="goPage('userArticle')">
        <view class="left">
          <image class="menu-icon" src="/static/myInfo_article.png"></image>
          <view class="menu-name">我的帖子</view>
        </view>
        <u-icon name="arrow-right"></u-icon>
      </view>
      <view class="menu-item" @click="goPage('userActivity')">
        <view class="left">
          <image class="menu-icon" src="/static/myInfo_activity.png"></image>
          <view class="menu-name">我的活动</view>
        </view>
        <u-icon name="arrow-right"></u-icon>
      </view>
      <view class="menu-item" @click="goPage('changeInfo')">
        <view class="left">
          <image class="menu-icon" src="/static/myInfo_user.png"></image>
          <view class="menu-name">个人资料</view>
        </view>
        <u-icon name="arrow-right"></u-icon>
      </view>
      <view class="menu-item" @click="goPage('concatMe')">
        <view class="left">
          <image class="menu-icon" src="/static/myInfo_concat.png"></image>
          <view class="menu-name">联系我们</view>
        </view>
        <u-icon name="arrow-right"></u-icon>
      </view>
      <view class="menu-item" @click="show=true">
        <view class="left">
          <image class="menu-icon" src="/static/myInfo_logout.png"></image>
          <view class="menu-name">退出登录</view>
        </view>
        <u-icon name="arrow-right"></u-icon>
      </view>
    </view>
    <!-- 列表菜单 - 结束 -->
  </view>
  <up-modal :show="show" content='确认退出登录' @confirm="logout" @cancel="show=false"
            :showCancelButton="true" contentTextAlign="center"></up-modal>
</template>

<script setup lang="ts">
import {ref} from "vue";
import type {User} from "@/types/user";
import {useUserStore} from "@/stores/user";
import {requestUserInfo} from "@/api/request/user";
import {onShow} from "@dcloudio/uni-app";

const show = ref(false)
const userStore = useUserStore()
const goPage = (pageName: string) => {
  uni.navigateTo({
    url: `/pages/${pageName}/${pageName}`
  })
}
const init = async () => {
  const res = await requestUserInfo()
  userInfo.value = res.data
}
const userInfo = ref<User>()
const logout = () => {
  userStore.clear();
  uni.navigateTo({
    url: "/pages/login/login"
  })
}
onShow(() => {
  init()
})
</script>
<style>
page {
  background: #F4F4F4;
}
</style>
<style scoped lang="scss">
.user {
  .user-header-image {
    width: 100%;
  }

  .user-info-box {
    background: #ffffff;
    padding: 30rpx;
    margin: -80rpx 20rpx 20rpx 20rpx;
    position: relative;
    border-radius: 20rpx;
    display: flex;
    align-items: center;

    .user-avg {
      width: 100rpx;
      height: 100rpx;
      border-radius: 10rpx;
    }

    .user-nickname {
      margin-left: 20rpx;
    }

    .user-phone {
      margin-left: 20rpx;
      font-size: 24rpx;
      color: #666666;
    }
  }

  .user-recharge-wrapper {
    background: #ffffff;
    border-radius: 20rpx;
    padding: 30rpx;
    margin: 20rpx;

    .user-recharge-box {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 20rpx;

      .recharge-info {
        .info-title {
          color: #333000;
          font-size: 28rpx;
          font-weight: bold;
        }

        .info-content {
          text-indent: 2em;
          color: black;
          font-size: 24rpx;
        }
      }

      .recharge-button {
        // background: #ff4131;
        color: #333;
        padding: 10rpx 20rpx;
        font-size: 22rpx;
        border-radius: 50rpx;
        flex-shrink: 0;
        border: 1rpx solid #dadbde;
      }
    }

    .recharge-user-money {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-top: 20rpx;
      font-size: 26rpx;
      color: #333;

      .recharge-money {
        font-weight: bold;
        font-size: 30rpx;
      }

      .recharge-money::first-letter {
        font-size: 22rpx;
      }
    }
  }

  .user-activity-menu {
    padding: 30rpx;
    margin: 20rpx;
    border-radius: 20rpx;
    background: #fff;

    .menu-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-top: 60rpx;

      .left {
        display: flex;
        align-items: center;

        .menu-icon {
          width: 40rpx;
          height: 40rpx;
        }


        .menu-name {
          font-size: 28rpx;
          margin-left: 20rpx;
          color: #333;
        }
      }

      .menu-number {
        font-size: 30rpx;
        color: #666666;
        letter-spacing: 2rpx;
      }
    }

    .menu-item:first-child {
      margin-top: 0;
    }
  }
}
</style>
