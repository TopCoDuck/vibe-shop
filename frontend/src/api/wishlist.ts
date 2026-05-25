import axios from '@/utils/axios'
import type { ApiResponse, WishlistItem } from '@/types'

export const wishlistApi = {
  getMyWishlist() {
    return axios.get<ApiResponse<WishlistItem[]>>('/wishlist')
  },
  getMyWishlistIds() {
    return axios.get<ApiResponse<number[]>>('/wishlist/ids')
  },
  isWished(productId: number) {
    return axios.get<ApiResponse<boolean>>(`/wishlist/${productId}/status`)
  },
  toggle(productId: number) {
    return axios.post<ApiResponse<{ wished: boolean }>>(`/wishlist/${productId}/toggle`)
  }
}
