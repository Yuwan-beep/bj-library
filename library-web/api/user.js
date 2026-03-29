// src/api/user.js

import service from './axios';

// 获取用户信息
export function getUserInfo(userId) {
    return service.get(`/users/${userId}`);
}
export function login(account,password){
    return service.post('/admin/login',{
        username: account,
        password: password
    })
}
// 更新用户信息
export function updateUserInfo(userId, data) {
    return service.put(`/users/${userId}`, data);
}

// 删除用户
export function deleteUser(userId) {
    return service.delete(`/users/${userId}`);
}

