import { describe, it, expect, vi, beforeEach } from 'vitest'
import { useCartStore } from '../cart'

vi.mock('@/api/cart', () => ({
  cartApi: {
    getCart: vi.fn(),
    addItem: vi.fn(),
    updateQuantity: vi.fn(),
    removeItem: vi.fn(),
    clearCart: vi.fn(),
  },
}))

import { cartApi } from '@/api/cart'

const mockCartItems = [
  { id: 1, productId: 1, productName: '상품A', price: 10000, quantity: 2, subtotal: 20000 },
  { id: 2, productId: 2, productName: '상품B', price: 5000, quantity: 1, subtotal: 5000 },
]

describe('useCartStore', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('초기 상태 - 빈 장바구니', () => {
    const store = useCartStore()
    expect(store.items).toHaveLength(0)
    expect(store.totalCount).toBe(0)
    expect(store.totalAmount).toBe(0)
  })

  it('장바구니 불러오기', async () => {
    vi.mocked(cartApi.getCart).mockResolvedValue({ data: { data: mockCartItems } } as any)

    const store = useCartStore()
    await store.fetchCart()

    expect(store.items).toHaveLength(2)
    expect(store.totalCount).toBe(3)
    expect(store.totalAmount).toBe(25000)
  })

  it('상품 추가 후 장바구니 갱신', async () => {
    vi.mocked(cartApi.addItem).mockResolvedValue({} as any)
    vi.mocked(cartApi.getCart).mockResolvedValue({ data: { data: mockCartItems } } as any)

    const store = useCartStore()
    await store.addItem(1, 2)

    expect(cartApi.addItem).toHaveBeenCalledWith(1, 2)
    expect(cartApi.getCart).toHaveBeenCalled()
  })

  it('장바구니 비우기', async () => {
    vi.mocked(cartApi.clearCart).mockResolvedValue({} as any)

    const store = useCartStore()
    store.items = [...mockCartItems] as any

    await store.clearCart()

    expect(store.items).toHaveLength(0)
    expect(cartApi.clearCart).toHaveBeenCalled()
  })

  it('totalCount - 수량 합계 계산', () => {
    const store = useCartStore()
    store.items = mockCartItems as any

    expect(store.totalCount).toBe(3)
  })

  it('totalAmount - 금액 합계 계산', () => {
    const store = useCartStore()
    store.items = mockCartItems as any

    expect(store.totalAmount).toBe(25000)
  })
})
