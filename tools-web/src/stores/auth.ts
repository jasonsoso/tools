import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import router from '@/router'

interface AuthResult {
  token: string
  userId: number
  username: string
}

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

  function logout() {
    token.value = ''
    userId.value = 0
    username.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    router.push('/login')
  }

  return { token, userId, username, isLoggedIn, setAuth, logout }
})
