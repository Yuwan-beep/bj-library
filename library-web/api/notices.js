import service from './axios';
export function getNoticeList(){
    return service.get('/notice/all');
}
export function updateNotice(notice){
    return service.post('/notice/update',notice);
}
export function deleteNotice(id){
    return service.delete('/notice?id='+id);
}
export function createNotice(notice){
    return service.post('/notice',notice);
}

