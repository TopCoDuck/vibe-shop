<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4">
    <div class="max-w-md w-full">
      <div class="text-center mb-8">
        <RouterLink to="/" class="text-3xl font-bold text-red-500">Vibe Shop</RouterLink>
        <h2 class="mt-4 text-2xl font-bold text-gray-900">로그인</h2>
      </div>
      <div class="card">
        <form @submit.prevent="handleLogin" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">이메일</label>
            <input v-model="form.email" type="email" required class="input-field" placeholder="이메일 입력" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">비밀번호</label>
            <input v-model="form.password" type="password" required class="input-field" placeholder="비밀번호 입력" />
          </div>

          <!-- 자동 로그인 체크박스 -->
          <div class="flex items-center gap-2">
            <input
              id="autoLogin"
              v-model="form.autoLogin"
              type="checkbox"
              class="w-4 h-4 text-red-500 border-gray-300 rounded cursor-pointer accent-red-500"
            />
            <label for="autoLogin" class="text-sm text-gray-600 cursor-pointer select-none">
              자동 로그인
              <span class="text-gray-400 text-xs">(최대 14일)</span>
            </label>
          </div>

          <p v-if="error" class="text-red-500 text-sm">{{ error }}</p>
          <button type="submit" :disabled="loading" class="btn-primary w-full">
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

const auth = useAuthStore()
const cartStore = useCartStore()
const router = useRouter()

const form = reactive({ email: '', password: '', autoLogin: false })
const loading = ref(false)
const error = ref('')

async function handleLogin() {
  loading.value = true
  error.value = ''
  try {
    await auth.login(form.email, form.password, form.autoLogin)
    await cartStore.fetchCart()
    router.push('/')
  } catch (e: any) {
    error.value = e.response?.data?.message || '이메일 또는 비밀번호가 올바르지 않습니다.'
  } finally {
    loading.value = false
  }
}
</script>
