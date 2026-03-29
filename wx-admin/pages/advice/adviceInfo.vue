<template>
	<view>
		<view class="container">
			<div class="body">
				<view class="title">
					<view>{{data.date}}</view>
				</view>
				<view class="name">{{data.title}}</view>
				<view class="content">
					<u--text :text="data.content" size="27"></u--text>
				</view>
				<view class="image">
					<image v-if="data.imgurl" :src="data.imgurl"></image>
				</view>
			</div>
		</view>
		<view class="comment">
			<text class="title">全部回复</text>
			<view v-for="item in comments" class="block">
				<view class="block-right">
					<text class="name">{{item.replyPerson}}</text>
					<text class="text">{{item.content}}</text>
				</view>
			</view>
		</view>
		<view class="input">
			<u-input placeholder="回复学生" v-model="requestComment.content">
				<template slot="suffix">
					<view style="display: flex;">
						<u-button type="error" size="mini" @click="publishComment">发表评论</u-button>
						<u-button type="error" size="mini" @click="hasSolve">问题解决</u-button>
					</view>
				</template>
			</u-input>
		</view>
	</view>
</template>

<script>
	import {
		request
	} from '@/util/request.js'
	import moment from 'moment';
	export default {
		data() {
			return {
				data: {},
				commentInput: '',
				items: [],
				requestComment: {
					replyPerson: '管理员',
					adviceId: '',
					content: ""
				}
			}
		},
		computed: {
			comments() {
				return this.items.reverse();
			}
		},
		methods: {
			hasSolve() {
				var that = this;
				uni.showModal({
					title: '问题已解决？',
					success: function(res) {
						if (res.confirm) {
							request({
								url: '/advice/solve' + '?id=' + that.data.id,
							}).then(res => {
								if (res.data.code === 200) {
									// uni.redirectTo({
									// 	url: '/pages/advice/advice'
									// })
									uni.navigateBack()
								} else {
									uni.showToast({
										title: '操作失败',
										icon: 'none'
									})
								}
							})
						}
					}
				})
			},
			getComments(e) {
				request({
					url: '/comments',
					data: {
						id: e
					}
				}).then(res => {
					this.items = res.data.data;
				})
			},
			publishComment() {
				this.requestComment.adviceId = this.data.id;
				request({
					url: '/comment',
					method: 'POST',
					data: this.requestComment
				}).then(res => {
					if (res.data.code === 200) {
						uni.showToast({
							title: '评论成功',
							icon: 'none'
						})
						this.getComments(this.data.id);
						this.requestComment.content = '';
					} else {
						return uni.showToast({
							title: '评论失败',
							icon: 'none'
						})
					}
				})
			}
		},
		onLoad(e) {
			request({
				url: '/advice',
				data: {
					id: e.id
				}
			}).then(res => {
				this.data = res.data.data;
				this.data.date = moment(new Date(res.data.data.date)).format('YYYY-MM-DD HH:mm:ss')
				this.getComments(res.data.data.id);
			})
		}
	}
</script>

<style scoped lang="scss">
	.container {
		border-radius: 7rpx;
		background-color: #fff;
		padding: 20rpx;
		margin-bottom: 13rpx;
		border-bottom: 1rpx solid #f8f8f8;

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
				font-size: 23rpx;
				color: #999;
			}

			.content {}

			.name {
				font-size: 30rpx;
				color: #333;
				font-weight: bold;
				word-break: break-all;
			}

			.bottom {
				margin: 15rpx 0;
				display: flex;
				flex-direction: row;
				justify-content: space-between;

				image {
					vertical-align: -5rpx;
					width: 25rpx;
					height: 25rpx;
					margin: 0 3rpx 0 15rpx;
				}

				.icon {
					display: flex;
					flex-direction: row;
					align-items: center;
					justify-content: space-around;
					font-size: 23rpx;
					color: #555;
					margin: 0 5rpx;

				}

				.detail {
					font-size: 25rpx;
					color: #3b82f6;
				}
			}
		}
	}

	.comment {
		padding: 20rpx;
		padding-bottom: 100rpx;

		.title {
			font-size: 30rpx;
			font-weight: bold;
		}

		.block {
			display: flex;
			padding: 15rpx 0;
			border-bottom: 1rpx solid #e3e3e3;

			.block-right {
				width: 100%;
				margin-left: 8rpx;

				.name {
					color: #333;
					display: block;
					font-size: 27rpx;
					font-weight: bold;
				}

				.text {
					word-break: break-all;
					font-size: 25rpx;
					color: #555;
				}
			}
		}
	}

	.input {
		width: 98%;
		padding: 10rpx;
		position: fixed;
		bottom: 0;
	}
</style>