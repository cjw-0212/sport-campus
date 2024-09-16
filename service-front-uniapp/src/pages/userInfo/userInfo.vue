<template>
  <view>
    <view style="width: 750rpx;height: 250rpx;">
      <view style="padding-left: 250rpx">
        <up-image width='200rpx' height='200rpx' :src="avatar?avatar:'/static/avatar_default.png'"
                  shape="circle"></up-image>
      </view>
    </view>
    <up-cell-group>
      <up-cell isLink>
        <template #title>
          <view>
            <up-row>
              <up-col span="6">
                <text>学号</text>
              </up-col>
              <text>{{ userId }}</text>
            </up-row>
          </view>
        </template>
      </up-cell>
      <up-cell isLink>
        <template #title>
          <view>
            <up-row>
              <up-col span="6">
                <text>昵称</text>
              </up-col>
              <text>{{ name }}</text>
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
                <text v-if="sex===1">男</text>
                <text v-else-if="sex===2">女</text>
                <text v-else>未知</text>
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
                <text>出生日期</text>
              </up-col>
              <up-col>
                {{ birthday }}
              </up-col>
            </up-row>
          </view>
        </template>
      </up-cell>
      <up-cell v-show="intro!=''">
        <template #title>
          <view>
            <up-row>
              <up-col span="6">
                <text>个人简介</text>
              </up-col>
            </up-row>
            <up-row>
              <text>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{{ intro}}</text>
            </up-row>
          </view>
        </template>
      </up-cell>
    </up-cell-group>
  </view>
</template>
<script setup lang="ts">
import {ref} from "vue";
import {requestUserInfoById} from "@/api/request/user";
import {onLoad} from "@dcloudio/uni-app";


let userId = ref()
const avatar = ref<string>('')
const intro = ref<string>('')
const sex = ref<number>(0)
const name = ref<string>('')
const birthday = ref<string>('')
const init = async () => {
  const res = await requestUserInfoById(userId.value);
  intro.value = res.data.intro
  name.value = res.data.name
  birthday.value = res.data.birthday
  sex.value = res.data.sex
  avatar.value = res.data.avatar
}
onLoad((option) => {
  userId.value = option!.id
  init()
})

</script>

<style scoped>

</style>
