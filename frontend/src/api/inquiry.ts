import axios from '@/utils/axios'
import type { ApiResponse, Inquiry } from '@/types'

export const inquiryApi = {
  create(data: { type: string; content: string; replyMethod: string; imageUrls?: string }) {
    return axios.post<ApiResponse<Inquiry>>('/inquiries', data)
  },
  getMyInquiries() {
    return axios.get<ApiResponse<Inquiry[]>>('/inquiries/my')
  },
}
