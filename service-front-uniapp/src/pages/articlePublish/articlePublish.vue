<template>
  <up-toast ref="uToastRef"></up-toast>
  <up-form labelPosition="left">
    <up-form-item label="标题" borderBottom>
      <up-textarea v-model="title" placeholder="请输入内容" count autoHeight border="bottom"
                   maxlength="20"></up-textarea>
    </up-form-item>
    <up-form-item label="内容" borderBottom>
      <up-textarea v-model="content" placeholder="请输入内容" count autoHeight border="bottom"
                   maxlength="2000"></up-textarea>
    </up-form-item>
    <up-form-item label="图片" borderBottom>
      <up-upload
          :fileList="tempFiles"
          @afterRead="afterRead"
          @delete="deletePic"
          multiple
          :maxCount="9"
          width="200rpx"
          height="200rpx"
      ></up-upload>
    </up-form-item>
    <up-form-item borderBottom>
      <up-button type="primary" text="发布" @click="submit"></up-button>
    </up-form-item>
  </up-form>
</template>

<script setup lang="ts">
import {ref} from "vue";
import {useUserStore} from "@/stores/user";
import {requestPublishArticleMedia, requestPublishArticleText} from "@/api/request/article";

const uToastRef = ref()
const userStore = useUserStore()
const title = ref<string>('')
const content = ref<string>('')
const tempFiles = ref([])
// 删除图片
const deletePic = (event: any) => {
  tempFiles.value.splice(event.index, 1);
};

// 新增图片
const afterRead = async (event: any) => {
  // 当设置 mutiple 为 true 时, file 为数组格式
  let lists = [].concat(event.file);
  lists.forEach(item => {
    tempFiles.value.push(item)
  })
}
const submit = async () => {
  if (!validate()) {
    return;
  }
  const res = await requestPublishArticleText(title.value, content.value.trim())
  if (res.code == 1) {
    let articleId = res.data
    tempFiles.value.forEach(item => {
      requestPublishArticleMedia(articleId, item.url)
    })
    uToastRef.value.show({
      type: 'success',
      message: "发布成功",
      position: "top",
      duration: 1000
    })
    setTimeout(() => {
      uni.switchTab({
        url: "/pages/articleList/articleList"
      })
    }, 1000)
  }
}
const validate = () => {
  if (title.value.trim().length == 0) {
    uToastRef.value.show({
      type: 'error',
      message: "标题不能为空",
      position: "top",
      duration: 1000
    })
    return false;
  } else if (content.value.trim().length == 0) {
    uToastRef.value.show({
      type: 'error',
      message: "内容不能为空",
      position: "top",
      duration: 1000
    })
    return false;
  }
  return true;
}
</script>

<style scoped lang="scss">

</style>
