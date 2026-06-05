<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
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
const showTree = ref(false)

// ---- Collapsible sidebar ----
const LS_KEY_JSON_LEFT = 'json-editor-left-collapsed'
const leftCollapsed = ref(localStorage.getItem(LS_KEY_JSON_LEFT) === 'true')

function toggleLeft() {
  leftCollapsed.value = !leftCollapsed.value
  localStorage.setItem(LS_KEY_JSON_LEFT, String(leftCollapsed.value))
}

const recordCount = computed(() => store.records.length)

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

// 监听记录 ID 变化，在已编辑/新建记录之间切换时重新加载
watch(recordId, (newId) => {
  if (newId) {
    store.loadRecord(newId).then(() => {
      if (store.currentRecord) {
        name.value = store.currentRecord.name
        input.value = store.currentRecord.content
        result.value = ''
        error.value = ''
      }
    })
  } else {
    // 切换到新建模式：清空表单
    name.value = ''
    input.value = ''
    result.value = ''
    error.value = ''
  }
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
  <div class="flex gap-5 h-[calc(100vh-7rem)]">
    <!-- Left sidebar — Record list -->
    <!-- Narrow strip (collapsed) -->
    <div
      v-if="leftCollapsed"
      class="sidebar-strip"
      @click="toggleLeft"
      title="展开记录列表"
    >
      <span class="sidebar-strip-icon">📋</span>
      <span class="sidebar-strip-count">{{ recordCount }}</span>
      <span class="sidebar-strip-label">记录</span>
    </div>

    <!-- Full panel (expanded) -->
    <div v-else class="sidebar-panel" style="width: 208px;">
      <RecordList
        :records="store.records"
        :current-id="recordId"
        :loading="store.loading"
        @select="(id: number) => router.push(`/json/${id}`)"
        @new="router.push('/json'); store.resetCurrent(); name = ''; input = ''; result = ''"
      />
    </div>

    <!-- Left collapse toggle button -->
    <button
      class="collapse-toggle"
      @click="toggleLeft"
      :title="leftCollapsed ? '展开记录列表' : '折叠记录列表'"
    >
      {{ leftCollapsed ? '▶' : '◀' }}
    </button>

    <!-- Main -->
    <div class="flex-1 flex flex-col gap-3 min-w-0">
      <!-- Header row -->
      <div class="flex items-center gap-3">
        <input
          v-model="name"
          type="text"
          placeholder="记录名称（必填）"
          class="flex-1 text-base font-medium px-4 py-2.5 bg-white border border-black/[0.06] rounded-xl text-zinc-900 placeholder:text-zinc-400 outline-none transition-all duration-200 focus:border-violet-300 focus:shadow-[0_0_0_3px_rgba(139,92,246,.06)]"
        />
        <button @click="handleSave" class="btn-primary">
          保存
        </button>
        <button v-if="recordId" @click="handleDelete" class="btn-danger">
          删除
        </button>
      </div>

      <!-- Toolbar -->
      <div class="flex items-center gap-2 flex-wrap">
        <button @click="handleFormat" class="btn-secondary">
          格式化
        </button>
        <button @click="handleCompress" class="btn-secondary">
          压缩
        </button>
        <div class="w-px h-4 bg-black/[0.08] mx-1" />
        <button @click="handleCopy" class="btn-secondary">
          {{ copied ? '已复制 ✓' : '复制结果' }}
        </button>
        <div class="w-px h-4 bg-black/[0.08] mx-1" />
        <label class="flex items-center gap-1.5 text-xs text-zinc-500 cursor-pointer select-none ml-1">
          <input v-model="showTree" type="checkbox" class="accent-indigo-500" />
          树形视图
        </label>
        <span v-if="!isValid && input.trim()" class="text-xs text-red-500 font-medium ml-auto">
          格式无效
        </span>
      </div>

      <!-- Error -->
      <div v-if="error" class="text-xs text-red-600 bg-red-50 px-3 py-2 rounded-lg">
        {{ error }}
      </div>

      <!-- Input + Output split -->
      <div class="flex-1 flex gap-4 min-h-0">
        <!-- Input -->
        <div class="flex-1 flex flex-col min-w-0">
          <label class="text-[10px] font-semibold text-zinc-400 uppercase tracking-widest mb-1.5 ml-1">
            输入
          </label>
          <textarea
            v-model="input"
            class="flex-1 w-full px-4 py-3 card font-mono text-sm resize-none outline-none transition-all duration-200 focus:border-violet-300 focus:shadow-[0_0_0_3px_rgba(139,92,246,.06)]"
            placeholder='{"key": "value"}'
            style="border-radius: 16px;"
          ></textarea>
        </div>

        <!-- Output -->
        <div class="flex-1 flex flex-col min-w-0">
          <label class="text-[10px] font-semibold text-zinc-400 uppercase tracking-widest mb-1.5 ml-1">
            结果
          </label>
          <div
            class="flex-1 card overflow-auto p-4"
            style="border-radius: 16px;"
          >
            <JsonTree v-if="showTree && result" :data="result" />
            <pre v-else-if="result" class="font-mono text-sm text-zinc-800 whitespace-pre-wrap">{{ result }}</pre>
            <p v-else class="text-sm text-zinc-400">点击格式化或压缩查看结果</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
