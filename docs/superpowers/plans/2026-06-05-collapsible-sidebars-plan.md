# 可折叠侧栏 实现计划

> **面向 AI 代理的工作者：** 使用 superpowers:subagent-driven-development（推荐）或 superpowers:executing-plans 逐任务实现此计划。步骤使用复选框（`- [ ]`）语法来跟踪进度。

**目标：** Markdown 编辑器的左侧文档列表和右侧目录均可独立折叠/展开为窄条，释放中间编辑区空间。

**架构：** 在 MarkdownEditor.vue 中新增两个 `ref` 状态（leftCollapsed/rightCollapsed），持久化到 localStorage。模板中为 DocumentList 和 MdOutline 包裹可折叠容器——容器包含展开面板 + 折叠窄条 + 切换按钮。CSS 控制 width transition 动画。

**技术栈：** Vue 3 Composition API + Tailwind CSS v4 + localStorage

---

### 任务 1：CSS — 添加侧栏折叠动画样式

**文件：**
- 修改：`tools-web/src/style.css`（文件末尾追加）

- [ ] **步骤 1：在 style.css 末尾追加折叠侧栏样式**

```css
/* ---- Collapsible sidebar ---- */
.sidebar-panel {
  transition: width 250ms ease, opacity 200ms ease;
  overflow: hidden;
  flex-shrink: 0;
}
.sidebar-panel.collapsed {
  width: 36px !important;
}

/* Collapse toggle button — sits between sidebar and main content */
.collapse-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  flex-shrink: 0;
  background: transparent;
  border: none;
  color: #a1a1aa;
  font-size: 10px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 150ms ease, color 150ms ease;
  padding: 0;
  margin: 0;
}
.collapse-toggle:hover {
  background: rgba(0, 0, 0, 0.04);
  color: #52525b;
}

/* Narrow strip — shown when sidebar collapsed */
.sidebar-strip {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  padding-top: 14px;
  gap: 8px;
  width: 36px;
  height: 100%;
  background: #f5f5f5;
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transition: background-color 150ms ease;
}
.sidebar-strip:hover {
  background: #eeeeee;
}
.sidebar-strip-icon {
  font-size: 14px;
  line-height: 1;
}
.sidebar-strip-count {
  font-size: 10px;
  font-weight: 500;
  color: #a1a1aa;
  line-height: 1;
}
.sidebar-strip-label {
  font-size: 9px;
  color: #a1a1aa;
  writing-mode: vertical-rl;
  text-orientation: mixed;
  letter-spacing: 0.05em;
  margin-top: 4px;
}
```

- [ ] **步骤 2：确认 CSS 文件保存无误**

运行：`head -5 tools-web/src/style.css && echo "..." && tail -5 tools-web/src/style.css`
预期：文件头包含 `@import "tailwindcss"; @plugin "@tailwindcss/typography";`，文件尾包含刚追加的 `.sidebar-strip-label` 样式

- [ ] **步骤 3：Commit**

```bash
git add tools-web/src/style.css
git commit -m "style: add collapsible sidebar CSS classes"
```

---

### 任务 2：MarkdownEditor — 脚本区添加折叠状态与函数

**文件：**
- 修改：`tools-web/src/views/MarkdownEditor.vue`（script setup 部分）

- [ ] **步骤 1：在 `viewMode` 声明后添加折叠状态**

在第 29 行 `const viewMode = ref<...>('split')` 之后插入：

```ts
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
```

- [ ] **步骤 2：运行类型检查确认无语法错误**

运行：`cd tools-web && npx vue-tsc --noEmit`
预期：无错误输出

- [ ] **步骤 3：Commit**

```bash
git add tools-web/src/views/MarkdownEditor.vue
git commit -m "feat: add collapse state and toggle functions for sidebars"
```

---

### 任务 3：MarkdownEditor — 模板区重构左侧文档列表为可折叠容器

**文件：**
- 修改：`tools-web/src/views/MarkdownEditor.vue`（template 部分）

- [ ] **步骤 1：替换左侧 DocumentList 的模板代码**

将模板中第 230-237 行的这段：

```html
    <!-- Sidebar -->
    <DocumentList
      :documents="store.documents"
      :current-id="docId"
      :loading="store.loading"
      @select="(id: number) => router.push(`/markdown/${id}`)"
      @new="router.push('/markdown'); store.resetCurrent(); title = ''; content = ''; updatePreview()"
    />
```

替换为：

```html
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
```

- [ ] **步骤 2：运行类型检查**

运行：`cd tools-web && npx vue-tsc --noEmit`
预期：无错误

- [ ] **步骤 3：运行测试**

运行：`cd tools-web && npx vitest run`
预期：25 个测试全部通过

- [ ] **步骤 4：Commit**

```bash
git add tools-web/src/views/MarkdownEditor.vue
git commit -m "feat: make left document sidebar collapsible"
```

---

### 任务 4：MarkdownEditor — 模板区重构右侧目录为可折叠容器

**文件：**
- 修改：`tools-web/src/views/MarkdownEditor.vue`（template 部分）

- [ ] **步骤 1：替换右侧 MdOutline 的模板代码**

将模板中第 295-298 行的这段：

```html
        <!-- Outline (only visible in split mode) -->
        <div v-show="viewMode === 'split'" class="w-48 flex-shrink-0">
          <MdOutline :items="outline" />
        </div>
```

替换为：

```html
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
```

**注意：** 步骤 1 的替换涉及原有 `<div class="flex-1 flex gap-4 min-h-0">` 的闭合标签变化——原来右侧目录是编辑器/预览 flex 容器的一个子元素，现在需要重构布局结构。

完整替换逻辑：
- 原第 295-298 行（Outine div）→ 替换为右折叠按钮
- 原整个外层的 `</div>` 闭合标签保持，新增右侧可折叠面板在原外层 `<div class="flex gap-5...">` 的最右侧

具体来说，`<!-- Editor + Preview split -->` 这个 `<div class="flex-1 flex gap-4 min-h-0">` 内部（第 271-299 行）需要改为：

```html
      <!-- Editor + Preview split -->
      <div class="flex-1 flex gap-4 min-h-0">
        <!-- Editor -->
        <div
          v-show="viewMode !== 'preview'"
          class="card overflow-hidden p-0 min-h-0 flex-1"
        >
          <div ref="editorContainer" class="h-full"></div>
        </div>

        <!-- Preview -->
        <div
          v-show="viewMode !== 'edit'"
          ref="previewScrollRef"
          class="card overflow-auto p-6 min-h-0 flex-1"
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
```

- [ ] **步骤 2：运行类型检查**

运行：`cd tools-web && npx vue-tsc --noEmit`
预期：无错误

- [ ] **步骤 3：运行测试**

运行：`cd tools-web && npx vitest run`
预期：25 个测试全部通过

- [ ] **步骤 4：Commit**

```bash
git add tools-web/src/views/MarkdownEditor.vue
git commit -m "feat: make right outline sidebar collapsible across all view modes"
```

---

### 任务 5：构建 + 完整验证

**文件：**
- 验证（不改文件）：`tools-web/`

- [ ] **步骤 1：运行完整类型检查**

运行：`cd tools-web && npx vue-tsc --noEmit`
预期：无错误

- [ ] **步骤 2：运行全部测试**

运行：`cd tools-web && npx vitest run`
预期：3 test files, 25 tests all passed

- [ ] **步骤 3：运行生产构建**

运行：`cd tools-web && npx vite build`
预期：构建成功，无错误（仅 chunk size 警告）

- [ ] **步骤 4：Commit**

```bash
git add -A
git commit -m "chore: final verification — all tests and build pass"
```
