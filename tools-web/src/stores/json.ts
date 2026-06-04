import { defineStore } from 'pinia'
import { ref } from 'vue'
import { jsonApi, type JsonRecord, type JsonRecordDto } from '@/api/json'

export const useJsonStore = defineStore('json', () => {
  const records = ref<JsonRecord[]>([])
  const currentRecord = ref<JsonRecord | null>(null)
  const loading = ref(false)

  async function loadList() {
    loading.value = true
    try {
      const res = await jsonApi.list()
      if (res.data.code === 200) {
        records.value = res.data.data
      }
    } finally {
      loading.value = false
    }
  }

  async function loadRecord(id: number) {
    loading.value = true
    try {
      const res = await jsonApi.get(id)
      if (res.data.code === 200) {
        currentRecord.value = res.data.data
      }
    } finally {
      loading.value = false
    }
  }

  async function create(data: JsonRecordDto) {
    const res = await jsonApi.create(data)
    if (res.data.code === 200) {
      currentRecord.value = res.data.data
      await loadList()
    }
    return res.data
  }

  async function update(id: number, data: JsonRecordDto) {
    const res = await jsonApi.update(id, data)
    if (res.data.code === 200) {
      currentRecord.value = res.data.data
      await loadList()
    }
    return res.data
  }

  async function remove(id: number) {
    const res = await jsonApi.delete(id)
    if (res.data.code === 200) {
      currentRecord.value = null
      await loadList()
    }
    return res.data
  }

  function resetCurrent() {
    currentRecord.value = null
  }

  return { records, currentRecord, loading, loadList, loadRecord, create, update, remove, resetCurrent }
})
