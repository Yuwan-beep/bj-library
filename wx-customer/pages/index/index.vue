<template>
	<view class="content">
		<image src="../../static/background.jpg"></image>
		<view class="container">
			<view class="tips">BeiJiang</view>
			<view class="logo">
				<image src="../../static/index/学校_学校信息.png"></image>
			</view>
			<view class="content-block">
				<view class="title">用户账号登录</view>
				<view class="account">
					<u-icon name="account" size="38"></u-icon>
					<input v-model="account" placeholder="请输入账号" maxlength="15"/>
				</view>
				<view class="password">
					<u-icon name="lock" size="38"></u-icon>
					<input v-model="password" placeholder="请输入密码" :password="true" maxlength="20"/>
				</view>
				<view class="register" @click="navigatRegister">注册账号</view>
				<view class="submit" @click="login">登录</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		request
	} from '../../util/request.js'
	export default {
		data() {
			return {
				account: '',
				password: ''
			}
		},
		onLoad() {
			if (uni.getStorageSync('token') && uni.getStorageSync('username')) {
				uni.navigateTo({
					url: '/pages/home/home'
				})
			}
		},
		methods: {
			login() {
				if(!this.account || !this.password){
					return uni.showToast({
						title:'账号或密码不能为空',
						icon:'none'
					})
				}
				let that = this;
				request({
					url: '/user/login',
					method: 'POST',
					data: {
						username: this.account,
						password: this.password
					}
				}).then(res => {
					if (res.data.code == 200) {
						console.log(res.data);
						uni.setStorageSync('token', res.data.data.token);
						uni.setStorageSync('username', that.account);
						uni.navigateTo({
							url: '/pages/home/home'
						})
					} else {
						return uni.showToast({
							title: '用户名或密码错误',
							icon: 'none'
						})
					}
				})
			},
			navigatRegister(){
				uni.navigateTo({
					url:'/pages/register/register'
				})
			}
		}
	}
</script>

<style lang="scss" scoped>
	* {
		margin: 0;
		padding: 0;
		box-sizing: border-box;
	}

	.content {
		overflow: hidden;

		image {
			width: 100vw;
			height: 100vh;
			position: absolute;
			z-index: -1;
		}

		.container {
			background: #fff;
			margin: 350rpx 100rpx;
			border-radius: 30rpx;
			// overflow: hidden;
			position: relative;

			.tips {
				color: #6e9df4;
				background: linear-gradient(to right, #004eea, #317efa);
				border-radius: 20rpx 20rpx 0 0;
				height: 40rpx;
				line-height: 40rpx;
				padding-left: 20rpx;
			}

			.logo {
				border-radius: 50%;
				background: #3073f8;
				width: 130rpx;
				height: 130rpx;
				display: flex;
				justify-content: center;
				align-items: center;
				margin-left: calc(50% - 75rpx);
				margin-top: -90rpx;
				box-shadow: 5rpx #4c4cff;

				image {
					width: 100rpx;
					height: 100rpx;
					z-index: 9;
				}
			}

			.content-block {
				position: relative;
				margin-top: 15rpx;
				padding: 30rpx 30rpx 80rpx 30rpx;

				.title {
					font-weight: bold;
					font-size: 29rpx;
					text-align: center;
					height: 35rpx;
					line-height: 35rpx;
					padding-bottom: 15rpx;
					border-bottom: 1rpx solid #eaeaec;
				}

				.account,
				.password {
					margin: 5rpx;
					padding: 15rpx;
					display: flex;
					border-bottom: 1rpx solid #eaeaec;

					input {
						margin-left: 5rpx;
					}
				}
				.register{
					color: #2b6df8;
					text-align: right;
					margin-top: 25rpx;
				}
				.submit {
					width: 350rpx;
					height: 90rpx;
					text-align: center;
					line-height: 90rpx;
					background: #2b6df8;
					border-radius: 20rpx;
					color: #fff;
					position: absolute;
					left: calc(50% - 175rpx);
					bottom: -45rpx;
				}
			}
		}
	}
</style>