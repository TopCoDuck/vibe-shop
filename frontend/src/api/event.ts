import axios from '@/utils/axios'
import type { ApiResponse } from '@/types'

export interface EventItem {
  id: number
  badge: string
  title: string
  subtitle?: string
  description?: string
  background: string
  textColor: string
  accentColor: string
  link: string
  startDate: string
  endDate: string
  sortOrder: number
}

export const eventApi = {
  getAll() {
    return axios.get<ApiResponse<EventItem[]>>('/events')
  },
}
