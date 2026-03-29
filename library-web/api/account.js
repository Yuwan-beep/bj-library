import service from './axios'

export function getAdmins() {
    return service.get('/admin/getAdmins')
}

export function getUsers() {
    return service.get('/user/getUsers')
}

export function createAdmin(data) {
    return service.post('/admin/create', data)
}

export function updateAdmin(data) {
    return service.post('/admin/updatePassword', data)
}

export function updateUser(data) {
    return service.post('/user/updatePassword', data)
}

export function updateAuthority(authority,id) {
    return service.get(`/admin/updateAuthority?authority=${authority}&id=${id}`)
}
export function updatePhone(phone,id) {
    return service.get(`/user/updatePhone?phone=${phone}&id=${id}`)
}
