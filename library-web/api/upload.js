import service from './axios';
export function upload(file){
    const formData = new FormData();
    formData.append('image', file);
    return service.post('/upload', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        withCredentials: true
    });
}

