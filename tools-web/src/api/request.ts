import axios from 'axios'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401 || error.response?.status === 403) {
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      localStorage.removeItem('username')
      const currentPath = router.currentRoute.value.fullPath
      if (currentPath !== '/login' && currentPath !== '/register') {
        router.push({ path: '/login', query: { redirect: currentPath } })
      }
    }
    return Promise.reject(error)
  }
)

export default request
