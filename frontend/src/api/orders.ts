import axios from '@/utils/axios'
import type { ApiResponse, PageResponse, Order } from '@/types'

export const orderApi = {
  createOrder(data: { cartItemIds: number[]; shippingAddress: string; receiverName: string; receiverPhone: string }) {
    return axios.post<ApiResponse<Order>>('/orders', data)
  },
  getMyOrders(params?: { page?: number; size?: number }) {
    return axios.get<ApiResponse<PageResponse<Order>>>('/orders', { params })
  },
  getOrder(orderId: number) {
    return axios.get<ApiResponse<Order>>(`/orders/${orderId}`)
  },
  cancelOrder(orderId: number) {
    return axios.post<ApiResponse<Order>>(`/orders/${orderId}/cancel`)
  },
  updateOrderStatus(orderId: number, status: string) {
    return axios.put<ApiResponse<Order>>(`/orders/admin/${orderId}/status`, { status })
  },
  getAllOrders(params?: { page?: number; size?: number }) {
    return axios.get<ApiResponse<PageResponse<Order>>>('/orders/admin/all', { params })
  }
}
