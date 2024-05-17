<template>
  <view class="comment_item">
    <view class="top">
      <view class="top_left">
        <img class="user_avatar"
             :src="props.data.publishUserAvatar.length===0?'../../static/avatar_default.png':props.data.publishUserAvatar"/>
        <uni-tag
            v-if="props.data.owner"
            class="tag"
            type="primary"
            :inverted="false"
            text="作者"
            size="mini"
            circle
        />
        <span class="user_name">{{ props.data.publishUserName }}</span>
        <span class="user_name">{{ cReplyName }}</span>
      </view>
      <view class="top_right" @tap="likeClick(props.data)">
        <span :class="[props.data.is_like ? 'active' : '', 'like_count']">{{
            cLikeCount
          }}</span>
        <uni-icons
            v-show="props.data.is_like"
            type="hand-up-filled"
            size="24"
            color="#007aff"
        ></uni-icons>
        <uni-icons
            v-show="!props.data.is_like"
            type="hand-up"
            size="24"
            color="#999"
        ></uni-icons>
      </view>
    </view>
    <view class="content" @tap="replyClick(props.data)">
      {{ c_content }}
      <span
          class="shrink"
          v-if="isShrink"
          @tap.stop="expandContentFun(props.data.content)"
      >...展开</span
      >
    </view>
    <view class="bottom">
      <span class="create_time">{{ props.data.createTime==='刚刚'?'刚刚':timestampFormat(props.data.createTime) }}</span>
      <span
          v-if="props.data.publishUserId===userStore.user.id"
          class="delete"
          @tap="deleteClick(props.data)"
      >删除</span
      >
      <!-- <span v-else class="reply" @tap="replyClick(props.data)"
        >回复</span
      > -->
    </view>
  </view>
</template>

<script setup>
import {computed, ref, watch} from "vue";
import {useUserStore} from "@/stores/user";

const props = defineProps({
  // 评论数据
  data: {
    type: Object,
    default: () => {
    },
  },
  // 父级评论数据
  pData: {
    type: Object,
    default: () => {
    },
  },
});

// 被回复人名称
const cReplyName = computed(() => {
  return props.data?.replyUserName ? ` ▸ ` + props.data?.replyUserName : "";
});

// 点赞数显示
const cLikeCount = computed(() => {
  if (props.data.agreeNumber===0&&props.data.is_like){
    return 1;
  }
  return props.data.agreeNumber === 0
      ? ""
      : props.data.agreeNumber > 99
          ? `99+`
          : props.data.agreeNumber;
});

// 评论过长处理
let contentShowLength = 70; // 默认显示评论字符

let content = props.data.content;
let isShrink = ref(content.length > contentShowLength); // 是否收缩评论
let c_content = ref("");
watch(
    () => isShrink.value,
    (newVal) => {
      c_content.value = newVal
          ? content.slice(0, contentShowLength + 1)
          : content;
    },
    {
      immediate: true,
    }
);
// 删除变更显示定制
watch(
    () => props.data.content,
    (newVal, oldVal) => {
      if (newVal !== oldVal) {
        c_content.value = newVal;
      }
    }
);
const timestampFormat = (timestamp) => {
  if (!timestamp) return '';

  function zeroize(num) {
    return (String(num).length === 1 ? '0' : '') + num;
  }

  let curTimestamp = parseInt(new Date().getTime() / 1000); //当前时间戳
  let timestampDiff = curTimestamp - timestamp; // 参数时间戳与当前时间戳相差秒数

  let curDate = new Date(curTimestamp * 1000); // 当前时间日期对象
  let tmDate = new Date(timestamp * 1000);  // 参数时间戳转换成的日期对象

  let Y = tmDate.getFullYear(), m = tmDate.getMonth() + 1, d = tmDate.getDate();
  let H = tmDate.getHours(), i = tmDate.getMinutes(), s = tmDate.getSeconds();

  if (timestampDiff < 60) { // 一分钟以内
    return "刚刚";
  } else if (timestampDiff < 3600) { // 一小时前之内
    return Math.floor(timestampDiff / 60) + "分钟前";
  } else if (curDate.getFullYear() === Y && curDate.getMonth() + 1 === m && curDate.getDate() === d) {
    return '今天' + zeroize(H) + ':' + zeroize(i);
  } else {
    let newDate = new Date((curTimestamp - 86400) * 1000); // 参数中的时间戳加一天转换成的日期对象
    if (newDate.getFullYear() === Y && newDate.getMonth() + 1 === m && newDate.getDate() === d) {
      return '昨天' + zeroize(H) + ':' + zeroize(i);
    } else if (curDate.getFullYear() === Y) {
      return zeroize(m) + '月' + zeroize(d) + '日 ' + zeroize(H) + ':' + zeroize(i);
    } else {
      return Y + '年' + zeroize(m) + '月' + zeroize(d) + '日 ' + zeroize(H) + ':' + zeroize(i);
    }
  }
}

// 展开评论
function expandContentFun() {
  isShrink.value = false;
}

const emit = defineEmits(["likeClick", "replyClick", "deleteClick"]);

// 点赞
function likeClick(item) {
  emit("likeClick", item);
}

const userStore = useUserStore()

// 回复
function replyClick(item) {
  // 自己不能回复自己
  if (item.publishUserId === userStore.user.id) return;
  emit("replyClick", item);
}

// 删除
function deleteClick(item) {
  emit("deleteClick", item);
}
</script>

<style lang="scss" scoped>
////////////////////////
.center {
  display: flex;
  align-items: center;
}

.ellipsis {
  overflow: hidden; //超出文本隐藏
  white-space: nowrap; //溢出不换行
  text-overflow: ellipsis; //溢出省略号显示
}

////////////////////////
.comment_item {
  font-size: 28rpx;

  .top {
    @extend .center;
    justify-content: space-between;

    .top_left {
      display: flex;
      align-items: center;
      overflow: hidden;

      .user_avatar {
        width: 68rpx;
        height: 68rpx;
        border-radius: 50%;
        margin-right: 12rpx;
      }

      .tag {
        margin-right: 6rpx;
      }

      .user_name {
        @extend .ellipsis;
        max-width: 180rpx;
        color: $uni-text-color-grey;
      }
    }

    .top_right {
      @extend .center;

      .like_count {
        color: $uni-text-color-grey;

        &.active {
          color: $uni-color-primary;
        }
      }
    }
  }

  .content {
    padding: 10rpx;
    margin-left: 70rpx;
    color: $uni-text-color;

    &:active {
      background-color: $uni-bg-color-hover;
    }

    .shrink {
      padding: 20rpx 20rpx 20rpx 0rpx;
      color: $uni-color-primary;
    }
  }

  .bottom {
    padding-left: 80rpx;
    font-size: 24rpx;

    .create_time {
      color: $uni-text-color-grey;
    }

    .delete {
      padding: 20rpx 20rpx 0 20rpx;
      color: $uni-border-color;
    }

    .reply {
      color: $uni-color-primary;
    }
  }
}
</style>
