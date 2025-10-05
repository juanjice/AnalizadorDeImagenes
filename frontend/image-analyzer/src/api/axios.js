import axios from 'axios'


const instance = axios.create({
    baseURL: (import.meta.env.VITE_API_BASE_URL || '').replace(/\/$/, ''),
})


instance.interceptors.response.use(
    (response) => response,(error) => {        
        const status = error?.response?.status
        const msg = error?.response?.data?.message || error?.message || 'Error de red'
        return Promise.reject(new Error(`HTTP ${status ?? '?'} — ${msg}`))
}
)
export default instance