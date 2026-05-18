<template>
  <div class="max-w-4xl mx-auto px-4 py-10">
    <h1 class="text-2xl font-bold text-gray-900 mb-8">주문/결제</h1>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <div class="lg:col-span-2 space-y-6">
        <!-- 배송 정보 -->
        <div class="card">
          <h2 class="font-semibold text-gray-800 mb-4">배송 정보</h2>
          <div class="space-y-3">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">수령인 *</label>
              <input v-model="form.receiverName" class="input-field" placeholder="수령인 이름" />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">연락처 *</label>
              <input v-model="form.receiverPhone" class="input-field" placeholder="010-0000-0000" />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">배송 주소 *</label>
              <input v-model="form.shippingAddress" class="input-field" placeholder="배송 주소를 입력해주세요" />
            </div>
          </div>
        </div>

        <!-- 주문 상품 -->
        <div class="card">
          <h2 class="font-semibold text-gray-800 mb-4">주문 상품</h2>
          <div class="space-y-3">
            <div v-for="item in cartStore.items" :key="item.id" class="flex items-center gap-3">
              <div class="w-12 h-12 bg-gray-100 rounded-lg shrink-0 overflow-hidden">
                <img v-if="item.imageUrl" :src="item.imageUrl" class="w-full h-full object-cover" />
              </div>
              <div class="flex-1">
                <p class="text-sm font-medium">{{ item.productName }}</p>
                <p class="text-xs text-gray-400">{{ item.quantity }}개</p>
              </div>
              <p class="text-sm font-semibold">{{ formatPrice(item.subtotal) }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 결제 요약 -->
      <div>
        <div class="card sticky top-24">
          <h2 class="font-semibold text-gray-800 mb-4">결제 금액</h2>
          <div class="space-y-2 mb-4">
            <div class="flex justify-between text-sm text-gray-600">
              <span>상품 금액</span><span>{{ formatPrice(cartStore.totalAmount) }}</span>
            </div>
            <div class="flex justify-between text-sm text-gray-600">
              <span>배송비</span><span class="text-green-600">무료</span>
            </div>
          </div>
          <div class="border-t pt-4 flex justify-between font-bold text-lg mb-4">
            <span>합계</span><span class="text-red-500">{{ formatPrice(cartStore.totalAmount) }}</span>
          </div>
          <p v-if="error" class="text-red-500 text-sm mb-3">{{ error }}</p>
          <button @click="placeOrder" :disabled="loading || cartStore.items.length === 0" class="btn-primary w-full">
            {{ loading ? '주문 처리 중...' : '결제하기' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { orderApi } from '@/api/orders'
import { useCartStore } from '@/stores/cart'
import { formatPrice } from '@/utils/format'

const cartStore = useCartStore()
const router = useRouter()

const form = reactive({ receiverName: '', receiverPhone: '', shippingAddress: '' })
const loading = ref(false)
const error = ref('')

async function placeOrder() {
  if (!form.receiverName || !form.receiverPhone || !form.shippingAddress) {
    error.value = '배송 정보를 모두 입력해주세요.'
    return
  }
  loading.value = true
  error.value = ''
  try {
    const cartItemIds = cartStore.items.map(i => i.id)
    const res = await orderApi.createOrder({ ...form, cartItemIds })
    await cartStore.fetchCart()
    router.push(`/orders/${res.data.data.id}`)
  } catch (e: any) {
    error.value = e.response?.data?.message || '주문에 실패했습니다.'
  } finally {
    loading.value = false
  }
}
</script>
