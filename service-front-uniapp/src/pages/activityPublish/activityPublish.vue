<template>
  <up-toast ref="uToastRef"></up-toast>
  <up-form labelPosition="left">
    <up-form-item label="标题" borderBottom>
      <up-textarea v-model="title" placeholder="请输入内容" count autoHeight border="bottom"
                   maxlength="20"></up-textarea>
    </up-form-item>
    <up-form-item label="内容" borderBottom>
      <up-textarea v-model="content" placeholder="请输入内容" count autoHeight border="bottom"
                   maxlength="200"></up-textarea>
    </up-form-item>
    <up-form-item label="人数" borderBottom>
      <up-slider v-model="number" :showValue="true" :min="1" :max="100"></up-slider>
    </up-form-item>
    <up-form-item label="位置" borderBottom>
      <up-row>
        <up-col :span="10">
          <up-textarea v-model="addressName" placeholder="点击搜索或手动输入" autoHeight border="bottom"
          ></up-textarea>
        </up-col>
        <up-col>
          <uni-icons type="location" size="30" @click="authVerification"></uni-icons>
        </up-col>
      </up-row>
    </up-form-item>
    <up-form-item label="时间" borderBottom @click="dateShow=true">
      {{ endTime === '' ? '点击选择活动开始时间' : endTime }}
    </up-form-item>
    <up-form-item label="封面" borderBottom>
      <up-upload
          :fileList="tempFiles"
          @afterRead="afterRead"
          @delete="deletePic"
          multiple
          :maxCount="1"
          width="200rpx"
          height="200rpx"
      ></up-upload>
    </up-form-item>
    <up-form-item label="" borderBottom>
      <up-button type="primary" text="发布" @click="submit"></up-button>
    </up-form-item>
  </up-form>
  <up-datetime-picker
      :show="dateShow"
      v-model="datetime"
      mode="datetime"
      @cancel="dateShow=false"
      @confirm="dateConfirm"
      :minDate="(new Date()).valueOf()"
  ></up-datetime-picker>
</template>

<script setup lang="ts">
import {ref} from "vue";
import {requestPublishActivityMedia, requestPublishActivityText} from "@/api/request/activity";

const dateShow = ref(false)
const uToastRef = ref()
const tempFiles = ref([])
const number = ref(1)
const content = ref('')
const title = ref('')

const longitude1 = ref(0)
const latitude1 = ref(0)
const address = ref('')
const addressName = ref('')
const datetime = ref()
const endTime = ref('');

const deletePic = (event: any) => {
  tempFiles.value.splice(event.index, 1);
};
const dateConfirm = (e: any) => {
  const date = new Date(e.value); // 根据时间戳创建Date对象
  const year = date.getFullYear(); // 获取年份
  const month = date.getMonth() + 1; // 获取月份，需要加1
  const day = date.getDate(); // 获取日期
  const hour = date.getHours(); // 获取小时
  const minute = date.getMinutes(); // 获取分钟
  endTime.value = [year, month, day].join("-") + ' ' + hour + ':' + minute
  console.log(endTime.value)
  dateShow.value = false
}
// 新增图片
const afterRead = async (event: any) => {
  // 当设置 mutiple 为 true 时, file 为数组格式
  let lists = [].concat(event.file);
  lists.forEach(item => {
    tempFiles.value.push(item)
  })
}
const submit = async () => {
  if (!validated()) {
    return;
  }
  const res = await requestPublishActivityText(title.value, content.value, number.value
      , longitude1.value, latitude1.value, address.value, addressName.value, endTime.value)
  if (res.code == 1) {
    let activityId = res.data
    await requestPublishActivityMedia(activityId, tempFiles.value[0].url)
    uToastRef.value.show({
      type: 'success',
      message: "发布成功",
      position: "top",
      duration: 1000
    })
    setTimeout(() => {
      uni.switchTab({
        url: "/pages/activityList/activityList"
      })
    }, 1000)
  }
}
const authVerification = () => {
  uni.getSetting({
    success: (res) => {
      if (res.authSetting['scope.userLocation']) { /* 用户授权成功时走这里 */
        handleChooseLocation('', '')
      } else if (res.authSetting['scope.userLocation'] === undefined) { /* 用户未授权时走这里 */
        console.log('没有授权')
        handleOpenSetting()
      } else { /* 用户拒绝了授权后走这里 */
        console.log('拒绝了授权 false')
        handleOpenSetting()
      }
    },
  })
}
const handleChooseLocation = (latitude: any, longitude: any) => {
  uni.chooseLocation({
    latitude: latitude || '',
    longitude: longitude || '',
    success: (res) => {
      longitude1.value = res.longitude
      latitude1.value = res.latitude
      address.value = res.address
      addressName.value = res.name
      console.log("获取值", res)
    },
    fail: function (err) {
      console.log('取消按钮', err)
    }
  })
}
const handleOpenSetting = () => {
  uni.openSetting({
    success: (res: any) => {
      console.log('定位 openSetting', res)
      if (res.authSetting["scope.userLocation"]) {
        handleChooseLocation('', '')
      }
    }
  })
}
const validated = () => {
  if (content.value.trim().length == 0) {
    errTip("活动内容不能为空")
    return false;
  }
  if (title.value.trim().length == 0) {
    errTip("活动标题不能为空")
    return false;
  }
  if (tempFiles.value[0] == null) {
    errTip("必须要有封面图片")
    return false;
  }
  if (number.value <= 0) {
    errTip("活动人数不能少于一")
    return false
  }
  if (longitude1.value == 0 && latitude1.value == 0 && address.value == '' && addressName.value == '') {
    errTip("活动位置信息缺失")
    return false;
  }
  if (endTime.value == '') {
    errTip("未选择活动开始时间")
    return false;
  }
  return true;
}
const errTip = (message: string) => {
  uToastRef.value.show({
    type: 'error',
    message: message,
    position: "top",
    duration: 1000
  })
}


</script>

<style scoped>

</style>
