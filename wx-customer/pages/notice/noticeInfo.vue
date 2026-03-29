<template>
	<view>
		<view class="container">
			<div class="body">
				<view class="title">
					<view>{{data.date}}</view>
				</view>
				<view class="name">{{data.title}}</view>
				<view class="content">
					<u--text :text="data.content" size="25"></u--text>
				</view>
				<view class="image">
					<image v-if="data.imgurl" :src="data.imgurl"></image>
				</view>
			</div>
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
				data: {},
			}
		},
		methods: {},
		onLoad(e) {
			request({
				url: '/notice',
				data: {
					id: e.id
				}
			}).then(res => {
				let date = moment(new Date(res.data.date)).format('YYYY-MM-DD HH:mm:ss')
				this.data = res.data;
				this.data.date = date;
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

		.body {
			display: flex;
			flex-direction: column;

			.title {
				margin-left: 6rpx;
				color: #999;
				font-size: 23rpx;
			}

			.content {
				font-size: 25rpx;
				color: #555;
			}

			.name {
				font-size: 30rpx;
				color: #333;
				font-weight: bold;
				word-break: break-all;
				line-height: 45rpx;
			}
		}
	}
</style>