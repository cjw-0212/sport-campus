<template>
  <view>
    <view style="width: 750rpx;height: 250rpx;">
      <view style="padding-left: 250rpx">
        <up-image width='200rpx' height='200rpx' :src="avatar?avatar:'/static/avatar_default.png'" shape="circle"></up-image>
      </view>
      <text style="color: #2979ff;padding-left: 290rpx" @click="changeHead">更换头像</text>
    </view>
    <up-cell-group>
      <up-cell isLink>
        <template #title>
          <view>
            <up-row>
              <up-col span="6">
                <text>用户名</text>
              </up-col>
              <up-col>
                <text>{{ name }}</text>
              </up-col>
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
                <uni-data-checkbox :value="sex"
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
    ></up-datetime-picker>
  </view>
  <view>
    <up-button type="primary" text="保存"></up-button>
  </view>
</template>

<script setup lang="ts">
import {useUserStore} from "@/stores/user";
import {onMounted, ref} from "vue";

const date = ref('')
const dateShow = ref(false)
const userStore = useUserStore()

const avatar = ref<string>()
const intro = ref<string>()
const sex = ref<number>()
const name = ref<string>()
const birthday = ref<string>()
const changeHead = () => {
  uni.chooseImage({
    count: 1,
    success: (res) => {
      avatar.value = res.tempFilePaths[0]
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
onMounted(() => {
  intro.value = userStore.user!.intro
  name.value = userStore.user!.name
  birthday.value = userStore.user!.birthday
  sex.value = userStore.user!.sex
  avatar.value = userStore.user!.avatar
})
</script>

<style scoped>

</style>
