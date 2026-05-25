<template>
  <div class="max-w-4xl mx-auto px-4 py-10">
    <h1 class="text-2xl font-bold text-gray-900 mb-8">마이페이지</h1>

    <!-- 탭 -->
    <div class="flex border-b mb-8">
      <button
        @click="activeTab = 'orders'"
        :class="activeTab === 'orders'
          ? 'border-b-2 border-red-500 text-red-500'
          : 'text-gray-500 hover:text-gray-700'"
        class="px-6 py-3 text-sm font-medium transition-colors"
      >
        주문내역
      </button>
      <button
        @click="activeTab = 'wishlist'"
        :class="activeTab === 'wishlist'
          ? 'border-b-2 border-red-500 text-red-500'
          : 'text-gray-500 hover:text-gray-700'"
        class="px-6 py-3 text-sm font-medium transition-colors"
      >
        찜리스트
        <span v-if="wishlistStore.items.length > 0"
          class="ml-1 bg-red-100 text-red-500 text-xs rounded-full px-2 py-0.5">
          {{ wishlistStore.items.length }}
        </span>
      </button>
    </div>

    <!-- 주문내역 탭 -->
    <div v-if="activeTab === 'orders'">
      <div v-if="ordersLoading" class="space-y-4">
        <div v-for="n in 3" :key="n" class="card animate-pulse h-28"></div>
      </div>

      <div v-else-if="orders.length === 0" class="text-center py-20 text-gray-400">
        <svg class="w-16 h-16 mx-auto mb-4 text-gray-200" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
            d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
        </svg>
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
            <span :class="statusClass(order.status)"
              class="px-2 py-1 text-xs font-medium rounded-full">
              {{ ORDER_STATUS_LABEL[order.status] }}
            </span>
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

      <Pagination
        v-if="totalPages > 1"
        :current="currentPage"
        :total="totalPages"
        @change="onPageChange"
      />
    </div>

    <!-- 찜리스트 탭 -->
    <div v-if="activeTab === 'wishlist'">
      <div v-if="wishlistLoading" class="grid grid-cols-2 md:grid-cols-3 gap-4">
        <div v-for="n in 6" :key="n" class="card animate-pulse h-48"></div>
      </div>

      <div v-else-if="wishlistStore.items.length === 0" class="text-center py-20 text-gray-400">
        <svg class="w-16 h-16 mx-auto mb-4 text-gray-200" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
            d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
        </svg>
        <p class="text-lg">찜한 상품이 없습니다.</p>
        <RouterLink to="/" class="btn-primary mt-4 inline-block">상품 둘러보기</RouterLink>
      </div>

      <div v-else class="grid grid-cols-2 md:grid-cols-3 gap-4">
        <div
          v-for="item in wishlistStore.items"
          :key="item.id"
          class="card relative group hover:shadow-md transition-shadow"
        >
          <RouterLink :to="`/products/${item.productId}`" class="block">
            <div class="aspect-square bg-gray-100 rounded-lg mb-3 overflow-hidden">
              <img
                v-if="item.imageUrl"
                :src="item.imageUrl"
                :alt="item.productName"
                class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
              />
              <div v-else class="w-full h-full flex items-center justify-center text-gray-300">
                <svg class="w-12 h-12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1"
                    d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
              </div>
            </div>
            <p class="text-xs text-gray-400 mb-1">{{ item.categoryName }}</p>
            <h3 class="font-medium text-gray-900 text-sm mb-1 line-clamp-2 group-hover:text-red-500 transition-colors">
              {{ item.productName }}
            </h3>
            <p class="font-bold text-gray-900">{{ formatPrice(item.price) }}</p>
            <p v-if="item.stock === 0" class="text-xs text-red-500 mt-1">품절</p>
          </RouterLink>

          <!-- 찜 해제 버튼 -->
          <button
            @click="removeWishlist(item.productId)"
            class="absolute top-2 right-2 p-1 text-red-400 hover:text-red-600 transition-colors"
            title="찜 해제"
          >
            <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
              <path
                d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
              />
            </svg>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { RouterLink } from 'vue-router'
import { orderApi } from '@/api/orders'
import { useWishlistStore } from '@/stores/wishlist'
import Pagination from '@/components/common/Pagination.vue'
import { formatPrice, formatDate } from '@/utils/format'
import { ORDER_STATUS_LABEL } from '@/types'
import type { Order } from '@/types'

const activeTab = ref<'orders' | 'wishlist'>('orders')

const wishlistStore = useWishlistStore()
const wishlistLoading = ref(false)

const orders = ref<Order[]>([])
const ordersLoading = ref(false)
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

async function fetchOrders(page = 0) {
  ordersLoading.value = true
  try {
    const res = await orderApi.getMyOrders({ page, size: 10 })
    orders.value = res.data.data.content
    totalPages.value = res.data.data.totalPages
    currentPage.value = page
  } finally {
    ordersLoading.value = false
  }
}

async function fetchWishlist() {
  if (wishlistStore.loaded && wishlistStore.items.length > 0) return
  wishlistLoading.value = true
  try {
    await wishlistStore.fetchWishlist()
  } finally {
    wishlistLoading.value = false
  }
}

async function removeWishlist(productId: number) {
  await wishlistStore.toggle(productId)
}

function onPageChange(page: number) {
  fetchOrders(page)
}

watch(activeTab, (tab) => {
  if (tab === 'wishlist') fetchWishlist()
})

onMounted(() => {
  fetchOrders()
})
</script>
