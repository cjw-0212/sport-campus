<template>
  <view class="dynamic" @click="clickDynamic()">
    <view class="user__container">
      <view class="user__header-warp">
        <!-- 头像组 -->
        <view class="user__header" @click.stop="clickUser()">
          <image class="user__header-image" :src="avatar" mode="aspectFill"></image>
        </view>
      </view>
      <view class="user__content">
        <view class="user__content-main">
          <text class="user__content-title uni-ellipsis" @click.stop="clickUser()">{{ name }}</text>
          <text class="user__content-note uni-ellipsis">{{ timestampFormat(publishTime) }}</text>
        </view>
      </view>
    </view>


    <view class="text">
      <up-read-more :toggle="true" showHeight="160">
        <rich-text :nodes="content"></rich-text>
      </up-read-more>
    </view>
    <view class="allImage">
      <view class="imgList">
        <view class="images" v-for="(item,index) in imgList" :key="index">
          <image @click.stop="previewImg(index)" class="oneimg" :src="item" mode="aspectFill"
                 :style="{width:imgWidth+'px','max-height':imgHeight+'px'}"></image>
        </view>
      </view>
    </view>
    <view class="operate" :style="'width: 100%;display:'+operateDisplay">
      <up-row>
        <up-col span="3"></up-col>
        <up-col span="1.5" textAlign="center" align="center">
           <span @click.stop="clickChat()">
						 <up-icon name="chat" size="18"
                      :label="chatNumber?chatNumber:''"
             ></up-icon>
					</span>
        </up-col>
        <up-col span="1.5"></up-col>
        <up-col span="1.5">
          <span @click.stop="clickThumbsup()">
            <up-icon :name="isLike?'heart-fill':'heart'" size="18"
                     :label="likeCount?likeCount:''"
                     :color="isLike?'#fb5f5f':'gray'"></up-icon>
					  </span>
        </up-col>
        <up-col span="1.5"></up-col>
        <up-col span="1.5">
          <span @click.stop="clickDelete()">
            <up-icon name="trash" size="18" v-if="publishUserId===userStore.user.id"></up-icon>
          </span>
        </up-col>
        <up-col span="1.5"></up-col>
      </up-row>
    </view>
    <view class="bottom-line"></view>
  </view>
</template>

<script>
import {useUserStore} from "@/stores/user";

const userStore = useUserStore()
export default {
  props: {
    avatar: {
      type: String
    },
    name: {
      type: String
    },
    publishTime: {
      type: Number
    },
    isFocusOn: {
      type: Boolean
    },
    content: {
      type: String
    },
    imgList: {
      type: Array
    },
    isLike: {
      type: Boolean
    },
    isGiveReward: {
      type: Boolean
    },
    likeNumber: {
      type: Number
    },
    giveRewardNumber: {
      type: Number
    },
    chatNumber: {
      type: Number
    },
    userNoShow: {
      type: Boolean
    },
    operateNoShow: {
      type: Boolean
    },
    publishUserId: {
      type: String
    },
  },
  data() {
    return {
      windowWidth: 0,	//屏幕可用宽度
      windowHeight: 0,	//屏幕可用高度
      imgWidth: 0,	//图片宽度
      imgHeight: 0,	//图片高度
      thumbsupColor: 'gray',
      heartColor: 'gray',
      userDisplay: 'block',
      operateDisplay: 'block',
      userStore
    }
  },
  mounted() {
    const res = uni.getSystemInfoSync();
    this.windowHeight = res.windowHeight;
    this.windowWidth = res.windowWidth;
    if (this.userNoShow) {
      this.userDisplay = 'none';
    }
    if (this.operateNoShow) {
      this.operateDisplay = 'none';
    }
    this.judgeImg();
    this.initOperate();
  },
  computed: {
    likeCount() {
      if (this.likeNumber === 0 && this.isLike) {
        return 1
      }
      return this.likeNumber
    }
  },
  methods: {
    // 预览图片
    previewImg(index) {
      uni.previewImage({
        urls: this.imgList,
        current: index
      })
    },
    initOperate() {
      if (this.isLike) this.thumbsupColor = '#fb5f5f';
    },
    // 自适应判断
    judgeImg() {
      if (this.imgList.length === 1) {
        this.imgWidth = this.windowWidth * 2 / 3;
        this.imgHeight = this.windowHeight * 3 / 5;
      } else if (this.imgList.length === 4) {
        this.imgWidth = this.windowWidth / 3.3;
        this.imgHeight = this.imgWidth;
      } else {
        this.imgWidth = this.windowWidth / 3.4;
        this.imgHeight = this.imgWidth;
      }
    },
    timestampFormat(timestamp) {
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
    },

    /** 触发父级事件 */
    // 点击动态
    clickDynamic() {
      this.$emit('clickDynamic');
    },
    // 点击用户信息
    clickUser() {
      this.$emit('clickUser');
    },
    // 点赞
    clickThumbsup() {
      this.$emit('clickThumbsup');
    },
    // 点击聊天
    clickChat() {
      this.$emit('clickChat');
    },
    clickDelete() {
      this.$emit('clickDelete');
    }
  }
}
</script>

