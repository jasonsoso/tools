<script setup lang="ts">
import { ref } from 'vue'
import { exportMarkdown, exportHtml } from '@/utils/markdown'

const props = defineProps<{
  content: string
  title: string
}>()

const open = ref(false)

function toggle() {
  open.value = !open.value
}

function handleExportMd() {
  exportMarkdown(props.content, props.title || 'document')
  open.value = false
}

function handleExportHtml() {
  exportHtml(props.content, props.title || 'document')
  open.value = false
}
</script>

<template>
  <div class="relative">
    <button @click="toggle" class="btn-secondary">
      导出
      <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" :class="open ? 'rotate-180' : ''" class="transition-transform duration-200">
        <path d="M6 9l6 6 6-6" />
      </svg>
    </button>
    <transition
      enter-active-class="transition-all duration-150 ease-out"
      enter-from-class="opacity-0 -translate-y-1 scale-95"
      enter-to-class="opacity-100 translate-y-0 scale-100"
      leave-active-class="transition-all duration-100 ease-in"
      leave-from-class="opacity-100 translate-y-0 scale-100"
      leave-to-class="opacity-0 -translate-y-1 scale-95"
    >
      <div
        v-if="open"
        class="absolute right-0 top-full mt-1.5 card p-1 z-10 w-36"
        style="border-radius: 12px;"
      >
        <button @click="handleExportMd" class="block w-full text-left px-3 py-2 text-sm text-zinc-600 hover:text-zinc-900 hover:bg-black/[0.03] rounded-lg transition-colors">
          导出 .md
        </button>
        <button @click="handleExportHtml" class="block w-full text-left px-3 py-2 text-sm text-zinc-600 hover:text-zinc-900 hover:bg-black/[0.03] rounded-lg transition-colors">
          导出 HTML
        </button>
      </div>
    </transition>
  </div>
</template>
