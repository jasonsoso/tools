<script setup lang="ts">
defineProps<{
  html: string
}>()
</script>

<template>
  <div class="prose prose-sm max-w-none prose-headings:text-zinc-900 prose-p:text-zinc-700 prose-a:text-indigo-600 prose-code:text-rose-600 prose-code:bg-rose-50 prose-code:px-1 prose-code:py-0.5 prose-code:rounded prose-code:text-xs prose-code:before:content-none prose-code:after:content-none prose-pre:bg-zinc-50 prose-pre:border prose-pre:border-black/[0.04] prose-pre:rounded-xl prose-blockquote:border-indigo-400 prose-blockquote:text-zinc-600 prose-blockquote:bg-indigo-50/40 prose-blockquote:rounded-r-lg prose-blockquote:py-1 prose-blockquote:px-4 prose-table:border prose-table:border-black/[0.06] prose-th:bg-zinc-50 prose-th:px-3 prose-th:py-2 prose-td:px-3 prose-td:py-2" v-html="html"></div>
</template>
