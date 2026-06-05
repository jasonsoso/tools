<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const authStore = useAuthStore()
const username = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

async function handleLogin() {
  error.value = ''
  loading.value = true
  try {
    const redirect = route.query.redirect as string | undefined
    const result = await authStore.login({ username: username.value, password: password.value }, redirect)
    if (result.code !== 200) {
      error.value = result.message
    }
  } catch (e: any) {
    error.value = e.response?.data?.message || '登录失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="flex items-center justify-center min-h-[60vh]">
    <div class="w-full max-w-md bg-white rounded-lg shadow-md p-8">
      <h2 class="text-2xl font-bold text-center mb-6">登录</h2>
      <form @submit.prevent="handleLogin" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
          <input v-model="username" type="text" required
                 class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500" />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">密码</label>
          <input v-model="password" type="password" required
                 class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500" />
        </div>
        <div v-if="error" class="text-red-500 text-sm">{{ error }}</div>
        <button type="submit" :disabled="loading"
                class="w-full bg-blue-600 text-white py-2 rounded-md hover:bg-blue-700 disabled:opacity-50">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>
      <p class="text-center text-sm text-gray-500 mt-4">
        还没有账号？
        <router-link :to="{ path: '/register', query: { redirect: $route.query.redirect } }" class="text-blue-600 hover:underline">注册</router-link>
      </p>
    </div>
  </div>
</template>
