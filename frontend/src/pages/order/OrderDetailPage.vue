<template>
  <div class="max-w-3xl mx-auto px-4 py-10">
    <div class="flex items-center gap-4 mb-8">
      <RouterLink to="/orders" class="text-gray-400 hover:text-gray-600">← 주문 내역</RouterLink>
      <h1 class="text-2xl font-bold text-gray-900">주문 상세</h1>
    </div>

    <div v-if="order" class="space-y-6">
      <!-- 주문 상태 -->
      <div class="card">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-400">주문번호 #{{ order.id }}</p>
            <p class="text-sm text-gray-400 mt-1">{{ formatDate(order.createdAt) }}</p>
          </div>
          <span :class="statusClass(order.status)" class="badge text-sm px-3 py-1">
            {{ ORDER_STATUS_LABEL[order.status] }}
          </span>
        </div>
        <div class="flex gap-2 mt-4 flex-wrap">
          <button v-if="order.status === 'PENDING'" @click="cancelOrder"
            :disabled="cancelling" class="btn-outline text-sm">
            {{ cancelling ? '취소 중...' : '주문 취소' }}
          </button>
          <button v-if="isClaimable && !existingClaim" @click="showClaimModal = true"
            class="btn-outline text-sm text-orange-600 border-orange-300 hover:bg-orange-50">
            클레임 신청
          </button>
        </div>
      </div>

      <!-- 클레임 정보 -->
      <div v-if="existingClaim" class="card border-l-4 border-orange-400">
        <div class="flex items-center justify-between mb-3">
          <h2 class="font-semibold text-gray-800">클레임 내역</h2>
          <span :class="claimStatusClass(existingClaim.status)"
            class="text-xs font-bold px-2 py-1 rounded-full">
            {{ CLAIM_STATUS_LABEL[existingClaim.status] }}
          </span>
        </div>
        <dl class="space-y-1.5 text-sm">
          <div class="flex gap-3"><dt class="w-20 text-gray-400 shrink-0">유형</dt><dd>{{ CLAIM_TYPE_LABEL[existingClaim.type] }}</dd></div>
          <div class="flex gap-3"><dt class="w-20 text-gray-400 shrink-0">사유</dt><dd>{{ CLAIM_REASON_LABEL[existingClaim.reason] }}</dd></div>
          <div v-if="existingClaim.reasonDetail" class="flex gap-3">
            <dt class="w-20 text-gray-400 shrink-0">상세</dt>
            <dd class="text-gray-700">{{ existingClaim.reasonDetail }}</dd>
          </div>
          <div v-if="existingClaim.adminComment" class="flex gap-3">
            <dt class="w-20 text-gray-400 shrink-0">처리 메모</dt>
            <dd class="text-gray-700">{{ existingClaim.adminComment }}</dd>
          </div>
          <div class="flex gap-3"><dt class="w-20 text-gray-400 shrink-0">접수일</dt><dd>{{ formatDate(existingClaim.createdAt) }}</dd></div>
        </dl>
        <button v-if="existingClaim.status === 'REQUESTED'" @click="cancelClaim"
          class="mt-4 text-xs text-gray-400 hover:text-red-500 underline">
          클레임 취소
        </button>
      </div>

      <!-- 배송 정보 -->
      <div class="card">
        <h2 class="font-semibold text-gray-800 mb-3">배송 정보</h2>
        <dl class="space-y-2 text-sm">
          <div class="flex"><dt class="w-24 text-gray-400">수령인</dt><dd>{{ order.receiverName }}</dd></div>
          <div class="flex"><dt class="w-24 text-gray-400">연락처</dt><dd>{{ order.receiverPhone }}</dd></div>
          <div class="flex"><dt class="w-24 text-gray-400">배송지</dt><dd>{{ order.shippingAddress }}</dd></div>
        </dl>
      </div>

      <!-- 주문 상품 -->
      <div class="card">
        <h2 class="font-semibold text-gray-800 mb-4">주문 상품</h2>
        <div class="space-y-4">
          <div v-for="item in order.items" :key="item.productId" class="flex gap-4">
            <div class="w-16 h-16 bg-gray-100 rounded-lg shrink-0 overflow-hidden">
              <img v-if="item.imageUrl" :src="item.imageUrl" class="w-full h-full object-cover" />
            </div>
            <div class="flex-1">
              <p class="font-medium">{{ item.productName }}</p>
              <p class="text-sm text-gray-400">{{ formatPrice(item.price) }} × {{ item.quantity }}개</p>
            </div>
            <p class="font-semibold">{{ formatPrice(item.subtotal) }}</p>
          </div>
        </div>
        <div class="border-t mt-4 pt-4 flex justify-between font-bold text-lg">
          <span>합계</span>
          <span class="text-red-500">{{ formatPrice(order.totalAmount) }}</span>
        </div>
      </div>
    </div>

    <!-- 클레임 모달 -->
    <ClaimModal
      v-if="showClaimModal && order"
      :orderId="order.id"
      :orderStatus="order.status"
      @close="showClaimModal = false"
      @success="onClaimSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import { orderApi } from '@/api/orders'
import { claimApi } from '@/api/claim'
import { formatPrice, formatDate } from '@/utils/format'
import { ORDER_STATUS_LABEL, CLAIM_TYPE_LABEL, CLAIM_REASON_LABEL, CLAIM_STATUS_LABEL } from '@/types'
import type { Order, Claim, ClaimStatus } from '@/types'
import ClaimModal from '@/components/claim/ClaimModal.vue'

const route = useRoute()
const order = ref<Order | null>(null)
const existingClaim = ref<Claim | null>(null)
const cancelling = ref(false)
const showClaimModal = ref(false)

const isClaimable = computed(() => {
  const s = order.value?.status
  return s === 'PENDING' || s === 'PAID' || s === 'DELIVERED'
})

function statusClass(status: string) {
  const map: Record<string, string> = {
    PENDING: 'bg-yellow-100 text-yellow-700',
    PAID: 'bg-blue-100 text-blue-700',
    SHIPPING: 'bg-purple-100 text-purple-700',
    DELIVERED: 'bg-green-100 text-green-700',
    CANCELLED: 'bg-gray-100 text-gray-500'
  }
  return map[status] || 'bg-gray-100 text-gray-500'
}

function claimStatusClass(status: ClaimStatus) {
  const map: Record<ClaimStatus, string> = {
    REQUESTED: 'bg-orange-100 text-orange-700',
    IN_PROGRESS: 'bg-blue-100 text-blue-700',
    COMPLETED: 'bg-green-100 text-green-700',
    REJECTED: 'bg-gray-100 text-gray-500',
  }
  return map[status]
}

async function cancelOrder() {
  cancelling.value = true
  try {
    const res = await orderApi.cancelOrder(Number(route.params.id))
    order.value = res.data.data
  } finally {
    cancelling.value = false
  }
}

async function cancelClaim() {
  if (!existingClaim.value || !confirm('클레임을 취소하시겠습니까?')) return
  try {
    await claimApi.cancel(existingClaim.value.id)
    existingClaim.value = null
  } catch (e: any) {
    alert(e?.response?.data?.message ?? '오류가 발생했습니다.')
  }
}

async function onClaimSuccess() {
  showClaimModal.value = false
  await fetchClaim()
}

async function fetchClaim() {
  try {
    const res = await claimApi.getByOrder(Number(route.params.id))
    existingClaim.value = res.data.data ?? null
  } catch {
    existingClaim.value = null
  }
}

onMounted(async () => {
  const res = await orderApi.getOrder(Number(route.params.id))
  order.value = res.data.data
  await fetchClaim()
})
</script>
