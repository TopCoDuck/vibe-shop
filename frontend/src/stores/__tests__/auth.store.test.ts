import { describe, it, expect, vi, beforeEach } from 'vitest'
import { useAuthStore } from '../auth'

// API 모킹
vi.mock('@/api/auth', () => ({
  authApi: {
    login: vi.fn(),
    signup: vi.fn(),
    getMe: vi.fn(),
  },
}))

import { authApi } from '@/api/auth'

describe('useAuthStore', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.getItem = vi.fn().mockReturnValue(null)
  })

  it('초기 상태 - 로그인되지 않음', () => {
    const store = useAuthStore()
    expect(store.isLoggedIn).toBe(false)
    expect(store.user).toBeNull()
  })

  it('로그인 성공 - 토큰 저장 및 사용자 정보 업데이트', async () => {
    const mockLoginResponse = {
      data: {
        data: {
          accessToken: 'mock-access-token',
          refreshToken: 'mock-refresh-token',
          email: 'test@example.com',
          name: '테스트유저',
          role: 'USER',
        },
      },
    }
    const mockMeResponse = {
      data: {
        data: {
          id: 1,
          email: 'test@example.com',
          name: '테스트유저',
          role: 'USER',
        },
      },
    }

    vi.mocked(authApi.login).mockResolvedValue(mockLoginResponse as any)
    vi.mocked(authApi.getMe).mockResolvedValue(mockMeResponse as any)

    const store = useAuthStore()
    await store.login('test@example.com', 'password123')

    expect(store.token).toBe('mock-access-token')
    expect(store.isLoggedIn).toBe(true)
    // 자동 로그인 미사용(기본값 false) 시 sessionStorage에 저장
    expect(sessionStorage.setItem).toHaveBeenCalledWith('accessToken', 'mock-access-token')
  })

  it('로그아웃 - 상태 초기화', async () => {
    const store = useAuthStore()
    store.token = 'some-token'

    store.logout()

    expect(store.token).toBeNull()
    expect(store.user).toBeNull()
    expect(store.isLoggedIn).toBe(false)
    expect(localStorage.removeItem).toHaveBeenCalledWith('accessToken')
    expect(localStorage.removeItem).toHaveBeenCalledWith('refreshToken')
  })

  it('isAdmin - ADMIN 역할일 때 true', () => {
    const store = useAuthStore()
    store.user = { id: 1, email: 'admin@example.com', name: '관리자', role: 'ADMIN' }

    expect(store.isAdmin).toBe(true)
  })

  it('isAdmin - USER 역할일 때 false', () => {
    const store = useAuthStore()
    store.user = { id: 1, email: 'user@example.com', name: '유저', role: 'USER' }

    expect(store.isAdmin).toBe(false)
  })

  it('fetchMe 실패 시 로그아웃', async () => {
    vi.mocked(authApi.getMe).mockRejectedValue(new Error('Unauthorized'))

    const store = useAuthStore()
    store.token = 'invalid-token'

    await store.fetchMe()

    expect(store.user).toBeNull()
    expect(store.token).toBeNull()
  })
})
