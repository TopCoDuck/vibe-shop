<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4">
    <div class="max-w-md w-full">
      <div class="text-center mb-8">
        <RouterLink to="/" class="text-3xl font-bold text-red-500">Vibe Shop</RouterLink>
        <h2 class="mt-4 text-2xl font-bold text-gray-900">회원가입</h2>
      </div>

      <div class="card">
        <form @submit.prevent="handleSignup" novalidate class="space-y-1">
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
            placeholder="영문+숫자 8자 이상"
            :required="true"
            :rules="[rules.password()]"
            :show-strength="true"
            hint="영문+숫자 조합 8자 이상"
          />

          <FormField
            ref="confirmRef"
            v-model="form.confirm"
            label="비밀번호 확인"
            type="password"
            placeholder="비밀번호를 다시 입력해주세요"
            :required="true"
            :rules="[rules.required('비밀번호 확인을 입력해주세요.'), confirmRule]"
            success-message="비밀번호가 일치합니다."
          />

          <FormField
            ref="nameRef"
            v-model="form.name"
            label="이름"
            type="text"
            placeholder="이름을 입력해주세요"
            :required="true"
            :rules="[rules.name()]"
            success-message="확인되었습니다."
          />

          <FormField
            ref="phoneRef"
            v-model="form.phone"
            label="휴대폰"
            type="tel"
            placeholder="010-0000-0000"
            :required="false"
            :rules="[rules.phone(false)]"
            hint="선택 입력 (예: 010-1234-5678)"
            success-message="올바른 번호입니다."
          />

          <!-- 서버 에러 / 성공 -->
          <p v-if="serverError" class="text-red-500 text-sm flex items-center gap-1 pt-1">
            <svg class="w-4 h-4 shrink-0" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd"
                d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z"
                clip-rule="evenodd" />
            </svg>
            {{ serverError }}
          </p>
          <p v-if="success" class="text-green-600 text-sm flex items-center gap-1 pt-1">
            <svg class="w-4 h-4 shrink-0" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd"
                d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                clip-rule="evenodd" />
            </svg>
            {{ success }}
          </p>

          <button
            type="submit"
            :disabled="loading"
            class="btn-primary w-full !mt-5"
          >
            {{ loading ? '가입 중...' : '회원가입' }}
          </button>
        </form>

        <p class="text-center text-sm text-gray-500 mt-4">
          이미 계정이 있으신가요?
          <RouterLink to="/login" class="text-red-500 font-medium hover:underline">로그인</RouterLink>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import FormField from '@/components/common/FormField.vue'
import { rules } from '@/utils/validationRules'

const auth = useAuthStore()
const router = useRouter()

const form = reactive({ email: '', password: '', confirm: '', name: '', phone: '' })
const loading = ref(false)
const serverError = ref('')
const success = ref('')

const emailRef   = ref<InstanceType<typeof FormField> | null>(null)
const passwordRef= ref<InstanceType<typeof FormField> | null>(null)
const confirmRef = ref<InstanceType<typeof FormField> | null>(null)
const nameRef    = ref<InstanceType<typeof FormField> | null>(null)
const phoneRef   = ref<InstanceType<typeof FormField> | null>(null)

// 비밀번호 확인 동적 룰 (form.password에 의존)
const confirmRule = (v: string) =>
  v === form.password || '비밀번호가 일치하지 않습니다.'

async function handleSignup() {
  // 전체 touch
  emailRef.value?.touch()
  passwordRef.value?.touch()
  confirmRef.value?.touch()
  nameRef.value?.touch()
  phoneRef.value?.touch()

  const hasAnyError = [emailRef, passwordRef, confirmRef, nameRef, phoneRef]
    .some(r => r.value?.hasError)
  if (hasAnyError) return

  loading.value = true
  serverError.value = ''
  try {
    await auth.signup(form.email, form.password, form.name, form.phone || undefined)
    success.value = '회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.'
    setTimeout(() => router.push('/login'), 1500)
  } catch (e: any) {
    serverError.value = e.response?.data?.message || '회원가입에 실패했습니다.'
  } finally {
    loading.value = false
  }
}
</script>
