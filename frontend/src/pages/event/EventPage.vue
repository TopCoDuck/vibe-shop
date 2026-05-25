<template>
  <div class="max-w-5xl mx-auto px-4 py-10">

    <!-- 헤더 -->
    <div class="text-center mb-10">
      <h1 class="text-3xl font-black text-gray-900 mb-2">이벤트 / 쿠폰</h1>
      <p class="text-gray-500 text-sm">특별한 혜택을 놓치지 마세요</p>
    </div>

    <!-- 탭 -->
    <div class="flex border-b border-gray-200 mb-8">
      <button v-for="tab in tabs" :key="tab.id" @click="activeTab = tab.id"
        class="px-8 py-3 text-sm font-semibold transition-colors whitespace-nowrap"
        :class="activeTab === tab.id
          ? 'border-b-2 border-red-500 text-red-500'
          : 'text-gray-500 hover:text-gray-700'">
        {{ tab.label }}
      </button>
    </div>

    <!-- ══ 이벤트 탭 ══ -->
    <div v-if="activeTab === 'event'">

      <!-- 필터 -->
      <div class="flex gap-2 mb-6">
        <button v-for="f in eventFilters" :key="f.value" @click="eventFilter = f.value"
          class="px-4 py-1.5 rounded-full text-sm font-medium border transition-colors"
          :class="eventFilter === f.value
            ? 'bg-red-500 text-white border-red-500'
            : 'border-gray-300 text-gray-600 hover:border-gray-400'">
          {{ f.label }}
        </button>
      </div>

      <!-- 로딩 -->
      <div v-if="eventLoading" class="grid grid-cols-1 sm:grid-cols-2 gap-5">
        <div v-for="n in 4" :key="n" class="rounded-xl h-56 bg-gray-100 animate-pulse"></div>
      </div>
      <!-- 에러 -->
      <div v-else-if="eventError" class="text-center py-16 text-red-400">
        <p class="text-sm">이벤트를 불러오지 못했습니다.</p>
        <button @click="fetchEvents" class="mt-2 text-xs text-red-500 underline">다시 시도</button>
      </div>

      <!-- 이벤트 카드 그리드 -->
      <div v-else class="grid grid-cols-1 sm:grid-cols-2 gap-5">
        <div v-for="event in filteredEvents" :key="event.id"
          class="rounded-xl overflow-hidden border border-gray-100 shadow-sm hover:shadow-md transition-shadow cursor-pointer group">

          <!-- 배너 이미지 영역 -->
          <div class="h-44 relative flex items-center justify-center overflow-hidden"
            :style="{ background: event.background }">
            <!-- 배경 장식 원 -->
            <div class="absolute -right-8 -top-8 w-32 h-32 rounded-full opacity-20"
              :style="{ background: event.accentColor }"></div>
            <div class="absolute -left-4 -bottom-4 w-24 h-24 rounded-full opacity-15"
              :style="{ background: event.accentColor }"></div>

            <div class="relative text-center px-6">
              <p class="text-xs font-semibold mb-1.5 opacity-80" :style="{ color: event.textColor }">
                {{ event.badge }}
              </p>
              <h3 class="text-2xl font-black leading-tight whitespace-pre-line"
                :style="{ color: event.textColor }">
                {{ event.title }}
              </h3>
              <p class="text-sm mt-2 opacity-75" :style="{ color: event.textColor }">
                {{ event.subtitle }}
              </p>
            </div>

            <!-- 상태 뱃지 -->
            <span class="absolute top-3 right-3 text-xs font-bold px-2.5 py-1 rounded-full"
              :class="statusClass(eventStatus(event))">
              {{ statusLabel(eventStatus(event)) }}
            </span>
          </div>

          <!-- 카드 하단 -->
          <div class="bg-white px-4 py-3 flex items-center justify-between">
            <div>
              <p class="text-xs text-gray-400">{{ event.startDate }} ~ {{ event.endDate }}</p>
              <p class="text-sm font-semibold text-gray-700 mt-0.5">{{ event.description }}</p>
            </div>
            <RouterLink :to="event.link"
              class="text-xs font-semibold text-red-500 border border-red-400 rounded-full px-3 py-1.5 hover:bg-red-50 transition-colors whitespace-nowrap">
              바로가기 →
            </RouterLink>
          </div>
        </div>
      </div>

      <!-- 빈 상태 -->
      <div v-if="!eventLoading && !eventError && filteredEvents.length === 0" class="text-center py-20 text-gray-400">
        <svg class="w-12 h-12 mx-auto mb-3 opacity-40" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
            d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
        </svg>
        <p class="text-sm">해당 조건의 이벤트가 없습니다.</p>
      </div>
    </div>

    <!-- ══ 쿠폰 탭 ══ -->
    <div v-if="activeTab === 'coupon'">

      <!-- 쿠폰 서브탭 -->
      <div class="flex gap-1 bg-gray-100 rounded-lg p-1 w-fit mb-6">
        <button v-for="st in couponSubTabs" :key="st.id" @click="couponSubTab = st.id"
          class="px-5 py-2 text-sm font-medium rounded-md transition-all"
          :class="couponSubTab === st.id
            ? 'bg-white text-gray-900 shadow-sm'
            : 'text-gray-500 hover:text-gray-700'">
          {{ st.label }}
        </button>
      </div>

      <!-- 다운로드 가능 쿠폰 -->
      <div v-if="couponSubTab === 'available'">
        <div v-if="couponLoading" class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div v-for="n in 4" :key="n" class="h-32 rounded-xl bg-gray-100 animate-pulse"></div>
        </div>
        <div v-else-if="couponError" class="text-center py-16 text-red-400">
          <p class="text-sm">쿠폰 목록을 불러오지 못했습니다.</p>
          <button @click="fetchCoupons" class="mt-2 text-xs text-red-500 underline">다시 시도</button>
        </div>
        <div v-else-if="coupons.length === 0" class="text-center py-20 text-gray-400">
          <p class="text-sm">현재 다운로드 가능한 쿠폰이 없습니다.</p>
        </div>
        <div v-else class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div v-for="coupon in coupons" :key="coupon.id"
            class="relative flex rounded-xl border overflow-hidden shadow-sm transition-shadow hover:shadow-md"
            :class="coupon.alreadyDownloaded ? 'border-gray-200 opacity-70' : 'border-red-100'">

            <!-- 왼쪽: 할인값 -->
            <div class="flex-shrink-0 w-28 flex flex-col items-center justify-center py-5 px-3"
              :class="coupon.alreadyDownloaded ? 'bg-gray-100' : 'bg-red-500'">
              <span class="font-black text-3xl leading-none"
                :class="coupon.alreadyDownloaded ? 'text-gray-400' : 'text-white'">
                {{ coupon.type === 'PERCENTAGE' ? coupon.value + '%' : (coupon.value / 1000).toFixed(0) + 'K' }}
              </span>
              <span class="text-xs mt-1 font-medium"
                :class="coupon.alreadyDownloaded ? 'text-gray-400' : 'text-red-100'">
                {{ coupon.type === 'PERCENTAGE' ? '할인' : '원 할인' }}
              </span>
            </div>

            <!-- 톱니 구분선 -->
            <div class="w-3 flex-shrink-0 relative"
              :class="coupon.alreadyDownloaded ? 'bg-gray-100' : 'bg-red-500'">
              <div class="absolute inset-0 flex flex-col justify-between py-2">
                <div v-for="i in 5" :key="i" class="w-3 h-3 rounded-full bg-gray-50 -ml-1.5"></div>
              </div>
            </div>

            <!-- 오른쪽: 쿠폰 정보 -->
            <div class="flex-1 bg-white px-4 py-4 flex flex-col justify-between">
              <div>
                <div class="flex items-start gap-1.5 mb-1">
                  <span v-if="coupon.targetCategory"
                    class="inline-block text-xs font-semibold text-red-500 bg-red-50 px-1.5 py-0.5 rounded flex-shrink-0">
                    {{ coupon.targetCategory }}
                  </span>
                  <p class="text-sm font-bold text-gray-800 leading-tight">{{ coupon.name }}</p>
                </div>
                <p v-if="coupon.description" class="text-xs text-gray-500 mt-0.5 line-clamp-1">{{ coupon.description }}</p>
                <div class="flex flex-wrap gap-2 mt-2">
                  <span class="text-xs text-gray-500">
                    최소 {{ coupon.minOrderAmount.toLocaleString() }}원 이상
                  </span>
                  <span v-if="coupon.maxDiscountAmount > 0" class="text-xs text-gray-500">
                    · 최대 {{ coupon.maxDiscountAmount.toLocaleString() }}원
                  </span>
                </div>
              </div>

              <div class="flex items-center justify-between mt-3">
                <p class="text-xs text-gray-400">~ {{ formatDate(coupon.endDate) }}까지</p>
                <button
                  @click="downloadCoupon(coupon)"
                  :disabled="coupon.alreadyDownloaded || !coupon.downloadable || downloadingId === coupon.id"
                  class="text-xs font-bold px-3 py-1.5 rounded-lg transition-colors"
                  :class="coupon.alreadyDownloaded
                    ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                    : coupon.downloadable
                      ? 'bg-red-500 text-white hover:bg-red-600'
                      : 'bg-gray-200 text-gray-400 cursor-not-allowed'">
                  <span v-if="downloadingId === coupon.id">처리중...</span>
                  <span v-else-if="coupon.alreadyDownloaded">다운완료 ✓</span>
                  <span v-else-if="!coupon.downloadable">마감</span>
                  <span v-else>다운로드</span>
                </button>
              </div>

              <!-- 잔여 수량 바 -->
              <div v-if="coupon.totalCount > 0" class="mt-2">
                <div class="flex justify-between text-xs text-gray-400 mb-0.5">
                  <span>잔여 {{ (coupon.totalCount - coupon.downloadedCount).toLocaleString() }}개</span>
                  <span>{{ Math.round(coupon.downloadedCount / coupon.totalCount * 100) }}% 소진</span>
                </div>
                <div class="h-1 bg-gray-100 rounded-full overflow-hidden">
                  <div class="h-full bg-red-400 rounded-full transition-all"
                    :style="{ width: Math.min(coupon.downloadedCount / coupon.totalCount * 100, 100) + '%' }">
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 전체 다운로드 버튼 -->
        <div v-if="coupons.some(c => !c.alreadyDownloaded && c.downloadable)" class="mt-6 text-center">
          <button @click="downloadAll"
            :disabled="downloadingAll"
            class="bg-gray-900 text-white text-sm font-semibold px-8 py-3 rounded-lg hover:bg-gray-700 transition-colors disabled:opacity-60">
            {{ downloadingAll ? '다운로드 중...' : '쿠폰 전체 다운로드' }}
          </button>
        </div>
      </div>

      <!-- 내 쿠폰함 -->
      <div v-if="couponSubTab === 'my'">
        <div v-if="!isLoggedIn" class="text-center py-20">
          <svg class="w-12 h-12 mx-auto mb-3 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
              d="M15 5v2m0 4v2m0 4v2M5 5a2 2 0 00-2 2v3a2 2 0 110 4v3a2 2 0 002 2h14a2 2 0 002-2v-3a2 2 0 110-4V7a2 2 0 00-2-2H5z" />
          </svg>
          <p class="text-gray-500 text-sm mb-4">로그인 후 내 쿠폰을 확인할 수 있습니다.</p>
          <RouterLink to="/login" class="inline-block bg-red-500 text-white text-sm font-semibold px-6 py-2.5 rounded-lg hover:bg-red-600 transition-colors">
            로그인하기
          </RouterLink>
        </div>
        <div v-else-if="myLoading" class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div v-for="n in 3" :key="n" class="h-32 rounded-xl bg-gray-100 animate-pulse"></div>
        </div>
        <div v-else-if="myCoupons.length === 0" class="text-center py-20 text-gray-400">
          <svg class="w-12 h-12 mx-auto mb-3 opacity-40" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
              d="M15 5v2m0 4v2m0 4v2M5 5a2 2 0 00-2 2v3a2 2 0 110 4v3a2 2 0 002 2h14a2 2 0 002-2v-3a2 2 0 110-4V7a2 2 0 00-2-2H5z" />
          </svg>
          <p class="text-sm">보유한 쿠폰이 없습니다.</p>
          <button @click="couponSubTab = 'available'"
            class="mt-3 text-sm text-red-500 hover:underline">쿠폰 다운받으러 가기 →</button>
        </div>
        <div v-else>
          <!-- 필터 -->
          <div class="flex gap-2 mb-5">
            <button @click="myFilter = 'all'"
              class="px-4 py-1.5 rounded-full text-sm font-medium border transition-colors"
              :class="myFilter === 'all' ? 'bg-gray-900 text-white border-gray-900' : 'border-gray-300 text-gray-600'">
              전체 {{ myCoupons.length }}
            </button>
            <button @click="myFilter = 'unused'"
              class="px-4 py-1.5 rounded-full text-sm font-medium border transition-colors"
              :class="myFilter === 'unused' ? 'bg-gray-900 text-white border-gray-900' : 'border-gray-300 text-gray-600'">
              미사용 {{ myCoupons.filter(c => !c.used).length }}
            </button>
            <button @click="myFilter = 'used'"
              class="px-4 py-1.5 rounded-full text-sm font-medium border transition-colors"
              :class="myFilter === 'used' ? 'bg-gray-900 text-white border-gray-900' : 'border-gray-300 text-gray-600'">
              사용완료 {{ myCoupons.filter(c => c.used).length }}
            </button>
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div v-for="uc in filteredMyCoupons" :key="uc.userCouponId"
              class="relative flex rounded-xl border overflow-hidden"
              :class="uc.used ? 'border-gray-200 opacity-60' : 'border-gray-200 shadow-sm'">

              <!-- 좌측 -->
              <div class="flex-shrink-0 w-28 flex flex-col items-center justify-center py-5 px-3"
                :class="uc.used ? 'bg-gray-200' : 'bg-gray-800'">
                <span class="font-black text-3xl leading-none"
                  :class="uc.used ? 'text-gray-400' : 'text-white'">
                  {{ uc.type === 'PERCENTAGE' ? uc.value + '%' : (uc.value / 1000).toFixed(0) + 'K' }}
                </span>
                <span class="text-xs mt-1 font-medium"
                  :class="uc.used ? 'text-gray-400' : 'text-gray-300'">
                  {{ uc.type === 'PERCENTAGE' ? '할인' : '원 할인' }}
                </span>
              </div>

              <!-- 톱니 -->
              <div class="w-3 flex-shrink-0 relative" :class="uc.used ? 'bg-gray-200' : 'bg-gray-800'">
                <div class="absolute inset-0 flex flex-col justify-between py-2">
                  <div v-for="i in 5" :key="i" class="w-3 h-3 rounded-full bg-gray-50 -ml-1.5"></div>
                </div>
              </div>

              <!-- 우측 -->
              <div class="flex-1 bg-white px-4 py-4 flex flex-col justify-between">
                <div>
                  <div class="flex items-center gap-1.5 mb-1">
                    <span v-if="uc.targetCategory"
                      class="text-xs font-semibold text-gray-500 bg-gray-100 px-1.5 py-0.5 rounded">
                      {{ uc.targetCategory }}
                    </span>
                    <p class="text-sm font-bold text-gray-800">{{ uc.name }}</p>
                  </div>
                  <p class="text-xs text-gray-500">
                    최소 {{ uc.minOrderAmount.toLocaleString() }}원
                    <span v-if="uc.maxDiscountAmount > 0"> · 최대 {{ uc.maxDiscountAmount.toLocaleString() }}원</span>
                  </p>
                </div>
                <div class="flex items-center justify-between mt-3">
                  <p class="text-xs text-gray-400">~ {{ formatDate(uc.endDate) }}까지</p>
                  <span class="text-xs font-bold px-3 py-1 rounded-lg"
                    :class="uc.used ? 'bg-gray-100 text-gray-400' : 'bg-green-50 text-green-600'">
                    {{ uc.used ? '사용완료' : '사용가능' }}
                  </span>
                </div>
              </div>

              <!-- 사용완료 도장 -->
              <div v-if="uc.used"
                class="absolute inset-0 flex items-center justify-center pointer-events-none">
                <div class="border-4 border-gray-300 text-gray-300 rounded-lg px-4 py-2 rotate-[-20deg] text-lg font-black opacity-40">
                  사용완료
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 다운로드 성공 토스트 -->
      <Transition name="toast">
        <div v-if="toast" class="fixed bottom-6 left-1/2 -translate-x-1/2 bg-gray-900 text-white text-sm font-medium px-5 py-3 rounded-full shadow-lg z-50 flex items-center gap-2">
          <svg class="w-4 h-4 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M5 13l4 4L19 7" />
          </svg>
          {{ toastMsg }}
        </div>
      </Transition>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { couponApi } from '@/api/coupon'
