const BASE_URL = 'http://110.40.159.129:8443/api'
// const BASE_URL = 'http://localhost:8443/api'
export const request = (options) => {
	return new Promise((resolve, reject) => {
		uni.request({
			url: BASE_URL + options.url,
			method: options.method || 'GET',
			data: options.data || {},
			header: {
				'X-Access-Token': uni.getStorageSync('token'), //自定义请求头信息
			} || {},
			success: (res) => {
				console.log(JSON.stringify(res));
				if (res.statusCode === 401) {
					uni.showToast({
						title: "token失效，请重新登录",
						icon: 'none',
						complete: function() {
							setTimeout(() => {
								uni.clearStorage();
								uni.reLaunch({
									url: '/pages/index/index'
								})
							}, 1500)
						}
					})
				}
				resolve(res)
			},
			fail: (err) => {
				reject(err)
			}
		})
	})
}