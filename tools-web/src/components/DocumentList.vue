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
  <div class="w-52 flex-shrink-0 card flex flex-col overflow-hidden p-0">
    <!-- Header -->
    <div class="px-4 py-3 flex items-center justify-between border-b border-black/[0.04]">
      <span class="text-xs font-semibold text-zinc-500 uppercase tracking-wider">文档</span>
      <button
        @click="emit('new')"
        class="text-xs font-medium transition-colors hover:text-zinc-900"
        style="color: #6366f1;"
      >
        新建
      </button>
    </div>

    <!-- List -->
    <div class="flex-1 overflow-auto">
      <div v-if="loading" class="p-4 text-xs text-zinc-400">加载中...</div>
      <div v-else-if="documents.length === 0" class="p-4 text-xs text-zinc-400">暂无文档</div>
      <button
        v-for="doc in documents"
        :key="doc.id"
        @click="emit('select', doc.id)"
        :class="[
          'w-full text-left px-4 py-2.5 text-sm border-b border-black/[0.03] transition-colors truncate',
          currentId === doc.id
            ? 'bg-indigo-50/60 text-zinc-900 font-medium'
            : 'text-zinc-600 hover:bg-black/[0.02]'
        ]"
      >
        {{ doc.title || '未命名文档' }}
      </button>
    </div>
  </div>
</template>
