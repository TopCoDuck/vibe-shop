<template>
  <div>
    <h2 class="text-xl font-bold text-gray-800 mb-6">주문 관리</h2>

    <div class="card overflow-hidden p-0">
      <table class="w-full text-sm">
        <thead class="bg-gray-50 border-b">
          <tr>
            <th class="text-left px-4 py-3 text-gray-600 font-medium">주문번호</th>
            <th class="text-left px-4 py-3 text-gray-600 font-medium">수령인</th>
            <th class="text-left px-4 py-3 text-gray-600 font-medium">상품</th>
            <th class="text-right px-4 py-3 text-gray-600 font-medium">결제금액</th>
            <th class="text-center px-4 py-3 text-gray-600 font-medium">상태</th>
            <th class="text-left px-4 py-3 text-gray-600 font-medium">주문일</th>
            <th class="text-center px-4 py-3 text-gray-600 font-medium">상태 변경</th>
          </tr>
        </thead>
        <tbody class="divide-y">
          <tr v-for="order in orders" :key="order.id" class="hover:bg-gray-50">
            <td class="px-4 py-3 text-gray-400">#{{ order.id }}</td>
            <td class="px-4 py-3 font-medium">{{ order.receiverName }}</td>
            <td class="px-4 py-3 text-gray-600">
              {{ order.items[0]?.productName }}
              <span v-if="order.items.length > 1" class="text-gray-400">외 {{ order.items.length - 1 }}건</span>
            </td>
            <td class="px-4 py-3 text-right font-semibold text-red-500">{{ formatPrice(order.totalAmount) }}</td>
            <td class="px-4 py-3 text-center">
              <span :class="statusClass(order.status)" class="badge">{{ ORDER_STATUS_LABEL[order.status] }}</span>
            </td>
            <td class="px-4 py-3 text-gray-400">{{ formatDate(order.createdAt) }}</td>
            <td class="px-4 py-3 text-center">
              <select @change="updateStatus(order.id, ($event.target as HTMLSelectElement).value)"
                :value="order.status" class="text-xs border rounded px-2 py-1">
                <option v-for="(label, key) in ORDER_STATUS_LABEL" :key="key" :value="key">{{ label }}</option>
              </select>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <Pagination v-if="totalPages > 1" :current="currentPage" :total="totalPages" @change="onPageChange" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { orderApi } from '@/api/orders'
import Pagination from '@/components/common/Pagination.vue'
import { formatPrice, formatDate } from '@/utils/format'
import { ORDER_STATUS_LABEL } from '@/types'
import type { Order } from '@/types'

const orders = ref<Order[]>([])
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
  const res = await orderApi.getAllOrders({ page: currentPage.value, size: 20 })
  orders.value = res.data.data.content
  totalPages.value = res.data.data.totalPages
}

function onPageChange(page: number) {
  currentPage.value = page
  fetchOrders()
}

async function updateStatus(orderId: number, status: string) {
  await orderApi.updateOrderStatus(orderId, status)
  await fetchOrders()
}

onMounted(fetchOrders)
</script>
