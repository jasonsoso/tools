import request from './request'

export interface MarkdownDocDto {
  title?: string
  content?: string
}

export interface MarkdownDoc {
  id: number
  userId: number
  title: string
  content: string
  createdAt: string
  updatedAt: string
}

export const markdownApi = {
  list() {
    return request.get<{ code: number; message: string; data: MarkdownDoc[] }>('/markdown')
  },
  get(id: number) {
    return request.get<{ code: number; message: string; data: MarkdownDoc }>(`/markdown/${id}`)
  },
  create(data: MarkdownDocDto) {
    return request.post<{ code: number; message: string; data: MarkdownDoc }>('/markdown', data)
  },
  update(id: number, data: MarkdownDocDto) {
    return request.put<{ code: number; message: string; data: MarkdownDoc }>(`/markdown/${id}`, data)
  },
  delete(id: number) {
    return request.delete<{ code: number; message: string; data: null }>(`/markdown/${id}`)
  }
}
