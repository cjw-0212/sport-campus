<template>
  <up-toast ref="uToastRef"></up-toast>
  <view>
    <view style="width: 750rpx;height: 250rpx;">
      <view style="padding-left: 250rpx">
        <up-image width='200rpx' height='200rpx' :src="avatar?avatar:'/static/avatar_default.png'"
                  shape="circle"></up-image>
      </view>
      <text style="color: #2979ff;padding-left: 290rpx" @click="changeHead">更换头像</text>
    </view>
    <up-cell-group>
      <up-cell isLink>
        <template #title>
          <view>
            <up-row>
              <up-col span="6">
                <text>昵称</text>
              </up-col>

              <uni-easyinput trim="all" v-model="name" :inputBorder="false"
                             :clearable="false" :maxlength="10"></uni-easyinput>
              <!--                <text>{{ name }}</text>-->

            </up-row>
          </view>
        </template>
      </up-cell>
      <up-cell isLink>
        <template #title>
          <view>
            <up-row>
              <up-col span="6">
                <text>性别</text>
              </up-col>
              <up-col>
                <uni-data-checkbox v-model="sex"
                                   :localdata="[{text:'男',value:1},{text:'女',value:2}]"></uni-data-checkbox>
              </up-col>
            </up-row>
          </view>
        </template>
      </up-cell>
      <up-cell isLink @click="dateShow=true">
        <template #title>
          <view>
            <up-row>
              <up-col span="6">
                <text>出生日期</text>
              </up-col>
              <up-col>
                {{ birthday }}
              </up-col>
            </up-row>
          </view>
        </template>
      </up-cell>
      <up-cell>
        <template #title>
          <view>
            <up-row>
              <up-col span="6">
                <text>个人简介</text>
              </up-col>
            </up-row>
            <up-row>
              <up-textarea v-model="intro" placeholder="请输入内容" autoHeight maxlength="200"
                           count></up-textarea>
            </up-row>
          </view>
        </template>
      </up-cell>
    </up-cell-group>
    <up-datetime-picker
        :show="dateShow"
        v-model="date"
        mode="date"
        @cancel="dateShow=false"
        @confirm="dateConfirm"
        :minDate="946656000000"
        :maxDate="1577808000000"
    ></up-datetime-picker>
  </view>
  <view>
    <up-button type="primary" text="保存" @click="changeAll"></up-button>
  </view>
</template>

<script setup lang="ts">
import {useUserStore} from "@/stores/user";
import {onMounted, ref} from "vue";
import {requestChangeAvatar, requestChangeInfo, requestUserInfo} from "@/api/request/user";

const uToastRef = ref()
const date = ref('')
const dateShow = ref(false)
const userStore = useUserStore()

const avatar = ref<string>('')
const intro = ref<string>('')
const sex = ref<number>(0)
const name = ref<string>('')
const birthday = ref<string>('')
const changeHead = () => {
  uni.chooseImage({
    count: 1,
    success: async (res) => {
      avatar.value = res.tempFilePaths[0]
      const res1 = await requestChangeAvatar(avatar.value);
      if (res1.code == 1) {
        userStore.user!.avatar = res1.data
      }
    }
  });
}
const dateTransform = (timestamp: number) => {
  let da = new Date(timestamp);
  let year = da.getFullYear();
  let month = da.getMonth() + 1;
  let date = da.getDate();
  return [year, month, date].join("-");
}
const dateConfirm = (e: any) => {
  birthday.value = dateTransform(e.value)
  dateShow.value = false
}
const changeAll = async () => {
  const res2 = await requestChangeInfo(name.value, sex.value, birthday.value, intro.value)
  if (res2.code == 1) {
    await init()
    uToastRef.value.show({
      type: 'success',
      message: "保存成功",
      position: "top",
      duration: 1000
    })
    setTimeout(() => {
      uni.switchTab({
        url: "/pages/myInfo/myInfo"
      })
    }, 1000)
  }
}
const init = async () => {
  const res = await requestUserInfo();
  intro.value = res.data.intro
  name.value = res.data.name
  birthday.value = res.data.birthday
  sex.value = res.data.sex
  avatar.value = res.data.avatar
}
onMounted(() => {
  init()
})
</script>

<style scoped>

</style>
