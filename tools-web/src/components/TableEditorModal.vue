<script setup lang="ts">
import { ref } from 'vue'

const emit = defineEmits<{
  confirm: [rows: number, cols: number]
  close: []
}>()

const rows = ref(3)
const cols = ref(3)
</script>

<template>
  <div class="fixed inset-0 bg-black/30 flex items-center justify-center z-50" @click.self="emit('close')">
    <div class="bg-white rounded-lg shadow-xl p-6 w-80">
      <h3 class="text-lg font-semibold mb-4">插入表格</h3>
      <div class="space-y-3">
        <div>
          <label class="block text-sm text-gray-600 mb-1">行数</label>
          <input v-model.number="rows" type="number" min="1" max="10"
                 class="w-full px-3 py-2 border border-gray-300 rounded-md" />
        </div>
        <div>
          <label class="block text-sm text-gray-600 mb-1">列数</label>
          <input v-model.number="cols" type="number" min="1" max="10"
                 class="w-full px-3 py-2 border border-gray-300 rounded-md" />
        </div>
      </div>
      <div class="flex justify-end gap-2 mt-4">
        <button @click="emit('close')" class="px-4 py-2 text-sm border border-gray-300 rounded-md hover:bg-gray-50">取消</button>
        <button @click="emit('confirm', rows, cols)" class="px-4 py-2 text-sm bg-blue-600 text-white rounded-md hover:bg-blue-700">确认</button>
      </div>
    </div>
  </div>
</template>
