export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}

export interface User {
  id: number
  email: string
  name: string
  phone?: string
  address?: string
  role: 'USER' | 'ADMIN'
}

export interface TokenResponse {
  accessToken: string
  refreshToken: string
  email: string
  name: string
  role: string
}

export interface Category {
  id: number
  name: string
  description?: string
  parent?: Category
}

export interface Product {
  id: number
  name: string
  description?: string
  price: number
  stock: number
  imageUrl?: string
  categoryId?: number
  categoryName?: string
  status: 'ACTIVE' | 'INACTIVE' | 'SOLD_OUT'
  createdAt: string
}

export interface CartItem {
  id: number
  productId: number
  productName: string
  imageUrl?: string
  price: number
  quantity: number
  subtotal: number
}

export interface OrderItem {
  productId: number
  productName: string
  imageUrl?: string
  quantity: number
  price: number
  subtotal: number
}

export interface Order {
  id: number
  items: OrderItem[]
  totalAmount: number
  status: 'PENDING' | 'PAID' | 'SHIPPING' | 'DELIVERED' | 'CANCELLED'
  shippingAddress: string
  receiverName: string
  receiverPhone: string
  createdAt: string
}

export const ORDER_STATUS_LABEL: Record<string, string> = {
  PENDING: '주문 대기',
  PAID: '결제 완료',
  SHIPPING: '배송 중',
  DELIVERED: '배송 완료',
  CANCELLED: '주문 취소'
}
