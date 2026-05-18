<template>
  <div class="max-w-4xl mx-auto px-4 py-10">
    <h1 class="text-2xl font-bold text-gray-900 mb-8">장바구니</h1>

    <div v-if="cartStore.items.length === 0" class="text-center py-20 text-gray-400">
      <svg class="w-16 h-16 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1"
          d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
      </svg>
      <p class="text-lg mb-4">장바구니가 비었습니다.</p>
      <RouterLink to="/" class="btn-primary">쇼핑 계속하기</RouterLink>
    </div>

    <div v-else class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <!-- 장바구니 목록 -->
      <div class="lg:col-span-2 space-y-3">
        <div v-for="item in cartStore.items" :key="item.id" class="card flex gap-4">
          <div class="w-20 h-20 bg-gray-100 rounded-lg shrink-0 overflow-hidden">
            <img v-if="item.imageUrl" :src="item.imageUrl" :alt="item.productName"
              class="w-full h-full object-cover" />
          </div>
          <div class="flex-1">
            <p class="font-medium text-gray-900 mb-1">{{ item.productName }}</p>
            <p class="text-red-500 font-semibold">{{ formatPrice(item.price) }}</p>
          </div>
          <div class="flex flex-col items-end justify-between">
            <button @click="cartStore.removeItem(item.id)" class="text-gray-400 hover:text-red-500">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
            <div class="flex items-center border rounded-lg">
              <button @click="cartStore.updateQuantity(item.id, item.quantity - 1)"
                class="px-2 py-1 text-gray-600 hover:bg-gray-100 text-sm">-</button>
              <span class="px-3 py-1 text-sm">{{ item.quantity }}</span>
              <button @click="cartStore.updateQuantity(item.id, item.quantity + 1)"
                class="px-2 py-1 text-gray-600 hover:bg-gray-100 text-sm">+</button>
            </div>
            <p class="text-sm font-semibold">{{ formatPrice(item.subtotal) }}</p>
          </div>
        </div>
      </div>

      <!-- 결제 요약 -->
      <div class="lg:col-span-1">
        <div class="card sticky top-24">
          <h2 class="font-semibold text-gray-800 mb-4">주문 요약</h2>
          <div class="space-y-2 mb-4">
            <div class="flex justify-between text-sm text-gray-600">
              <span>상품 금액</span><span>{{ formatPrice(cartStore.totalAmount) }}</span>
            </div>
            <div class="flex justify-between text-sm text-gray-600">
              <span>배송비</span><span class="text-green-600">무료</span>
            </div>
          </div>
          <div class="border-t pt-4 flex justify-between font-bold text-lg">
            <span>합계</span><span class="text-red-500">{{ formatPrice(cartStore.totalAmount) }}</span>
          </div>
          <RouterLink to="/checkout" class="btn-primary w-full block text-center mt-4">
            주문하기 ({{ cartStore.totalCount }}개)
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { formatPrice } from '@/utils/format'

const cartStore = useCartStore()
onMounted(() => cartStore.fetchCart())
</script>
