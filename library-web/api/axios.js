// src/api/axios.js

import axios from 'axios';
import router from "@/router";
let VUE_APP_BASE_API='http://110.40.159.129:8443/api'
// let VUE_APP_BASE_API='http://localhost:8443/api'
// 创建 Axios 实例
const service = axios.create({
    baseURL: VUE_APP_BASE_API, // 设置 API 的基础 URL
    timeout: 25000, // 请求超时时间
});

// 请求拦截器
service.interceptors.request.use(
    config => {
        // 在发送请求之前做些什么
        // 例如：添加 token
        const token = localStorage.getItem('token');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    error => {
        // 对请求错误做些什么
        return Promise.reject(error);
    }
);

// 响应拦截器
service.interceptors.response.use(
    response => {
        // 对响应数据做些什么
        return response.data;
    },
    error => {
        // 对响应错误做些什么
        if (error.response) {
            // 根据不同的状态码进行不同的处理
            switch (error.response.status) {
                case 401:
                    // 未授权，跳转到登录页面
                    router.push('/login');
                    break;
                case 404:
                    // 资源未找到
                    console.error('请求的资源不存在');
                    break;
                default:
                    console.error('请求失败', error.response.data);
            }
        } else if (error.request) {
            // 请求已发出，但没有收到响应
            console.error('请求超时或网络错误', error.request);
        } else {
            // 其他错误
            console.error('请求配置错误', error.message);
        }
        return Promise.reject(error);
    }
);

export default service;
