import { test, expect } from '@playwright/test'

const EMAIL = `wishlist_${Date.now()}@vibeshop.com`
const PASSWORD = 'test1234!'

test.beforeAll(async ({ browser }) => {
  // 테스트용 계정 회원가입
  const page = await browser.newPage()
  await page.goto('/signup')
  await page.getByPlaceholder('이메일 입력').fill(EMAIL)
  await page.getByPlaceholder('비밀번호 입력').fill(PASSWORD)
  await page.getByPlaceholder('이름 입력').fill('찜테스터')
  await page.getByRole('button', { name: '회원가입' }).click()
  await page.waitForURL('/login')
  await page.close()
})

async function login(page: any) {
  await page.goto('/login')
  await page.getByPlaceholder('이메일 입력').fill(EMAIL)
  await page.getByPlaceholder('비밀번호 입력').fill(PASSWORD)
  await page.getByRole('button', { name: '로그인' }).click()
  await page.waitForURL('/')
}

test('상품 목록에 찜하기 하트 버튼이 표시된다', async ({ page }) => {
  await page.goto('/')
  // 상품 카드의 찜 버튼 확인 (로그인 없이도 표시)
  const heartBtn = page.locator('button[title="찜하기"]').first()
  await expect(heartBtn).toBeVisible()
})

test('로그인 없이 찜하기 클릭 시 로그인 페이지로 이동', async ({ page }) => {
  await page.goto('/')
  const heartBtn = page.locator('button[title="찜하기"]').first()
  await heartBtn.click()
  await expect(page).toHaveURL('/login')
})

test('로그인 후 찜하기 토글 - API 호출 확인', async ({ page }) => {
  await login(page)
  await page.goto('/')

  const heartBtn = page.locator('button[title="찜하기"]').first()
  await expect(heartBtn).toBeVisible()

  // 찜하기 클릭 - API 응답 대기
  const [response] = await Promise.all([
    page.waitForResponse(res => res.url().includes('/wishlist/') && res.url().includes('/toggle') && res.status() === 200),
    heartBtn.click()
  ])

  const body = await response.json()
  expect(body.data.wished).toBe(true)
  console.log('✅ 찜하기 API 호출 성공, wished =', body.data.wished)

  // Vue 반응성 업데이트 대기
  await expect(page.locator('button[title="찜 해제"]').first()).toBeVisible({ timeout: 3000 })
  console.log('✅ 찜하기 버튼 상태 변경 확인')

  // 찜 해제 클릭 - API 응답 대기
  const [response2] = await Promise.all([
    page.waitForResponse(res => res.url().includes('/wishlist/') && res.url().includes('/toggle') && res.status() === 200),
    page.locator('button[title="찜 해제"]').first().click()
  ])

  const body2 = await response2.json()
  expect(body2.data.wished).toBe(false)
  console.log('✅ 찜 해제 API 호출 성공, wished =', body2.data.wished)

  await expect(page.locator('button[title="찜하기"]').first()).toBeVisible({ timeout: 3000 })
  console.log('✅ 찜 해제 버튼 상태 변경 확인')
})

test('마이페이지에 주문내역과 찜리스트 탭이 표시된다', async ({ page }) => {
  await login(page)
  await page.goto('/mypage')

  await expect(page.getByRole('button', { name: '주문내역' })).toBeVisible()
  await expect(page.getByRole('button', { name: /찜리스트/ })).toBeVisible()
})

test('찜한 상품이 마이페이지 찜리스트에 표시된다', async ({ page }) => {
  await login(page)
  await page.goto('/')

  // 첫 번째 상품의 이름 기억
  const firstCard = page.locator('div.relative').first()
  const productName = await firstCard.locator('h3').textContent()

  // 찜하기 클릭 - API 응답 대기
  const heartBtn = firstCard.locator('button[title="찜하기"]')
  const [response] = await Promise.all([
    page.waitForResponse(res => res.url().includes('/wishlist/') && res.url().includes('/toggle') && res.status() === 200),
    heartBtn.click()
  ])

  const body = await response.json()
  expect(body.data.wished).toBe(true)

  // 마이페이지 → 찜리스트 탭
  await page.goto('/mypage')
  await page.getByRole('button', { name: /찜리스트/ }).click()

  // 찜한 상품이 목록에 표시됨
  await expect(page.getByText(productName!.trim())).toBeVisible()
  console.log(`✅ 찜리스트에 "${productName?.trim()}" 표시 확인`)
})
