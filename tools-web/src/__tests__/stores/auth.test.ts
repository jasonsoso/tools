import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'

// Mock localStorage with a real implementation
const lsData: Record<string, string> = {}
const mockLocalStorage = {
  getItem: (key: string) => lsData[key] ?? null,
  setItem: (key: string, value: string) => { lsData[key] = value },
  removeItem: (key: string) => { delete lsData[key] },
}
vi.stubGlobal('localStorage', mockLocalStorage)

// Mock the auth API module
vi.mock('@/api/auth', () => ({
  authApi: {
    login: vi.fn(),
    register: vi.fn(),
  },
}))

// Mock vue-router
vi.mock('@/router', () => ({
  default: {
    push: vi.fn(),
  },
}))

import { useAuthStore } from '@/stores/auth'

describe('useAuthStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    Object.keys(lsData).forEach(k => delete lsData[k])
  })

  it('should have initial logged-out state', () => {
    const authStore = useAuthStore()
    expect(authStore.isLoggedIn).toBe(false)
    expect(authStore.token).toBe('')
    expect(authStore.userId).toBe(0)
    expect(authStore.username).toBe('')
  })

  it('should update state and localStorage on setAuth', () => {
    const authStore = useAuthStore()
    authStore.setAuth({ token: 'jwt.test.token', userId: 1, username: 'testuser' })
    expect(authStore.isLoggedIn).toBe(true)
    expect(authStore.token).toBe('jwt.test.token')
    expect(authStore.userId).toBe(1)
    expect(authStore.username).toBe('testuser')
    expect(mockLocalStorage.getItem('token')).toBe('jwt.test.token')
    expect(mockLocalStorage.getItem('userId')).toBe('1')
    expect(mockLocalStorage.getItem('username')).toBe('testuser')
  })

  it('should clear state and localStorage on logout', () => {
    const authStore = useAuthStore()
    authStore.setAuth({ token: 'jwt.test.token', userId: 1, username: 'testuser' })
    authStore.logout()
    expect(authStore.isLoggedIn).toBe(false)
    expect(authStore.token).toBe('')
    expect(authStore.userId).toBe(0)
    expect(authStore.username).toBe('')
    expect(mockLocalStorage.getItem('token')).toBeNull()
    expect(mockLocalStorage.getItem('userId')).toBeNull()
    expect(mockLocalStorage.getItem('username')).toBeNull()
  })

  it('should load token from localStorage on init', () => {
    mockLocalStorage.setItem('token', 'saved.token')
    mockLocalStorage.setItem('userId', '42')
    mockLocalStorage.setItem('username', 'saveduser')

    const authStore = useAuthStore()
    expect(authStore.isLoggedIn).toBe(true)
    expect(authStore.token).toBe('saved.token')
    expect(authStore.userId).toBe(42)
    expect(authStore.username).toBe('saveduser')
  })
})
