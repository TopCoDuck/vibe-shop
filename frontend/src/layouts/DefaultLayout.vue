<template>
  <div class="min-h-screen flex flex-col">
    <header class="bg-white shadow-sm sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between h-16">
          <RouterLink to="/" class="text-2xl font-bold text-red-500">Vibe Shop</RouterLink>

          <div class="flex-1 max-w-xl mx-8">
            <input
              v-model="keyword"
              @keyup.enter="search"
              type="text"
              placeholder="상품을 검색하세요..."
              class="input-field"
            />
          </div>

          <nav class="flex items-center gap-4">
            <RouterLink to="/cart" class="relative p-2 text-gray-600 hover:text-red-500">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
              <span v-if="cartStore.totalCount > 0"
                class="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
                {{ cartStore.totalCount }}
              </span>
            </RouterLink>

            <template v-if="auth.isLoggedIn">
              <RouterLink v-if="auth.isAdmin" to="/admin" class="text-sm text-gray-600 hover:text-red-500">관리자</RouterLink>
              <RouterLink to="/orders" class="text-sm text-gray-600 hover:text-red-500">주문내역</RouterLink>
              <button @click="logout" class="text-sm text-gray-600 hover:text-red-500">로그아웃</button>
            </template>
            <template v-else>
              <RouterLink to="/login" class="text-sm text-gray-600 hover:text-red-500">로그인</RouterLink>
              <RouterLink to="/signup" class="btn-primary text-sm">회원가입</RouterLink>
            </template>
          </nav>
        </div>
      </div>
    </header>

    <main class="flex-1">
      <RouterView />
    </main>

    <footer class="bg-gray-800 text-gray-400 py-8 mt-16">
      <div class="max-w-7xl mx-auto px-4 text-center text-sm">
        <p>© 2026 Vibe Shop. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, RouterLink, RouterView } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'

const auth = useAuthStore()
const cartStore = useCartStore()
const router = useRouter()
const keyword = ref('')

if (auth.isLoggedIn) cartStore.fetchCart()

function search() {
  if (keyword.value.trim()) {
    router.push({ path: '/', query: { keyword: keyword.value } })
  }
}

function logout() {
  auth.logout()
  router.push('/login')
}
</script>
