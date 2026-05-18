<template>
  <div class="max-w-3xl mx-auto px-4 py-10">
    <div class="flex items-center gap-4 mb-8">
      <RouterLink to="/orders" class="text-gray-400 hover:text-gray-600">← 주문 내역</RouterLink>
      <h1 class="text-2xl font-bold text-gray-900">주문 상세</h1>
    </div>

    <div v-if="order" class="space-y-6">
      <!-- 주문 상태 -->
      <div class="card">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-400">주문번호 #{{ order.id }}</p>
            <p class="text-sm text-gray-400 mt-1">{{ formatDate(order.createdAt) }}</p>
          </div>
          <span :class="statusClass(order.status)" class="badge text-sm px-3 py-1">
            {{ ORDER_STATUS_LABEL[order.status] }}
          </span>
        </div>
        <button v-if="order.status === 'PENDING'" @click="cancelOrder"
          :disabled="cancelling" class="btn-outline mt-4 text-sm">
          {{ cancelling ? '취소 중...' : '주문 취소' }}
        </button>
      </div>

      <!-- 배송 정보 -->
      <div class="card">
        <h2 class="font-semibold text-gray-800 mb-3">배송 정보</h2>
        <dl class="space-y-2 text-sm">
          <div class="flex"><dt class="w-24 text-gray-400">수령인</dt><dd>{{ order.receiverName }}</dd></div>
          <div class="flex"><dt class="w-24 text-gray-400">연락처</dt><dd>{{ order.receiverPhone }}</dd></div>
          <div class="flex"><dt class="w-24 text-gray-400">배송지</dt><dd>{{ order.shippingAddress }}</dd></div>
        </dl>
      </div>

      <!-- 주문 상품 -->
      <div class="card">
        <h2 class="font-semibold text-gray-800 mb-4">주문 상품</h2>
        <div class="space-y-4">
          <div v-for="item in order.items" :key="item.productId" class="flex gap-4">
            <div class="w-16 h-16 bg-gray-100 rounded-lg shrink-0 overflow-hidden">
              <img v-if="item.imageUrl" :src="item.imageUrl" class="w-full h-full object-cover" />
            </div>
            <div class="flex-1">
              <p class="font-medium">{{ item.productName }}</p>
              <p class="text-sm text-gray-400">{{ formatPrice(item.price) }} × {{ item.quantity }}개</p>
            </div>
            <p class="font-semibold">{{ formatPrice(item.subtotal) }}</p>
          </div>
        </div>
        <div class="border-t mt-4 pt-4 flex justify-between font-bold text-lg">
          <span>합계</span>
          <span class="text-red-500">{{ formatPrice(order.totalAmount) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import { orderApi } from '@/api/orders'
import { formatPrice, formatDate } from '@/utils/format'
import { ORDER_STATUS_LABEL } from '@/types'
import type { Order } from '@/types'

const route = useRoute()
const order = ref<Order | null>(null)
const cancelling = ref(false)

function statusClass(status: string) {
  const map: Record<string, string> = {
    PENDING: 'bg-yellow-100 text-yellow-700',
    PAID: 'bg-blue-100 text-blue-700',
    SHIPPING: 'bg-purple-100 text-purple-700',
    DELIVERED: 'bg-green-100 text-green-700',
    CANCELLED: 'bg-gray-100 text-gray-500'
  }
  return map[status] || 'bg-gray-100 text-gray-500'
}

async function cancelOrder() {
  cancelling.value = true
  try {
    const res = await orderApi.cancelOrder(Number(route.params.id))
    order.value = res.data.data
  } finally {
    cancelling.value = false
  }
}

onMounted(async () => {
  const res = await orderApi.getOrder(Number(route.params.id))
  order.value = res.data.data
})
</script>
