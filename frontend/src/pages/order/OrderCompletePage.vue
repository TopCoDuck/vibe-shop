<template>
  <div class="max-w-3xl mx-auto px-4 py-10">
    <!-- 진행 스텝 -->
    <div class="flex justify-center gap-8 md:gap-10 mb-8 text-sm">
      <span class="text-gray-400 font-medium">01 장바구니</span>
      <span class="text-gray-400 font-medium">02 주문/결제</span>
      <span class="text-red-500 font-extrabold">03 주문완료</span>
    </div>

    <div v-if="loading" class="flex justify-center py-24">
      <div class="animate-spin rounded-full h-10 w-10 border-4 border-red-500 border-t-transparent"></div>
    </div>

    <template v-else-if="order">
      <!-- 완료 히어로 -->
      <div class="bg-white rounded-xl shadow-sm p-10 text-center mb-4">
        <div class="w-16 h-16 rounded-full bg-green-500 flex items-center justify-center mx-auto mb-5">
          <svg class="w-9 h-9 text-white" fill="none" stroke="currentColor" stroke-width="3"
            stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24">
            <polyline points="20 6 9 17 4 12" />
          </svg>
        </div>
        <h1 class="text-xl font-black text-gray-900 mb-2">주문이 완료되었습니다!</h1>
        <span class="inline-block bg-red-50 text-red-500 font-bold text-xs px-3 py-1 rounded-full mb-3">
          주문번호 #{{ order.id }}
        </span>
        <p class="text-sm text-gray-500">주문하신 상품을 빠르게 배송해 드릴게요.</p>
      </div>

      <!-- 로켓배송 배너 -->
      <div class="bg-blue-50 border border-blue-100 rounded-xl px-5 py-3.5 mb-4 flex items-center gap-3 text-sm text-blue-700">
        <span class="bg-blue-700 text-white text-[10px] font-black px-2 py-0.5 rounded shrink-0">로켓배송</span>
        <span>내일 도착 예정 · <strong>무료배송</strong></span>
      </div>

      <!-- 주문상품 + 배송정보 -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
        <!-- 주문 상품 -->
        <div class="bg-white rounded-xl shadow-sm p-5">
          <h2 class="font-bold text-gray-900 text-sm mb-3 pb-3 border-b border-gray-100">주문 상품</h2>
          <div class="space-y-3">
            <div v-for="item in order.items" :key="item.productId" class="flex items-center gap-3">
              <div class="w-12 h-12 rounded-lg bg-gray-100 shrink-0 overflow-hidden">
                <img v-if="item.imageUrl" :src="item.imageUrl" class="w-full h-full object-cover" />
              </div>
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-gray-900 truncate">{{ item.productName }}</p>
                <p class="text-xs text-gray-400">{{ item.quantity }}개</p>
              </div>
              <p class="text-sm font-bold text-gray-900 shrink-0">{{ formatPrice(item.subtotal) }}</p>
            </div>
          </div>
        </div>

        <!-- 배송 정보 -->
        <div class="bg-white rounded-xl shadow-sm p-5">
          <h2 class="font-bold text-gray-900 text-sm mb-3 pb-3 border-b border-gray-100">배송 정보</h2>
          <dl class="space-y-2 text-sm">
            <div class="flex gap-3">
              <dt class="w-12 text-gray-400 shrink-0">수령인</dt>
              <dd class="font-medium text-gray-700">{{ order.receiverName }}</dd>
            </div>
            <div class="flex gap-3">
              <dt class="w-12 text-gray-400 shrink-0">연락처</dt>
              <dd class="font-medium text-gray-700">{{ order.receiverPhone }}</dd>
            </div>
            <div class="flex gap-3">
              <dt class="w-12 text-gray-400 shrink-0">배송지</dt>
              <dd class="font-medium text-gray-700">{{ order.shippingAddress }}</dd>
            </div>
          </dl>
        </div>
      </div>

      <!-- 결제 금액 -->
      <div class="bg-white rounded-xl shadow-sm p-5 mb-5">
        <h2 class="font-bold text-gray-900 text-sm mb-3 pb-3 border-b border-gray-100">결제 금액</h2>
        <div class="space-y-2 text-sm text-gray-500">
          <div class="flex justify-between">
            <span>상품 금액</span>
            <span>{{ formatPrice(order.totalAmount) }}</span>
          </div>
          <div class="flex justify-between">
            <span>배송비</span>
            <span class="text-green-600 font-medium">무료</span>
          </div>
        </div>
        <div class="flex justify-between font-black text-base pt-3 mt-2 border-t border-gray-100">
          <span>최종 결제금액</span>
          <span class="text-red-500">{{ formatPrice(order.totalAmount) }}</span>
        </div>
      </div>

      <!-- 버튼 -->
      <div class="flex gap-3">
        <RouterLink :to="`/orders/${order.id}`"
          class="flex-1 py-3.5 text-center text-sm font-bold text-gray-700 border-2 border-gray-200 rounded-lg hover:bg-gray-50 transition-colors">
          주문 상세 보기
        </RouterLink>
        <RouterLink to="/"
          class="flex-1 py-3.5 text-center text-sm font-bold text-white bg-red-500 rounded-lg hover:bg-red-600 transition-colors">
          쇼핑 계속하기 →
        </RouterLink>
      </div>
    </template>

    <!-- 에러 -->
    <div v-else class="text-center py-20 text-gray-400">
      <p>주문 정보를 불러올 수 없습니다.</p>
      <RouterLink to="/" class="text-red-500 font-bold mt-4 inline-block">홈으로</RouterLink>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import { orderApi } from '@/api/orders'
import { formatPrice } from '@/utils/format'
import type { Order } from '@/types'

const route = useRoute()
const order = ref<Order | null>(null)
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await orderApi.getOrder(Number(route.params.id))
    order.value = res.data.data
  } finally {
    loading.value = false
  }
})
</script>
