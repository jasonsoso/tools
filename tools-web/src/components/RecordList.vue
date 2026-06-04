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
  <div class="w-48 flex-shrink-0 bg-white border border-gray-200 rounded-lg overflow-hidden flex flex-col">
    <div class="p-3 border-b border-gray-200 flex items-center justify-between">
      <span class="text-sm font-semibold text-gray-700">记录列表</span>
      <button @click="emit('new')" class="text-blue-600 text-xs hover:text-blue-800">+ 新建</button>
    </div>
    <div class="flex-1 overflow-auto">
      <div v-if="loading" class="p-3 text-sm text-gray-400">加载中...</div>
      <div v-else-if="records.length === 0" class="p-3 text-sm text-gray-400">暂无记录</div>
      <button
        v-for="rec in records"
        :key="rec.id"
        @click="emit('select', rec.id)"
        :class="[
          'w-full text-left px-3 py-2 text-sm border-b border-gray-100 hover:bg-gray-50 truncate',
          currentId === rec.id ? 'bg-blue-50 text-blue-700' : 'text-gray-700'
        ]"
      >
        {{ rec.name || '未命名记录' }}
      </button>
    </div>
  </div>
</template>
