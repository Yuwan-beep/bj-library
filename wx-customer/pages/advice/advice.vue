<template>
	<view class="container">
		<view v-if="items.length===0">
			<u-empty mode="search" icon="/static/guangbo/nosearch.png">
			</u-empty>
		</view>
		<view v-for="item in news" class="item">
			<div class="body">
				<view class="name">{{item.title}}</view>
				<view class="content">
					<u--text :lines="1" :text="item.content.toString().trim()" size="25"></u--text>
				</view>
				<view class="bottom">
					<view class="date">
						{{item.date}}
					</view>
					<view class="detail" @click="information(item.id)">
						查看详情
					</view>
				</view>
			</div>
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
			add(){
				uni.navigateTo({
					url:'/pages/advice/addAdvice'
				})
			},
			information(e) {
				uni.navigateTo({
					url: `/pages/advice/adviceInfo?id=${e}`
				})
			},
			getAllDate() {
				request({
					url: `/advice/byUser`,
					data:{
						username:uni.getStorageSync('username')
					}
				}).then(res => {
					// this.items = res.data.data;
					this.items = res.data.data.map(item => {
					    return {
					      ...item,
					      // 转换时间字段
					      date: moment(new Date(item.date)).format('YYYY-MM-DD HH:mm:ss')
					    };
					  });
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
	.container {
		padding: 10rpx 0;
		background-color: #f8f8f8;

		.item {
			border-radius: 7rpx;
			background-color: #fff;
			padding: 15rpx;
			margin-bottom: 13rpx;

			.avatar {
				display: flex;
				flex-direction: column;
				justify-content: start;
				align-items: center;
			}

			.body {
				display: flex;
				flex-direction: column;

				.title {
					display: flex;
					flex-direction: row;
					justify-content: space-between;

					:nth-child(1) {
						display: flex;

						text {
							margin-left: 7rpx;
							color: #999;
							font-size: 27rpx;
						}
					}

					:nth-child(2) {
						color: #999;
						font-size: 27rpx;
					}
				}

				.content {
					font-size: 25rpx;
				}


				.name {
					font-size: 28rpx;
					color: #333;
					font-weight: bold;
					word-break: break-all;
				}

				.bottom {
					margin: 15rpx 0;
					display: flex;
					flex-direction: row;
					justify-content: flex-end;

					image {
						vertical-align: -5rpx;
						width: 25rpx;
						height: 25rpx;
						margin: 0 3rpx 0 15rpx;
					}

					.date {
						flex-grow: 2;
						font-size: 23rpx;
						color: #777;
						margin: 0 5rpx;
						justify-items: flex-start;
					}

					.delete {
						color: #f56c6c;
						font-size: 25rpx;
						height: 25rpx;
						line-height: 25rpx;
					}

					.detail {
						font-size: 25rpx;
						height: 25rpx;
						line-height: 25rpx;
						margin-left: 20rpx;
						color: #3b82f6;
					}

				}
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