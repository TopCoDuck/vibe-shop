<template>
  <div class="max-w-4xl mx-auto px-4 py-10">
    <h1 class="text-2xl font-bold text-gray-900 mb-8">주문 내역</h1>

    <div v-if="loading" class="space-y-4">
      <div v-for="n in 3" :key="n" class="card animate-pulse h-28"></div>
    </div>

    <div v-else-if="orders.length === 0" class="text-center py-20 text-gray-400">
      <p class="text-lg">주문 내역이 없습니다.</p>
      <RouterLink to="/" class="btn-primary mt-4 inline-block">쇼핑하러 가기</RouterLink>
    </div>

    <div v-else class="space-y-4">
      <RouterLink
        v-for="order in orders"
        :key="order.id"
        :to="`/orders/${order.id}`"
        class="card block hover:shadow-md transition-shadow"
      >
        <div class="flex items-center justify-between mb-3">
          <span class="text-sm text-gray-400">주문번호: #{{ order.id }}</span>
          <span :class="statusClass(order.status)" class="badge">{{ ORDER_STATUS_LABEL[order.status] }}</span>
        </div>
        <p class="font-medium text-gray-800 mb-1">
          {{ order.items[0]?.productName }}
          <span v-if="order.items.length > 1" class="text-gray-400"> 외 {{ order.items.length - 1 }}건</span>
        </p>
        <div class="flex items-center justify-between">
          <span class="text-sm text-gray-400">{{ formatDate(order.createdAt) }}</span>
          <span class="font-bold text-red-500">{{ formatPrice(order.totalAmount) }}</span>
        </div>
      </RouterLink>
    </div>

    <Pagination v-if="totalPages > 1" :current="currentPage" :total="totalPages" @change="onPageChange" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { orderApi } from '@/api/orders'
import Pagination from '@/components/common/Pagination.vue'
import { formatPrice, formatDate } from '@/utils/format'
import { ORDER_STATUS_LABEL } from '@/types'
import type { Order } from '@/types'

const orders = ref<Order[]>([])
const loading = ref(false)
const currentPage = ref(0)
const totalPages = ref(0)

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

async function fetchOrders() {
  loading.value = true
  try {
    const res = await orderApi.getMyOrders({ page: currentPage.value, size: 10 })
    orders.value = res.data.data.content
    totalPages.value = res.data.data.totalPages
  } finally {
    loading.value = false
  }
}

function onPageChange(page: number) {
  currentPage.value = page
  fetchOrders()
}

onMounted(fetchOrders)
</script>
