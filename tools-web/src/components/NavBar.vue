<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { useRoute } from 'vue-router'

const authStore = useAuthStore()
const route = useRoute()

function handleLogout() {
  authStore.logout()
}

const isActive = (path: string) => route.path.startsWith(path)
</script>

<template>
  <nav class="glass sticky top-0 z-40">
    <div class="max-w-6xl mx-auto px-6 h-14 flex items-center justify-between">
      <!-- Left: Logo + Nav -->
      <div class="flex items-center gap-8">
        <router-link to="/" class="text-base font-semibold tracking-tight text-zinc-900 no-underline">
          Toolbox
        </router-link>
        <div class="flex items-center gap-1">
          <router-link
            to="/markdown"
            :class="[
              'px-3 py-1.5 text-sm rounded-lg transition-colors no-underline',
              isActive('/markdown')
                ? 'bg-black/[0.04] text-zinc-900 font-medium'
                : 'text-zinc-500 hover:text-zinc-800 hover:bg-black/[0.02]'
            ]"
          >
            Markdown
          </router-link>
          <router-link
            to="/json"
            :class="[
              'px-3 py-1.5 text-sm rounded-lg transition-colors no-underline',
              isActive('/json')
                ? 'bg-black/[0.04] text-zinc-900 font-medium'
                : 'text-zinc-500 hover:text-zinc-800 hover:bg-black/[0.02]'
            ]"
          >
            JSON
          </router-link>
        </div>
      </div>

      <!-- Right: User -->
      <div class="flex items-center gap-3">
        <template v-if="authStore.isLoggedIn">
          <span class="text-xs text-zinc-400 font-medium">{{ authStore.username }}</span>
          <button @click="handleLogout" class="text-xs text-zinc-400 hover:text-zinc-700 transition-colors font-medium">
            退出
          </button>
        </template>
        <template v-else>
          <router-link to="/login" class="text-sm text-zinc-500 hover:text-zinc-800 transition-colors no-underline font-medium">
            登录
          </router-link>
          <router-link
            to="/register"
            class="text-sm px-4 py-1.5 rounded-lg text-white no-underline font-medium transition-all duration-200 hover:-translate-y-px hover:shadow-lg"
            style="background: linear-gradient(135deg, #6366f1, #8b5cf6); box-shadow: 0 2px 8px rgba(99,102,241,.18);"
          >
            注册
          </router-link>
        </template>
      </div>
    </div>
  </nav>
</template>
