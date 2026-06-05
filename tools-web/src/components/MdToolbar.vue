<script setup lang="ts">
const emit = defineEmits<{
  action: [name: string]
}>()

interface ToolBtn {
  label: string
  title: string
  action: string
  style?: string
}

const groups: { items: ToolBtn[] }[] = [
  {
    items: [
      { label: 'B', title: '加粗', action: 'bold', style: 'font-bold' },
      { label: 'I', title: '斜体', action: 'italic', style: 'italic' },
    ]
  },
  {
    items: [
      { label: 'H1', title: '一级标题', action: 'h1' },
      { label: 'H2', title: '二级标题', action: 'h2' },
      { label: 'H3', title: '三级标题', action: 'h3' },
    ]
  },
  {
    items: [
      { label: '🔗', title: '链接', action: 'link' },
      { label: '🖼', title: '图片', action: 'image' },
    ]
  },
  {
    items: [
      { label: '• ≡', title: '无序列表', action: 'ul' },
      { label: '1.', title: '有序列表', action: 'ol' },
    ]
  },
  {
    items: [
      { label: '<>', title: '代码块', action: 'code' },
      { label: '∑', title: '数学公式', action: 'formula' },
      { label: '⊞', title: '表格', action: 'table' },
    ]
  },
]
</script>

<template>
  <div class="flex items-center gap-0.5 flex-wrap bg-white border border-black/[0.05] px-1.5 py-1 rounded-xl">
    <template v-for="(group, gi) in groups" :key="gi">
      <div v-if="gi > 0" class="w-px h-4 bg-black/[0.06] mx-1" />
      <button
        v-for="btn in group.items"
        :key="btn.action"
        :title="btn.title"
        @click="emit('action', btn.action)"
        class="tool-btn"
        :class="btn.style"
      >
        {{ btn.label }}
      </button>
    </template>
  </div>
</template>
