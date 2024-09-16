<template>
  <uni-card :cover="cover" @click="handleToDetail">
    <text>{{ title }}</text>
  </uni-card>
</template>

<script>
import {toRefs} from 'vue';

export default {
  props: {
    //图片
    cover: String,
    //标题
    title: String,
    //总人数
    totalPerson: Number,
    //活动状态
    status: Boolean,
    id: String,
    count: Number,
    publishUserId: String,
    isCard: Number,
    isFinish: Number
  },
  setup(props) {
    const {
      cover,
      title,
      totalPerson,
      status,
      id,
      count,
      publishUserId,
        isCard,
        isFinish
    } = toRefs(props);

    const handleToDetail = () => {
      uni.navigateTo({
        url: `activity-detail?id=${id.value}&publishUserId=${publishUserId.value}&isCard=${isCard.value}
        &isFinish=${isFinish.value}`
      });
    }

    return {
      count,
      cover,
      title,
      totalPerson,
      status,
      handleToDetail,
      isCard,
      isFinish
    };
  },
};
</script>

<style lang="scss" scoped>
.container {
  margin-bottom: 30rpx;
  width: 100%;
  min-height: 300rpx;
  border-radius: 4rpx;
  box-shadow: 0px 4px 10px 0px rgba(0, 0, 0, 0.1);

  .cover-wrap {
    position: relative;
    width: 100%;
    height: 300rpx;

    .tag {
      position: absolute;
      top: 10rpx;
      left: 0;
    }
  }

  .activity-footer-wrap {
    padding: 20rpx;
    box-sizing: border-box;

    .activity-footer-title {
      font-size: 24rpx;
      color: #333;

    }

    .activity-status-wrap {
      display: flex;
      align-items: center;
      font-size: 24rpx;
      color: #333;
    }
  }

}
</style>
