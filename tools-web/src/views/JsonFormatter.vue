<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useJsonStore } from '@/stores/json'
import RecordList from '@/components/RecordList.vue'
import JsonTree from '@/components/JsonTree.vue'
import { isValidJson, formatJson, compressJson, copyToClipboard } from '@/utils/json'

const route = useRoute()
const router = useRouter()
const store = useJsonStore()

const name = ref('')
const input = ref('')
const result = ref('')
const error = ref('')
const copied = ref(false)
const showTree = ref(true)

const recordId = computed(() => route.params.id ? Number(route.params.id) : null)

const isValid = computed(() => input.value.trim() === '' || isValidJson(input.value))

onMounted(() => {
  if (recordId.value) {
    store.loadRecord(recordId.value).then(() => {
      if (store.currentRecord) {
        name.value = store.currentRecord.name
        input.value = store.currentRecord.content
      }
    })
  }
  store.loadList()
})

function handleFormat() {
  error.value = ''
  if (!isValidJson(input.value)) {
    error.value = 'JSON 格式无效'
    result.value = ''
    return
  }
  result.value = formatJson(input.value)
}

function handleCompress() {
  error.value = ''
  if (!isValidJson(input.value)) {
    error.value = 'JSON 格式无效'
    result.value = ''
    return
  }
  result.value = compressJson(input.value)
}

async function handleCopy() {
  const text = result.value || input.value
  const ok = await copyToClipboard(text)
  if (ok) {
    copied.value = true
    setTimeout(() => copied.value = false, 2000)
  }
}

async function handleSave() {
  error.value = ''
  if (!name.value.trim()) {
    error.value = '记录名称不能为空'
    return
  }
  if (!isValidJson(input.value)) {
    error.value = '请先修正 JSON 格式错误再保存'
    return
  }
  try {
    if (recordId.value) {
      await store.update(recordId.value, { name: name.value, content: input.value })
    } else {
      const result = await store.create({ name: name.value, content: input.value })
      if (result.code === 200 && store.currentRecord) {
        router.replace(`/json/${store.currentRecord.id}`)
      }
    }
  } catch (e: any) {
    error.value = e.response?.data?.message || '保存失败'
  }
}

async function handleDelete() {
  if (!recordId.value) return
  await store.remove(recordId.value)
  router.push('/json')
  name.value = ''
  input.value = ''
  result.value = ''
}
</script>

<template>
  <div class="flex gap-4 h-[calc(100vh-6rem)]">
    <!-- Records Sidebar -->
    <RecordList
      :records="store.records"
      :current-id="recordId"
      :loading="store.loading"
      @select="(id: number) => router.push(`/json/${id}`)"
      @new="router.push('/json'); store.resetCurrent(); name = ''; input = ''; result = ''"
    />

    <!-- Main Content -->
    <div class="flex-1 flex flex-col gap-3 min-w-0">
      <!-- Header -->
      <div class="flex items-center gap-3">
        <input v-model="name" type="text" placeholder="记录名称（必填）"
               class="flex-1 text-lg font-semibold px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500" />
        <button @click="handleSave" class="px-4 py-2 bg-blue-600 text-white text-sm rounded-md hover:bg-blue-700">
          保存
        </button>
        <button v-if="recordId" @click="handleDelete" class="px-4 py-2 bg-red-600 text-white text-sm rounded-md hover:bg-red-700">
          删除
        </button>
      </div>

      <!-- Toolbar -->
      <div class="flex gap-2 flex-wrap">
        <button @click="handleFormat" class="px-3 py-1.5 text-sm bg-green-600 text-white rounded-md hover:bg-green-700">
          格式化
        </button>
        <button @click="handleCompress" class="px-3 py-1.5 text-sm bg-gray-600 text-white rounded-md hover:bg-gray-700">
          压缩
        </button>
        <button @click="handleCopy" class="px-3 py-1.5 text-sm bg-blue-500 text-white rounded-md hover:bg-blue-600">
          {{ copied ? '已复制 ✓' : '复制结果' }}
        </button>
        <label class="flex items-center gap-1 text-sm text-gray-600 ml-2">
          <input v-model="showTree" type="checkbox" /> 树形视图
        </label>
        <span v-if="!isValid && input.trim()" class="text-red-500 text-sm self-center">
          格式无效
        </span>
      </div>

      <div v-if="error" class="text-red-500 text-sm px-3 py-2 bg-red-50 rounded-md">{{ error }}</div>

      <!-- Input + Output Split -->
      <div class="flex-1 flex gap-3 min-h-0">
        <!-- Input -->
        <div class="flex-1 flex flex-col min-w-0">
          <label class="text-xs font-semibold text-gray-500 mb-1">输入</label>
          <textarea v-model="input"
                    class="flex-1 w-full px-3 py-2 border border-gray-300 rounded-md font-mono text-sm resize-none focus:ring-2 focus:ring-blue-500"
                    placeholder='{"key": "value"}'></textarea>
        </div>

        <!-- Output -->
        <div class="flex-1 flex flex-col min-w-0">
          <label class="text-xs font-semibold text-gray-500 mb-1">结果</label>
          <div class="flex-1 border border-gray-300 rounded-md overflow-auto bg-white p-3">
            <JsonTree v-if="showTree && result" :data="result" />
            <pre v-else-if="result" class="font-mono text-sm whitespace-pre-wrap">{{ result }}</pre>
            <p v-else class="text-gray-400 text-sm">点击格式化或压缩查看结果</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
