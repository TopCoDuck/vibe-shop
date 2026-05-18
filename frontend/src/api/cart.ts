import axios from '@/utils/axios'
import type { ApiResponse, CartItem } from '@/types'

export const cartApi = {
  getCart() {
    return axios.get<ApiResponse<CartItem[]>>('/cart')
  },
  addItem(productId: number, quantity: number) {
    return axios.post<ApiResponse<CartItem>>('/cart', { productId, quantity })
  },
  updateQuantity(cartItemId: number, quantity: number) {
    return axios.put<ApiResponse<CartItem>>(`/cart/${cartItemId}`, { quantity })
  },
  removeItem(cartItemId: number) {
    return axios.delete<ApiResponse<void>>(`/cart/${cartItemId}`)
  },
  clearCart() {
    return axios.delete<ApiResponse<void>>('/cart')
  }
}
