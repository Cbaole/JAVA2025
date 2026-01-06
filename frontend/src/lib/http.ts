import axios from 'axios'
import type { ApiResponse } from '@/types/api'
import { useAuthStore } from '@/stores/auth'

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 15000
})

http.interceptors.request.use((config) => {
  const store = useAuthStore()
  if (store.token) {
    config.headers = config.headers ?? {}
    config.headers.Authorization = `Bearer ${store.token}`
  }
  return config
})

http.interceptors.response.use(
  (resp) => resp,
  (error) => {
    const store = useAuthStore()
    const status = error?.response?.status
    if (status === 401) {
      store.logout()
    }
    if (!error?.response) {
      const baseURL = error?.config?.baseURL || http.defaults.baseURL || ''
      return Promise.reject(new Error(`无法连接到后端服务：${baseURL || '-'}，请确认后端已启动且端口一致`))
    }
    const data = error?.response?.data
    if (data && typeof data === 'object' && data.success === false) {
      return Promise.reject(new Error(data.message || '请求失败'))
    }
    return Promise.reject(error)
  }
)

export async function unwrap<T>(p: Promise<{ data: ApiResponse<T> }>): Promise<T> {
  const resp = await p
  if (!resp.data.success) {
    throw new Error(resp.data.message || '请求失败')
  }
  return resp.data.data
}
