import axios from '@/utils/axios'
import type { ApiResponse, Category } from '@/types'

export const categoryApi = {
  getAll() {
    return axios.get<ApiResponse<Category[]>>('/categories')
  },
  getRoots() {
    return axios.get<ApiResponse<Category[]>>('/categories/roots')
  },
  create(data: { name: string; description?: string; parentId?: number }) {
    return axios.post<ApiResponse<Category>>('/categories/admin', data)
  },
  update(id: number, data: { name: string; description?: string }) {
    return axios.put<ApiResponse<Category>>(`/categories/admin/${id}`, data)
  },
  delete(id: number) {
    return axios.delete<ApiResponse<void>>(`/categories/admin/${id}`)
  }
}
