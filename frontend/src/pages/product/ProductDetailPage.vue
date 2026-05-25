<template>
  <div class="max-w-7xl mx-auto px-4 py-6">
    <div v-if="loading" class="flex justify-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-4 border-red-500 border-t-transparent"></div>
    </div>

    <div v-else-if="product">
      <!-- 브레드크럼 -->
      <p class="text-xs text-gray-500 mb-4">
        <RouterLink to="/" class="hover:text-red-500">홈</RouterLink>
        <span v-if="product.categoryName"> › {{ product.categoryName }}</span>
        <span class="text-gray-700"> › {{ product.name }}</span>
      </p>

      <div class="grid grid-cols-1 md:grid-cols-[520px_1fr] gap-8">
        <!-- ── 이미지 갤러리 ── -->
        <div>
          <div class="aspect-square bg-gray-100 rounded overflow-hidden">
            <img v-if="product.imageUrl" :src="product.imageUrl" :alt="product.name"
              class="w-full h-full object-cover" />
            <div v-else class="w-full h-full flex items-center justify-center text-gray-300">
              <svg class="w-24 h-24" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1"
                  d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
            </div>
          </div>
          <!-- 썸네일 줄 (같은 이미지 반복 - 백엔드에 다중이미지 추가 전까지 자리잡기용) -->
          <div v-if="product.imageUrl" class="grid grid-cols-5 gap-2 mt-3">
            <div v-for="i in 5" :key="i"
              class="aspect-square bg-gray-100 rounded overflow-hidden cursor-pointer"
              :class="i === 1 ? 'ring-2 ring-red-500' : 'border border-gray-200'">
              <img :src="product.imageUrl" class="w-full h-full object-cover" alt="" />
            </div>
          </div>
        </div>

        <!-- ── 상품 정보 패널 ── -->
        <div class="flex flex-col">
          <!-- 카테고리 + 찜 -->
          <div class="flex items-start justify-between">
            <p class="text-xs text-gray-500">{{ product.categoryName }}</p>
            <WishlistButton :productId="product.id" class="flex-shrink-0" />
          </div>
          <h1 class="text-2xl font-bold text-gray-900 leading-snug mt-1.5">{{ product.name }}</h1>

          <!-- 평점 (UI 데모용 부가정보) -->
          <div class="mt-3 flex items-center gap-1.5 text-sm">
            <span class="text-deal-400 text-base leading-none">★★★★★</span>
            <span class="font-bold text-gray-900">{{ formatRating(display.rating) }}</span>
            <span class="text-gray-500">상품평 {{ display.reviewCount.toLocaleString() }}건</span>
          </div>

          <!-- 가격 블록 -->
          <div class="border-y border-gray-200 py-5 mt-5">
            <p class="text-sm text-gray-400 line-through">{{ formatPrice(display.originalPrice) }}</p>
            <div class="flex items-baseline gap-2 mt-1">
              <span class="text-2xl font-black text-red-500">{{ display.discountRate }}%</span>
              <span class="text-3xl font-black text-gray-900">{{ formatPrice(product.price) }}</span>
            </div>
            <p class="mt-2 text-xs text-gray-500">
              <span class="badge-coupon mr-1.5">쿠폰</span>
              와우회원 추가 5% 할인 적용가
              <strong class="text-red-500">{{ formatPrice(display.wowPrice) }}</strong>
            </p>
          </div>

          <!-- 배송 정보 -->
          <div v-if="display.rocketDelivery" class="py-4 border-b border-gray-200 text-sm">
            <div class="flex items-center gap-2">
              <span class="badge-rocket">로켓배송</span>
              <span class="font-bold text-gray-900">내일(수) 도착 보장</span>
            </div>
            <p class="mt-1.5 text-xs text-gray-500">평일 자정 전 주문 시 · 무료배송 · 30일 무료반품</p>
          </div>

          <!-- 재고 -->
          <div class="mt-4 flex justify-between items-center text-sm">
            <span class="text-gray-500">재고</span>
            <span :class="product.stock > 0 ? 'text-green-600 font-bold' : 'text-red-500 font-bold'">
              {{ product.stock > 0 ? `${product.stock}개 남음` : '품절' }}
            </span>
          </div>

          <!-- 수량 -->
          <div class="mt-5 flex items-center gap-3">
            <label class="text-sm font-medium text-gray-700">수량</label>
            <div class="flex items-center border border-gray-300 rounded">
              <button @click="qty > 1 && qty--" class="px-3 py-2 text-gray-600 hover:bg-gray-100">−</button>
              <span class="px-4 py-2 text-sm font-bold border-x border-gray-300">{{ qty }}</span>
              <button @click="qty < product.stock && qty++" class="px-3 py-2 text-gray-600 hover:bg-gray-100">+</button>
            </div>
          </div>

          <!-- CTA -->
          <div class="flex gap-2 mt-6">
            <button @click="addToCart" :disabled="product.stock === 0 || addingToCart"
              class="btn-outline-bold flex-1 text-base">
              {{ addingToCart ? '담는 중...' : '장바구니 담기' }}
            </button>
            <button @click="buyNow" :disabled="product.stock === 0"
              class="btn-primary-bold flex-1 text-base">
              바로 구매
            </button>
          </div>

          <p v-if="cartMessage" class="text-green-600 text-sm mt-3">{{ cartMessage }}</p>
        </div>
      </div>

      <!-- ── 탭 ── -->
      <div class="mt-12 border-t-2 border-gray-800">
        <div class="flex border-b border-gray-200 overflow-x-auto">
          <button v-for="(t, i) in tabs" :key="t"
            @click="activeTab = i"
            class="px-6 py-4 text-sm font-bold -mb-px whitespace-nowrap"
            :class="activeTab === i
              ? 'text-gray-900 border-b-[3px] border-gray-900'
              : 'text-gray-500 hover:text-gray-700'">
            {{ t }}
          </button>
        </div>

        <div v-if="activeTab === 0" class="py-8">
          <h2 class="font-extrabold text-gray-900 text-lg mb-4">상품 설명</h2>
          <p v-if="product.description"
            class="text-gray-700 text-sm leading-relaxed whitespace-pre-line max-w-3xl">
            {{ product.description }}
          </p>
          <p v-else class="text-gray-400 text-sm">상품 상세 설명이 없습니다.</p>
        </div>
        <div v-else class="py-20 text-center text-gray-400 text-sm">
          준비 중입니다.
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import { productApi } from '@/api/products'
import { useCartStore } from '@/stores/cart'
import { useAuthStore } from '@/stores/auth'
import { useWishlistStore } from '@/stores/wishlist'
import { formatPrice } from '@/utils/format'
import { getProductDisplay, formatRating } from '@/utils/productDisplay'
import type { Product } from '@/types'
import WishlistButton from '@/components/product/WishlistButton.vue'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const auth = useAuthStore()
const wishlistStore = useWishlistStore()

