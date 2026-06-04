/**
 * Validate whether a string is valid JSON.
 */
export function isValidJson(str: string): boolean {
  try {
    JSON.parse(str)
    return true
  } catch {
    return false
  }
}

/**
 * Format JSON string with indentation (2 spaces).
 */
export function formatJson(str: string): string {
  try {
    const parsed = JSON.parse(str)
    return JSON.stringify(parsed, null, 2)
  } catch {
    return str
  }
}

/**
 * Compress JSON string to single line.
 */
export function compressJson(str: string): string {
  try {
    const parsed = JSON.parse(str)
    return JSON.stringify(parsed)
  } catch {
    return str
  }
}

/**
 * Copy text to clipboard.
 */
export async function copyToClipboard(text: string): Promise<boolean> {
  try {
    await navigator.clipboard.writeText(text)
    return true
  } catch {
    return false
  }
}
