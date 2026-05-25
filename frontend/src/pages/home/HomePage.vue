<template>
  <div class="bg-gray-50 pb-16">

    <!-- ══ 1. 히어로 배너 슬라이더 ══ -->
    <section class="max-w-7xl mx-auto px-4 pt-5">
      <div class="grid grid-cols-1 md:grid-cols-[1fr_300px] gap-3">

        <!-- 메인 슬라이더 -->
        <div class="relative rounded overflow-hidden h-[260px] md:h-[320px] cursor-pointer select-none"
          @mouseenter="pauseSlider" @mouseleave="resumeSlider">
          <div class="w-full h-full transition-all duration-500"
            :style="{ background: slides[currentSlide].bg }">
            <div class="absolute inset-0 flex flex-col justify-center px-8 md:px-14 text-white">
              <p class="text-xs md:text-sm font-semibold opacity-80 mb-2">{{ slides[currentSlide].sub }}</p>
              <h2 class="text-3xl md:text-5xl font-black leading-tight whitespace-pre-line">
                {{ slides[currentSlide].title }}
              </h2>
              <RouterLink :to="slides[currentSlide].link"
                class="mt-6 inline-block w-fit bg-white text-gray-900 font-bold px-5 py-2.5 rounded text-sm hover:bg-gray-100 transition">
                {{ slides[currentSlide].cta }} →
              </RouterLink>
            </div>
          </div>

          <!-- 슬라이더 인디케이터 -->
          <div class="absolute bottom-4 left-1/2 -translate-x-1/2 flex gap-2">
            <button v-for="(_, i) in slides" :key="i"
              @click="currentSlide = i"
              class="w-2 h-2 rounded-full transition-all"
              :class="i === currentSlide ? 'bg-white w-5' : 'bg-white/50'" />
          </div>
          <!-- 좌우 화살표 -->
          <button @click="prevSlide"
            class="absolute left-3 top-1/2 -translate-y-1/2 bg-black/20 hover:bg-black/40 text-white rounded-full w-8 h-8 flex items-center justify-center">
            ‹
          </button>
          <button @click="nextSlide"
            class="absolute right-3 top-1/2 -translate-y-1/2 bg-black/20 hover:bg-black/40 text-white rounded-full w-8 h-8 flex items-center justify-center">
            ›
          </button>
        </div>

        <!-- 사이드 배너 2개 -->
        <div class="grid grid-rows-2 gap-3">
          <RouterLink to="/products?categoryId=3"
            class="rounded overflow-hidden p-5 flex flex-col justify-between hover:brightness-95 transition"
            style="background:#fff0d4">
            <div>
              <p class="font-extrabold text-gray-900 text-lg">로켓프레시</p>
              <p class="text-xs text-gray-600 mt-1">식품/건강 신선배송</p>
            </div>
            <p class="text-xs font-bold text-red-500 mt-3">지금 보기 →</p>
          </RouterLink>
          <RouterLink to="/products?categoryId=1"
            class="rounded overflow-hidden p-5 flex flex-col justify-between hover:brightness-95 transition"
            style="background:#d8e8ff">
            <div>
              <p class="font-extrabold text-gray-900 text-lg">로켓직구</p>
              <p class="text-xs text-gray-600 mt-1">전자제품 최저가</p>
            </div>
            <p class="text-xs font-bold text-rocket-500 mt-3">지금 보기 →</p>
          </RouterLink>
        </div>
      </div>
    </section>

    <!-- ══ 2. 카테고리 아이콘 바 ══ -->
    <section class="max-w-7xl mx-auto px-4 mt-6">
      <div class="bg-white rounded border border-gray-200 py-5 px-4">
        <div class="grid grid-cols-5 gap-2 md:gap-4">
          <RouterLink v-for="cat in catIcons" :key="cat.id"
            :to="`/products?categoryId=${cat.id}`"
            class="flex flex-col items-center gap-2 p-3 rounded hover:bg-red-50 transition group">
            <div class="w-12 h-12 rounded-full flex items-center justify-center text-2xl"
              :style="{ background: cat.bg }">
              {{ cat.icon }}
            </div>
            <span class="text-xs font-medium text-gray-700 group-hover:text-red-500 text-center leading-tight">
              {{ cat.name }}
            </span>
          </RouterLink>
        </div>
      </div>
    </section>

    <!-- ══ 3. 오늘의 특가 ══ -->
    <section class="max-w-7xl mx-auto px-4 mt-8">
      <div class="flex items-center justify-between mb-3">
        <div class="flex items-center gap-2">
          <span class="text-xl">⚡</span>
          <h2 class="text-lg font-black text-gray-900">오늘의 특가</h2>
          <span class="badge-sq bg-red-500 text-white ml-1">LIVE</span>
        </div>
        <RouterLink to="/products" class="text-xs text-gray-500 hover:text-red-500">전체보기 ›</RouterLink>
      </div>

      <div v-if="loadingDeals" class="flex gap-3 overflow-hidden">
        <div v-for="n in 6" :key="n" class="card-flat p-3 animate-pulse shrink-0 w-44">
          <div class="aspect-square bg-gray-200 rounded mb-2"></div>
          <div class="h-3 bg-gray-200 rounded mb-1"></div>
          <div class="h-3 bg-gray-200 rounded w-2/3"></div>
        </div>
      </div>

      <div v-else class="flex gap-3 overflow-x-auto pb-2 scrollbar-hide">
        <RouterLink v-for="p in dealProducts" :key="p.id"
          :to="`/products/${p.id}`"
          class="card-flat p-3 shrink-0 w-44 hover:shadow-md transition-shadow group">
          <div class="aspect-square bg-gray-100 rounded mb-2 overflow-hidden relative">
            <img v-if="p.imageUrl" :src="p.imageUrl" :alt="p.name"
              class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" />
            <span class="badge-discount absolute top-1.5 left-1.5">
              −{{ getDisplay(p).discountRate }}%
            </span>
          </div>
          <p class="text-[11px] text-gray-400 mb-0.5">{{ p.categoryName }}</p>
          <p class="text-[12px] text-gray-900 font-medium line-clamp-2 min-h-[2.5em]">{{ p.name }}</p>
          <div class="mt-1 flex items-baseline gap-1">
            <span class="text-red-500 font-extrabold text-sm">{{ getDisplay(p).discountRate }}%</span>
            <span class="text-gray-900 font-extrabold text-sm">{{ formatPrice(p.price) }}</span>
          </div>
          <p class="text-[10px] text-gray-400 line-through">{{ formatPrice(getDisplay(p).originalPrice) }}</p>
        </RouterLink>
      </div>
    </section>

    <!-- ══ 4. 카테고리별 추천 상품 ══ -->
    <section v-for="cat in categoryStore.categories" :key="cat.id"
      class="max-w-7xl mx-auto px-4 mt-10">
      <div class="flex items-center justify-between mb-3">
        <h2 class="text-lg font-black text-gray-900">{{ cat.name }} 추천</h2>
        <RouterLink :to="`/products?categoryId=${cat.id}`"
          class="text-xs text-gray-500 hover:text-red-500">전체보기 ›</RouterLink>
      </div>

      <!-- 로딩 스켈레톤 -->
      <div v-if="!catProducts[cat.id]" class="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-3">
        <div v-for="n in 6" :key="n" class="card-flat p-3 animate-pulse">
          <div class="aspect-square bg-gray-200 rounded mb-2"></div>
          <div class="h-3 bg-gray-200 rounded mb-1"></div>
          <div class="h-3 bg-gray-200 rounded w-2/3"></div>
        </div>
      </div>

      <div v-else class="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-3">
        <div v-for="p in catProducts[cat.id]" :key="p.id" class="relative group">
          <RouterLink :to="`/products/${p.id}`"
            class="card-flat p-3 hover:shadow-md transition-shadow block">
            <div class="aspect-square bg-gray-100 rounded mb-2 overflow-hidden relative">
              <img v-if="p.imageUrl" :src="p.imageUrl" :alt="p.name"
                class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" />
              <div v-else class="w-full h-full flex items-center justify-center text-gray-300 text-3xl">📦</div>
              <span class="badge-discount absolute top-1.5 left-1.5">
                −{{ getDisplay(p).discountRate }}%
              </span>
              <div v-if="p.stock === 0"
                class="absolute inset-0 bg-black/50 flex items-center justify-center text-white text-xs font-bold">
                품절
              </div>
            </div>
            <p class="text-[12px] text-gray-900 font-medium line-clamp-2 min-h-[2.5em] group-hover:text-red-500 transition-colors">
              {{ p.name }}
            </p>
            <div class="mt-1 flex items-baseline gap-1">
              <span class="text-red-500 font-extrabold text-xs">{{ getDisplay(p).discountRate }}%</span>
              <span class="text-gray-900 font-extrabold text-sm">{{ formatPrice(p.price) }}</span>
            </div>
            <div v-if="getDisplay(p).rocketDelivery" class="mt-1.5">
              <span class="badge-rocket">로켓배송</span>
            </div>
          </RouterLink>
          <div class="absolute top-2 right-2 z-10">
            <WishlistButton :productId="p.id" />
          </div>
        </div>
      </div>
    </section>

    <!-- ══ 5. 이벤트/기획전 배너 ══ -->
    <section class="max-w-7xl mx-auto px-4 mt-10">
      <h2 class="text-lg font-black text-gray-900 mb-3">이벤트 · 기획전</h2>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-3">
        <RouterLink to="/products?categoryId=5"
          class="rounded overflow-hidden h-36 flex items-end p-5 relative hover:brightness-95 transition"
          style="background: linear-gradient(135deg,#1a1a2e,#16213e)">
          <div class="text-white">
            <p class="text-xs opacity-70 mb-1">스포츠/레저</p>
            <p class="font-extrabold text-lg leading-tight">운동 용품<br/>최대 40% 할인</p>
          </div>
        </RouterLink>
        <RouterLink to="/products?categoryId=4"
          class="rounded overflow-hidden h-36 flex items-end p-5 relative hover:brightness-95 transition"
          style="background: linear-gradient(135deg,#2d6a4f,#52b788)">
          <div class="text-white">
            <p class="text-xs opacity-70 mb-1">홈/리빙</p>
            <p class="font-extrabold text-lg leading-tight">홈 인테리어<br/>봄 신상 입고</p>
          </div>
        </RouterLink>
        <RouterLink to="/products?categoryId=2"
          class="rounded overflow-hidden h-36 flex items-end p-5 relative hover:brightness-95 transition"
          style="background: linear-gradient(135deg,#7b2d8b,#c77dff)">
          <div class="text-white">
            <p class="text-xs opacity-70 mb-1">패션/의류</p>
            <p class="font-extrabold text-lg leading-tight">여름 시즌<br/>패션 특가전</p>
          </div>
        </RouterLink>
      </div>
    </section>

    <!-- ══ 6. 신상품 ══ -->
    <section class="max-w-7xl mx-auto px-4 mt-10">
      <div class="flex items-center justify-between mb-3">
        <div class="flex items-center gap-2">
          <span class="text-xl">🆕</span>
          <h2 class="text-lg font-black text-gray-900">신상품</h2>
        </div>
        <RouterLink to="/products" class="text-xs text-gray-500 hover:text-red-500">전체보기 ›</RouterLink>
      </div>

      <div v-if="loadingNew" class="grid grid-cols-2 md:grid-cols-4 gap-3">
        <div v-for="n in 8" :key="n" class="card-flat p-3 animate-pulse">
          <div class="aspect-square bg-gray-200 rounded mb-2"></div>
          <div class="h-3 bg-gray-200 rounded mb-1"></div>
          <div class="h-3 bg-gray-200 rounded w-2/3"></div>
        </div>
      </div>

      <div v-else class="grid grid-cols-2 md:grid-cols-4 gap-3">
        <ProductCard v-for="p in newProducts" :key="p.id" :product="p" />
      </div>
    </section>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { RouterLink } from 'vue-router'
