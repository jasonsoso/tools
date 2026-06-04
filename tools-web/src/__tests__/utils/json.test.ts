import { describe, it, expect } from 'vitest'
import { isValidJson, formatJson, compressJson } from '@/utils/json'

describe('isValidJson', () => {
  it('should return true for valid JSON object', () => {
    expect(isValidJson('{"a":1,"b":2}')).toBe(true)
  })

  it('should return true for valid JSON array', () => {
    expect(isValidJson('[1,2,3]')).toBe(true)
  })

  it('should return false for invalid JSON', () => {
    expect(isValidJson('{bad}')).toBe(false)
  })

  it('should return false for empty string', () => {
    expect(isValidJson('')).toBe(false)
  })

  it('should return false for plain text', () => {
    expect(isValidJson('hello world')).toBe(false)
  })
})

describe('formatJson', () => {
  it('should format compact JSON with indentation', () => {
    const result = formatJson('{"a":1,"b":2}')
    expect(result).toContain('\n')
    expect(result).toContain('  ')
  })

  it('should return original string on invalid JSON', () => {
    const result = formatJson('{bad}')
    expect(result).toBe('{bad}')
  })

  it('should format array correctly', () => {
    const result = formatJson('[1,2,3]')
    expect(result).toContain('\n')
    expect(result).toContain('1')
  })
})

describe('compressJson', () => {
  it('should remove whitespace from formatted JSON', () => {
    const result = compressJson('{ "a": 1, "b": 2 }')
    expect(result).toBe('{"a":1,"b":2}')
  })

  it('should return original string on invalid JSON', () => {
    const result = compressJson('{bad}')
    expect(result).toBe('{bad}')
  })
})
