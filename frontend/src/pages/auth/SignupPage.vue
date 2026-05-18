<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4">
    <div class="max-w-md w-full">
      <div class="text-center mb-8">
        <RouterLink to="/" class="text-3xl font-bold text-red-500">Vibe Shop</RouterLink>
        <h2 class="mt-4 text-2xl font-bold text-gray-900">회원가입</h2>
      </div>
      <div class="card">
        <form @submit.prevent="handleSignup" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">이메일 *</label>
            <input v-model="form.email" type="email" required class="input-field" placeholder="이메일 입력" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">비밀번호 * (8자 이상)</label>
            <input v-model="form.password" type="password" required minlength="8" class="input-field" placeholder="비밀번호 입력" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">이름 *</label>
            <input v-model="form.name" type="text" required class="input-field" placeholder="이름 입력" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">휴대폰</label>
            <input v-model="form.phone" type="tel" class="input-field" placeholder="010-0000-0000" />
          </div>
          <p v-if="error" class="text-red-500 text-sm">{{ error }}</p>
          <p v-if="success" class="text-green-600 text-sm">{{ success }}</p>
          <button type="submit" :disabled="loading" class="btn-primary w-full">
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

const auth = useAuthStore()
const router = useRouter()

const form = reactive({ email: '', password: '', name: '', phone: '' })
const loading = ref(false)
const error = ref('')
const success = ref('')

async function handleSignup() {
  loading.value = true
  error.value = ''
  try {
    await auth.signup(form.email, form.password, form.name, form.phone || undefined)
    success.value = '회원가입이 완료되었습니다. 로그인해주세요.'
    setTimeout(() => router.push('/login'), 1500)
  } catch (e: any) {
    error.value = e.response?.data?.message || '회원가입에 실패했습니다.'
  } finally {
    loading.value = false
  }
}
</script>
