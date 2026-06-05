<script setup lang="ts">
const emit = defineEmits<{
  action: [name: string]
}>()

interface ToolBtn {
  label: string
  action: string
  icon: string // SVG path
  viewBox?: string
}

const groups: { items: ToolBtn[] }[] = [
  {
    items: [
      { label: '加粗', action: 'bold', icon: 'M6 4h8a4 4 0 0 1 0 8H6z M6 12h9a4 4 0 0 1 0 8H6z' },
      { label: '斜体', action: 'italic', icon: 'M10 5v12M14 5v12M6 5h11M7 17h9' },
      { label: '删除线', action: 'strikethrough', icon: 'M4 12h16M8 8c0-2 2-3 4-3s4 1 4 3-2 3-4 3M8 16c0 2 2 3 4 3s4-1 4-3' },
    ]
  },
  {
    items: [
      { label: '一级标题', action: 'h1', icon: 'M4 6h3v12H4zM12 6h3v5h-3zM12 14h3v4h-3zM8 11h7' },
      { label: '二级标题', action: 'h2', icon: 'M4 6h3v12H4zM12 6h3v5h-3zM12 14h3v4h-3zM8 11h7M8 17h7' },
      { label: '三级标题', action: 'h3', icon: 'M4 6h3v12H4zM12 6h3v5h-3zM12 14h3v4h-3zM8 11h7M8 17h7M8 14h7' },
    ]
  },
  {
    items: [
      { label: '链接', action: 'link', icon: 'M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71 M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71' },
      { label: '图片', action: 'image', icon: 'M3 7a4 4 0 0 1 4-4h10a4 4 0 0 1 4 4v10a4 4 0 0 1-4 4H7a4 4 0 0 1-4-4V7z M8 9a1 1 0 1 0 0-2 1 1 0 0 0 0 2z M21 15l-3.086-3.086a2 2 0 0 0-2.828 0L6 21' },
    ]
  },
  {
    items: [
      { label: '无序列表', action: 'ul', icon: 'M8 6h13M8 12h13M8 18h13M3 6h.01M3 12h.01M3 18h.01' },
      { label: '有序列表', action: 'ol', icon: 'M3 6h.01M3 12h.01M8 12h13M3 18h.01M8 6h13M8 18h13' },
      { label: '任务列表', action: 'task', icon: 'M9 11l2 2 4-4 M3 5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5z' },
    ]
  },
  {
    items: [
      { label: '引用', action: 'quote', icon: 'M3 12v6h6v-6H5c0-2 1-3 1-3M21 12v6h-6v-6h4c0-2-1-3-1-3' },
      { label: '代码块', action: 'code', icon: 'M16 18l6-6-6-6M8 6l-6 6 6 6' },
      { label: '公式', action: 'formula', icon: 'M7 4h10M7 20h10M7 12h10M4 4v16M20 4v16' },
      { label: '表格', action: 'table', icon: 'M3 7h18M3 12h18M3 17h18M8 3v18M16 3v18' },
      { label: '分割线', action: 'hr', icon: 'M4 12h16M8 6l4-4 4 4M8 18l4 4 4-4' },
    ]
  },
  {
    items: [
      { label: '全屏', action: 'fullscreen', icon: 'M8 3H5a2 2 0 0 0-2 2v3m18 0V5a2 2 0 0 0-2-2h-3m0 18h3a2 2 0 0 0 2-2v-3M3 16v3a2 2 0 0 0 2 2h3' },
    ]
  },
]
</script>

<template>
  <div class="flex items-center gap-0.5 bg-white border border-black/[0.05] px-1.5 py-1 rounded-xl overflow-x-auto">
    <template v-for="(group, gi) in groups" :key="gi">
      <div v-if="gi > 0" class="w-px h-5 bg-black/[0.06] mx-1 flex-shrink-0" />
      <button
        v-for="btn in group.items"
        :key="btn.action"
        :title="btn.label"
        @click="emit('action', btn.action)"
        class="tool-btn flex-shrink-0"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="16"
          height="16"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <path :d="btn.icon" />
        </svg>
      </button>
    </template>
  </div>
</template>
