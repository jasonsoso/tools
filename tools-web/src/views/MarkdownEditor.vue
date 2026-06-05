<script setup lang="ts">
import { ref, watch, onMounted, computed } from 'vue'
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

let editorView: EditorView | null = null
const editorContainer = ref<HTMLDivElement>()

const docId = computed(() => route.params.id ? Number(route.params.id) : null)

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
    case 'h1': insertMarkdown('# $1'); break
    case 'h2': insertMarkdown('## $1'); break
    case 'h3': insertMarkdown('### $1'); break
    case 'link': insertMarkdown('[$1](url)'); break
    case 'image': insertMarkdown('![$1](url)'); break
    case 'ul': insertMarkdown('- $1'); break
    case 'ol': insertMarkdown('1. $1'); break
    case 'code': insertMarkdown('```\n$1\n```'); break
    case 'formula': insertMarkdown('$$1$'); break
    case 'table': showTableModal.value = true; break
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
          updateListener
        ]
      }),
      parent: editorContainer.value
    })
  }

  if (docId.value) {
    loadDocToEditor(docId.value)
  }
  store.loadList()
})

watch(content, () => {
  updatePreview()
})

// 监听文档 ID 变化，在已编辑和新建文档之间切换时重新加载
watch(docId, (newId) => {
  if (newId) {
    loadDocToEditor(newId)
  } else {
    // 切换到新建模式：清空编辑器和预览
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
</script>

<template>
  <div class="flex gap-5 h-[calc(100vh-7rem)]" @keydown="handleKeydown">
    <!-- Sidebar -->
    <DocumentList
      :documents="store.documents"
      :current-id="docId"
      :loading="store.loading"
      @select="(id: number) => router.push(`/markdown/${id}`)"
      @new="router.push('/markdown'); store.resetCurrent(); title = ''; content = ''; updatePreview()"
    />

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
        <div class="flex-1 card overflow-hidden p-0">
          <div ref="editorContainer" class="h-full"></div>
        </div>

        <!-- Preview -->
        <div class="flex-1 card overflow-auto p-5">
          <MdPreview :html="htmlPreview" />
        </div>

        <!-- Outline -->
        <div class="w-48 flex-shrink-0">
          <MdOutline :items="outline" />
        </div>
      </div>
    </div>

    <!-- Table modal -->
    <TableEditorModal
      v-if="showTableModal"
      @confirm="handleTableInsert"
      @close="showTableModal = false"
    />
  </div>
</template>
