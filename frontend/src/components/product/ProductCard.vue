<template>
  <div class="relative group">
    <RouterLink :to="`/products/${product.id}`" class="card-flat p-3 hover:shadow-md transition-shadow block">
      <!-- 이미지 + 할인 뱃지 -->
      <div class="aspect-square bg-gray-100 rounded mb-3 overflow-hidden relative">
        <img
          v-if="product.imageUrl"
          :src="product.imageUrl"
          :alt="product.name"
          class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
        />
        <div v-else class="w-full h-full flex items-center justify-center text-gray-300">
          <svg class="w-16 h-16" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1"
              d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
          </svg>
        </div>
        <!-- 할인율 뱃지 -->
        <span class="badge-discount absolute top-2 left-2">−{{ display.discountRate }}%</span>
        <!-- 품절 오버레이 -->
        <div v-if="product.stock === 0"
          class="absolute inset-0 bg-black/50 flex items-center justify-center text-white font-bold">
          품절
        </div>
      </div>

      <!-- 카테고리 -->
      <p class="text-[11px] text-gray-400 mb-1">{{ product.categoryName }}</p>

      <!-- 상품명 -->
      <h3 class="text-[13px] text-gray-900 leading-snug mb-1.5 line-clamp-2 min-h-[2.6em] group-hover:text-red-500 transition-colors">
        {{ product.name }}
      </h3>

      <!-- 가격: 할인% + 현재가 -->
      <div class="flex items-baseline gap-1">
        <span class="text-red-500 font-extrabold text-sm">{{ display.discountRate }}%</span>
        <span class="text-gray-900 font-extrabold text-base">{{ formatPrice(product.price) }}</span>
      </div>
      <p class="text-[11px] text-gray-400 line-through">{{ formatPrice(display.originalPrice) }}</p>

      <!-- 평점 + 리뷰 (UI 데모용 부가정보) -->
      <div class="mt-1.5 flex items-center gap-1 text-[11px] text-gray-500">
        <span class="text-deal-400 leading-none">★</span>
        <span class="font-semibold text-gray-700">{{ formatRating(display.rating) }}</span>
        <span>({{ display.reviewCount.toLocaleString() }})</span>
      </div>

      <!-- 로켓배송 -->
      <div v-if="display.rocketDelivery" class="mt-2 flex items-center gap-1.5">
        <span class="badge-rocket">로켓배송</span>
        <span class="text-[11px] text-gray-500">내일 도착</span>
      </div>
      <p v-else-if="product.stock > 0" class="mt-2 text-[11px] text-gray-400">재고 {{ product.stock }}개</p>
    </RouterLink>

    <!-- 찜하기 버튼 (기존 컴포넌트 유지) -->
    <div class="absolute top-2 right-2 z-10">
      <WishlistButton :productId="product.id" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import type { Product } from '@/types'
import { formatPrice } from '@/utils/format'
import { getProductDisplay, formatRating } from '@/utils/productDisplay'
import WishlistButton from './WishlistButton.vue'

const props = defineProps<{ product: Product }>()
const display = computed(() => getProductDisplay(props.product))
</script>