import { productApi } from '@/api/products'
import { useCategoryStore } from '@/stores/category'
import { getProductDisplay } from '@/utils/productDisplay'
import { formatPrice } from '@/utils/format'
import ProductCard from '@/components/product/ProductCard.vue'
import WishlistButton from '@/components/product/WishlistButton.vue'
import type { Product } from '@/types'

const categoryStore = useCategoryStore()

// ── 슬라이더 ──
const slides = [
  {
    bg: 'linear-gradient(135deg, #ef4444, #b91c1c)',
    sub: 'WOW MEMBERSHIP · 와우회원 단독',
    title: '오늘만\n최대 70% 할인',
    cta: '특가 보기',
    link: '/products',
  },
  {
    bg: 'linear-gradient(135deg, #0074e4, #005bb5)',
    sub: '로켓배송 · 내일 도착 보장',
    title: '전자제품\n빠른 직구',
    cta: '전자제품 보기',
    link: '/products?categoryId=1',
  },
  {
    bg: 'linear-gradient(135deg, #2d6a4f, #40916c)',
    sub: '식품/건강 · 신선 배송',
    title: '건강한 하루\n시작하기',
    cta: '식품 보기',
    link: '/products?categoryId=3',
  },
  {
    bg: 'linear-gradient(135deg, #7b2d8b, #9d4edd)',
    sub: '패션/의류 · 여름 시즌',
    title: '트렌디한\n패션 특가',
    cta: '패션 보기',
    link: '/products?categoryId=2',
  },
]
const currentSlide = ref(0)
let sliderTimer: ReturnType<typeof setInterval> | null = null

