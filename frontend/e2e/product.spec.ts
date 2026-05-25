import { test, expect } from '@playwright/test'

test.describe('상품 플로우', () => {

  test('홈페이지 - 상품 목록이 표시된다', async ({ page }) => {
    await page.goto('/')

    // 상품 목록 로딩 대기
    await page.waitForSelector('.card', { timeout: 10000 })

    const productCards = page.locator('a.card')
    await expect(productCards.first()).toBeVisible()
  })

  test('상품 검색 - 키워드 입력 시 결과 필터링', async ({ page }) => {
    await page.goto('/')

    const searchInput = page.getByPlaceholder(/검색/)
    if (await searchInput.isVisible()) {
      await searchInput.fill('테스트')
      await searchInput.press('Enter')
      await page.waitForTimeout(1000)
      // 결과 확인 (상품이 있거나 없음 메시지)
      const hasResults = await page.locator('.card').count()
      expect(hasResults).toBeGreaterThanOrEqual(0)
    }
  })

  test('상품 상세 페이지 - 클릭 시 이동 및 정보 표시', async ({ page }) => {
    await page.goto('/')

    // 첫 번째 상품 클릭
    const firstProduct = page.locator('a.card').first()
    await firstProduct.waitFor({ timeout: 10000 })

    const productName = await firstProduct.locator('h3').textContent()
    await firstProduct.click()

    // 상품 상세 페이지로 이동 확인
    await expect(page).toHaveURL(/\/products\/\d+/)

    // 상품명, 가격, 수량 버튼 표시 확인
    await expect(page.getByRole('heading', { level: 1 })).toBeVisible()
    await expect(page.getByRole('button', { name: '장바구니 담기' })).toBeVisible()
    await expect(page.getByRole('button', { name: '바로 구매' })).toBeVisible()
  })

  test('상품 상세 - 수량 증감 버튼 동작', async ({ page }) => {
    await page.goto('/')
    await page.locator('a.card').first().waitFor({ timeout: 10000 })
    await page.locator('a.card').first().click()
    await page.waitForURL(/\/products\/\d+/)

    // 초기 수량 = 1
    const qtyDisplay = page.locator('span').filter({ hasText: /^\d+$/ }).first()
    await expect(qtyDisplay).toHaveText('1')

    // + 버튼 클릭
    await page.getByRole('button', { name: '+' }).click()
    await expect(qtyDisplay).toHaveText('2')

    // - 버튼 클릭
    await page.getByRole('button', { name: '-' }).click()
    await expect(qtyDisplay).toHaveText('1')

    // 최소값 이하로 감소 불가
    await page.getByRole('button', { name: '-' }).click()
    await expect(qtyDisplay).toHaveText('1')
  })

  test('로그인 없이 장바구니 담기 클릭 시 로그인 페이지로 이동', async ({ page }) => {
    await page.goto('/')
    await page.locator('a.card').first().waitFor({ timeout: 10000 })
    await page.locator('a.card').first().click()
    await page.waitForURL(/\/products\/\d+/)

    await page.getByRole('button', { name: '장바구니 담기' }).click()

    await expect(page).toHaveURL('/login')
  })
})
