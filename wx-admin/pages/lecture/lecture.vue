<template>
	<view class="container">
		<view v-if="items.length===0">
			<u-empty mode="search" icon="/static/guangbo/nosearch.png">
			</u-empty>
		</view>
		<view v-for="(item,index) in news" class="item">
			<div class="body">
				<view class="body-left" v-if="item.imgurl">
					<image :src="item.imgurl"></image>
				</view>
				<view class="body-right">
					<view class="text-block">
						<view class="title">主题:</view>
						<view class="text">{{item.title}}</view>
					</view>
					<view class="text-block">
						<view class="title">主讲:</view>
						<view class="text">{{item.moderator}}</view>
					</view>
					<view class="text-block">
						<view class="title">时间:</view>
						<view class="text">{{item.date}}</view>
					</view>
					<view class="text-block">
						<view class="title">地点:</view>
						<view class="text">{{item.location}}</view>
					</view>
					<view class="text-block">
						<view class="title">详细地址:</view>
						<view class="text">{{item.address}}</view>
					</view>
				</view>
			</div>
			<view class="delect" @click="del(item.id)">删除</view>
		</view>
		<view class="returnTop">
			<u-back-top :scroll-top="scrollTop" top="200" right="30" bottom="80"></u-back-top>
		</view>
		<view class="add">
			<image src="../../static/guangbo/加号2-fill.png" @click="add"></image>
		</view>
	</view>
</template>

<script>
	import moment from 'moment'
	import {
		request
	} from '@/util/request.js'
	export default {
		data() {
			return {
				items: [],
				scrollTop: 0
			}
		},
		computed: {
			news() {
				return this.items.reverse()
			}
		},
		methods: {
			del(e) {
				var that = this;
				uni.showModal({
					title: "确定删除吗？",
					success: function(res) {
						if (res.confirm) {
							request({
								url: '/lecture' + '?id=' + e,
								method: 'DELETE',
							}).then(res => {
								if (res.data.code === 200) {
									uni.showToast({
										title: '删除成功',
										icon: 'none'
									})
									that.getAllDate()
								} else {
									uni.showToast({
										title: '删除失败',
										icon: 'none'
									})
								}
							})
						}
					}
				})
			},
			add() {
				uni.navigateTo({
					url: '/pages/lecture/addLecture'
				})
			},
			information(e) {
				uni.navigateTo({
					url: `/pages/notice/noticeInfo?id=${e}`
				})
			},
			getAllDate() {
				request({
					url: `/lecture/all`,
				}).then(res => {
					// this.items = res.data.data;
					this.items = res.data.data;
				})
			},
		},
		onShow() {
			this.getAllDate();
		},
		mounted() {}
	}
</script>

<style scoped lang="scss">
	* {
		word-break: break-all;
	}

	.container {
		padding: 10rpx 0;
		background-color: #f8f8f8;

		.item {
			border-radius: 7rpx;
			background-color: #fff;
			padding: 15rpx;
			margin-bottom: 13rpx;

			.body {
				display: flex;
				flex-direction: row;

				.body-left {
					width: 180rpx;
					display: flex;
					justify-content: center;
					align-items: center;

					image {
						width: 150rpx;
						height: 150rpx;
						border-radius: 5rpx;
					}
				}

				.body-right {
					.text-block {
						display: flex;
						margin: 10rpx;

						.title {
							font-size: 25rpx;
							line-height: 28rpx;
							font-weight: bold;
							color: #999;
							min-width: 55rpx;
						}

						.text {
							margin-left: 5rpx;
							font-size: 25rpx;
							line-height: 28rpx;
							color: #333;
						}
					}
				}

			}

			.delect {
				color: #ff4b4b;
				font-size: 25rpx;
				text-align: end;
			}
		}

		.add {
			position: fixed;
			bottom: 40rpx;
			right: 45rpx;

			image {
				height: 85rpx;
				width: 85rpx;
				border-radius: 43rpx;
			}
		}

		.returnTop {}
	}
</style>