import { eventApi, type EventItem } from '@/api/event'
import type { Coupon, MyCoupon } from '@/types'

const auth = useAuthStore()
const isLoggedIn = computed(() => auth.isLoggedIn)

/* ── 탭 ── */
const tabs = [
  { id: 'event', label: '🎉 이벤트' },
  { id: 'coupon', label: '🎟 쿠폰' },
]
const activeTab = ref('event')

/* ══ 이벤트 ══ */
const eventFilters = [
  { value: 'all', label: '전체' },
  { value: 'active', label: '진행중' },
  { value: 'upcoming', label: '예정' },
  { value: 'ended', label: '종료' },
]
const eventFilter = ref('all')

const today = new Date()
function parseDate(s: string) { return new Date(s) }
function eventStatus(e: { startDate: string; endDate: string }) {
  const s = parseDate(e.startDate), end = parseDate(e.endDate)
  if (today < s) return 'upcoming'
  if (today > end) return 'ended'
  return 'active'
}
function statusLabel(s: string) {
  return s === 'active' ? '진행중' : s === 'upcoming' ? '예정' : '종료'
}
function statusClass(s: string) {
  return s === 'active'
    ? 'bg-green-500 text-white'
    : s === 'upcoming'
    ? 'bg-blue-500 text-white'
    : 'bg-gray-300 text-gray-600'
}

