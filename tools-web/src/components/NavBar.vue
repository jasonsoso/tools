<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

function handleLogout() {
  authStore.logout()
}
</script>

<template>
  <nav class="bg-white shadow-sm border-b border-gray-200">
    <div class="max-w-7xl mx-auto px-4 h-14 flex items-center justify-between">
      <div class="flex items-center gap-6">
        <router-link to="/" class="text-lg font-bold text-blue-600 hover:text-blue-700">
          工具包
        </router-link>
        <router-link to="/markdown" class="text-gray-600 hover:text-gray-900 text-sm">
          Markdown 编辑器
        </router-link>
        <router-link to="/json" class="text-gray-600 hover:text-gray-900 text-sm">
          JSON 格式化
        </router-link>
      </div>
      <div class="flex items-center gap-4">
        <template v-if="authStore.isLoggedIn">
          <span class="text-sm text-gray-500">{{ authStore.username }}</span>
          <button @click="handleLogout" class="text-sm text-gray-500 hover:text-red-600">
            退出
          </button>
        </template>
        <template v-else>
          <router-link to="/login" class="text-sm text-gray-600 hover:text-blue-600">
            登录
          </router-link>
          <router-link to="/register" class="text-sm text-gray-600 hover:text-blue-600">
            注册
          </router-link>
        </template>
      </div>
    </div>
  </nav>
</template>
