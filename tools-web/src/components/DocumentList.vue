<script setup lang="ts">
import type { MarkdownDoc } from '@/api/markdown'

defineProps<{
  documents: MarkdownDoc[]
  currentId: number | null
  loading: boolean
}>()

const emit = defineEmits<{
  select: [id: number]
  new: []
}>()
</script>

<template>
  <div class="w-48 flex-shrink-0 bg-white border border-gray-200 rounded-lg overflow-hidden flex flex-col">
    <div class="p-3 border-b border-gray-200 flex items-center justify-between">
      <span class="text-sm font-semibold text-gray-700">文档列表</span>
      <button @click="emit('new')" class="text-blue-600 text-xs hover:text-blue-800">+ 新建</button>
    </div>
    <div class="flex-1 overflow-auto">
      <div v-if="loading" class="p-3 text-sm text-gray-400">加载中...</div>
      <div v-else-if="documents.length === 0" class="p-3 text-sm text-gray-400">暂无文档</div>
      <button
        v-for="doc in documents"
        :key="doc.id"
        @click="emit('select', doc.id)"
        :class="[
          'w-full text-left px-3 py-2 text-sm border-b border-gray-100 hover:bg-gray-50 truncate',
          currentId === doc.id ? 'bg-blue-50 text-blue-700' : 'text-gray-700'
        ]"
      >
        {{ doc.title || '未命名文档' }}
      </button>
    </div>
  </div>
</template>
