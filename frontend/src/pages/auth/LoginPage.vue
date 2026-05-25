<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4">
    <div class="max-w-md w-full">
      <div class="text-center mb-8">
        <RouterLink to="/" class="text-3xl font-bold text-red-500">Vibe Shop</RouterLink>
        <h2 class="mt-4 text-2xl font-bold text-gray-900">로그인</h2>
      </div>

      <div class="card">
        <form @submit.prevent="handleLogin" novalidate class="space-y-1">
          <FormField
            ref="emailRef"
            v-model="form.email"
            label="이메일"
            type="email"
            placeholder="이메일을 입력해주세요"
            :required="true"
            :rules="[rules.email()]"
            success-message="사용 가능한 이메일입니다."
          />

          <FormField
            ref="passwordRef"
            v-model="form.password"
            label="비밀번호"
            type="password"
            placeholder="비밀번호를 입력해주세요"
            :required="true"
            :rules="[rules.required('비밀번호를 입력해주세요.')]"
          />

          <!-- 자동 로그인 -->
          <div class="flex items-center gap-2 pt-1">
            <input
              id="autoLogin"
              v-model="form.autoLogin"
              type="checkbox"
              class="w-4 h-4 border-gray-300 rounded cursor-pointer accent-red-500"
            />
            <label for="autoLogin" class="text-sm text-gray-600 cursor-pointer select-none">
              자동 로그인
              <span class="text-gray-400 text-xs">(최대 14일)</span>
            </label>
          </div>

          <!-- 서버 에러 -->
          <p v-if="serverError" class="text-red-500 text-sm flex items-center gap-1 pt-1">
            <svg class="w-4 h-4 shrink-0" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd"
                d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z"
                clip-rule="evenodd" />
            </svg>
            {{ serverError }}
          </p>

          <button
            type="submit"
            :disabled="loading"
            class="btn-primary w-full mt-4 !mt-4"
          >
            {{ loading ? '로그인 중...' : '로그인' }}
          </button>
        </form>

        <p class="text-center text-sm text-gray-500 mt-4">
          계정이 없으신가요?
          <RouterLink to="/signup" class="text-red-500 font-medium hover:underline">회원가입</RouterLink>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import FormField from '@/components/common/FormField.vue'
import { rules } from '@/utils/validationRules'

const auth = useAuthStore()
const cartStore = useCartStore()
const router = useRouter()

const form = reactive({ email: '', password: '', autoLogin: false })
const loading = ref(false)
const serverError = ref('')

const emailRef    = ref<InstanceType<typeof FormField> | null>(null)
const passwordRef = ref<InstanceType<typeof FormField> | null>(null)

async function handleLogin() {
  // 제출 시 전체 필드 touch → 미입력 에러 즉시 표시
  emailRef.value?.touch()
  passwordRef.value?.touch()

  if (emailRef.value?.hasError || passwordRef.value?.hasError) return
  if (!form.email || !form.password) return

  loading.value = true
  serverError.value = ''
  try {
    await auth.login(form.email, form.password, form.autoLogin)
    await cartStore.fetchCart()
    router.push('/')
  } catch (e: any) {
    serverError.value = e.response?.data?.message || '이메일 또는 비밀번호가 올바르지 않습니다.'
  } finally {
    loading.value = false
  }
}
</script>
