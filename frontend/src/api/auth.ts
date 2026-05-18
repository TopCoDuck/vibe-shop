import axios from '@/utils/axios'
import type { ApiResponse, TokenResponse, User } from '@/types'

export const authApi = {
  signup(data: { email: string; password: string; name: string; phone?: string }) {
    return axios.post<ApiResponse<User>>('/auth/signup', data)
  },
  login(data: { email: string; password: string }) {
    return axios.post<ApiResponse<TokenResponse>>('/auth/login', data)
  },
  getMe() {
    return axios.get<ApiResponse<User>>('/auth/me')
  }
}
