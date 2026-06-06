<template>
  <Teleport to="body">
    <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div class="bg-white rounded-xl w-full max-w-md shadow-2xl">
        <!-- 헤더 -->
        <div class="flex items-center justify-between px-6 py-4 border-b">
          <h2 class="font-bold text-gray-900 text-lg">클레임 신청</h2>
          <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600 text-xl leading-none">✕</button>
        </div>

        <div class="px-6 py-5 space-y-5">
          <!-- 클레임 유형 -->
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">클레임 유형</label>
            <div class="grid grid-cols-2 gap-2">
              <button v-for="opt in typeOptions" :key="opt.value"
                @click="form.type = opt.value"
                :disabled="!isTypeAvailable(opt.value)"
                class="py-2.5 px-3 rounded-lg border text-sm font-medium transition-colors"
                :class="form.type === opt.value
                  ? 'border-rocket-500 bg-rocket-50 text-rocket-600'
                  : isTypeAvailable(opt.value)
                    ? 'border-gray-200 text-gray-700 hover:border-gray-300'
                    : 'border-gray-100 text-gray-300 cursor-not-allowed bg-gray-50'">
                {{ opt.label }}
                <span v-if="!isTypeAvailable(opt.value)" class="block text-xs text-gray-300 font-normal">불가</span>
              </button>
            </div>
          </div>

          <!-- 사유 -->
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">사유</label>
            <select v-model="form.reason"
              class="w-full border border-gray-300 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:border-rocket-400">
              <option value="" disabled>사유를 선택하세요</option>
              <option v-for="opt in reasonOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
            </select>
          </div>

          <!-- 상세 사유 -->
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">
              상세 사유 <span class="text-gray-400 font-normal">(선택)</span>
            </label>
            <textarea v-model="form.reasonDetail" rows="3" placeholder="구체적인 사유를 입력해 주세요."
              class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-rocket-400 resize-none"></textarea>
          </div>

          <p v-if="error" class="text-red-500 text-xs">{{ error }}</p>
        </div>

        <div class="px-6 pb-5 flex gap-2 justify-end">
          <button @click="$emit('close')"
            class="px-5 py-2.5 text-sm text-gray-600 border border-gray-300 rounded-lg hover:bg-gray-50">
            취소
          </button>
          <button @click="submit" :disabled="!isValid || submitting"
            class="px-5 py-2.5 text-sm font-bold bg-rocket-500 text-white rounded-lg hover:bg-rocket-600 disabled:opacity-50">
            {{ submitting ? '신청 중...' : '클레임 신청' }}
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { claimApi } from '@/api/claim'
import type { ClaimType, ClaimReason } from '@/types'
import { CLAIM_TYPE_LABEL, CLAIM_REASON_LABEL } from '@/types'

const props = defineProps<{
  orderId: number
  orderStatus: string
}>()

const emit = defineEmits<{
  close: []
  success: []
}>()

const typeOptions: { value: ClaimType; label: string }[] = [
  { value: 'CANCEL', label: CLAIM_TYPE_LABEL.CANCEL },
  { value: 'RETURN', label: CLAIM_TYPE_LABEL.RETURN },
  { value: 'EXCHANGE', label: CLAIM_TYPE_LABEL.EXCHANGE },
  { value: 'REFUND', label: CLAIM_TYPE_LABEL.REFUND },
]

const reasonOptions: { value: ClaimReason; label: string }[] = Object.entries(CLAIM_REASON_LABEL).map(
  ([value, label]) => ({ value: value as ClaimReason, label })
)

const form = ref<{ type: ClaimType | ''; reason: ClaimReason | ''; reasonDetail: string }>({
  type: '',
  reason: '',
  reasonDetail: '',
})
const submitting = ref(false)
const error = ref('')

function isTypeAvailable(type: ClaimType): boolean {
  const s = props.orderStatus
  if (type === 'CANCEL') return s === 'PENDING' || s === 'PAID'
  return s === 'DELIVERED'
}

const isValid = computed(() => form.value.type !== '' && form.value.reason !== '')

async function submit() {
  if (!isValid.value) return
  submitting.value = true
  error.value = ''
  try {
    await claimApi.create(props.orderId, {
      type: form.value.type as ClaimType,
      reason: form.value.reason as ClaimReason,
      reasonDetail: form.value.reasonDetail || undefined,
    })
    emit('success')
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? '오류가 발생했습니다.'
  } finally {
    submitting.value = false
  }
}
</script>
