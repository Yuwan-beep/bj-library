<template>
	<view>
		<view class="icon-container" v-if="!ISBN" @click="scan">
			<view class="icon">
				<u-icon name="scan" size="150"></u-icon>
			</view>
		</view>
		<view v-if="ISBN && !bookData" style="margin-top: 100rpx;">
			<u-loading-icon text="加载中" textSize="50" size="100"></u-loading-icon>
		</view>

		<view v-if="ISBN && bookData" class="data-container">
			<image :src="bookData.pictures"></image>
			<view class="block">
				<text class="title">
					书名
				</text>
				<text class="content">
					{{bookData.bookName}}
				</text>
			</view>
			<view class="block">
				<text class="title">
					作者
				</text>
				<text class="content">
					{{bookData.author}}
				</text>
			</view>
			<view class="block">
				<text class="title">
					出版社
				</text>
				<text class="content">
					{{bookData.press}}
				</text>
			</view>
			<view class="block">
				<text class="title">
					ISBN
				</text>
				<text class="content">
					{{bookData.isbn}}
				</text>
			</view>
			<view class="button-block">
				<view class="button" @click="submit">
					还书
				</view>
			</view>
		</view>

	</view>
</template>

<script>
	import {
		request
	} from '../../util/request';
	export default {
		data() {
			return {
				ISBN: '',
				bookData: null
			};
		},
		methods: {
			scan() {
				wx.scanCode({
					onlyFromCamera: true,
					scanType: ['barCode'],
					success: res => {
						this.ISBN = res.result;
						this.getBookData().then(res=>{
							this.bookData = res.data.data;
							console.log(res);
						},error=>{
							this.ISBN=null
							return;
						})
					},
					fail: err => {
						return uni.showToast({
							title: '识别失败!',
							icon: 'none'
						})
					}
				})
			},
			submit(){
				request({
					url:'/back',
					data:{
						isbn:this.bookData.isbn,
						username:uni.getStorageSync('username')
					}
				}).then(res=>{
					if(res.data.code === 200){
						uni.showToast({
							title:'还书成功'
						})
						setTimeout(()=>{
							uni.navigateBack()
						},1300)
					} else {
						uni.showToast({
							title:res.data.message,
							icon:'none'
						})
					}
				})
			},
			async getBookData() {
				return new Promise((resolve, reject) => {
					uni.request({
						url: 'https://api.zbape.com/api/isbn/query?key=s8IlKTCSMbxDHBzKS9nti4tw4htG2AvH&isbn=' + this
							.ISBN,
						success: (res) => {
							if(res.data.code!=200){
								reject(res.data.msg)
								return uni.showToast({
									title:"数据不存在",
									icon:'none'
								})
							}
							resolve(res)
						},
						fail: (err) => {
							reject(err)
						}
					})
				})

			},
		}
	}
</script>

<style lang="scss" scoped>
	*{
		word-break: break-all;
	}
	.icon-container {
		width: 100%;
		height: 500rpx;
		display: flex;
		justify-content: center;
		align-items: center;

		.icon {
			width: 250rpx;
			height: 250rpx;
			border: 5rpx dotted #e3e3e3;
			border-radius: 20rpx;
			display: flex;
			justify-content: center;
			align-items: center;
		}
	}
	.data-container{
		padding: 20rpx;
		image{
			width: 200rpx;
			height: 200rpx;
			border-radius: 5rpx;
			margin-top: 50rpx;
			margin-left: calc(50% - 100rpx);
		}
		.block{
			padding: 20rpx;
			border-bottom: 1rpx solid #e3e3e3;
			.title{
				font-size: 28rpx;
				height: 35rpx;
				line-height: 35rpx;
				font-weight: bold;
				display: block;
			}
			.content{
				
			}
		}
		.button-block{
			margin-top: 50rpx;
			display: flex;
			justify-content: center;
			align-items: center;
			.button{
				height: 100rpx;
				background-color: #3073f8;
				width: 450rpx;
				border-radius: 50rpx;
				line-height: 100rpx;
				font-size: 30rpx;
				font-weight: bold;
				color: #fff;
				text-align: center;
			}
		}
	}
</style>