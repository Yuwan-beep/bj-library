import service from './axios';

export function getAdviceList() {
    return service.get('/advice/all');
}

export function getCommentList(id) {
    return service.get(`/comments?id=${id}`);
}

export function replyAdvice(advice) {
    return service.post('/comment', advice);
}

export function resolveAdvice(id) {
    return service.get('/advice/solve?id=' + id);
}
