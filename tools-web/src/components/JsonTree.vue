<script setup lang="ts">
import { ref, computed } from 'vue'
import TreeNodeItem from './TreeNodeItem.vue'

const props = defineProps<{
  data: string
}>()

const collapsed = ref<Set<string>>(new Set())

interface TreeNode {
  key: string
  value: string
  type: 'string' | 'number' | 'boolean' | 'null' | 'object' | 'array'
  children?: TreeNode[]
}

function buildNodes(key: string, value: any): TreeNode[] {
  if (value === null) return [{ key, value: 'null', type: 'null' }]
  if (typeof value === 'string') return [{ key, value: `"${value}"`, type: 'string' }]
  if (typeof value === 'number') return [{ key, value: String(value), type: 'number' }]
  if (typeof value === 'boolean') return [{ key, value: String(value), type: 'boolean' }]
  if (Array.isArray(value)) {
    const children = value.map((item, i) => buildNodes(String(i), item)[0])
    return [{ key, value: `Array(${value.length})`, type: 'array', children }]
  }
  if (typeof value === 'object') {
    const children = Object.entries(value).map(([k, v]) => buildNodes(k, v)[0])
    return [{ key, value: `Object(${Object.keys(value).length})`, type: 'object', children }]
  }
  return [{ key, value: String(value), type: 'string' }]
}

const nodes = computed(() => {
  try {
    const obj = JSON.parse(props.data)
    return buildNodes('root', obj)
  } catch {
    return []
  }
})

function toggle(path: string) {
  if (collapsed.value.has(path)) {
    collapsed.value.delete(path)
  } else {
    collapsed.value.add(path)
  }
}
</script>

<template>
  <div class="font-mono text-sm">
    <template v-for="node in nodes" :key="node.key">
      <TreeNodeItem
        :node="node"
        :path="node.key"
        :collapsed="collapsed"
        :depth="0"
        @toggle="toggle"
      />
    </template>
  </div>
</template>
