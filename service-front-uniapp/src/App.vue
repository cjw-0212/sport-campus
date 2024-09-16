<template>
  <div>

  </div>
</template>

<script setup lang="ts">
import {onLaunch} from "@dcloudio/uni-app";

onLaunch(() => {
  uni.authorize({
    scope: 'scope.userLocation',
    success: () => {
      console.log("用户同意授权")
    }
  })
})
</script>

<style lang="scss" scoped>
@import "uview-plus/index.scss";
</style>
