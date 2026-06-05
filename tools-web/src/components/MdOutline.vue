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
  <div class="card h-full overflow-auto p-3">
    <h4 class="text-[10px] font-semibold text-zinc-400 uppercase tracking-widest mb-3 ml-1">
      目录
    </h4>
    <div v-if="items.length === 0" class="text-xs text-zinc-400 px-1">暂无标题</div>
    <ul class="space-y-0.5">
      <li
        v-for="(item, i) in items"
        :key="i"
        :style="{ paddingLeft: `${(item.level - 1) * 10}px` }"
      >
        <button
          @click="scrollToId(item.id)"
          class="text-xs text-zinc-500 hover:text-zinc-900 text-left w-full truncate py-0.5 px-1 rounded-md transition-colors hover:bg-black/[0.03]"
        >
          {{ item.text }}
        </button>
      </li>
    </ul>
  </div>
</template>
