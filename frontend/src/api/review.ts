import axios from '@/utils/axios'
import type { ApiResponse, ReviewSummary, Review } from '@/types'

export interface ReviewRequest {
  rating: number
  title?: string
  content: string
  imageUrl?: string
}

export const reviewApi = {
  getByProduct(productId: number) {
    return axios.get<ApiResponse<ReviewSummary>>(`/products/${productId}/reviews`)
  },
  create(productId: number, data: ReviewRequest) {
    return axios.post<ApiResponse<Review>>(`/products/${productId}/reviews`, data)
  },
  update(reviewId: number, data: ReviewRequest) {
    return axios.put<ApiResponse<Review>>(`/reviews/${reviewId}`, data)
  },
  delete(reviewId: number) {
    return axios.delete<ApiResponse<null>>(`/reviews/${reviewId}`)
  },
}
