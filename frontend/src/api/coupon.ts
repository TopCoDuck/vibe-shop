import axios from '@/utils/axios'
import type { ApiResponse, Coupon, MyCoupon } from '@/types'

export const couponApi = {
  getAvailable() {
    return axios.get<ApiResponse<Coupon[]>>('/coupons')
  },
  download(id: number) {
    return axios.post<ApiResponse<null>>(`/coupons/${id}/download`)
  },
  getMyCoupons() {
    return axios.get<ApiResponse<MyCoupon[]>>('/coupons/my')
  },
}
