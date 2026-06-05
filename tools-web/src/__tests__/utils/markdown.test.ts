import { describe, it, expect } from 'vitest'
import { renderMarkdown, extractOutline } from '@/utils/markdown'

describe('renderMarkdown', () => {
  it('should render heading', () => {
    const html = renderMarkdown('# Hello')
    expect(html).toContain('<h1>')
    expect(html).toContain('Hello')
  })

  it('should render bold text', () => {
    const html = renderMarkdown('**bold**')
    expect(html).toContain('<strong>')
    expect(html).toContain('bold')
  })

  it('should render italic text', () => {
    const html = renderMarkdown('*italic*')
    expect(html).toContain('<em>')
    expect(html).toContain('italic')
  })

  it('should render code blocks', () => {
    const html = renderMarkdown('```js\nconst x = 1;\n```')
    expect(html).toContain('<code')
    // With highlight.js, source code is wrapped in span tags
    expect(html).toContain('const')
    expect(html).toContain('x = ')
  })

  it('should render inline math', () => {
    const html = renderMarkdown('$E=mc^2$')
    expect(html).toContain('katex')
  })

  it('should render display math', () => {
    const html = renderMarkdown('$$\nx = 1\n$$')
    expect(html).toContain('katex')
  })

  it('should handle empty string', () => {
    const html = renderMarkdown('')
    expect(html).toBeDefined()
  })
})

describe('extractOutline', () => {
  it('should extract headings from markdown', () => {
    const items = extractOutline('# H1\n## H2\n### H3')
    expect(items).toHaveLength(3)
    expect(items[0]).toEqual({ level: 1, text: 'H1', id: 'h1' })
    expect(items[1]).toEqual({ level: 2, text: 'H2', id: 'h2' })
    expect(items[2]).toEqual({ level: 3, text: 'H3', id: 'h3' })
  })

  it('should return empty array for no headings', () => {
    const items = extractOutline('just plain text')
    expect(items).toHaveLength(0)
  })

  it('should only extract H1-H3', () => {
    const items = extractOutline('# H1\n#### H4')
    expect(items).toHaveLength(1)
    expect(items[0].level).toBe(1)
  })

  it('should generate slug-style IDs', () => {
    const items = extractOutline('## Hello World')
    expect(items[0].id).toBe('hello-world')
  })
})
