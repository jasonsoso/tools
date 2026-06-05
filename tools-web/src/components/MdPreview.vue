<script setup lang="ts">
defineProps<{
  html: string
}>()
</script>

<template>
  <div
    class="prose prose-zinc max-w-none prose-headings:scroll-mt-4
      prose-headings:text-zinc-900 prose-headings:font-semibold
      prose-h1:text-2xl prose-h1:mt-6 prose-h1:mb-4
      prose-h2:text-xl prose-h2:mt-5 prose-h2:mb-3
      prose-h3:text-lg prose-h3:mt-4 prose-h3:mb-2
      prose-p:text-zinc-700 prose-p:leading-relaxed
      prose-a:text-indigo-600 prose-a:no-underline hover:prose-a:underline
      prose-strong:text-zinc-900
      prose-code:text-rose-600 prose-code:bg-rose-50/60 prose-code:px-1.5 prose-code:py-0.5 prose-code:rounded-md prose-code:text-sm prose-code:font-normal
      prose-code:before:content-none prose-code:after:content-none
      prose-pre:bg-[#f8f8f8] prose-pre:border prose-pre:border-black/[0.06] prose-pre:rounded-xl
      prose-pre:shadow-sm prose-pre:text-sm
      prose-blockquote:border-indigo-400 prose-blockquote:text-zinc-600 prose-blockquote:bg-indigo-50/40
      prose-blockquote:rounded-r-lg prose-blockquote:py-3 prose-blockquote:px-5 prose-blockquote:not-italic
      prose-table:border prose-table:border-black/[0.06] prose-table:rounded-lg
      prose-th:bg-zinc-50 prose-th:px-4 prose-th:py-2.5 prose-th:text-xs prose-th:font-semibold prose-th:text-zinc-500
      prose-td:px-4 prose-td:py-2.5 prose-td:text-sm
      prose-img:rounded-xl prose-img:shadow-sm
      prose-li:text-zinc-700 prose-li:marker:text-zinc-400
      prose-hr:border-zinc-200
    "
    v-html="html"
  ></div>
</template>