function nextSlide() { currentSlide.value = (currentSlide.value + 1) % slides.length }
function prevSlide() { currentSlide.value = (currentSlide.value - 1 + slides.length) % slides.length }
function pauseSlider() { if (sliderTimer) clearInterval(sliderTimer) }
function resumeSlider() { sliderTimer = setInterval(nextSlide, 4000) }

// ── 카테고리 아이콘 바 ──
const catIcons = [
  { id: 1, name: '전자제품', icon: '💻', bg: '#e0f0ff' },
  { id: 2, name: '패션/의류', icon: '👗', bg: '#fce4ec' },
  { id: 3, name: '식품/건강', icon: '🥦', bg: '#e8f5e9' },
  { id: 4, name: '홈/리빙', icon: '🏠', bg: '#fff8e1' },
  { id: 5, name: '스포츠/레저', icon: '⚽', bg: '#f3e5f5' },
]

// ── 상품 데이터 ──
const dealProducts = ref<Product[]>([])
const newProducts  = ref<Product[]>([])
const catProducts  = reactive<Record<number, Product[]>>({})
const loadingDeals = ref(true)
const loadingNew   = ref(true)

function getDisplay(p: Product) {
  return getProductDisplay(p)
}

async function fetchDeals() {
  try {
    const res = await productApi.getAll({ page: 0, size: 20 })
    const all = res.data.data.content.filter(p => p.status === 'ACTIVE' && p.stock > 0)
    // 시드 셔플로 매번 같은 순서 유지
    dealProducts.value = all.slice(0, 12)
  } finally {
    loadingDeals.value = false
  }
}

async function fetchNewProducts() {
  try {
    const res = await productApi.getAll({ page: 0, size: 8 })
    newProducts.value = res.data.data.content
  } finally {
    loadingNew.value = false
  }
}

async function fetchCategoryProducts(catId: number) {
  const res = await productApi.getAll({ categoryId: catId, page: 0, size: 6 })
  catProducts[catId] = res.data.data.content
}

onMounted(async () => {
  // 슬라이더 자동 재생
  sliderTimer = setInterval(nextSlide, 4000)

  // 카테고리 로드
  if (categoryStore.categories.length === 0) {
    await categoryStore.fetchCategories()
  }

  // 병렬 데이터 패치
  await Promise.all([
    fetchDeals(),
    fetchNewProducts(),
    ...categoryStore.categories.map(c => fetchCategoryProducts(c.id)),
  ])
})

onUnmounted(() => {
  if (sliderTimer) clearInterval(sliderTimer)
})
</script>

<style scoped>
.scrollbar-hide::-webkit-scrollbar { display: none; }
.scrollbar-hide { -ms-overflow-style: none; scrollbar-width: none; }
</style>
