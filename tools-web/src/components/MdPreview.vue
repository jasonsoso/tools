<script setup lang="ts">
defineProps<{
  html: string
}>()
</script>

<template>
  <div class="prose prose-sm max-w-none" v-html="html"></div>
</template>
