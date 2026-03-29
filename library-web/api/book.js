// src/api/user.js

import service from './axios';
// 获取用户信息
export function getBookInfo(classification) {
    if(classification.index === 0){
        return service.get(`/book/all`);
    } else if(classification.index>0 && classification.index<=22){
        return service.get('/book/allByClass?classification='+classification.classNumber)
    } else {
        return service.get('/book/allIsNull');
    }
}
export function updateBook(bookInfo) {
    return service.post('/book/update',bookInfo);
}
export function deleteBook(id) {
    return service.get('/book/delete?id='+id);
}
export function getLendList(){
    return service.get('/lendList');
}
