import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'
import type { User } from '@/types'

const AUTO_LOGIN_KEY = 'autoLoginExpiry'
const ACCESS_TOKEN_KEY = 'accessToken'
const REFRESH_TOKEN_KEY = 'refreshToken'
const AUTO_LOGIN_DAYS = 14

function isAutoLoginValid(): boolean {
  const expiry = localStorage.getItem(AUTO_LOGIN_KEY)
  if (!expiry) return false
  return Date.now() < Number(expiry)
}

function getStoredToken(): string | null {
  if (isAutoLoginValid()) {
    return localStorage.getItem(ACCESS_TOKEN_KEY)
  }
  return sessionStorage.getItem(ACCESS_TOKEN_KEY)
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const token = ref<string | null>(getStoredToken())

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  async function login(email: string, password: string, autoLogin = false) {
    const res = await authApi.login({ email, password })
    const data = res.data.data

    token.value = data.accessToken

    if (autoLogin) {
      // 자동 로그인: localStorage에 저장 + 14일 만료 시각 기록
      const expiry = Date.now() + AUTO_LOGIN_DAYS * 24 * 60 * 60 * 1000
      localStorage.setItem(AUTO_LOGIN_KEY, String(expiry))
      localStorage.setItem(ACCESS_TOKEN_KEY, data.accessToken)
      localStorage.setItem(REFRESH_TOKEN_KEY, data.refreshToken)
    } else {
      // 자동 로그인 미사용: sessionStorage (브라우저 종료 시 삭제)
      localStorage.removeItem(AUTO_LOGIN_KEY)
      localStorage.removeItem(ACCESS_TOKEN_KEY)
      localStorage.removeItem(REFRESH_TOKEN_KEY)
      sessionStorage.setItem(ACCESS_TOKEN_KEY, data.accessToken)
      sessionStorage.setItem(REFRESH_TOKEN_KEY, data.refreshToken)
    }

    await fetchMe()
  }

  async function signup(email: string, password: string, name: string, phone?: string) {
    await authApi.signup({ email, password, name, phone })
  }

  async function fetchMe() {
    try {
      const res = await authApi.getMe()
      user.value = res.data.data
    } catch {
      logout()
    }
  }

  function logout() {
    user.value = null
    token.value = null
    localStorage.removeItem(AUTO_LOGIN_KEY)
    localStorage.removeItem(ACCESS_TOKEN_KEY)
    localStorage.removeItem(REFRESH_TOKEN_KEY)
    sessionStorage.removeItem(ACCESS_TOKEN_KEY)
    sessionStorage.removeItem(REFRESH_TOKEN_KEY)
  }

  // 앱 초기화 시 자동 로그인 유효성 검사
  if (token.value) {
    if (!isAutoLoginValid() && !sessionStorage.getItem(ACCESS_TOKEN_KEY)) {
      // 자동 로그인 만료 + 세션 토큰도 없으면 로그아웃
      logout()
    } else {
      fetchMe()
    }
  }

  return { user, token, isLoggedIn, isAdmin, login, signup, logout, fetchMe }
})