<style>
/* 想法图片排列样式 */

.dynamic {
  width: 100%;
}

.allImage {
  display: flex;
  margin-top: 10rpx;
  flex-wrap: wrap;
  justify-content: flex-start;
}

.imgList {
  margin: 0 3%;
}

.images:not(:nth-child(3n)) {
  /* margin-right: 10rpx; */
}

.text {
  margin: 1% 3% 2%;
  white-space: pre-line;
}

.images {
  margin-right: 10rpx;
  display: inline-block;
}

.operate {
  width: 94%;
  padding: 3%;
  font-size: 14px;
}

.bottom-line {
  border-bottom: 10px solid #efefef;
}


.user__container {
  display: flex;
  -webkit-box-orient: horizontal;
  -webkit-box-direction: normal;
  /* -webkit-flex-direction: row; */
  flex-direction: row;
  -webkit-box-flex: 1;
  /* -webkit-flex: 1; */
  flex: 1;
  padding: 10px 15px;
  position: relative;
  overflow: hidden;
}

.user__header {
  display: flex;
  width: 45px;
  height: 45px;
  -webkit-border-radius: 5px;
  border-color: #eee;
  border-width: 1px;
  border-style: solid;
  overflow: hidden;
  border-radius: 50px;
}

.user__header-image {
  display: flex;
  align-content: center;
  -webkit-box-orient: horizontal;
  -webkit-box-direction: normal;
  flex-direction: row;
  -webkit-box-pack: center;
  justify-content: center;
  -webkit-box-align: center;
  align-items: center;
  flex-wrap: wrap-reverse;
  width: 45px;
  height: 45px;
  border-radius: 5px;
  border-color: #eee;
  border-width: 1px;
  border-style: solid;
  overflow: hidden;
}

.user__content {
  display: flex;
  -webkit-box-orient: horizontal;
  -webkit-box-direction: normal;
  flex-direction: row;
  -webkit-box-flex: 1;
  flex: 1;
  overflow: hidden;
  padding: 2px 0;
}

.user__content-main {
  display: -webkit-box;
  display: -webkit-flex;
  display: flex;
  -webkit-box-orient: vertical;
  -webkit-box-direction: normal;
  -webkit-flex-direction: column;
  flex-direction: column;
  -webkit-box-pack: justify;
  -webkit-justify-content: space-between;
  justify-content: space-between;
  padding-left: 10px;
  -webkit-box-flex: 1;
  -webkit-flex: 1;
  flex: 1;
  overflow: hidden;
}

.user__content-note {
  margin-top: 3px;
  color: #999;
  font-size: 12px;
  font-weight: normal;
  overflow: hidden;
}


</style>
