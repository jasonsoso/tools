<script setup lang="ts">
import { ref, watch, onMounted, onBeforeUnmount, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMarkdownStore } from '@/stores/markdown'
import DocumentList from '@/components/DocumentList.vue'
import MdToolbar from '@/components/MdToolbar.vue'
import MdPreview from '@/components/MdPreview.vue'
import MdOutline from '@/components/MdOutline.vue'
import ExportMenu from '@/components/ExportMenu.vue'
import TableEditorModal from '@/components/TableEditorModal.vue'
import { renderMarkdown, extractOutline, type OutlineItem } from '@/utils/markdown'
import { basicSetup } from 'codemirror'
import { EditorView, keymap } from '@codemirror/view'
import { EditorState } from '@codemirror/state'
import { markdown } from '@codemirror/lang-markdown'
import { indentWithTab } from '@codemirror/commands'

const route = useRoute()
const router = useRouter()
const store = useMarkdownStore()

const title = ref('')
const content = ref('')
const htmlPreview = ref('')
const outline = ref<OutlineItem[]>([])
const saving = ref(false)
const saveError = ref('')
const showTableModal = ref(false)
const viewMode = ref<'edit' | 'split' | 'preview'>('split')

// ---- Collapsible sidebars ----
const LS_KEY_LEFT = 'md-editor-left-collapsed'
const LS_KEY_RIGHT = 'md-editor-right-collapsed'

const leftCollapsed = ref(localStorage.getItem(LS_KEY_LEFT) === 'true')
const rightCollapsed = ref(localStorage.getItem(LS_KEY_RIGHT) === 'true')

function toggleLeft() {
  leftCollapsed.value = !leftCollapsed.value
  localStorage.setItem(LS_KEY_LEFT, String(leftCollapsed.value))
}

function toggleRight() {
  rightCollapsed.value = !rightCollapsed.value
  localStorage.setItem(LS_KEY_RIGHT, String(rightCollapsed.value))
}

const docCount = computed(() => store.documents.length)
const outlineCount = computed(() => outline.value.length)

// ---- Fullscreen mode ----
const isFullscreen = ref(false)
const fullscreenRoot = ref<HTMLDivElement>()

function toggleFullscreen() {
  if (!fullscreenRoot.value) return
  if (isFullscreen.value) {
    document.exitFullscreen()
  } else {
    fullscreenRoot.value.requestFullscreen().catch(() => {
      // Browser denied fullscreen — ignore
    })
  }
}

function syncFullscreenState() {
  isFullscreen.value = !!document.fullscreenElement
}

let editorView: EditorView | null = null
const editorContainer = ref<HTMLDivElement>()
const previewScrollRef = ref<HTMLDivElement>()

const docId = computed(() => route.params.id ? Number(route.params.id) : null)

// ---- Scroll sync between editor and preview ----
let syncingScroll = false

function setupScrollSync() {
  if (!editorView || !previewScrollRef.value) return

  const editorScroll = editorView.scrollDOM
  const previewScroll = previewScrollRef.value

  function syncEditorToPreview() {
    if (syncingScroll) return
    syncingScroll = true
    const ratio = editorScroll.scrollTop / (editorScroll.scrollHeight - editorScroll.clientHeight)
    previewScroll.scrollTop = ratio * (previewScroll.scrollHeight - previewScroll.clientHeight)
    requestAnimationFrame(() => { syncingScroll = false })
  }

  function syncPreviewToEditor() {
    if (syncingScroll) return
    syncingScroll = true
    const ratio = previewScroll.scrollTop / (previewScroll.scrollHeight - previewScroll.clientHeight)
    editorScroll.scrollTop = ratio * (editorScroll.scrollHeight - editorScroll.clientHeight)
    requestAnimationFrame(() => { syncingScroll = false })
  }

  editorScroll.addEventListener('scroll', syncEditorToPreview, { passive: true })
  previewScroll.addEventListener('scroll', syncPreviewToEditor, { passive: true })
}

const viewModeLabels: Record<string, { label: string; icon: string }> = {
  edit: { label: '编辑', icon: '📝' },
  split: { label: '分屏', icon: '⬌' },
  preview: { label: '预览', icon: '👁' },
}

