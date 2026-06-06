import axios from '@/utils/axios'
import type { ApiResponse, Claim, ClaimType, ClaimReason } from '@/types'

export interface ClaimRequest {
  type: ClaimType
  reason: ClaimReason
  reasonDetail?: string
  imageUrl?: string
}

export const claimApi = {
  create(orderId: number, data: ClaimRequest) {
    return axios.post<ApiResponse<Claim>>(`/orders/${orderId}/claims`, data)
  },
  getByOrder(orderId: number) {
    return axios.get<ApiResponse<Claim>>(`/orders/${orderId}/claims`)
  },
  getMyClaims() {
    return axios.get<ApiResponse<Claim[]>>('/claims/my')
  },
  getById(claimId: number) {
    return axios.get<ApiResponse<Claim>>(`/claims/${claimId}`)
  },
  cancel(claimId: number) {
    return axios.delete<ApiResponse<null>>(`/claims/${claimId}`)
  },
}
