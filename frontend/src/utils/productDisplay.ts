// src/utils/productDisplay.ts
//
// 쿠팡 스타일 UI를 위한 부가 정보(평점, 리뷰수, 할인%, 로켓배송 등)는
// 현재 백엔드 응답에 없습니다. 백엔드가 추가되기 전까지 화면을 풍부하게 보이게
// 하려고, product.id 기반으로 결정적인(같은 상품은 항상 같은) 가짜 값을 만듭니다.
//
// 백엔드에서 실제 필드를 내려주기 시작하면, 이 파일의 함수들을
// "field 있으면 그 값, 없으면 derive" 패턴으로 바꾸면 됩니다.
//
// ── 적용 위치 ──
//   - components/product/ProductCard.vue  (목록 카드 부가정보)
//   - pages/product/ProductDetailPage.vue (상세 가격/배송/뱃지)
//   - pages/cart/CartPage.vue             (원가 표시, 로켓배송 그룹)

import type { Product, CartItem } from '@/types'

// 단순 시드 PRNG (id가 같으면 항상 같은 값)
function seeded(id: number, salt = 0) {
  const x = Math.sin(id * 9301 + salt * 49297) * 233280
  return x - Math.floor(x)
}

export interface ProductDisplay {
  /** 표시용 원가 (현재가의 ~115%~140%) */
  originalPrice: number
  /** 표시용 할인율 % */
  discountRate: number
  /** 평점 (4.4 ~ 4.9) */
  rating: number
  /** 리뷰 수 */
  reviewCount: number
  /** 로켓배송 여부 (대다수 상품 true) */
  rocketDelivery: boolean
  /** 와우회원가 (현재가 * 0.95) */
  wowPrice: number
}

export function getProductDisplay(p: Pick<Product, 'id' | 'price'>): ProductDisplay {
  const r1 = seeded(p.id, 1)
  const r2 = seeded(p.id, 2)
  const r3 = seeded(p.id, 3)
  const discountRate = Math.round(10 + r1 * 30) // 10~40%
  const originalPrice = Math.round(p.price / (1 - discountRate / 100) / 100) * 100
  return {
    originalPrice,
    discountRate,
    rating: 4.4 + r2 * 0.5,
    reviewCount: Math.round(50 + r3 * 5000),
    rocketDelivery: r3 > 0.15, // 약 85% 상품
    wowPrice: Math.round(p.price * 0.95),
  }
}

export function getCartItemDisplay(item: Pick<CartItem, 'productId' | 'price'>): ProductDisplay {
  return getProductDisplay({ id: item.productId, price: item.price })
}

// 헬퍼: 소수점 1자리
export function formatRating(r: number): string {
  return r.toFixed(1)
}
