<script setup lang="ts">
import ToolCard from '@/components/ToolCard.vue'
</script>

<template>
  <div class="py-12">
    <h1 class="text-3xl font-bold text-center mb-10 text-gray-800">在线工具包</h1>
    <div class="grid grid-cols-1 md:grid-cols-2 gap-8 max-w-2xl mx-auto">
      <ToolCard
        title="Markdown 编辑器"
        description="在线编辑 Markdown 文档，支持实时预览、数学公式、表格编辑和文档导出。"
        to="/markdown"
        icon="📝"
      />
      <ToolCard
        title="JSON 格式化器"
        description="格式化、压缩和验证 JSON 数据，支持树形视图和记录管理。"
        to="/json"
        icon="🔧"
      />
    </div>
  </div>
</template>
