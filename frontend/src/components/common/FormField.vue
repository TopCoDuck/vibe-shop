<template>
  <div>
    <!-- 라벨 -->
    <label
      v-if="label"
      :for="inputId"
      class="block text-sm font-medium mb-1 transition-colors"
      :class="hasError ? 'text-red-600' : 'text-gray-700'"
    >
      {{ label }}
      <span v-if="required" class="text-red-500 ml-0.5">*</span>
    </label>

    <!-- 입력 영역 -->
    <div class="relative">
      <input
        :id="inputId"
        v-bind="$attrs"
        :value="modelValue"
        :type="inputType"
        :placeholder="placeholder"
        class="input-field w-full pr-10 transition-colors duration-150"
        :class="{
          'border-red-400 focus:ring-red-300 bg-red-50': hasError,
          'border-green-400 focus:ring-green-300': isValid && dirty,
          'border-gray-300': !hasError && !(isValid && dirty),
        }"
        @input="onInput"
        @blur="onBlur"
      />

      <!-- 비밀번호 토글 버튼 -->
      <button
        v-if="type === 'password'"
        type="button"
        tabindex="-1"
        @click="showPassword = !showPassword"
        class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
      >
        <svg v-if="showPassword" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 4.411m0 0L21 21" />
        </svg>
        <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
        </svg>
      </button>

      <!-- 상태 아이콘 (비밀번호 제외) -->
      <div v-else-if="dirty" class="absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none">
        <svg v-if="hasError" class="w-4 h-4 text-red-500" fill="currentColor" viewBox="0 0 20 20">
          <path fill-rule="evenodd"
            d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z"
            clip-rule="evenodd" />
        </svg>
        <svg v-else-if="isValid" class="w-4 h-4 text-green-500" fill="currentColor" viewBox="0 0 20 20">
          <path fill-rule="evenodd"
            d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
            clip-rule="evenodd" />
        </svg>
      </div>
    </div>

    <!-- 피드백 메시지 영역 (항상 자리를 차지해 레이아웃 흔들림 방지) -->
    <div class="mt-1 min-h-[1.25rem]">
      <!-- 에러 메시지 -->
      <p v-if="hasError" class="text-xs text-red-500 flex items-center gap-1 animate-fadeIn">
        <svg class="w-3 h-3 shrink-0" fill="currentColor" viewBox="0 0 20 20">
          <path fill-rule="evenodd"
            d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z"
            clip-rule="evenodd" />
        </svg>
        {{ errorMessage }}
      </p>
      <!-- 성공 메시지 -->
      <p v-else-if="isValid && dirty && successMessage" class="text-xs text-green-600 flex items-center gap-1 animate-fadeIn">
        <svg class="w-3 h-3 shrink-0" fill="currentColor" viewBox="0 0 20 20">
          <path fill-rule="evenodd"
            d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
            clip-rule="evenodd" />
        </svg>
        {{ successMessage }}
      </p>
      <!-- 힌트 메시지 (아직 입력 전) -->
      <p v-else-if="hint && !dirty" class="text-xs text-gray-400">{{ hint }}</p>
    </div>

    <!-- 비밀번호 강도 바 (signup 비밀번호만) -->
    <div v-if="showStrength && dirty" class="mt-1">
      <div class="flex gap-1 mb-1">
        <div v-for="n in 4" :key="n"
          class="h-1 flex-1 rounded-full transition-all duration-300"
          :class="n <= passwordStrength ? strengthColor : 'bg-gray-200'" />
      </div>
      <p class="text-xs" :class="strengthTextColor">{{ strengthLabel }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, useId } from 'vue'

interface Props {
  modelValue: string
  label?: string
  type?: string
  placeholder?: string
  required?: boolean
  hint?: string
  successMessage?: string
  rules?: Array<(v: string) => string | true>
  showStrength?: boolean   // 비밀번호 강도 바 표시 여부
}

const props = withDefaults(defineProps<Props>(), {
  type: 'text',
  required: false,
  showStrength: false,
})

const emit = defineEmits<{ 'update:modelValue': [string] }>()

const inputId = `field-${Math.random().toString(36).slice(2)}`
const dirty = ref(false)        // 한 번이라도 입력했는지
const blurred = ref(false)      // 포커스를 잃은 적 있는지
const showPassword = ref(false)

const inputType = computed(() => {
  if (props.type === 'password') return showPassword.value ? 'text' : 'password'
  return props.type
})

// 룰 검사 결과
const errorMessage = computed<string>(() => {
  if (!dirty.value && !blurred.value) return ''
  if (!props.rules) return ''
  for (const rule of props.rules) {
    const result = rule(props.modelValue ?? '')
    if (result !== true) return result as string
  }
  return ''
})

const hasError = computed(() => (dirty.value || blurred.value) && !!errorMessage.value)
const isValid  = computed(() => dirty.value && !errorMessage.value)

// 비밀번호 강도
const passwordStrength = computed(() => {
  const v = props.modelValue ?? ''
  let score = 0
  if (v.length >= 8)  score++
  if (v.length >= 12) score++
  if (/[0-9]/.test(v) && /[a-zA-Z]/.test(v)) score++
  if (/[^a-zA-Z0-9]/.test(v)) score++
  return Math.min(score, 4)
})

const strengthColor = computed(() => {
  const map = ['bg-red-400', 'bg-orange-400', 'bg-yellow-400', 'bg-green-500']
  return map[passwordStrength.value - 1] ?? 'bg-gray-200'
})
const strengthTextColor = computed(() => {
  const map = ['text-red-500', 'text-orange-500', 'text-yellow-600', 'text-green-600']
  return map[passwordStrength.value - 1] ?? 'text-gray-400'
})
const strengthLabel = computed(() => {
  const map = ['매우 약함', '약함', '보통', '강함']
  return `비밀번호 강도: ${map[passwordStrength.value - 1] ?? '없음'}`
})

function onInput(e: Event) {
  dirty.value = true
  emit('update:modelValue', (e.target as HTMLInputElement).value)
}

function onBlur() {
  blurred.value = true
}

// 외부에서 유효성 강제 체크 (제출 시 호출)
function touch() {
  dirty.value = true
  blurred.value = true
}

defineExpose({ touch, isValid, hasError })
</script>

<style scoped>
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-4px); }
  to   { opacity: 1; transform: translateY(0); }
}
.animate-fadeIn { animation: fadeIn 0.15s ease-out; }
</style>
