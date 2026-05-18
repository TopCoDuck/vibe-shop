<template>
  <div class="min-h-screen flex">
    <aside class="w-56 bg-gray-900 text-white flex flex-col">
      <div class="p-6">
        <RouterLink to="/" class="text-xl font-bold text-red-400">Vibe Shop</RouterLink>
        <p class="text-xs text-gray-400 mt-1">관리자 패널</p>
      </div>
      <nav class="flex-1 px-3">
        <RouterLink to="/admin" class="nav-item" exact-active-class="bg-red-600">대시보드</RouterLink>
        <RouterLink to="/admin/products" class="nav-item" active-class="bg-red-600">상품 관리</RouterLink>
        <RouterLink to="/admin/orders" class="nav-item" active-class="bg-red-600">주문 관리</RouterLink>
      </nav>
      <div class="p-4 border-t border-gray-700">
        <RouterLink to="/" class="text-sm text-gray-400 hover:text-white">← 쇼핑몰로</RouterLink>
      </div>
    </aside>
    <div class="flex-1 flex flex-col bg-gray-50">
      <header class="bg-white shadow-sm px-8 py-4 flex items-center justify-between">
        <h1 class="text-lg font-semibold text-gray-800">관리자</h1>
        <span class="text-sm text-gray-500">{{ auth.user?.name }}</span>
      </header>
      <main class="flex-1 p-8">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
</script>

<style scoped>
.nav-item {
  @apply flex items-center px-4 py-2.5 rounded-lg text-sm text-gray-300 hover:bg-gray-700 hover:text-white mb-1 transition-colors;
}
</style>
