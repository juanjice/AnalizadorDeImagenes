import api from './axios'


export async function postImage(file) {
    const path = import.meta.env.VITE_ANALYZE_PATH + '/api/analyze'
    const formData = new FormData()
    formData.append('file', file)

    const { data } = await api.post(path, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    })
    return data
}

export async function getImageById(id) {
  const path = (import.meta.env.VITE_ANALYZE_PATH || '') + `/api/${id}`
  const { data } = await api.get(path)
  return data
}