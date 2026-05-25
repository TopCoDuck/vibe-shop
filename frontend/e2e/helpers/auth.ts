import { Page } from '@playwright/test'

export const TEST_USER = {
  email: 'e2e@vibeshop.com',
  password: 'password123!',
  name: 'E2E테스터',
}

export const ADMIN_USER = {
  email: 'admin@vibeshop.com',
  password: 'admin1234!',
  name: '관리자',
}

/** 로그인 헬퍼 */
export async function login(page: Page, email = TEST_USER.email, password = TEST_USER.password) {
  await page.goto('/login')
  await page.getByPlaceholder('이메일 입력').fill(email)
  await page.getByPlaceholder('비밀번호 입력').fill(password)
  await page.getByRole('button', { name: '로그인' }).click()
  await page.waitForURL('/')
}

/** 로그아웃 헬퍼 */
export async function logout(page: Page) {
  await page.evaluate(() => {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
  })
  await page.goto('/')
}

/** localStorage에 토큰 직접 주입 (API 로그인 건너뛰기) */
export async function injectToken(page: Page, token: string) {
  await page.addInitScript((t) => {
    localStorage.setItem('accessToken', t)
  }, token)
}
