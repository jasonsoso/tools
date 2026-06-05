<script setup lang="ts">
defineProps<{
  title: string
  description: string
  to: string
  icon: 'markdown' | 'json'
}>()
</script>

<template>
  <router-link :to="to" class="card group block p-8 no-underline">
    <!-- Icon -->
    <div class="w-11 h-11 rounded-xl flex items-center justify-center mb-5 transition-colors duration-300"
         :class="icon === 'markdown'
           ? 'bg-indigo-50 text-indigo-500 group-hover:bg-indigo-100'
           : 'bg-violet-50 text-violet-500 group-hover:bg-violet-100'">

      <!-- Markdown icon (M↓) -->
      <svg v-if="icon === 'markdown'" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
        <path d="M4 6h16M4 12h10M4 18h7" />
        <path d="M15 13l3 3 3-3" />
        <path d="M18 10v6" />
      </svg>

      <!-- JSON icon ({ }) -->
      <svg v-else width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
        <path d="M8 3H7a2 2 0 0 0-2 2v3" />
        <path d="M16 3h1a2 2 0 0 1 2 2v3" />
        <path d="M8 21H7a2 2 0 0 1-2-2v-3" />
        <path d="M16 21h1a2 2 0 0 0 2-2v-3" />
        <path d="M9 8v1" />
        <path d="M15 8v1" />
        <path d="M9 15v1" />
        <path d="M15 15v1" />
      </svg>
    </div>

    <!-- Text -->
    <h3 class="text-lg font-semibold text-zinc-900 mb-2 tracking-tight">{{ title }}</h3>
    <p class="text-sm text-zinc-500 leading-relaxed">{{ description }}</p>

    <!-- Arrow -->
    <div class="mt-5 flex items-center gap-1 text-xs font-medium transition-all duration-300"
         :class="icon === 'markdown' ? 'text-indigo-500' : 'text-violet-500'">
      <span class="opacity-0 -translate-x-2 transition-all duration-300 group-hover:opacity-100 group-hover:translate-x-0">打开</span>
      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="transition-transform duration-300 group-hover:translate-x-1">
        <path d="M5 12h14M12 5l7 7-7 7" />
      </svg>
    </div>
  </router-link>
</template>
