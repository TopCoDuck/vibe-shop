<template>
  <div class="max-w-7xl mx-auto px-4 py-8">
    <!-- 히어로 배너 -->
    <div v-if="!route.query.keyword && !route.query.categoryId"
      class="bg-gradient-to-r from-red-500 to-red-700 rounded-2xl text-white p-12 mb-10 text-center">
      <h1 class="text-4xl font-bold mb-3">새로운 트렌드를 발견하세요</h1>
      <p class="text-red-100 text-lg">Vibe Shop에서 다양한 상품을 만나보세요</p>
    </div>

    <div class="flex gap-8">
      <!-- 카테고리 사이드바 -->
      <aside class="w-48 shrink-0">
        <h2 class="font-semibold text-gray-700 mb-3">카테고리</h2>
        <ul class="space-y-1">
          <li>
            <RouterLink to="/"
              class="block px-3 py-2 rounded-lg text-sm hover:bg-red-50 hover:text-red-600"
              :class="!route.query.categoryId ? 'bg-red-50 text-red-600 font-medium' : 'text-gray-600'">
              전체
            </RouterLink>
          </li>
          <li v-for="cat in categoryStore.categories" :key="cat.id">
            <RouterLink :to="{ path: '/', query: { categoryId: cat.id } }"
              class="block px-3 py-2 rounded-lg text-sm hover:bg-red-50 hover:text-red-600"
              :class="Number(route.query.categoryId) === cat.id ? 'bg-red-50 text-red-600 font-medium' : 'text-gray-600'">
              {{ cat.name }}
            </RouterLink>
          </li>
        </ul>
      </aside>

      <!-- 상품 목록 -->
      <div class="flex-1">
        <div class="flex items-center justify-between mb-4">
          <p class="text-sm text-gray-500">
            <span v-if="route.query.keyword">
              "<strong>{{ route.query.keyword }}</strong>" 검색 결과
            </span>
            <span v-else>총 {{ totalElements }}개 상품</span>
          </p>
        </div>

        <div v-if="loading" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
          <div v-for="n in 8" :key="n" class="card animate-pulse">
            <div class="aspect-square bg-gray-200 rounded-lg mb-3"></div>
            <div class="h-4 bg-gray-200 rounded mb-2"></div>
            <div class="h-4 bg-gray-200 rounded w-2/3"></div>
          </div>
        </div>

        <div v-else-if="products.length === 0" class="text-center py-20 text-gray-400">
          <p class="text-lg">상품이 없습니다.</p>
        </div>

        <div v-else class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
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
