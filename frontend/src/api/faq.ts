import axios from '@/utils/axios'
import type { ApiResponse, Faq } from '@/types'

export const faqApi = {
  getAll() {
    return axios.get<ApiResponse<Faq[]>>('/faq')
  },
}
