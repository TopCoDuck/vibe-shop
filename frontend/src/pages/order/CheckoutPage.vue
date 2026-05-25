<template>
  <div class="max-w-4xl mx-auto px-4 py-10">
    <h1 class="text-2xl font-bold text-gray-900 mb-8">주문/결제</h1>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <div class="lg:col-span-2 space-y-6">
        <!-- 배송 정보 -->
        <div class="card">
          <h2 class="font-semibold text-gray-800 mb-4">배송 정보</h2>
          <div class="space-y-1">
            <FormField
              ref="nameRef"
              v-model="form.receiverName"
              label="수령인"
              type="text"
              placeholder="수령인 이름을 입력해주세요"
              :required="true"
              :rules="[rules.name()]"
              success-message="확인되었습니다."
            />

            <FormField
              ref="phoneRef"
              v-model="form.receiverPhone"
              label="연락처"
              type="tel"
              placeholder="010-0000-0000"
              :required="true"
              :rules="[rules.phone(true)]"
              hint="예: 010-1234-5678"
              success-message="올바른 번호입니다."
            />

            <FormField
              ref="addressRef"
              v-model="form.shippingAddress"
              label="배송 주소"
              type="text"
              placeholder="배송 주소를 입력해주세요"
              :required="true"
              :rules="[rules.address()]"
              success-message="주소가 입력되었습니다."
            />
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
          <p v-if="serverError" class="text-red-500 text-sm mb-3 flex items-center gap-1">
            <svg class="w-4 h-4 shrink-0" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd"
                d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z"
                clip-rule="evenodd" />
            </svg>
            {{ serverError }}
          </p>
          <button
            @click="placeOrder"
            :disabled="loading || cartStore.items.length === 0"
            class="btn-primary w-full"
          >
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
import FormField from '@/components/common/FormField.vue'
import { rules } from '@/utils/validationRules'

const cartStore = useCartStore()
const router = useRouter()

const form = reactive({ receiverName: '', receiverPhone: '', shippingAddress: '' })
const loading = ref(false)
const serverError = ref('')

const nameRef    = ref<InstanceType<typeof FormField> | null>(null)
const phoneRef   = ref<InstanceType<typeof FormField> | null>(null)
const addressRef = ref<InstanceType<typeof FormField> | null>(null)

async function placeOrder() {
  // 전체 touch → 미입력 필드 즉시 에러 표시
  nameRef.value?.touch()
  phoneRef.value?.touch()
  addressRef.value?.touch()

  const hasAnyError = [nameRef, phoneRef, addressRef].some(r => r.value?.hasError)
  if (hasAnyError) return

  loading.value = true
  serverError.value = ''
  try {
    const cartItemIds = cartStore.items.map(i => i.id)
    const res = await orderApi.createOrder({ ...form, cartItemIds })
    await cartStore.fetchCart()
    router.push(`/orders/${res.data.data.id}`)
  } catch (e: any) {
    serverError.value = e.response?.data?.message || '주문에 실패했습니다.'
  } finally {
    loading.value = false
  }
}
</script>
