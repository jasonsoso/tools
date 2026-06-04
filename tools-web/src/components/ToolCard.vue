<script setup lang="ts">
defineProps<{
  title: string
  description: string
  to: string
  icon: string
}>()
</script>

<template>
  <router-link :to="to" class="block bg-white rounded-lg shadow-md p-8 hover:shadow-lg transition-shadow border border-gray-200 hover:border-blue-300">
    <div class="text-4xl mb-4">{{ icon }}</div>
    <h3 class="text-xl font-semibold text-gray-800 mb-2">{{ title }}</h3>
    <p class="text-gray-500 text-sm">{{ description }}</p>
  </router-link>
</template>
