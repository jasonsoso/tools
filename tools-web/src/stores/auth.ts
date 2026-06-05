import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi, type AuthResult } from '@/api/auth'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref(Number(localStorage.getItem('userId')) || 0)
  const username = ref(localStorage.getItem('username') || '')

  const isLoggedIn = computed(() => !!token.value)

  function setAuth(result: AuthResult) {
    token.value = result.token
    userId.value = result.userId
    username.value = result.username
    localStorage.setItem('token', result.token)
    localStorage.setItem('userId', String(result.userId))
    localStorage.setItem('username', result.username)
  }

  async function login(params: { username: string; password: string }, redirect?: string) {
    const res = await authApi.login(params)
    if (res.data.code === 200) {
      setAuth(res.data.data)
      router.push(redirect || '/')
    }
    return res.data
  }

  async function register(params: { username: string; email: string; password: string }, redirect?: string) {
    const res = await authApi.register(params)
    if (res.data.code === 200) {
      setAuth(res.data.data)
      router.push(redirect || '/')
    }
    return res.data
  }

  function logout() {
    token.value = ''
    userId.value = 0
    username.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    router.push('/')
  }

  return { token, userId, username, isLoggedIn, setAuth, login, register, logout }
})
