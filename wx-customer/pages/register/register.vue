<template>
	<view>
		<view class="input-block">
			<u-input placeholder="点此输入" color="#333" v-model="post.username" maxlength="15" type="text"
				showWordLimit="true" border="none" clearable>
				<u--text text="账号:" slot="prefix" margin="0 3px 0 0" color="#666" size="28"></u--text>
			</u-input>
		</view>
		<view class="input-block">
			<u-input placeholder="点此输入(密码需8-20位，且包含字母和数字)" color="#333" v-model="post.password" maxlength="20" type="text"
				showWordLimit="true" border="none" password clearable>
				<u--text text="密码:" slot="prefix" margin="0 3px 0 0" color="#666" size="28"></u--text>
			</u-input>
		</view>
		<view class="input-block">
			<u-input placeholder="点此输入" color="#333" v-model="verifyPassword" maxlength="20" type="text"
				showWordLimit="true" border="none" password clearable>
				<u--text text="再次输入密码:" slot="prefix" margin="0 3px 0 0" color="#666" size="28"></u--text>
			</u-input>
		</view>
		<view class="input-block">
			<u-input placeholder="点此输入" color="#333" v-model="post.phone" maxlength="11" type="number"
				showWordLimit="true" border="none" clearable>
				<u--text text="手机号:" slot="prefix" margin="0 3px 0 0" color="#666" size="28"></u--text>
			</u-input>
		</view>
		<view class="submit" @click="submitRegister">注册</view>
	</view>
</template>

<script>
import { request } from '../../util/request';
	export default {
		data() {
			return {
				post: {
					username: '',
					password: '',
					phone: ''
				},
				verifyPassword:''
			};
		},
		methods:{
			submitRegister(){
				if(!this.post.username){
					return uni.showToast({
						title:'账号不能为空',
						icon:'none'
					})
				}
				var that = this;
				// 密码复杂度校验
				const passwordReg = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,20}$/;
				if (!passwordReg.test(this.post.password)) {
					return uni.showToast({
						title: '密码需8-20位，且包含字母和数字',
						icon: 'none'
					})
				}
				// 手机号正则校验
				const phoneReg = /^1[3-9]\d{9}$/;
				if (!phoneReg.test(this.post.phone)) {
					return uni.showToast({
						title: '请输入正确的手机号',
						icon: 'none'
					})
				}
				if(this.post.password!=this.verifyPassword){
					return uni.showToast({
						title:'两次密码不一致',
						icon:'none'
					})
				}
				request({
					url:'/user/register',
					method:'POST',
					data:this.post
				}).then(res=>{
					if(res.data.code === 200){
						uni.showToast({
							title:'注册成功'
						})
						setTimeout(()=>{
							uni.navigateBack()
						},1000);
					} else {
						return uni.showToast({
							title:res.data.message,
							icon:'none'
						})
					}
				})
			}
		}
	}
</script>

<style lang="scss" scoped>
	.input-block {
		padding: 25rpx;
		border-bottom: 1rpx solid #e3e3e3;
	}

	.submit {
		width: 350rpx;
		height: 90rpx;
		text-align: center;
		line-height: 90rpx;
		background: #2b6df8;
		border-radius: 20rpx;
		color: #fff;
		position: relative;
		left: calc(50% - 175rpx);
		margin-top: 60rpx;
	}
</style>