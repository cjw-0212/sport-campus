<template>
  <up-toast ref="uToastRef"></up-toast>
  <view class="login-bg">
    <image class="img-a" src="@/static/login_bg1.png"></image>
    <view class="t-login">
      <view class="t-b">登 录</view>
      <form class="cl">
        <view class="t-a">
          <image src="@/static/login_user.png"></image>
          <input type="text" name="phone" placeholder="请输入用户名" maxlength="11" v-model="name"/>
        </view>
        <view class="t-a">
          <image src="@/static/login_pwd.png"></image>
          <input type="password" name="code" maxlength="16" placeholder="请输入密码" v-model="password"/>
        </view>
        <!--        <view class="t-c" @click="forgotPwd">忘记密码</view>-->
        <button @click="login">登 录</button>
      </form>
    </view>
    <!--    <view class="cardBox">
          <view>
            还没有登录账号？
            <text class="txt" @click="register">立刻注册</text>
          </view>
        </view>-->
    <image class="img-b" src="/static/login_bg2.png"></image>
  </view>
</template>

<script setup lang="ts">
import {ref} from "vue";
import {requestUserLogin} from "@/api/request/user";
import {useUserStore} from "@/stores/user";
import {onLoad, onShow} from "@dcloudio/uni-app";

const userStore = useUserStore();
const uToastRef = ref()
const name = ref<string>('21251104203')
const password = ref<string>('123456')
const register = () => {
  uni.navigateTo({
    url: "/pages/register/register"
  })
}
const login = () => {
  if (!paramsValidated()) {
    return
  }
  requestUserLogin(name.value, password.value)
      .then((res) => {
        if (res.code == 1) {
          //保存token和个人信息
          let userStore = useUserStore()
          userStore.setUserInf(res.data)
          userStore.setToken(res.message)
          //跳转主页
          uToastRef.value.show({
            type: 'success',
            message: "登录成功",
            position: "top",
            duration: 1000
          })
          setTimeout(() => {
            uni.switchTab({
              url: "/pages/articleList/articleList"
            })
          }, 1000)
        } else {
          uToastRef.value.show({
            type: 'error',
            message: res.message,
            position: "top",
            duration: 1000
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
const forgotPwd = () => {
  console.log("忘记密码")
}
const judgeLogin = () => {
  const token = userStore.token;
  if (token != null && token.length !== 0) {
    uni.switchTab({
      url: "/pages/articleList/articleList"
    })
  }
}
onLoad((option) => {
  judgeLogin()
  /*if (option!.name != null && option!.password != null) {
    name.value = option!.name
    password.value = option!.password
  }*/
})
onShow(() => {
  judgeLogin()
})
</script>

<style lang="scss" scoped>
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

.t-login .t-d {
  text-align: center;
  color: #999;
  margin: 80rpx 0;
}

.t-login .t-c {
  text-align: right;
  color: #c0c0c0;
  margin: -20rpx 30rpx 40rpx 0;
}

.t-login .t-f {
  text-align: center;
  margin: 200rpx 0 0 0;
  color: #666;
}

.t-login .t-f text {
  margin-left: 20rpx;
  color: #aaaaaa;
  font-size: 27rpx;
}

.t-login .uni-input-placeholder {
  color: #aeaeae;
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

.cardBox {
  -webkit-box-orient: horizontal;
  -webkit-box-direction: normal;
  -webkit-flex-direction: row;
  flex-direction: row;
  -webkit-box-align: center;
  -webkit-align-items: center;
  align-items: center;
  padding: 5px;
  background: #ffffff;
  opacity: 0.9;
  -webkit-border-radius: 20rpx;
  border-radius: 0 0 20rpx 20rpx;
  height: 70rpx;
  width: 600rpx;
  margin: 0 auto;
  position: relative;
  text-align: center;
  line-height: 70rpx;
  color: #aaa;
  font-size: 28rpx;
}

.cardBox .txt {
  color: #2979ff;
}
</style>