const events = ref<EventItem[]>([])
const eventLoading = ref(false)
const eventError = ref(false)

async function fetchEvents() {
  eventLoading.value = true
  eventError.value = false
  try {
    const res = await eventApi.getAll()
    events.value = res.data.data
  } catch {
    eventError.value = true
  } finally {
    eventLoading.value = false
  }
}

const filteredEvents = computed(() => {
  if (eventFilter.value === 'all') return events.value
  return events.value.filter(e => eventStatus(e) === eventFilter.value)
})

/* ══ 쿠폰 ══ */
const couponSubTabs = [
  { id: 'available', label: '다운로드 쿠폰' },
  { id: 'my', label: '내 쿠폰함' },
]
const couponSubTab = ref('available')

const coupons = ref<Coupon[]>([])
const myCoupons = ref<MyCoupon[]>([])
const couponLoading = ref(false)
const myLoading = ref(false)
const couponError = ref(false)
const downloadingId = ref<number | null>(null)
const downloadingAll = ref(false)
const toast = ref(false)
const toastMsg = ref('')
const myFilter = ref('all')

const filteredMyCoupons = computed(() => {
  if (myFilter.value === 'unused') return myCoupons.value.filter(c => !c.used)
  if (myFilter.value === 'used') return myCoupons.value.filter(c => c.used)
  return myCoupons.value
})

