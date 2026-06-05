<script setup lang="ts">
import ToolCard from '@/components/ToolCard.vue'
</script>

<template>
  <div class="py-16 flex flex-col items-center">
    <!-- Hero -->
    <div class="text-center mb-16 max-w-lg">
      <h1 class="text-4xl font-bold tracking-tight text-zinc-900 mb-4">
        在线工具包
      </h1>
      <p class="text-base text-zinc-500 leading-relaxed">
        简洁 · 高效 · 专注于创作
      </p>
    </div>

    <!-- Tool Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6 w-full max-w-2xl">
      <ToolCard
        title="Markdown 编辑器"
        description="实时预览、数学公式、表格编辑与文档导出"
        to="/markdown"
        icon="markdown"
      />
      <ToolCard
        title="JSON 格式化器"
        description="格式化、压缩、验证与树形视图"
        to="/json"
        icon="json"
      />
    </div>
  </div>
</template>