/** 加载指定文档到编辑器 */
function loadDocToEditor(id: number) {
  store.loadDocument(id).then(() => {
    if (store.currentDoc) {
      title.value = store.currentDoc.title
      content.value = store.currentDoc.content || ''
      if (editorView) {
        editorView.dispatch({
          changes: { from: 0, to: editorView.state.doc.length, insert: content.value }
        })
      }
      updatePreview()
    }
  })
}

function insertMarkdown(syntax: string) {
  if (!editorView) return
  const selection = editorView.state.selection.main
  const text = editorView.state.sliceDoc(selection.from, selection.to)
  editorView.dispatch({
    changes: { from: selection.from, to: selection.to, insert: syntax.replace('$1', text) }
  })
  editorView.focus()
}

function handleToolbarAction(action: string) {
  switch (action) {
    case 'bold': insertMarkdown('**$1**'); break
    case 'italic': insertMarkdown('*$1*'); break
    case 'strikethrough': insertMarkdown('~~$1~~'); break
    case 'h1': insertMarkdown('# $1'); break
    case 'h2': insertMarkdown('## $1'); break
    case 'h3': insertMarkdown('### $1'); break
    case 'link': insertMarkdown('[$1](url)'); break
    case 'image': insertMarkdown('![$1](url)'); break
    case 'ul': insertMarkdown('- $1'); break
    case 'ol': insertMarkdown('1. $1'); break
    case 'task': insertMarkdown('- [ ] $1'); break
    case 'quote': insertMarkdown('> $1'); break
    case 'code': insertMarkdown('```\n$1\n```'); break
    case 'formula': insertMarkdown('$$1$'); break
    case 'table': showTableModal.value = true; break
    case 'hr': insertMarkdown('\n---\n'); break
    case 'fullscreen': toggleFullscreen(); break
  }
}

function handleTableInsert(rows: number, cols: number) {
  let table = '|' + ' Header |'.repeat(cols) + '\n'
  table += '|' + ' --- |'.repeat(cols) + '\n'
  for (let i = 0; i < rows; i++) {
    table += '|' + ' Cell |'.repeat(cols) + '\n'
  }
  insertMarkdown(table)
  showTableModal.value = false
}

function updatePreview() {
  htmlPreview.value = renderMarkdown(content.value)
  outline.value = extractOutline(content.value)
}

onMounted(() => {
  if (editorContainer.value) {
    const updateListener = EditorView.updateListener.of((update) => {
      if (update.docChanged) {
        content.value = update.state.doc.toString()
        updatePreview()
      }
    })

    editorView = new EditorView({
      state: EditorState.create({
        doc: content.value,
        extensions: [
          basicSetup,
          markdown(),
          keymap.of([indentWithTab]),
          EditorView.lineWrapping,
          updateListener
        ]
      }),
      parent: editorContainer.value
    })

    // Setup scroll sync after editor is mounted
    requestAnimationFrame(() => setupScrollSync())
  }

  if (docId.value) {
    loadDocToEditor(docId.value)
  }
  store.loadList()

  document.addEventListener('keydown', handleKeydown)
  document.addEventListener('fullscreenchange', syncFullscreenState)
})

onBeforeUnmount(() => {
  document.removeEventListener('keydown', handleKeydown)
  document.removeEventListener('fullscreenchange', syncFullscreenState)
})

watch(content, () => {
  updatePreview()
})

// 监听文档 ID 变化，在已编辑和新建文档之间切换时重新加载
watch(docId, (newId) => {
  if (newId) {
    loadDocToEditor(newId)
  } else {
    title.value = ''
    content.value = ''
    if (editorView) {
      editorView.dispatch({
        changes: { from: 0, to: editorView.state.doc.length, insert: '' }
      })
    }
    updatePreview()
  }
})

async function handleSave() {
  saveError.value = ''
  if (!title.value.trim()) {
    saveError.value = '文档标题不能为空'
    return
  }
  saving.value = true
  try {
    if (docId.value) {
      await store.update(docId.value, { title: title.value, content: content.value })
    } else {
      const result = await store.create({ title: title.value, content: content.value })
      if (result.code === 200 && store.currentDoc) {
        router.replace(`/markdown/${store.currentDoc.id}`)
      }
    }
  } finally {
    saving.value = false
  }
}

function handleKeydown(e: KeyboardEvent) {
  if ((e.ctrlKey || e.metaKey) && e.key === 's') {
    e.preventDefault()
    handleSave()
  }
}

function cycleViewMode() {
  const modes: Array<'edit' | 'split' | 'preview'> = ['edit', 'split', 'preview']
  const i = modes.indexOf(viewMode.value)
  viewMode.value = modes[(i + 1) % 3]
}
</script>

