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
const showTableModal = ref(false)

let editorView: EditorView | null = null
const editorContainer = ref<HTMLDivElement>()

const docId = computed(() => route.params.id ? Number(route.params.id) : null)

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
    store.loadDocument(docId.value).then(() => {
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
  store.loadList()
})

watch(content, () => {
  updatePreview()
})

async function handleSave() {
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
  <div class="flex gap-4 h-[calc(100vh-6rem)]" @keydown="handleKeydown">
    <!-- Document List Sidebar -->
    <DocumentList
      :documents="store.documents"
      :current-id="docId"
      :loading="store.loading"
      @select="(id: number) => router.push(`/markdown/${id}`)"
      @new="router.push('/markdown'); store.resetCurrent(); title = ''; content = ''; updatePreview()"
    />

    <!-- Main Editor Area -->
    <div class="flex-1 flex flex-col gap-2 min-w-0">
      <!-- Title -->
      <div class="flex items-center gap-3">
        <input v-model="title" type="text" placeholder="文档标题"
               class="flex-1 text-lg font-semibold px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500" />
        <button @click="handleSave" :disabled="saving"
                class="px-4 py-2 bg-blue-600 text-white text-sm rounded-md hover:bg-blue-700 disabled:opacity-50">
          {{ saving ? '保存中...' : '保存' }}
        </button>
        <ExportMenu :content="content" :title="title" />
      </div>

      <!-- Toolbar -->
      <MdToolbar @action="handleToolbarAction" />

      <!-- Editor + Preview Split -->
      <div class="flex-1 flex gap-3 min-h-0">
        <!-- CodeMirror Editor -->
        <div class="flex-1 border border-gray-300 rounded-md overflow-hidden bg-white">
          <div ref="editorContainer" class="h-full"></div>
        </div>

        <!-- Preview Panel -->
        <div class="flex-1 border border-gray-300 rounded-md overflow-auto bg-white p-4">
          <MdPreview :html="htmlPreview" />
        </div>

        <!-- Outline Panel -->
        <div class="w-48 flex-shrink-0">
          <MdOutline :items="outline" />
        </div>
      </div>
    </div>

    <!-- Table Editor Modal -->
    <TableEditorModal
      v-if="showTableModal"
      @confirm="handleTableInsert"
      @close="showTableModal = false"
    />
  </div>
</template>
