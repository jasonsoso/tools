import MarkdownIt from 'markdown-it'
import katex from 'katex'

const md = new MarkdownIt({
  html: true,
  linkify: true,
  breaks: true
})

/**
 * Render Markdown string to HTML, with KaTeX math support.
 * Supports inline $...$ and block $$...$$ formulas.
 */
export function renderMarkdown(content: string): string {
  // First, protect code blocks from math processing
  const codeBlocks: string[] = []
  let processed = content.replace(/```[\s\S]*?```/g, (match) => {
    codeBlocks.push(match)
    return `__CODE_BLOCK_${codeBlocks.length - 1}__`
  })

  // Process block math: $$...$$
  processed = processed.replace(/\$\$([\s\S]*?)\$\$/g, (_match, formula: string) => {
    try {
      return katex.renderToString(formula.trim(), { displayMode: true })
    } catch {
      return _match
    }
  })

  // Process inline math: $...$
  processed = processed.replace(/\$(.*?)\$/g, (_match, formula: string) => {
    try {
      return katex.renderToString(formula.trim(), { displayMode: false })
    } catch {
      return _match
    }
  })

  // Restore code blocks
  codeBlocks.forEach((block, i) => {
    processed = processed.replace(`__CODE_BLOCK_${i}__`, block)
  })

  return md.render(processed)
}

export interface OutlineItem {
  level: number
  text: string
  id: string
}

/**
 * Extract H1-H3 headings from Markdown content for table of contents.
 */
export function extractOutline(content: string): OutlineItem[] {
  const headingRegex = /^(#{1,3})\s+(.+)$/gm
  const items: OutlineItem[] = []
  let match: RegExpExecArray | null
  while ((match = headingRegex.exec(content)) !== null) {
    const level = match[1].length
    const text = match[2].trim()
    const id = text.toLowerCase().replace(/\s+/g, '-').replace(/[^\w-]/g, '')
    items.push({ level, text, id })
  }
  return items
}

/**
 * Export content as a .md file download.
 */
export function exportMarkdown(content: string, filename: string) {
  const blob = new Blob([content], { type: 'text/markdown' })
  downloadBlob(blob, `${filename}.md`)
}

/**
 * Export rendered HTML as a complete HTML page download.
 */
export function exportHtml(content: string, filename: string) {
  const html = `<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <title>${filename}</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/katex.min.css">
  <style>
    body { max-width: 800px; margin: 0 auto; padding: 20px; font-family: system-ui, sans-serif; }
    pre { background: #f4f4f4; padding: 12px; border-radius: 4px; overflow-x: auto; }
    code { background: #f4f4f4; padding: 2px 4px; border-radius: 2px; }
    table { border-collapse: collapse; width: 100%; }
    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
    th { background: #f4f4f4; }
  </style>
</head>
<body>
${renderMarkdown(content)}
</body>
</html>`
  const blob = new Blob([html], { type: 'text/html' })
  downloadBlob(blob, `${filename}.html`)
}

function downloadBlob(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  a.click()
  URL.revokeObjectURL(url)
}
