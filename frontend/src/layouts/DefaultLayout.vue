<template>
  <div class="min-h-screen flex flex-col">
    <header class="bg-white sticky top-0 z-50 border-b border-gray-100">
      <!-- ── 상단 유틸바 ── -->
      <div class="border-b border-gray-100 text-xs text-gray-500">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-8 flex items-center justify-end gap-5">
          <RouterLink to="/cs" class="hover:text-red-500">고객센터</RouterLink>
          <span class="cursor-pointer hover:text-red-500">입점신청</span>
          <template v-if="auth.isLoggedIn">
            <RouterLink v-if="auth.isAdmin" to="/admin" class="hover:text-red-500">관리자</RouterLink>
            <RouterLink to="/orders" class="hover:text-red-500">주문배송</RouterLink>
            <RouterLink to="/mypage" class="hover:text-red-500">마이페이지</RouterLink>
            <button @click="logout" class="hover:text-red-500">로그아웃</button>
          </template>
          <template v-else>
            <RouterLink to="/login" class="hover:text-red-500">로그인</RouterLink>
            <RouterLink to="/signup" class="hover:text-red-500">회원가입</RouterLink>
          </template>
        </div>
      </div>

      <!-- ── 메인 헤더: 로고 + 검색 + 액션 ── -->
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-5 pb-4 flex items-center gap-8">
        <RouterLink to="/" class="flex items-baseline gap-1.5 shrink-0">
          <span class="text-3xl font-black text-red-500 tracking-tight">Vibe</span>
          <span class="text-3xl font-black text-gray-900 tracking-tight">shop</span>
          <span class="badge-rocket ml-1">로켓배송</span>
        </RouterLink>

        <div class="flex-1 relative">
          <input
            v-model="keyword"
            @keyup.enter="search"
            type="text"
            placeholder="검색어를 입력해 주세요"
            class="input-search"
          />
          <button
            @click="search"
            class="absolute right-0 top-0 bottom-0 w-11 bg-red-500 hover:bg-red-600 text-white flex items-center justify-center rounded-r"
            aria-label="검색"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M21 21l-4.35-4.35M11 18a7 7 0 100-14 7 7 0 000 14z" />
            </svg>
          </button>
        </div>

        <nav class="flex gap-5 text-xs text-gray-700 text-center shrink-0">
          <RouterLink to="/cart" class="relative hover:text-red-500">
            <svg class="w-7 h-7 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.6"
                d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
            </svg>
            <span class="block mt-0.5">장바구니</span>
            <span v-if="cartStore.totalCount > 0"
              class="absolute -top-1 right-0 bg-red-500 text-white rounded-full text-[10px] font-bold px-1.5 min-w-[18px] text-center leading-[18px]">
              {{ cartStore.totalCount }}
            </span>
          </RouterLink>
          <RouterLink v-if="auth.isLoggedIn" to="/orders" class="hover:text-red-500">
            <svg class="w-7 h-7 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.6"
                d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
            </svg>
            <span class="block mt-0.5">주문배송</span>
          </RouterLink>
          <RouterLink v-if="auth.isLoggedIn" to="/mypage" class="hover:text-red-500">
            <svg class="w-7 h-7 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.6"
                d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
            </svg>
            <span class="block mt-0.5">마이페이지</span>
          </RouterLink>
        </nav>
      </div>

      <!-- ── 카테고리 nav ── -->
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pb-3 flex items-center gap-6 text-sm text-gray-700">
        <!-- 전체 카테고리 드롭다운 -->
        <div class="relative" ref="catMenuRef">
          <button
            @click="toggleCatMenu"
            class="font-bold text-red-500 flex items-center gap-1.5 focus:outline-none"
          >
            <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
              <path d="M3 6h18v2H3zM3 11h18v2H3zM3 16h18v2H3z" />
            </svg>
            전체 카테고리
            <svg class="w-3 h-3 transition-transform" :class="catMenuOpen ? 'rotate-180' : ''" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M5.23 7.21a.75.75 0 011.06.02L10 11.168l3.71-3.938a.75.75 0 111.08 1.04l-4.25 4.5a.75.75 0 01-1.08 0l-4.25-4.5a.75.75 0 01.02-1.06z" clip-rule="evenodd" />
            </svg>
          </button>

          <!-- 드롭다운 패널 -->
          <div
            v-if="catMenuOpen"
            class="absolute top-full left-0 mt-1 w-52 bg-white border border-gray-200 rounded shadow-lg z-50 py-1"
          >
            <RouterLink
              to="/products"
              @click="catMenuOpen = false"
              class="flex items-center gap-2 px-4 py-2.5 text-sm text-gray-700 hover:bg-red-50 hover:text-red-500 font-bold"
            >
              <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
              </svg>
              전체
            </RouterLink>
            <div class="border-t border-gray-100 my-1"></div>
            <RouterLink
              v-for="cat in categoryStore.categories"
              :key="cat.id"
              :to="{ path: '/products', query: { categoryId: cat.id } }"
              @click="catMenuOpen = false"
              class="flex items-center gap-2 px-4 py-2.5 text-sm text-gray-700 hover:bg-red-50 hover:text-red-500"
            >
              <span class="w-4 h-4 flex items-center justify-center text-gray-400 text-xs">●</span>
              {{ cat.name }}
            </RouterLink>
          </div>
        </div>

        <span class="hover:text-red-500 cursor-pointer">로켓배송</span>
        <span class="hover:text-red-500 cursor-pointer">로켓프레시</span>
        <span class="hover:text-red-500 cursor-pointer">로켓직구</span>
        <span class="hover:text-red-500 cursor-pointer">골드박스</span>
        <span class="hover:text-red-500 cursor-pointer">와우회원</span>
        <span class="hover:text-red-500 cursor-pointer">이벤트/쿠폰</span>
        <span class="font-bold text-red-500 cursor-pointer">오늘만 특가</span>
      </div>
    </header>

    <main class="flex-1 bg-gray-50">
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
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter, RouterLink, RouterView } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { useWishlistStore } from '@/stores/wishlist'
import { useCategoryStore } from '@/stores/category'

const auth = useAuthStore()
const cartStore = useCartStore()
const wishlistStore = useWishlistStore()
const categoryStore = useCategoryStore()
const router = useRouter()
const keyword = ref('')

// 전체 카테고리 드롭다운
const catMenuOpen = ref(false)
const catMenuRef = ref<HTMLElement | null>(null)

function toggleCatMenu() {
  catMenuOpen.value = !catMenuOpen.value
}

function onClickOutside(e: MouseEvent) {
  if (catMenuRef.value && !catMenuRef.value.contains(e.target as Node)) {
    catMenuOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', onClickOutside)
  if (categoryStore.categories.length === 0) {
    categoryStore.fetchCategories()
  }
})

onUnmounted(() => {
  document.removeEventListener('click', onClickOutside)
})

if (auth.isLoggedIn) {
  cartStore.fetchCart()
  wishlistStore.fetchWishlistIds()
}

function search() {
  if (keyword.value.trim()) {
    router.push({ path: '/products', query: { keyword: keyword.value } })
  }
}

function logout() {
  auth.logout()
  wishlistStore.reset()
  router.push('/login')
}
</script>
