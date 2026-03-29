import service from './axios';

// 获取讲座列表
export function getLectureList() {
    return service.get('/lecture/all');
}

// 删除讲座
export function deleteLecture(id) {
    return service.delete(`/lecture?id=${id}`);
}

// 添加讲座
export function addLecture(data) {
    return service.post('/lecture',data);
}

// 更新讲座
export function updateLecture(data) {
    return service.put('/lecture/update',data);
}

// 获取讲座详情
export function getLectureDetail(id) {
    return service.get(`/lecture/detail/${id}`);
} 