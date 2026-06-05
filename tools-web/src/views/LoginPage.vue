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
  <div class="flex items-center justify-center min-h-[65vh]">
    <div class="card w-full max-w-sm p-8">
      <!-- Header -->
      <div class="text-center mb-8">
        <h2 class="text-xl font-semibold text-zinc-900 tracking-tight">欢迎回来</h2>
        <p class="text-sm text-zinc-500 mt-1">登录以继续使用工具</p>
      </div>

      <!-- Form -->
      <form @submit.prevent="handleLogin" class="space-y-4">
        <div>
          <label class="block text-xs font-medium text-zinc-500 mb-1.5 ml-0.5">用户名</label>
          <input
            v-model="username"
            type="text"
            required
            class="input-field"
            placeholder="请输入用户名"
          />
        </div>
        <div>
          <label class="block text-xs font-medium text-zinc-500 mb-1.5 ml-0.5">密码</label>
          <input
            v-model="password"
            type="password"
            required
            class="input-field"
            placeholder="请输入密码"
          />
        </div>

        <!-- Error -->
        <div v-if="error" class="text-xs text-red-500 bg-red-50 px-3 py-2 rounded-lg">
          {{ error }}
        </div>

        <!-- Submit -->
        <button type="submit" :disabled="loading" class="btn-primary w-full">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>

      <!-- Footer -->
      <p class="text-center text-xs text-zinc-400 mt-6">
        还没有账号？
        <router-link
          :to="{ path: '/register', query: { redirect: $route.query.redirect } }"
          class="font-medium hover:text-zinc-700 transition-colors"
          style="color: #6366f1;"
        >
          注册
        </router-link>
      </p>
    </div>
  </div>
</template>
