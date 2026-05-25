<template>
  <div class="max-w-7xl mx-auto px-4 py-5">
    <!-- ── 히어로 + 사이드 배너 (검색/카테고리 필터가 없을 때만) ── -->
    <div v-if="!route.query.keyword && !route.query.categoryId"
      class="grid grid-cols-1 md:grid-cols-[1fr_320px] gap-3 mb-5">
      <!-- 메인 히어로 -->
      <div class="relative rounded overflow-hidden h-[280px] md:h-[320px] bg-gradient-to-br from-red-500 to-red-700">
        <div class="absolute inset-0 flex flex-col justify-center px-8 md:px-12 text-white">
          <p class="text-sm font-semibold opacity-90">WOW MEMBERSHIP · 와우회원 단독</p>
          <h1 class="text-3xl md:text-5xl font-black mt-2 leading-tight">
            오늘만<br />최대 70% 할인
          </h1>
          <div class="mt-5 inline-block w-fit bg-white text-red-500 font-bold px-5 py-2.5 rounded text-sm">
            지금 바로 보기 →
          </div>
        </div>
      </div>
      <!-- 사이드 배너 -->
      <div class="grid grid-rows-2 gap-3">
        <div class="rounded p-5 flex flex-col justify-between" style="background:#ffd8d8">
          <div>
            <p class="font-extrabold text-gray-900 text-lg">로켓프레시</p>
            <p class="text-xs text-gray-700 mt-1">신선식품 새벽배송</p>
          </div>
          <p class="text-xs font-bold text-red-500">혜택 보기 →</p>
        </div>
        <div class="rounded p-5 flex flex-col justify-between" style="background:#d8e8ff">
          <div>
            <p class="font-extrabold text-gray-900 text-lg">로켓직구</p>
            <p class="text-xs text-gray-700 mt-1">해외 빠른배송 무료</p>
          </div>
          <p class="text-xs font-bold text-rocket-500">혜택 보기 →</p>
        </div>
      </div>
    </div>

    <div class="flex gap-5">
      <!-- ── 카테고리 + 필터 사이드바 ── -->
      <aside class="w-48 shrink-0 space-y-3 hidden md:block">
        <div class="card-flat p-4">
          <h2 class="font-extrabold text-gray-900 text-sm mb-3">카테고리</h2>
          <ul class="space-y-0.5">
            <li>
              <RouterLink to="/"
                class="block px-2 py-1.5 rounded text-[13px] hover:bg-red-50 hover:text-red-600"
                :class="!route.query.categoryId ? 'bg-red-50 text-red-600 font-bold' : 'text-gray-700'">
                전체
              </RouterLink>
            </li>
            <li v-for="cat in categoryStore.categories" :key="cat.id">
              <RouterLink :to="{ path: '/', query: { categoryId: cat.id } }"
                class="block px-2 py-1.5 rounded text-[13px] hover:bg-red-50 hover:text-red-600"
                :class="Number(route.query.categoryId) === cat.id ? 'bg-red-50 text-red-600 font-bold' : 'text-gray-700'">
                {{ cat.name }}
              </RouterLink>
            </li>
          </ul>
        </div>

        <div class="card-flat p-4">
          <h2 class="font-extrabold text-gray-900 text-sm mb-3">가격</h2>
          <label v-for="p in priceRanges" :key="p"
            class="flex items-center gap-2 py-1 text-[13px] text-gray-700 cursor-pointer">
            <input type="checkbox" class="accent-red-500" /> {{ p }}
          </label>
        </div>

        <div class="card-flat p-4">
          <h2 class="font-extrabold text-gray-900 text-sm mb-3">혜택/서비스</h2>
          <label v-for="(b, i) in benefits" :key="b"
            class="flex items-center gap-2 py-1 text-[13px] text-gray-700 cursor-pointer">
            <input type="checkbox" class="accent-red-500" :checked="i === 0" /> {{ b }}
          </label>
        </div>
      </aside>

      <!-- ── 상품 목록 ── -->
      <div class="flex-1 min-w-0">
        <!-- 정렬바 -->
        <div class="card-flat p-3.5 mb-3 flex justify-between items-center text-[13px]">
          <div>
            <span v-if="route.query.keyword">
              <strong class="font-bold">"{{ route.query.keyword }}"</strong> 검색 결과
              <span class="text-gray-500"> 총 <strong class="text-red-500">{{ totalElements }}</strong>개</span>
            </span>
            <span v-else class="text-gray-500">
              총 <strong class="text-red-500">{{ totalElements }}</strong>개 상품
            </span>
          </div>
          <div class="hidden md:flex gap-4">
            <button v-for="(s, i) in sortOptions" :key="s"
              :class="i === 0 ? 'text-gray-900 font-bold' : 'text-gray-500 hover:text-gray-900'">
              {{ s }}
            </button>
          </div>
        </div>

        <!-- 로딩 -->
        <div v-if="loading" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">
          <div v-for="n in 8" :key="n" class="card-flat p-3 animate-pulse">
            <div class="aspect-square bg-gray-200 rounded mb-3"></div>
            <div class="h-3 bg-gray-200 rounded mb-2"></div>
            <div class="h-3 bg-gray-200 rounded w-2/3"></div>
          </div>
        </div>

        <!-- 비어있음 -->
        <div v-else-if="products.length === 0" class="card text-center py-20 text-gray-400">
          <p class="text-lg">상품이 없습니다.</p>
        </div>

        <!-- 그리드 -->
        <div v-else class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">
          <ProductCard v-for="p in products" :key="p.id" :product="p" />
        </div>

        <Pagination v-if="totalPages > 1" :current="currentPage" :total="totalPages" @change="onPageChange" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import { productApi } from '@/api/products'
import { useCategoryStore } from '@/stores/category'
import ProductCard from '@/components/product/ProductCard.vue'
import Pagination from '@/components/common/Pagination.vue'
import type { Product } from '@/types'

const route = useRoute()
const categoryStore = useCategoryStore()

const products = ref<Product[]>([])
const loading = ref(false)
const currentPage = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)

// UI 전용 옵션 (현재는 동작 X — 차후 백엔드 필터 추가 시 연결)
const sortOptions = ['쿠팡 랭킹순', '낮은가격순', '높은가격순', '판매량순', '최신순']
const priceRanges = ['~ 3만원', '3만 ~ 5만원', '5만 ~ 10만원', '10만원 이상']
const benefits = ['로켓배송', '로켓프레시', '로켓직구', '무료배송']

async function fetchProducts() {
  loading.value = true
  try {
    const res = await productApi.getAll({
      categoryId: route.query.categoryId ? Number(route.query.categoryId) : undefined,
      keyword: route.query.keyword as string | undefined,
      page: currentPage.value,
      size: 12
    })
    const data = res.data.data
    products.value = data.content
    totalPages.value = data.totalPages
    totalElements.value = data.totalElements
  } finally {
    loading.value = false
  }
}

function onPageChange(page: number) {
  currentPage.value = page
  fetchProducts()
}

watch(() => [route.query.categoryId, route.query.keyword], () => {
  currentPage.value = 0
  fetchProducts()
})

onMounted(() => {
  categoryStore.fetchCategories()
  fetchProducts()
})
</script>