function formatDate(d: string) {
  return new Date(d).toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric' })
}

async function fetchCoupons() {
  couponLoading.value = true
  couponError.value = false
  try {
    const res = await couponApi.getAvailable()
    coupons.value = res.data.data
  } catch {
    couponError.value = true
  } finally {
    couponLoading.value = false
  }
}

async function fetchMyCoupons() {
  if (!isLoggedIn.value) return
  myLoading.value = true
  try {
    const res = await couponApi.getMyCoupons()
    myCoupons.value = res.data.data
  } catch {
    /* ignore */
  } finally {
    myLoading.value = false
  }
}

async function downloadCoupon(coupon: Coupon) {
  if (!isLoggedIn.value) {
    showToast('로그인 후 쿠폰을 다운로드할 수 있습니다.')
    return
  }
  if (coupon.alreadyDownloaded || !coupon.downloadable) return
  downloadingId.value = coupon.id
  try {
    await couponApi.download(coupon.id)
    coupon.alreadyDownloaded = true
    coupon.downloadedCount++
    showToast(`"${coupon.name}" 쿠폰이 다운로드되었습니다!`)
    fetchMyCoupons()
  } catch (err: any) {
    const msg = err?.response?.data?.message || '다운로드에 실패했습니다.'
    showToast(msg)
  } finally {
    downloadingId.value = null
  }
}