const product = ref<Product | null>(null)
const loading = ref(false)
const qty = ref(1)
const addingToCart = ref(false)
const cartMessage = ref('')
const activeTab = ref(0)

const display = computed(() =>
  product.value ? getProductDisplay(product.value) : { originalPrice: 0, discountRate: 0, rating: 0, reviewCount: 0, rocketDelivery: false, wowPrice: 0 }
)

const tabs = computed(() => [
  '상품상세',
  `상품평 (${display.value.reviewCount.toLocaleString()})`,
  '상품문의',
  '배송/교환/반품',
])

async function fetchProduct() {
  loading.value = true
  try {
    const res = await productApi.getOne(Number(route.params.id))
    product.value = res.data.data
    // 로그인 상태면 찜 여부 로드
    if (auth.isLoggedIn && !wishlistStore.loaded) {
      await wishlistStore.fetchWishlistIds()
    }
  } finally {
    loading.value = false
  }
}

async function addToCart() {
  if (!auth.isLoggedIn) return router.push('/login')
  addingToCart.value = true
  try {
    await cartStore.addItem(product.value!.id, qty.value)
    cartMessage.value = '장바구니에 담겼습니다!'
    setTimeout(() => cartMessage.value = '', 2000)
  } finally {
    addingToCart.value = false
  }
}

async function buyNow() {
  if (!auth.isLoggedIn) return router.push('/login')
  await cartStore.addItem(product.value!.id, qty.value)
  router.push('/cart')
}

onMounted(fetchProduct)
</script>
