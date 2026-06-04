<script setup lang="ts">
interface TreeNode {
  key: string
  value: string
  type: 'string' | 'number' | 'boolean' | 'null' | 'object' | 'array'
  children?: TreeNode[]
}

defineProps<{
  node: TreeNode
  path: string
  collapsed: Set<string>
  depth: number
}>()

const emit = defineEmits<{
  toggle: [path: string]
}>()

function getTypeClass(type: string): string {
  switch (type) {
    case 'string': return 'text-green-600'
    case 'number': return 'text-blue-600'
    case 'boolean': return 'text-purple-600'
    case 'null': return 'text-gray-400'
    default: return 'text-gray-700'
  }
}
</script>

<template>
  <div :style="{ paddingLeft: `${depth * 16}px` }">
    <div class="flex items-center gap-1 py-0.5">
      <button
        v-if="node.type === 'object' || node.type === 'array'"
        @click="emit('toggle', path)"
        class="text-gray-400 hover:text-gray-600 w-4 text-xs"
      >
        {{ collapsed.has(path) ? '▶' : '▼' }}
      </button>
      <span v-else class="w-4"></span>
      <span class="text-gray-700">{{ node.key }}</span>
      <span v-if="node.type !== 'object' && node.type !== 'array'" class="text-gray-400">: </span>
      <span :class="getTypeClass(node.type)">{{ node.value }}</span>
    </div>
    <template v-if="node.children && !collapsed.has(path)">
      <TreeNodeItem
        v-for="child in node.children"
        :key="`${path}.${child.key}`"
        :node="child"
        :path="`${path}.${child.key}`"
        :collapsed="collapsed"
        :depth="depth + 1"
        @toggle="emit('toggle', $event)"
      />
    </template>
  </div>
</template>
