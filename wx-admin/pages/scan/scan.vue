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
			<view class="block" v-if="bookData.bookDesc">
				<text class="title">
					摘要
				</text>
				<text class="content">
					{{bookData.bookDesc}}
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
			<view class="block" v-if="bookData.pressDate">
				<text class="title">
					出版时间
				</text>
				<text class="content">
					{{bookData.pressDate}}
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
			<view class="block">
				<text class="title">
					中图法分类
				</text>
				<text class="content" v-if="bookData.clcCode">
					{{bookData.clcCode}}
				</text>
				<text class="content" v-if="!bookData.clcCode">
					其他
				</text>
			</view>
			<view class="block" v-if="bookData.clcName">
				<text class="title">
					分类名
				</text>
				<text class="content">
					{{bookData.clcName}}
				</text>
			</view>
			<view class="button-block">
				<view class="button" @click="submit">
					入库
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
			async getBookData() {
				return new Promise((resolve, reject) => {
					uni.request({
						url: 'https://api.zbape.com/api/isbn/query?key=s8IlKTCSMbxDHBzKS9nti4tw4htG2AvH&isbn=' + this
							.ISBN,
							timeout:5000,
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
			submit(){
				var that = this;
				request({
					url:'/book/enterBook',
					method:'POST',
					data:{
						name:this.bookData.bookName,
						author:this.bookData.author,
						publish:this.bookData.press,
						isbn:this.bookData.isbn,
						introduction:this.bookData.bookDesc,
						price:this.bookData.price,
						pubDate:this.bookData.pressDate,
						classification:this.bookData.clcCode,
						imgurl:this.bookData.pictures
					}
				}).then(res=>{
					if(res.data.code==200){
						uni.showToast({
							title:'入库成功!',
							
						})
						setTimeout(()=>{
							that.ISBN = '';
							that.bookData=null;
						},500)
					} else {
						return uni.showToast({
							title:'入库失败！',
							icon:'none'
						})
					}
				})
			}
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