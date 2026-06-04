<script setup lang="ts">
const emit = defineEmits<{
  action: [name: string]
}>()

const buttons = [
  { label: 'B', title: '加粗', action: 'bold' },
  { label: 'I', title: '斜体', action: 'italic' },
  { label: 'H1', title: '一级标题', action: 'h1' },
  { label: 'H2', title: '二级标题', action: 'h2' },
  { label: 'H3', title: '三级标题', action: 'h3' },
  { label: '🔗', title: '链接', action: 'link' },
  { label: '🖼', title: '图片', action: 'image' },
  { label: '•≡', title: '无序列表', action: 'ul' },
  { label: '1.', title: '有序列表', action: 'ol' },
  { label: '<>', title: '代码块', action: 'code' },
  { label: '𝑓', title: '数学公式', action: 'formula' },
  { label: '⊞', title: '表格', action: 'table' },
]
</script>

<template>
  <div class="flex gap-1 flex-wrap bg-gray-100 px-2 py-1 rounded-md border border-gray-200">
    <button
      v-for="btn in buttons"
      :key="btn.action"
      :title="btn.title"
      @click="emit('action', btn.action)"
      class="px-2 py-1 text-xs font-medium bg-white border border-gray-300 rounded hover:bg-gray-50 text-gray-700"
    >
      {{ btn.label }}
    </button>
  </div>
</template>
