<template>
	<view>
		<u-input placeholder="点此输入" color="#333" v-model="post.title" maxlength="128" type="text" showWordLimit="true">
			<u--text text="标题:" slot="prefix" margin="0 3px 0 0" color="#666" size="28"></u--text>
		</u-input>
		<u-input placeholder="点此输入" color="#333" v-model="post.moderator" maxlength="32" type="text"
			showWordLimit="true">
			<u--text text="主持人:" slot="prefix" margin="0 3px 0 0" color="#666" size="28"></u--text>
		</u-input>
		<view @click="selectLocation">
			<u-input placeholder="点击选择" color="#333" v-model="location" maxlength="32" disabled type="text"
				showWordLimit="true" @click="selectLocation">
				<u--text text="选择地点:" slot="prefix" margin="0 3px 0 0" color="#666" size="28"></u--text>
			</u-input>
		</view>
		<view @click="selectLocation">
			<u-input placeholder="点击选择" color="#333" v-model="post.location" maxlength="32" disabled type="text"
				showWordLimit="true" @click="selectLocation">
				<u--text text="地址:" slot="prefix" margin="0 3px 0 0" color="#666" size="28"></u--text>
			</u-input>
		</view>
		<u-input placeholder="点击输入" color="#333" v-model="detailLocation" maxlength="64" type="text"
			showWordLimit="true">
			<u--text text="详细地址:" slot="prefix" margin="0 3px 0 0" color="#666" size="28"></u--text>
		</u-input>
		<view>
			<u-input placeholder="点击右侧按钮选择" v-model="post.date" disabled disabledColor="#ffffff">
				<u--text text="时间:" slot="prefix" margin="0 3px 0 0" color="#666" size="28"></u--text>
				<template slot="suffix">
					<view style="display: flex;">
						<u-button type="error" size="mini" @click="selectTime">点击选择时间</u-button>
					</view>
				</template>
			</u-input>
			<u-datetime-picker ref="datetimePicker" :show="show" v-model="value1" mode="datetime" :formatter="formatter"
				@confirm="subTime" @cancel="closePicker"></u-datetime-picker>
		</view>
		<!-- 		<view class="input">
			<u--textarea v-model="post.content" placeholder="正文" count border="none" height="150"
				maxlength="8192"></u--textarea>
		</view> -->
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
				show: false,
				value1: Number(new Date()),
				post: {
					"title": "",
					"content": "",
					"imgurl": "",
					moderator: '',
					address: '',
					date: null,
					location:''
				},
				location: '',
				detailLocation: '',
				address: '',
				latitude:0,
				longitude:0
			}
		},
		methods: {
			selectLocation() {
				console.log(this.latitude)
				console.log(this.longitude)
				const key = 'CRUBZ-IOB3Q-RPD5M-4SO3P-EZRPO-NWFRQ'; //使用在腾讯位置服务申请的key
				const referer = '毕设需要'; //调用插件的app的名称
				const location = JSON.stringify({
					latitude: this.latitude,
					longitude: this.longitude
				});
				wx.navigateTo({
					url: 'plugin://chooseLocation/index?key=' + key + '&referer=' + referer
				});
			},
			closePicker() {
				this.show = false;
			},
			selectTime() {
				this.show = true;
			},
			async subTime(e) {
				console.log("e" + e);
				this.post.date = await moment(new Date(e.value)).format('YYYY-MM-DD HH:mm:ss')
				this.closePicker();
			},
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
							uni.showToast({
								title: '上传失败，文件过大!',
								icon: 'none'
							})
							reject(err);
						}
					});
				});
			},
			publish() {
				if(this.post.title===""){
					return uni.showToast({
						title:"标题不能为空",
						icon:'none'
					})
				}
				if(this.post.moderator===""){
					return uni.showToast({
						title:"主持人不能为空",
						icon:'none'
					})
				}
				if(this.location===""){
					return uni.showToast({
						title:"请选择地点!",
						icon:'none'
					})
				}
				if(this.post.date===null){
					return uni.showToast({
						title:"请选择时间!",
						icon:'none'
					})
				}
				this.post.address = this.location+' '+this.detailLocation;
				request({
					url: '/lecture',
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
							// 	url: '/pages/lecture/lecture'
							// })
							uni.navigateBack();
						}, 1200)
					} else {
						uni.showToast({
							title: '发布失败',
							icon: 'none'
						})
					}
				})
			},
			formatter(type, value) {
				if (type === 'year') {
					return `${value}年`
				}
				if (type === 'month') {
					return `${value}月`
				}
				if (type === 'day') {
					return `${value}日`
				}
				if (type === 'hour') {
					return `${value}时`
				}
				if (type === 'minute') {
					return `${value}分`
				}
				return value
			},
		},
		onShow: function() {
			// 从地图选点插件返回后，在页面的onShow生命周期函数中能够调用插件接口，取得选点结果对象
			// 如果点击确认选点按钮，则返回选点结果对象，否则返回null
			const chooseLocation = requirePlugin('chooseLocation');
			const location = chooseLocation.getLocation();
			if (location === null) return;
			this.location = location.name;
			this.post.location = location.address
			console.log(location)
			// if (location) {
			// 	this.setData({
			// 		address: location.address ? location.address : "",
			// 		locationName: location.name ? location.name : ""
			// 	});
			// }
		},
		created() {
			// 获取当前的位置信息
			uni.getLocation({
				type: 'wgs84',
				success: (res) => {
					console.log(123,res)
					this.latitude = res.latitude
					this.longitude = res.longitude
				}
			})
		},
		onReady() {
			// 微信小程序需要用此写法
			this.$refs.datetimePicker.setFormatter(this.formatter);
		},
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