<template>
  <div class="relative group">
    <RouterLink :to="`/products/${product.id}`" class="card hover:shadow-md transition-shadow block">
      <div class="aspect-square bg-gray-100 rounded-lg mb-3 overflow-hidden">
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
      </div>
      <p class="text-xs text-gray-400 mb-1">{{ product.categoryName }}</p>
      <h3 class="font-medium text-gray-900 mb-1 line-clamp-2 group-hover:text-red-500 transition-colors">
        {{ product.name }}
      </h3>
      <p class="text-lg font-bold text-gray-900">{{ formatPrice(product.price) }}</p>
      <p v-if="product.stock === 0" class="text-xs text-red-500 mt-1">품절</p>
      <p v-else class="text-xs text-gray-400 mt-1">재고 {{ product.stock }}개</p>
    </RouterLink>

    <!-- 찜하기 버튼 -->
    <div class="absolute top-2 right-2 z-10">
      <WishlistButton :productId="product.id" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'
import type { Product } from '@/types'
import { formatPrice } from '@/utils/format'
import WishlistButton from './WishlistButton.vue'

defineProps<{ product: Product }>()
</script>
