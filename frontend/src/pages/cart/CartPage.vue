<template>
  <div class="max-w-6xl mx-auto px-4 py-6">
    <h1 class="text-2xl font-black text-gray-900 mb-5">장바구니</h1>

    <!-- 진행 스텝 -->
    <div class="flex justify-center gap-8 md:gap-10 mb-6 text-sm">
      <span class="text-red-500 font-extrabold">01 장바구니</span>
      <span class="text-gray-400 font-medium">02 주문/결제</span>
      <span class="text-gray-400 font-medium">03 주문완료</span>
    </div>

    <!-- 비어있음 -->
    <div v-if="cartStore.items.length === 0" class="text-center py-20 text-gray-400">
      <svg class="w-16 h-16 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1"
          d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
      </svg>
      <p class="text-lg mb-4">장바구니가 비었습니다.</p>
      <RouterLink to="/" class="btn-primary inline-block">쇼핑 계속하기</RouterLink>
    </div>

    <div v-else class="grid grid-cols-1 lg:grid-cols-[1fr_340px] gap-5">
      <!-- ── 장바구니 목록 ── -->
      <div>
        <div class="card-flat">
          <!-- 전체선택 바 -->
          <div class="px-4 py-3 flex items-center gap-3 text-sm border-b border-gray-100">
            <input type="checkbox" :checked="allSelected" @change="toggleAll"
              class="accent-red-500 w-4 h-4" />
            <span class="font-bold">전체선택 ({{ selectedIds.size }}/{{ cartStore.items.length }})</span>
            <button @click="removeSelected" class="ml-auto text-gray-500 text-xs hover:text-red-500"
              :disabled="selectedIds.size === 0">선택삭제</button>
          </div>

          <!-- 로켓배송 그룹 헤더 -->
          <div class="bg-gray-50 px-4 py-2.5 text-sm font-bold flex items-center gap-2 border-b border-gray-100">
            <span class="badge-rocket">로켓배송</span>
            <span class="text-gray-700">내일(수) 도착 예정 · 무료배송</span>
          </div>

          <!-- 아이템 -->
          <div v-for="item in cartStore.items" :key="item.id"
            class="px-4 py-4 flex gap-4 items-center border-b border-gray-100 last:border-b-0">
            <input type="checkbox" :checked="selectedIds.has(item.id)" @change="toggle(item.id)"
              class="accent-red-500 w-4 h-4 shrink-0" />
            <div class="w-[90px] h-[90px] bg-gray-100 rounded overflow-hidden shrink-0">
              <img v-if="item.imageUrl" :src="item.imageUrl" :alt="item.productName"
                class="w-full h-full object-cover" />
            </div>
            <div class="flex-1 min-w-0">
              <RouterLink :to="`/products/${item.productId}`"
                class="text-sm font-medium text-gray-900 line-clamp-2 hover:text-red-500">
                {{ item.productName }}
              </RouterLink>
              <p class="mt-1.5 text-[11px] text-gray-500">무료배송 · 내일(수) 도착보장</p>
            </div>
            <!-- 수량 스텝퍼 -->
            <div class="flex items-center border border-gray-300 rounded shrink-0">
              <button @click="cartStore.updateQuantity(item.id, item.quantity - 1)"
                class="px-2.5 py-1 text-gray-600 hover:bg-gray-100"
                :disabled="item.quantity <= 1">−</button>
              <span class="px-3 py-1 text-sm font-bold border-x border-gray-300 min-w-[36px] text-center">
                {{ item.quantity }}
              </span>
              <button @click="cartStore.updateQuantity(item.id, item.quantity + 1)"
                class="px-2.5 py-1 text-gray-600 hover:bg-gray-100">+</button>
            </div>
            <!-- 가격 -->
            <div class="w-[110px] text-right shrink-0">
              <p class="text-[11px] text-gray-400 line-through">
                {{ formatPrice(getCartItemDisplay(item).originalPrice * item.quantity) }}
              </p>
              <p class="text-base font-extrabold text-gray-900">{{ formatPrice(item.subtotal) }}</p>
            </div>
            <button @click="cartStore.removeItem(item.id)"
              class="text-gray-400 hover:text-red-500 text-lg shrink-0"
              aria-label="삭제">×</button>
          </div>
        </div>
      </div>

      <!-- ── 결제 요약 ── -->
      <div>
        <div class="card-flat p-5 sticky top-24">
          <h2 class="font-extrabold text-gray-900 text-base mb-4">결제 예정금액</h2>
          <div class="space-y-1.5 text-sm">
            <div class="flex justify-between py-1 text-gray-700">
              <span>상품금액</span><span>{{ formatPrice(subtotalOriginal) }}</span>
            </div>
            <div v-if="totalDiscount > 0" class="flex justify-between py-1 text-red-500">
              <span>상품할인</span><span>−{{ formatPrice(totalDiscount) }}</span>
            </div>
            <div class="flex justify-between py-1 text-gray-700">
              <span>배송비</span><span class="text-rocket-500 font-bold">무료</span>
            </div>
          </div>
          <div class="border-t border-gray-200 mt-3 pt-4 flex justify-between items-baseline">
            <span class="font-extrabold text-gray-900">총 결제금액</span>
            <span class="text-2xl font-black text-red-500">{{ formatPrice(cartStore.totalAmount) }}</span>
          </div>
          <p class="mt-2 text-xs text-gray-400">· 와우회원 가입 시 추가 5% 할인 적용</p>
          <RouterLink to="/checkout"
            class="btn-primary-bold w-full block text-center mt-4 text-base">
            {{ cartStore.totalCount }}개 상품 주문하기
          </RouterLink>
          <RouterLink to="/"
            class="btn-outline-bold w-full block text-center mt-2 text-sm !py-2.5 !font-semibold">
            계속 쇼핑하기
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { formatPrice } from '@/utils/format'
import { getCartItemDisplay } from '@/utils/productDisplay'

const cartStore = useCartStore()

// 체크박스 선택 상태
const selectedIds = ref<Set<number>>(new Set())

const allSelected = computed(() =>
  cartStore.items.length > 0 && selectedIds.value.size === cartStore.items.length
)

function toggle(id: number) {
  const next = new Set(selectedIds.value)
  if (next.has(id)) next.delete(id)
  else next.add(id)
  selectedIds.value = next
}

function toggleAll() {
  if (allSelected.value) selectedIds.value = new Set()
  else selectedIds.value = new Set(cartStore.items.map(i => i.id))
}

async function removeSelected() {
  for (const id of Array.from(selectedIds.value)) {
    await cartStore.removeItem(id)
  }
  selectedIds.value = new Set()
}

// 원가 합계 (데모용 - productDisplay 유틸 기반)
const subtotalOriginal = computed(() =>
  cartStore.items.reduce(
    (sum, i) => sum + getCartItemDisplay(i).originalPrice * i.quantity,
    0
  )
)
const totalDiscount = computed(() => subtotalOriginal.value - cartStore.totalAmount)

// 장바구니가 바뀌면 전체선택으로 초기화
watch(() => cartStore.items.map(i => i.id).join(','), () => {
  selectedIds.value = new Set(cartStore.items.map(i => i.id))
})

onMounted(async () => {
  await cartStore.fetchCart()
  selectedIds.value = new Set(cartStore.items.map(i => i.id))
})
</script>
