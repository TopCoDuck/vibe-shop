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

export interface WishlistItem {
  id: number
  productId: number
  productName: string
  imageUrl?: string
  price: number
  categoryName?: string
  stock: number
  createdAt: string
}

export interface Inquiry {
  id: number
  type: string
  content: string
  replyMethod: string
  imageUrls?: string
  status: string
  createdAt: string
}

export interface Faq {
  id: number
  category: string
  question: string
  answer: string
  link?: string
  sortOrder: number
}

export interface Coupon {
  id: number
  name: string
  description?: string
  type: 'PERCENTAGE' | 'FIXED'
  value: number
  minOrderAmount: number
  maxDiscountAmount: number
  targetCategory?: string
  startDate: string
  endDate: string
  totalCount: number
  downloadedCount: number
  downloadable: boolean
  alreadyDownloaded: boolean
}

export interface MyCoupon {
  userCouponId: number
  couponId: number
  name: string
  description?: string
  type: 'PERCENTAGE' | 'FIXED'
  value: number
  minOrderAmount: number
  maxDiscountAmount: number
  targetCategory?: string
  endDate: string
  used: boolean
  downloadedAt: string
  usedAt?: string
}

export interface Review {
  id: number
  userId: number
  userName: string
  productId: number
  rating: number
  title?: string
  content: string
  imageUrl?: string
  createdAt: string
  myReview: boolean
}

export interface ReviewSummary {
  avgRating: number
  totalCount: number
  reviews: Review[]
}

export type ClaimType = 'CANCEL' | 'RETURN' | 'EXCHANGE' | 'REFUND'
export type ClaimReason = 'CHANGE_MIND' | 'DEFECTIVE' | 'WRONG_ITEM' | 'DIFFERENT_FROM_DESC' | 'DELAYED_DELIVERY' | 'OTHER'
export type ClaimStatus = 'REQUESTED' | 'IN_PROGRESS' | 'COMPLETED' | 'REJECTED'

export interface Claim {
  id: number
  orderId: number
  type: ClaimType
  reason: ClaimReason
  reasonDetail?: string
  imageUrl?: string
  status: ClaimStatus
  adminComment?: string
  createdAt: string
}

export const CLAIM_TYPE_LABEL: Record<ClaimType, string> = {
  CANCEL: '주문 취소',
  RETURN: '반품',
  EXCHANGE: '교환',
  REFUND: '환불',
}

export const CLAIM_REASON_LABEL: Record<ClaimReason, string> = {
  CHANGE_MIND: '단순 변심',
  DEFECTIVE: '상품 불량/파손',
  WRONG_ITEM: '오배송',
  DIFFERENT_FROM_DESC: '상품 설명과 다름',
  DELAYED_DELIVERY: '배송 지연',
  OTHER: '기타',
}

export const CLAIM_STATUS_LABEL: Record<ClaimStatus, string> = {
  REQUESTED: '접수',
  IN_PROGRESS: '처리 중',
  COMPLETED: '처리 완료',
  REJECTED: '반려',
}

export const ORDER_STATUS_LABEL: Record<string, string> = {
  PENDING: '주문 대기',
  PAID: '결제 완료',
  SHIPPING: '배송 중',
  DELIVERED: '배송 완료',
  CANCELLED: '주문 취소'
}
