<template>
  <div class="max-w-5xl mx-auto px-4 py-10">
    <div v-if="loading" class="flex justify-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-red-500"></div>
    </div>

    <div v-else-if="product" class="grid grid-cols-1 md:grid-cols-2 gap-10">
      <!-- 상품 이미지 -->
      <div class="aspect-square bg-gray-100 rounded-2xl overflow-hidden">
        <img v-if="product.imageUrl" :src="product.imageUrl" :alt="product.name"
          class="w-full h-full object-cover" />
        <div v-else class="w-full h-full flex items-center justify-center text-gray-300">
          <svg class="w-24 h-24" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1"
              d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
          </svg>
        </div>
      </div>

      <!-- 상품 정보 -->
      <div class="flex flex-col">
        <p class="text-sm text-gray-400 mb-2">{{ product.categoryName }}</p>
        <h1 class="text-2xl font-bold text-gray-900 mb-4">{{ product.name }}</h1>
        <p class="text-3xl font-bold text-red-500 mb-6">{{ formatPrice(product.price) }}</p>

        <div class="bg-gray-50 rounded-lg p-4 mb-6">
          <div class="flex items-center justify-between text-sm">
            <span class="text-gray-500">재고</span>
            <span :class="product.stock > 0 ? 'text-green-600' : 'text-red-500'" class="font-medium">
              {{ product.stock > 0 ? `${product.stock}개 남음` : '품절' }}
            </span>
          </div>
        </div>

        <div class="flex items-center gap-3 mb-6">
          <label class="text-sm font-medium text-gray-700">수량</label>
          <div class="flex items-center border rounded-lg">
            <button @click="qty > 1 && qty--" class="px-3 py-2 text-gray-600 hover:bg-gray-100">-</button>
            <span class="px-4 py-2 text-sm font-medium">{{ qty }}</span>
            <button @click="qty < product.stock && qty++" class="px-3 py-2 text-gray-600 hover:bg-gray-100">+</button>
          </div>
        </div>

        <div class="flex gap-3">
          <button @click="addToCart" :disabled="product.stock === 0 || addingToCart"
            class="btn-outline flex-1">
            {{ addingToCart ? '담는 중...' : '장바구니 담기' }}
          </button>
          <button @click="buyNow" :disabled="product.stock === 0"
            class="btn-primary flex-1">
            바로 구매
          </button>
        </div>

        <p v-if="cartMessage" class="text-green-600 text-sm mt-3">{{ cartMessage }}</p>

        <div v-if="product.description" class="mt-8 border-t pt-6">
          <h2 class="font-semibold text-gray-800 mb-3">상품 설명</h2>
          <p class="text-gray-600 text-sm leading-relaxed whitespace-pre-line">{{ product.description }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { productApi } from '@/api/products'
import { useCartStore } from '@/stores/cart'
import { useAuthStore } from '@/stores/auth'
import { formatPrice } from '@/utils/format'
import type { Product } from '@/types'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const auth = useAuthStore()

const product = ref<Product | null>(null)
const loading = ref(false)
const qty = ref(1)
const addingToCart = ref(false)
const cartMessage = ref('')

async function fetchProduct() {
  loading.value = true
  try {
    const res = await productApi.getOne(Number(route.params.id))
    product.value = res.data.data
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
