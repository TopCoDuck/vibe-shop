import { test, expect } from '@playwright/test'

const EMAIL = `autologin_${Date.now()}@vibeshop.com`
const PASSWORD = 'test1234!'

test.beforeAll(async ({ browser }) => {
  // 테스트용 계정 회원가입
  const page = await browser.newPage()
  await page.goto('/signup')
  await page.getByPlaceholder('이메일 입력').fill(EMAIL)
  await page.getByPlaceholder('비밀번호 입력').fill(PASSWORD)
  await page.getByPlaceholder('이름 입력').fill('자동로그인테스터')
  await page.getByRole('button', { name: '회원가입' }).click()
  await page.waitForURL('/login')
  await page.close()
})

test('자동 로그인 체크박스가 로그인 화면에 표시된다', async ({ page }) => {
  await page.goto('/login')
  await expect(page.getByLabel(/자동 로그인/)).toBeVisible()
  await expect(page.getByText('최대 14일')).toBeVisible()
})

test('자동 로그인 체크 시 localStorage에 만료 시각 저장', async ({ page }) => {
  await page.goto('/login')

  await page.getByPlaceholder('이메일 입력').fill(EMAIL)
  await page.getByPlaceholder('비밀번호 입력').fill(PASSWORD)
  await page.getByLabel(/자동 로그인/).check()
  await page.getByRole('button', { name: '로그인' }).click()
  await page.waitForURL('/')

  const expiry = await page.evaluate(() => localStorage.getItem('autoLoginExpiry'))
  expect(Number(expiry)).toBeGreaterThan(Date.now())

  const daysLeft = (Number(expiry) - Date.now()) / (1000 * 60 * 60 * 24)
  console.log(`✅ 자동 로그인 만료까지 ${daysLeft.toFixed(1)}일 남음`)
  expect(daysLeft).toBeCloseTo(14, 0)
})

test('자동 로그인 미체크 시 sessionStorage에 토큰 저장', async ({ page }) => {
  await page.goto('/login')

  await page.getByPlaceholder('이메일 입력').fill(EMAIL)
  await page.getByPlaceholder('비밀번호 입력').fill(PASSWORD)
  // 체크박스 체크 안함
  await page.getByRole('button', { name: '로그인' }).click()
  await page.waitForURL('/')

  const localToken = await page.evaluate(() => localStorage.getItem('accessToken'))
  const sessionToken = await page.evaluate(() => sessionStorage.getItem('accessToken'))
  const autoExpiry = await page.evaluate(() => localStorage.getItem('autoLoginExpiry'))

  expect(localToken).toBeNull()
  expect(sessionToken).not.toBeNull()
  expect(autoExpiry).toBeNull()
  console.log('✅ 자동 로그인 미체크: sessionStorage에만 토큰 저장됨')
})
