<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const username = ref('')
const email = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

async function handleRegister() {
  error.value = ''
  loading.value = true
  try {
    const result = await authStore.register({
      username: username.value,
      email: email.value,
      password: password.value
    })
    if (result.code !== 200) {
      error.value = result.message
    }
  } catch (e: any) {
    error.value = e.response?.data?.message || '注册失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="flex items-center justify-center min-h-[60vh]">
    <div class="w-full max-w-md bg-white rounded-lg shadow-md p-8">
      <h2 class="text-2xl font-bold text-center mb-6">注册</h2>
      <form @submit.prevent="handleRegister" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
          <input v-model="username" type="text" required minlength="3"
                 class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500" />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">邮箱</label>
          <input v-model="email" type="email" required
                 class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500" />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">密码</label>
          <input v-model="password" type="password" required minlength="6"
                 class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500" />
        </div>
        <div v-if="error" class="text-red-500 text-sm">{{ error }}</div>
        <button type="submit" :disabled="loading"
                class="w-full bg-blue-600 text-white py-2 rounded-md hover:bg-blue-700 disabled:opacity-50">
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>
      <p class="text-center text-sm text-gray-500 mt-4">
        已有账号？
        <router-link to="/login" class="text-blue-600 hover:underline">登录</router-link>
      </p>
    </div>
  </div>
</template>
