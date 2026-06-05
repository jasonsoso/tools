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
  <div
    class="fixed inset-0 z-50 flex items-center justify-center"
    style="background: rgba(0,0,0,.25); backdrop-filter: blur(4px);"
    @click.self="emit('close')"
  >
    <div class="card p-6 w-80" style="border-radius: 20px;">
      <h3 class="text-base font-semibold text-zinc-900 mb-5 tracking-tight">插入表格</h3>
      <div class="space-y-4">
        <div>
          <label class="block text-xs font-medium text-zinc-500 mb-1.5 ml-0.5">行数</label>
          <input
            v-model.number="rows"
            type="number"
            min="1"
            max="10"
            class="input-field"
          />
        </div>
        <div>
          <label class="block text-xs font-medium text-zinc-500 mb-1.5 ml-0.5">列数</label>
          <input
            v-model.number="cols"
            type="number"
            min="1"
            max="10"
            class="input-field"
          />
        </div>
      </div>
      <div class="flex justify-end gap-2 mt-5">
        <button @click="emit('close')" class="btn-secondary">取消</button>
        <button @click="emit('confirm', rows, cols)" class="btn-primary">确认</button>
      </div>
    </div>
  </div>
</template>
