import axios from '@/utils/axios'
import type { ApiResponse, PageResponse, Product } from '@/types'

export const productApi = {
  getAll(params?: { categoryId?: number; keyword?: string; page?: number; size?: number }) {
    return axios.get<ApiResponse<PageResponse<Product>>>('/products', { params })
  },
  getOne(id: number) {
    return axios.get<ApiResponse<Product>>(`/products/${id}`)
  },
  create(data: Partial<Product>) {
    return axios.post<ApiResponse<Product>>('/products/admin', data)
  },
  update(id: number, data: Partial<Product>) {
    return axios.put<ApiResponse<Product>>(`/products/admin/${id}`, data)
  },
  delete(id: number) {
    return axios.delete<ApiResponse<void>>(`/products/admin/${id}`)
  },
  getAllForAdmin(params?: { page?: number; size?: number }) {
    return axios.get<ApiResponse<PageResponse<Product>>>('/products/admin/all', { params })
  }
}
