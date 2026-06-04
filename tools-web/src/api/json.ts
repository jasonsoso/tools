import request from './request'

export interface JsonRecordDto {
  name?: string
  content?: string
}

export interface JsonRecord {
  id: number
  userId: number
  name: string
  content: string
  createdAt: string
  updatedAt: string
}

export const jsonApi = {
  list() {
    return request.get<{ code: number; message: string; data: JsonRecord[] }>('/json')
  },
  get(id: number) {
    return request.get<{ code: number; message: string; data: JsonRecord }>(`/json/${id}`)
  },
  create(data: JsonRecordDto) {
    return request.post<{ code: number; message: string; data: JsonRecord }>('/json', data)
  },
  update(id: number, data: JsonRecordDto) {
    return request.put<{ code: number; message: string; data: JsonRecord }>(`/json/${id}`, data)
  },
  delete(id: number) {
    return request.delete<{ code: number; message: string; data: null }>(`/json/${id}`)
  }
}
