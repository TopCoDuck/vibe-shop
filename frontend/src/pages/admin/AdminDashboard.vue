<template>
  <div>
    <h2 class="text-xl font-bold text-gray-800 mb-6">대시보드</h2>
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <div class="card border-l-4 border-red-500">
        <p class="text-sm text-gray-500 mb-1">전체 주문</p>
        <p class="text-3xl font-bold text-gray-800">{{ stats.totalOrders }}</p>
      </div>
      <div class="card border-l-4 border-blue-500">
        <p class="text-sm text-gray-500 mb-1">전체 상품</p>
        <p class="text-3xl font-bold text-gray-800">{{ stats.totalProducts }}</p>
      </div>
      <div class="card border-l-4 border-green-500">
        <p class="text-sm text-gray-500 mb-1">최근 주문</p>
        <p class="text-3xl font-bold text-gray-800">{{ stats.recentOrders }}</p>
      </div>
    </div>
    <div class="mt-8 card">
      <h3 class="font-semibold text-gray-700 mb-4">빠른 링크</h3>
      <div class="flex gap-3">
        <RouterLink to="/admin/products" class="btn-primary text-sm">상품 관리</RouterLink>
        <RouterLink to="/admin/orders" class="btn-secondary text-sm">주문 관리</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { productApi } from '@/api/products'
import { orderApi } from '@/api/orders'

const stats = ref({ totalOrders: 0, totalProducts: 0, recentOrders: 0 })

onMounted(async () => {
  const [products, orders] = await Promise.all([
    productApi.getAllForAdmin({ page: 0, size: 1 }),
    orderApi.getAllOrders({ page: 0, size: 1 })
  ])
  stats.value.totalProducts = products.data.data.totalElements
  stats.value.totalOrders = orders.data.data.totalElements
  stats.value.recentOrders = orders.data.data.totalElements
})
</script>
