<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
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
    const redirect = route.query.redirect as string | undefined
    const result = await authStore.register({
      username: username.value,
      email: email.value,
      password: password.value
    }, redirect)
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
  <div class="flex items-center justify-center min-h-[65vh]">
    <div class="card w-full max-w-sm p-8">
      <!-- Header -->
      <div class="text-center mb-8">
        <h2 class="text-xl font-semibold text-zinc-900 tracking-tight">创建账号</h2>
        <p class="text-sm text-zinc-500 mt-1">注册后即可使用全部工具</p>
      </div>

      <!-- Form -->
      <form @submit.prevent="handleRegister" class="space-y-4">
        <div>
          <label class="block text-xs font-medium text-zinc-500 mb-1.5 ml-0.5">用户名</label>
          <input
            v-model="username"
            type="text"
            required
            minlength="3"
            class="input-field"
            placeholder="至少 3 个字符"
          />
        </div>
        <div>
          <label class="block text-xs font-medium text-zinc-500 mb-1.5 ml-0.5">邮箱</label>
          <input
            v-model="email"
            type="email"
            required
            class="input-field"
            placeholder="your@email.com"
          />
        </div>
        <div>
          <label class="block text-xs font-medium text-zinc-500 mb-1.5 ml-0.5">密码</label>
          <input
            v-model="password"
            type="password"
            required
            minlength="6"
            class="input-field"
            placeholder="至少 6 个字符"
          />
        </div>

        <!-- Error -->
        <div v-if="error" class="text-xs text-red-500 bg-red-50 px-3 py-2 rounded-lg">
          {{ error }}
        </div>

        <!-- Submit -->
        <button type="submit" :disabled="loading" class="btn-primary w-full">
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>

      <!-- Footer -->
      <p class="text-center text-xs text-zinc-400 mt-6">
        已有账号？
        <router-link
          :to="{ path: '/login', query: { redirect: $route.query.redirect } }"
          class="font-medium hover:text-zinc-700 transition-colors"
          style="color: #6366f1;"
        >
          登录
        </router-link>
      </p>
    </div>
  </div>
</template>
