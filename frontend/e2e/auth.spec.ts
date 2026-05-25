import { test, expect } from '@playwright/test'
import { TEST_USER, login, logout } from './helpers/auth'

test.describe('인증 플로우', () => {

  test.describe('회원가입', () => {
    test('정상 회원가입 후 로그인 페이지로 이동', async ({ page }) => {
      const uniqueEmail = `test_${Date.now()}@vibeshop.com`

      await page.goto('/signup')
      await expect(page.getByRole('heading', { name: '회원가입' })).toBeVisible()

      await page.getByPlaceholder('이메일 입력').fill(uniqueEmail)
      await page.getByPlaceholder('비밀번호 입력').fill('password123!')
      await page.getByPlaceholder('이름 입력').fill('신규회원')
      await page.getByPlaceholder('010-0000-0000').fill('010-1234-5678')
      await page.getByRole('button', { name: '회원가입' }).click()

      await expect(page.getByText('회원가입이 완료되었습니다')).toBeVisible()
      await page.waitForURL('/login')
    })

    test('이미 사용 중인 이메일로 가입 시 에러 표시', async ({ page }) => {
      await page.goto('/signup')

      await page.getByPlaceholder('이메일 입력').fill(TEST_USER.email)
      await page.getByPlaceholder('비밀번호 입력').fill('password123!')
      await page.getByPlaceholder('이름 입력').fill('중복회원')
      await page.getByRole('button', { name: '회원가입' }).click()

      await expect(page.getByText(/이미 사용 중인 이메일|회원가입에 실패/)).toBeVisible()
    })
  })

  test.describe('로그인', () => {
    test('정상 로그인 후 홈으로 이동', async ({ page }) => {
      await page.goto('/login')

      await page.getByPlaceholder('이메일 입력').fill(TEST_USER.email)
      await page.getByPlaceholder('비밀번호 입력').fill(TEST_USER.password)
      await page.getByRole('button', { name: '로그인' }).click()

      await page.waitForURL('/')
      await expect(page).toHaveURL('/')
    })

    test('잘못된 비밀번호로 로그인 시 에러 표시', async ({ page }) => {
      await page.goto('/login')

      await page.getByPlaceholder('이메일 입력').fill(TEST_USER.email)
      await page.getByPlaceholder('비밀번호 입력').fill('wrongPassword!')
      await page.getByRole('button', { name: '로그인' }).click()

      await expect(page.getByText(/로그인에 실패|아이디 또는 비밀번호/)).toBeVisible()
    })

    test('로그인 없이 장바구니 접근 시 로그인 페이지로 리다이렉트', async ({ page }) => {
      await page.goto('/cart')
      await page.waitForURL('/login')
      await expect(page).toHaveURL('/login')
    })
  })

  test.describe('로그아웃', () => {
    test('로그인 후 로그아웃 시 상태 초기화', async ({ page }) => {
      await login(page)

      // 로그아웃 (localStorage 클리어)
      await logout(page)

      // 인증 필요 페이지 접근 불가 확인
      await page.goto('/cart')
      await expect(page).toHaveURL('/login')
    })
  })
})
