import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'
import type { User } from '@/types'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const token = ref<string | null>(localStorage.getItem('accessToken'))

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  async function login(email: string, password: string) {
    const res = await authApi.login({ email, password })
    const data = res.data.data
    token.value = data.accessToken
    localStorage.setItem('accessToken', data.accessToken)
    localStorage.setItem('refreshToken', data.refreshToken)
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
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
  }

  if (token.value) {
    fetchMe()
  }

  return { user, token, isLoggedIn, isAdmin, login, signup, logout, fetchMe }
})
