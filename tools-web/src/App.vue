<script setup lang="ts">
import NavBar from '@/components/NavBar.vue'
</script>

<template>
  <div class="min-h-screen" style="background-color: #fafafa;">
    <NavBar />
    <main class="max-w-6xl mx-auto px-6 py-8">
      <router-view />
    </main>
  </div>
</template>