async function downloadAll() {
  if (!isLoggedIn.value) {
    showToast('로그인 후 쿠폰을 다운로드할 수 있습니다.')
    return
  }
  downloadingAll.value = true
  const targets = coupons.value.filter(c => !c.alreadyDownloaded && c.downloadable)
  let count = 0
  for (const c of targets) {
    try {
      await couponApi.download(c.id)
      c.alreadyDownloaded = true
      c.downloadedCount++
      count++
    } catch { /* skip already downloaded */ }
  }
  downloadingAll.value = false
  if (count > 0) {
    showToast(`쿠폰 ${count}개가 다운로드되었습니다!`)
    fetchMyCoupons()
  } else {
    showToast('새로 다운로드할 쿠폰이 없습니다.')
  }
}

function showToast(msg: string) {
  toastMsg.value = msg
  toast.value = true
  setTimeout(() => { toast.value = false }, 3000)
}

watch(couponSubTab, (v) => {
  if (v === 'my') fetchMyCoupons()
})

onMounted(() => {
  fetchEvents()
  fetchCoupons()
})
</script>

<style scoped>
.toast-enter-active, .toast-leave-active { transition: all 0.3s ease; }
.toast-enter-from, .toast-leave-to { opacity: 0; transform: translate(-50%, 20px); }
</style>
