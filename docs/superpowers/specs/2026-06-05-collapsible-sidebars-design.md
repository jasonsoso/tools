# 可折叠侧栏设计规格

**日期**: 2026-06-05
**状态**: 已批准
**关联**: Markdown 编辑器可用性增强

## 目标

Markdown 编辑器的左侧文档列表和右侧目录均可独立折叠/展开，最大化中间编辑区和预览区的可用空间。

## 设计决策

| 决策点 | 选择 | 理由 |
|--------|------|------|
| 折叠交互 | 边缘按钮（方案 A） | 位置明确，视觉线索清晰，误触率低 |
| 折叠后状态 | 留 36px 窄条 | 保留信息速览（文档数、标题数），hover 可预览 |
| 过渡动画 | 平滑滑动 250ms ease | 视觉连贯，不突兀 |
| 目录在编辑/预览模式 | 始终可用 | 用户完全控制权，不局限于分屏模式 |
| 折叠状态持久化 | localStorage | 刷新/切换文档后保持一致体验 |
| 两侧独立性 | 各自独立折叠 | 灵活性最大化 |

## 布局规格

### 展开状态

```
┌──────────────┬──┬────────────────────────┬──┬──────────────┐
│ DocumentList │◀ │  Editor / Preview       │▶ │  MdOutline   │
│   w-52       │  │  flex-1                │  │  w-48        │
│   (208px)    │  │                        │  │  (192px)     │
└──────────────┴──┴────────────────────────┴──┴──────────────┘
```

### 折叠后（以左侧为例）

```
┌────┬──┬──────────────────────────────────────┐
│ 📄 │▶ │   Editor / Preview                   │
│ 5  │  │   flex-1                             │
│    │  │                                      │
└────┴──┴──────────────────────────────────────┘
 36px  22px
```

- 折叠按钮: 22px 宽，绝对定位或 flex 项，显示 `◀` 或 `▶`
- 窄条: 36px 宽，竖排布局，图标 + 文档/标题计数
- 窄条背景稍深于主背景以区分

## 与视图模式的关系

三档视图模式（编辑/分屏/预览）与侧栏折叠**正交**——各自独立控制：

| 视图模式 | 左侧(文档) | 编辑器 | 预览 | 右侧(目录) |
|----------|-----------|--------|------|-----------|
| 编辑 | 可折叠 | ✅ | — | 可折叠 |
| 分屏 | 可折叠 | ✅ | ✅ | 可折叠 |
| 预览 | 可折叠 | — | ✅ | 可折叠 |

- 目录侧栏不再受 `v-show="viewMode === 'split'"` 限制
- 改为由 `rightCollapsed` 独立控制显隐
- 视图模式切换不改变折叠状态

## 状态管理

```ts
// 在 MarkdownEditor.vue 中
const leftCollapsed = ref(false)   // 左侧文档列表
const rightCollapsed = ref(false)  // 右侧目录

// localStorage key
const LS_KEY_LEFT = 'md-editor-left-collapsed'
const LS_KEY_RIGHT = 'md-editor-right-collapsed'

// 初始化时从 localStorage 读取
// 折叠/展开时写入 localStorage
```

## CSS 动画

```css
.sidebar-panel {
  transition: width 250ms ease, opacity 200ms ease, padding 200ms ease;
  overflow: hidden;
}
.sidebar-panel.collapsed {
  width: 36px;
  /* 内容淡出 */
}
.collapse-btn {
  width: 22px;
  transition: background-color 150ms ease;
}
```

## 边界情况

| 场景 | 行为 |
|------|------|
| 首次访问（无 localStorage） | 两侧默认展开 |
| 两侧同时折叠 | 中间区撑满全部可用空间 |
| 窗口宽度 < 768px | 侧栏默认折叠，折叠按钮保留 |
| 切换文档 | 折叠状态不变（从 localStorage 读取） |
| 新建文档 | 折叠状态不变 |

## 改动范围

| 文件 | 改动类型 | 说明 |
|------|---------|------|
| `src/views/MarkdownEditor.vue` | 修改 | 新增加折叠状态、折叠按钮、窄条模板、localStorage 读写 |
| `src/style.css` | 追加 | 侧栏动画 transition、折叠窄条样式、折叠按钮样式 |

**不改的文件**: `DocumentList.vue`, `MdOutline.vue`, `MdPreview.vue`, `MdToolbar.vue`, `utils/markdown.ts`, `stores/markdown.ts`

## 验收标准

- [ ] 左侧文档列表可点击 `◀` 折叠为 36px 窄条
- [ ] 点击窄条或 `▶` 按钮可展开回 208px
- [ ] 右侧目录可独立折叠/展开（同上逻辑）
- [ ] 折叠/展开带有 250ms 平滑过渡
- [ ] 刷新页面后折叠状态保持
- [ ] 三档视图模式切换不影响折叠状态
- [ ] 目录在编辑/预览模式下也可展开
- [ ] 现有 25 个测试不受影响
- [ ] TypeScript 编译无错误
- [ ] Vite 构建成功
