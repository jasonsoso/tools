<script setup lang="ts">
import type { OutlineItem } from '@/utils/markdown'

defineProps<{
  items: OutlineItem[]
}>()

function scrollToId(id: string) {
  const el = document.getElementById(id)
  if (el) {
    el.scrollIntoView({ behavior: 'smooth' })
  }
}
</script>

<template>
  <div class="bg-white border border-gray-200 rounded-lg p-3 h-full overflow-auto">
    <h4 class="text-xs font-semibold text-gray-500 uppercase mb-2">目录大纲</h4>
    <div v-if="items.length === 0" class="text-xs text-gray-400">暂无标题</div>
    <ul class="space-y-1">
      <li v-for="(item, i) in items" :key="i"
          :style="{ paddingLeft: `${(item.level - 1) * 12}px` }">
        <button @click="scrollToId(item.id)"
                class="text-xs text-gray-600 hover:text-blue-600 text-left w-full truncate">
          {{ item.text }}
        </button>
      </li>
    </ul>
  </div>
</template>