<template>
  <div class="flex gap-5 h-[calc(100vh-7rem)]">
    <!-- Left sidebar — Document list -->
    <!-- Narrow strip (collapsed) -->
    <div
      v-if="leftCollapsed"
      class="sidebar-strip"
      @click="toggleLeft"
      title="展开文档列表"
    >
      <span class="sidebar-strip-icon">📄</span>
      <span class="sidebar-strip-count">{{ docCount }}</span>
      <span class="sidebar-strip-label">文档</span>
    </div>

    <!-- Full panel (expanded) -->
    <div v-else class="sidebar-panel" style="width: 208px;">
      <DocumentList
        :documents="store.documents"
        :current-id="docId"
        :loading="store.loading"
        @select="(id: number) => router.push(`/markdown/${id}`)"
        @new="router.push('/markdown'); store.resetCurrent(); title = ''; content = ''; updatePreview()"
      />
    </div>

    <!-- Left collapse toggle button -->
    <button
      class="collapse-toggle"
      @click="toggleLeft"
      :title="leftCollapsed ? '展开文档列表' : '折叠文档列表'"
    >
      {{ leftCollapsed ? '▶' : '◀' }}
    </button>

    <!-- Main -->
    <div class="flex-1 flex flex-col gap-3 min-w-0">
      <!-- Header row -->
      <div class="flex items-center gap-3">
        <input
          v-model="title"
          type="text"
          placeholder="文档标题（必填）"
          class="flex-1 text-base font-medium px-4 py-2.5 bg-white border border-black/[0.06] rounded-xl text-zinc-900 placeholder:text-zinc-400 outline-none transition-all duration-200 focus:border-indigo-300 focus:shadow-[0_0_0_3px_rgba(99,102,241,.06)]"
        />
        <span v-if="saveError" class="text-xs text-red-500 whitespace-nowrap">{{ saveError }}</span>

        <!-- View mode toggle -->
        <button
          @click="cycleViewMode"
          class="btn-secondary text-xs"
          :title="viewModeLabels[viewMode].label + '模式'"
        >
          {{ viewModeLabels[viewMode].icon }}
          {{ viewModeLabels[viewMode].label }}
        </button>

        <button @click="handleSave" :disabled="saving" class="btn-primary">
          {{ saving ? '保存中...' : '保存' }}
        </button>
        <ExportMenu :content="content" :title="title" />
      </div>

      <!-- Toolbar -->
      <MdToolbar @action="handleToolbarAction" />

      <!-- Editor + Preview split -->
      <div class="flex-1 flex gap-4 min-h-0">
        <!-- Editor -->
        <div
          v-show="viewMode !== 'preview'"
          :class="[
            'card overflow-hidden p-0 min-h-0',
            viewMode === 'edit' ? 'flex-1' : 'flex-1'
          ]"
        >
          <div ref="editorContainer" class="h-full"></div>
        </div>

        <!-- Preview -->
        <div
          v-show="viewMode !== 'edit'"
          ref="previewScrollRef"
          :class="[
            'card overflow-auto p-6 min-h-0',
            viewMode === 'preview' ? 'flex-1' : 'flex-1'
          ]"
        >
          <MdPreview :html="htmlPreview" />
        </div>

        <!-- Right collapse toggle button -->
        <button
          class="collapse-toggle"
          @click="toggleRight"
          :title="rightCollapsed ? '展开目录' : '折叠目录'"
        >
          {{ rightCollapsed ? '◀' : '▶' }}
        </button>
      </div>
    </div>

    <!-- Right sidebar — Outline -->
    <!-- Narrow strip (collapsed) -->
    <div
      v-if="rightCollapsed"
      class="sidebar-strip"
      @click="toggleRight"
      title="展开目录"
    >
      <span class="sidebar-strip-icon">📑</span>
      <span class="sidebar-strip-count">{{ outlineCount }}</span>
      <span class="sidebar-strip-label">目录</span>
    </div>

    <!-- Full panel (expanded) -->
    <div v-else class="sidebar-panel" style="width: 192px;">
      <MdOutline :items="outline" />
    </div>

    <!-- Table modal -->
    <TableEditorModal
      v-if="showTableModal"
      @confirm="handleTableInsert"
      @close="showTableModal = false"
    />
  </div>
</template>
