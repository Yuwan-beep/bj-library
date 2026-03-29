<template>
	<view>
		<view class="title">
			北江中学图书馆
		</view>
		<u-grid :border="true" @click="click" col="2">
			<u-grid-item v-for="(baseListItem,baseListIndex) in baseList" :key="baseListIndex">
				<u-icon :customStyle="{paddingTop:20+'rpx'}" :name="baseListItem.name" :size="80"></u-icon>
				<text class="grid-text">{{baseListItem.title}}</text>
			</u-grid-item>
		</u-grid>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				baseList: [{
						name: 'scan',
						title: '扫码入库图书'
					},
					{
						name: 'volume',
						title: '发布通知'
					},
					{
						name: 'volume',
						title: '回复留言'
					},
					{
						name: 'volume',
						title: '发布座谈会'
					},
					{
						name: 'server-man',
						title: '询问AI'
					},
					{
						name: 'star',
						title: '退出登录'
					}
				]
			};
		},
		methods:{
			click(index){
				if(index === 0){
					uni.navigateTo({
						url:'/pages/scan/scan'
					})
				} else if (index === 1){
					uni.navigateTo({
						url:'/pages/notice/notice'
					})
				} else if (index === 2){
					uni.navigateTo({
						url:'/pages/advice/advice'
					})
				} else if (index === 3){
					uni.navigateTo({
						url:'/pages/lecture/lecture'
					})
				} else if (index === 4){
					uni.navigateTo({
						url:'/pages/ai/ai'
					})
				} else {
					uni.clearStorageSync();
					uni.reLaunch({
						url:'/pages/index/index'
					})
				}
			}
		}
	}
</script>

<style lang="scss" scoped>
	.title {
		font-size: 40rpx;
		height: 150rpx;
		line-height: 150rpx;
		font-weight: bold;
		text-align: center;
	}
	.grid-text {
	        font-size: 14px;
	        color: #909399;
	        padding: 10rpx 0 20rpx 0rpx;
	    }
</style>