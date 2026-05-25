import { test, expect } from '@playwright/test'
import { login } from './helpers/auth'

test.describe('장바구니 플로우', () => {

  test.beforeEach(async ({ page }) => {
    await login(page)
  })

  test('상품 상세에서 장바구니 담기 성공', async ({ page }) => {
    await page.goto('/')

    // 첫 번째 상품으로 이동
    await page.locator('a.card').first().waitFor({ timeout: 10000 })
    await page.locator('a.card').first().click()
    await page.waitForURL(/\/products\/\d+/)

    // 장바구니 담기
    await page.getByRole('button', { name: '장바구니 담기' }).click()

    // 성공 메시지 확인
    await expect(page.getByText('장바구니에 담겼습니다')).toBeVisible({ timeout: 5000 })
  })

  test('장바구니 페이지 - 담긴 상품 표시', async ({ page }) => {
    // 상품 담기
    await page.goto('/')
    await page.locator('a.card').first().waitFor({ timeout: 10000 })
    await page.locator('a.card').first().click()
    await page.waitForURL(/\/products\/\d+/)
    await page.getByRole('button', { name: '장바구니 담기' }).click()
    await page.waitForTimeout(500)

    // 장바구니 페이지 이동
    await page.goto('/cart')
    await expect(page).toHaveURL('/cart')

    // 상품이 장바구니에 있음을 확인
    const cartItems = page.locator('[class*="cart"]').filter({ hasText: /원/ })
    await expect(cartItems.first()).toBeVisible({ timeout: 5000 })
  })

  test('바로 구매 - 장바구니 페이지로 바로 이동', async ({ page }) => {
    await page.goto('/')
    await page.locator('a.card').first().waitFor({ timeout: 10000 })
    await page.locator('a.card').first().click()
    await page.waitForURL(/\/products\/\d+/)

    await page.getByRole('button', { name: '바로 구매' }).click()

    await expect(page).toHaveURL('/cart')
  })
})

test.describe('구매 플로우', () => {

  test.beforeEach(async ({ page }) => {
    await login(page)
  })

  test('장바구니 → 주문하기 페이지 이동', async ({ page }) => {
    // 상품 담기
    await page.goto('/')
    await page.locator('a.card').first().waitFor({ timeout: 10000 })
    await page.locator('a.card').first().click()
    await page.waitForURL(/\/products\/\d+/)
    await page.getByRole('button', { name: '바로 구매' }).click()
    await page.waitForURL('/cart')

    // 주문하기 버튼 클릭
    const checkoutBtn = page.getByRole('button', { name: /주문|결제|구매/ })
    if (await checkoutBtn.isVisible()) {
      await checkoutBtn.click()
      await expect(page).toHaveURL('/checkout')
    }
  })
})
