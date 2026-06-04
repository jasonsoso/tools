import { defineStore } from 'pinia'
import { ref } from 'vue'
import { markdownApi, type MarkdownDoc, type MarkdownDocDto } from '@/api/markdown'

export const useMarkdownStore = defineStore('markdown', () => {
  const documents = ref<MarkdownDoc[]>([])
  const currentDoc = ref<MarkdownDoc | null>(null)
  const loading = ref(false)

  async function loadList() {
    loading.value = true
    try {
      const res = await markdownApi.list()
      if (res.data.code === 200) {
        documents.value = res.data.data
      }
    } finally {
      loading.value = false
    }
  }

  async function loadDocument(id: number) {
    loading.value = true
    try {
      const res = await markdownApi.get(id)
      if (res.data.code === 200) {
        currentDoc.value = res.data.data
      }
    } finally {
      loading.value = false
    }
  }

  async function create(data: MarkdownDocDto) {
    const res = await markdownApi.create(data)
    if (res.data.code === 200) {
      currentDoc.value = res.data.data
      await loadList()
    }
    return res.data
  }

  async function update(id: number, data: MarkdownDocDto) {
    const res = await markdownApi.update(id, data)
    if (res.data.code === 200) {
      currentDoc.value = res.data.data
      await loadList()
    }
    return res.data
  }

  async function remove(id: number) {
    const res = await markdownApi.delete(id)
    if (res.data.code === 200) {
      currentDoc.value = null
      await loadList()
    }
    return res.data
  }

  function resetCurrent() {
    currentDoc.value = null
  }

  return { documents, currentDoc, loading, loadList, loadDocument, create, update, remove, resetCurrent }
})
