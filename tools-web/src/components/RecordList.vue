<script setup lang="ts">
import type { JsonRecord } from '@/api/json'

defineProps<{
  records: JsonRecord[]
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
      <span class="text-xs font-semibold text-zinc-500 uppercase tracking-wider">记录</span>
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
      <div v-else-if="records.length === 0" class="p-4 text-xs text-zinc-400">暂无记录</div>
      <button
        v-for="rec in records"
        :key="rec.id"
        @click="emit('select', rec.id)"
        :class="[
          'w-full text-left px-4 py-2.5 text-sm border-b border-black/[0.03] transition-colors truncate',
          currentId === rec.id
            ? 'bg-violet-50/60 text-zinc-900 font-medium'
            : 'text-zinc-600 hover:bg-black/[0.02]'
        ]"
      >
        {{ rec.name || '未命名记录' }}
      </button>
    </div>
  </div>
</template>
