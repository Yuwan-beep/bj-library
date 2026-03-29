<template>
	<view>
		<u-input placeholder="点此输入" color="#333" v-model="post.title" maxlength="128" type="text" showWordLimit="true">
			<u--text text="标题:" slot="prefix" margin="0 3px 0 0" color="#666" size="28"></u--text>
		</u-input>
		<view class="input">
			<u--textarea v-model="post.content" placeholder="正文" count border="none"
				height="150" maxlength="8192"></u--textarea>
		</view>
		<view style="margin: 15rpx;">
			<u-upload :fileList="fileList1" @afterRead="afterRead" @delete="deletePic" name="1" multiple :maxCount="1"
				:previewFullImage="true" width="155" height="155"></u-upload>
		</view>
		<view class="bottom">
			<view class="button" @click="publish">
				发布
			</view>
		</view>
	</view>
</template>

<script>
	import moment from 'moment';
	import {
		request
	} from '@/util/request.js'
	export default {
		data() {
			return {
				fileList1: [],
				text: '',
				post: {
					"title": "",
					"content": "",
					"imgurl": ""
				}
			}
		},
		methods: {
			getCurrentTime() {
				//获取当前时间并打印
				return moment(Number(new Date())).format('YYYY-MM-DD')
			},
			// 删除图片
			deletePic(event) {
				this[`fileList${event.name}`].splice(event.index, 1)
			},
			// 新增图片
			async afterRead(event) {
				// 当设置 multiple 为 true 时, file 为数组格式，否则为对象格式
				console.log(event);
				let lists = [].concat(event.file)
				console.log(this)
				console.log(this[`imgArr{event.name}`])
				let fileListLen = this[`fileList${event.name}`].length
				lists.map((item) => {
					this[`fileList${event.name}`].push({
						...item,
						status: 'uploading',
						message: '上传中'
					})
				})
				for (let i = 0; i < lists.length; i++) {
					const result = await this.uploadFilePromise(lists[i].url)
					let item = this[`fileList${event.name}`][fileListLen]
					this[`fileList${event.name}`].splice(fileListLen, 1, Object.assign(item, {
						status: 'success',
						message: '',
						url: result
					}))
					fileListLen++
				}
			},
			uploadFilePromise(url) {
				var that = this;
				return new Promise((resolve, reject) => {
					uni.uploadFile({
						url: 'http://110.40.159.129:8443/api/upload', // 仅为示例，非真实的接口地址
						filePath: url, // 指定要上传的文件路径
						name: 'image', // 指定上传文件的字段名，要和后端保持一致
						success: (res) => {
							that.post.imgurl = JSON.parse(res.data).data;
							setTimeout(() => {
								resolve(res.data.data);
							}, 100);
						},
						fail: (err) => {
							console.error('文件上传失败:', err);
							reject(err);
						}
					});
				});
			},
			publish() {
				if(this.post.title===""){
					return uni.showToast({
						title:"标题不能为空!",
						icon:'none'
					})
				}
				request({
					url: '/notice',
					method: 'POST',
					data: this.post
				}).then(res => {
					if (res.data.code === 200) {
						uni.showToast({
							title: '发布成功',
							icon: 'none'
						})
						setTimeout(() => {
							// uni.navigateTo({
							// 	url: '/pages/notice/notice'
							// })
							uni.navigateBack()
						}, 1200)
					} else {
						uni.showToast({
							title: '发布失败',
							icon: 'none'
						})
					}
				})
			}
		}
	}
</script>

<style scoped lang="scss">
	.input {
		width: 100%;
		height: 250rpx;
	}

	.scrollPhoto {
		margin: 15rpx 15rpx;

		.phoneList {
			display: flex;

			image {
				width: 180rpx;
				height: 180rpx;
				margin: 15rpx;
			}

			.icon {
				width: 180rpx;
				height: 180rpx;
				border-radius: 25rpx;
				border: 1rpx dotted #808080;
				display: flex;
				justify-content: center;
				align-items: center;
				
				image {
					width: 80rpx;
					height: 80rpx;
					margin: 15rpx;
				}
			}
		}
	}

	.bottom {
		position: fixed;
		height: 130rpx;
		width: 100%;
		background: #fff;
		border-top: 1rpx solid #f8f8f8;
		bottom: 0;
		display: flex;
		justify-content: center;
		align-items: center;

		.button {
			width: 500rpx;
			background-color: #d81e06;
			height: 90rpx;
			line-height: 90rpx;
			text-align: center;
			border-radius: 45rpx;
			color: #fff;
		}
	}
</style>