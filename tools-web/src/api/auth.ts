import request from './request'

export interface LoginParams {
  username: string
  password: string
}

export interface RegisterParams {
  username: string
  email: string
  password: string
}

export interface AuthResult {
  token: string
  userId: number
  username: string
}

export const authApi = {
  login(data: LoginParams) {
    return request.post<{ code: number; message: string; data: AuthResult }>('/auth/login', data)
  },
  register(data: RegisterParams) {
    return request.post<{ code: number; message: string; data: AuthResult }>('/auth/register', data)
  }
}
