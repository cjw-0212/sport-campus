<template>
  <up-toast ref="uToastRef"></up-toast>
  <view class="login-bg">
    <image class="img-a" src="/static/login_bg1.png"></image>
    <view class="t-login">
      <view class="t-b">注 册</view>
      <form class="cl">
        <view class="t-a">
          <image src="/static/login_user.png"></image>
          <input type="text" name="phone" placeholder="请输入用户名" maxlength="10" v-model="name"/>
        </view>
        <view class="t-a">
          <image src="/static/login_pwd.png"></image>
          <input type="password" name="code" maxlength="16" placeholder="请输入密码" v-model="password"/>
        </view>
        <button @click="register">注册</button>
      </form>
    </view>
    <image class="img-b" src="/static/login_bg2.png"></image>
  </view>
</template>

<script setup lang="ts">
import {ref} from "vue";
import {requestUserRegister} from "@/api/request/user";

const uToastRef = ref()
const name = ref<string>('')
const password = ref<string>('')
const register = () => {
  if (!paramsValidated()) {
    return
  }
  requestUserRegister(name.value, password.value)
      .then((res) => {
        if (res.code == 1) {
          uToastRef.value.show({
            type: 'success',
            message: "注册成功,请点击登录",
            position: "top",
          })
          //跳转登陆页面并传递账号信息
          setTimeout(() => {
            uni.navigateTo({
              url: `/pages/login/login?name=${name.value}&password=${password.value}`
            })
          }, 500)
        } else {
          uToastRef.value.show({
            type: 'error',
            message: res.message,
            position: "top"
          })
        }
      })
}
const paramsValidated = () => {
  if (name.value == '') {
    uToastRef.value.show({
      type: 'error',
      message: '用户名不能为空',
      position: "top"
    })
    return false;
  }
  if (password.value == '' || password.value.length < 6) {
    uToastRef.value.show({
      type: 'error',
      message: '密码应为6-16位',
      position: "top"
    })
    return false;
  }
  return true;
}
</script>

<style scoped lang="scss">
.img-a {
  width: 100%;
}

.img-b {
  width: 100%;
  height: 45px;
  bottom: 0;
  position: absolute;
}

.login-bg {
  height: 100vh;
  background: linear-gradient(to bottom, #2979ff, #a0cfff);
}

.t-login {
  width: 580rpx;
  padding: 55rpx;
  margin: 0 auto;
  font-size: 28rpx;
  background-color: #ffffff;
  border-radius: 20rpx;
  position: relative;
  margin-top: -200rpx;
  box-shadow: 0 5px 7px 0 rgba(0, 0, 0, 0.15);
  z-index: 9;
}

.t-login button {
  font-size: 28rpx;
  background: linear-gradient(to right, #2979ff, #a0cfff);
  color: #fff;
  height: 90rpx;
  line-height: 90rpx;
  border-radius: 50rpx;
}

.t-login input {
  padding: 0 20rpx 0 120rpx;
  height: 90rpx;
  line-height: 90rpx;
  margin-bottom: 50rpx;
  background: #f6f6f6;
  border: 1px solid #f6f6f6;
  font-size: 28rpx;
  border-radius: 50rpx;
}

.t-login .t-a {
  position: relative;
}

.t-login .t-a image {
  width: 40rpx;
  height: 40rpx;
  position: absolute;
  left: 40rpx;
  top: 28rpx;
  padding-right: 20rpx;
}

.t-login .t-b {
  text-align: left;
  font-size: 46rpx;
  color: #2979ff;
  font-weight: bold;
  margin: 0 0 50rpx 20rpx;
}

.t-login .t-f text {
  margin-left: 20rpx;
  color: #aaaaaa;
  font-size: 27rpx;
}

.cl {
  zoom: 1;
}

.cl:after {
  clear: both;
  display: block;
  visibility: hidden;
  height: 0;
  content: '\20';
}
</style>
