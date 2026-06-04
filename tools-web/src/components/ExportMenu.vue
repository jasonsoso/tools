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
    <button @click="toggle" class="px-3 py-2 text-sm bg-gray-100 border border-gray-300 rounded-md hover:bg-gray-200">
      导出 ▾
    </button>
    <div v-if="open" class="absolute right-0 top-full mt-1 bg-white border border-gray-200 rounded-md shadow-lg z-10 w-32">
      <button @click="handleExportMd" class="block w-full text-left px-3 py-2 text-sm hover:bg-gray-50">导出 .md</button>
      <button @click="handleExportHtml" class="block w-full text-left px-3 py-2 text-sm hover:bg-gray-50">导出 HTML</button>
    </div>
  </div>
</template>
