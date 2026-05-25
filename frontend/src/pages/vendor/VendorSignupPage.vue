<template>
  <div class="min-h-screen bg-gray-50 flex items-center justify-center py-12 px-4">
    <div class="w-full max-w-[440px]">

      <!-- 로고 -->
      <div class="text-center mb-8">
        <RouterLink to="/" class="inline-flex items-baseline gap-1.5">
          <span class="text-3xl font-black text-red-500 tracking-tight">Vibe</span>
          <span class="text-3xl font-black text-gray-900 tracking-tight">shop</span>
        </RouterLink>
        <p class="mt-2 text-sm text-gray-500">판매자 입점신청</p>
      </div>

      <!-- 성공 상태 -->
      <div v-if="submitted" class="bg-white border border-gray-200 rounded-lg p-10 text-center shadow-sm">
        <div class="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg class="w-8 h-8 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
          </svg>
        </div>
        <h2 class="text-xl font-bold text-gray-900 mb-2">입점신청이 완료되었습니다!</h2>
        <p class="text-sm text-gray-500 mb-6">
          입력하신 이메일(<span class="font-medium text-gray-700">{{ form.email }}</span>)로<br>
          검토 결과를 안내드리겠습니다.
        </p>
        <RouterLink to="/" class="inline-block bg-red-500 text-white text-sm font-medium px-6 py-2.5 rounded hover:bg-red-600 transition-colors">
          홈으로 돌아가기
        </RouterLink>
      </div>

      <!-- 입점신청 폼 -->
      <div v-else class="bg-white border border-gray-200 rounded-lg shadow-sm p-8">

        <!-- 판매시장 선택 -->
        <div class="mb-5">
          <p class="text-sm text-gray-600 mb-1.5">판매시장 선택</p>
          <div class="relative">
            <select
              v-model="form.market"
              class="w-full border border-gray-300 rounded px-3 py-2.5 text-sm appearance-none focus:outline-none focus:border-rocket-500 bg-white"
            >
              <option value="KR">한국</option>
              <option value="US">미국</option>
              <option value="JP">일본</option>
              <option value="TW">대만</option>
            </select>
            <svg class="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 pointer-events-none" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
            </svg>
          </div>
        </div>

        <!-- 입력 필드 -->
        <div class="space-y-2.5 mb-5">

          <!-- 아이디 -->
          <div>
            <input
              v-model="form.username"
              type="text"
              placeholder="아이디"
              class="w-full border rounded px-3 py-2.5 text-sm focus:outline-none transition-colors"
              :class="fieldClass('username')"
              @blur="touch('username')"
            />
            <p v-if="touched.username && errors.username" class="text-xs text-red-500 mt-1 pl-1">{{ errors.username }}</p>
          </div>

          <!-- 비밀번호 -->
          <div>
            <div class="relative">
              <input
                v-model="form.password"
                :type="showPw ? 'text' : 'password'"
                placeholder="비밀번호"
                class="w-full border rounded px-3 py-2.5 text-sm focus:outline-none pr-10 transition-colors"
                :class="fieldClass('password')"
                @blur="touch('password')"
              />
              <button type="button" @click="showPw = !showPw"
                class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600">
                <svg v-if="showPw" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />
                </svg>
                <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
              </button>
            </div>
            <p v-if="touched.password && errors.password" class="text-xs text-red-500 mt-1 pl-1">{{ errors.password }}</p>
          </div>

          <!-- 비밀번호 확인 -->
          <div>
            <input
              v-model="form.passwordConfirm"
              :type="showPw ? 'text' : 'password'"
              placeholder="비밀번호 확인"
              class="w-full border rounded px-3 py-2.5 text-sm focus:outline-none transition-colors"
              :class="fieldClass('passwordConfirm')"
              @blur="touch('passwordConfirm')"
            />
            <p v-if="touched.passwordConfirm && errors.passwordConfirm" class="text-xs text-red-500 mt-1 pl-1">{{ errors.passwordConfirm }}</p>
          </div>

          <!-- 이름 -->
          <div>
            <input
              v-model="form.name"
              type="text"
              placeholder="이름"
              class="w-full border rounded px-3 py-2.5 text-sm focus:outline-none transition-colors"
              :class="fieldClass('name')"
              @blur="touch('name')"
            />
            <p v-if="touched.name && errors.name" class="text-xs text-red-500 mt-1 pl-1">{{ errors.name }}</p>
          </div>

          <!-- 이메일 -->
          <div>
            <input
              v-model="form.email"
              type="email"
              placeholder="이메일"
              class="w-full border rounded px-3 py-2.5 text-sm focus:outline-none transition-colors"
              :class="fieldClass('email')"
              @blur="touch('email')"
            />
            <p v-if="touched.email && errors.email" class="text-xs text-red-500 mt-1 pl-1">{{ errors.email }}</p>
          </div>

          <!-- 핸드폰번호 -->
          <div>
            <div class="flex gap-1.5">
              <div class="relative">
                <select
                  v-model="form.countryCode"
                  class="border border-gray-300 rounded px-2 py-2.5 text-xs text-gray-700 appearance-none pr-5 focus:outline-none focus:border-rocket-500 bg-white"
                >
                  <option value="KR+82">KR+82</option>
                  <option value="US+1">US+1</option>
                  <option value="JP+81">JP+81</option>
                </select>
                <svg class="absolute right-1 top-1/2 -translate-y-1/2 w-3 h-3 text-gray-400 pointer-events-none" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                </svg>
              </div>
              <input
                v-model="form.phone"
                type="tel"
                placeholder="핸드폰번호"
                class="flex-1 border rounded px-3 py-2.5 text-sm focus:outline-none transition-colors"
                :class="fieldClass('phone')"
                @blur="touch('phone')"
              />
              <button
                type="button"
                @click="requestVerification"
                :disabled="phoneVerified || !form.phone"
                class="border border-gray-300 rounded px-3 py-2.5 text-xs text-gray-600 whitespace-nowrap transition-colors"
                :class="phoneVerified ? 'bg-green-50 border-green-400 text-green-600' : 'hover:bg-gray-50 disabled:opacity-50'"
              >
                {{ phoneVerified ? '인증완료' : '인증 요청' }}
              </button>
            </div>
            <p v-if="touched.phone && errors.phone" class="text-xs text-red-500 mt-1 pl-1">{{ errors.phone }}</p>
            <!-- 인증번호 입력 -->
            <div v-if="verificationSent && !phoneVerified" class="flex gap-1.5 mt-1.5">
              <input
                v-model="verificationCode"
                type="text"
                placeholder="인증번호 6자리 입력"
                maxlength="6"
                class="flex-1 border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-rocket-500"
              />
              <button
                type="button"
                @click="confirmVerification"
                class="border border-rocket-500 text-rocket-500 rounded px-3 py-2 text-xs whitespace-nowrap hover:bg-blue-50"
              >
                확인
              </button>
            </div>
          </div>
        </div>

        <!-- 동의 섹션 -->
        <div class="mb-5">
          <!-- 모두 동의 -->
          <label class="flex items-center gap-2.5 mb-2 cursor-pointer select-none">
            <input
              type="checkbox"
              :checked="isAllAgreed"
              @change="toggleAll"
              class="w-4 h-4 rounded accent-rocket-500 cursor-pointer"
            />
            <span class="font-semibold text-sm">모두 동의합니다</span>
          </label>
          <p class="text-xs text-gray-500 mb-3 leading-5 pl-6">
            모두 동의에는 필수 및 선택 목적(광고성 정보 수신 포함)에 대한 동의가 포함되어있으며,
            선택 목적에 동의를 거부하시는 경우에도 서비스 이용이 가능합니다.
          </p>

          <!-- 개별 항목 -->
          <div class="border border-gray-200 rounded divide-y divide-gray-100 text-sm">

            <!-- 만 19세 -->
            <label class="flex items-center gap-2.5 px-3 py-3 cursor-pointer hover:bg-gray-50 select-none">
              <input type="checkbox" v-model="consents.age" class="w-4 h-4 accent-rocket-500" />
              <span class="flex-1">
                <span class="text-xs font-medium text-blue-500 mr-1">[필수]</span>만 19세 이상입니다
              </span>
            </label>

            <!-- 서비스 이용약관 -->
            <div class="flex items-center gap-2.5 px-3 py-3 hover:bg-gray-50">
              <input type="checkbox" v-model="consents.tos" class="w-4 h-4 accent-rocket-500 cursor-pointer flex-shrink-0" />
              <span class="flex-1 cursor-pointer" @click="consents.tos = !consents.tos">
                <span class="text-xs font-medium text-blue-500 mr-1">[필수]</span>Vibe Shop 서비스 이용약관 - 사업자용
              </span>
              <button type="button" class="text-gray-400 hover:text-gray-600 text-lg leading-none flex-shrink-0">›</button>
            </div>

            <!-- 전자금융거래 -->
            <div class="flex items-center gap-2.5 px-3 py-3 hover:bg-gray-50">
              <input type="checkbox" v-model="consents.payment" class="w-4 h-4 accent-rocket-500 cursor-pointer flex-shrink-0" />
              <span class="flex-1 cursor-pointer" @click="consents.payment = !consents.payment">
                <span class="text-xs font-medium text-blue-500 mr-1">[필수]</span>전자금융거래 이용약관
              </span>
              <button type="button" class="text-gray-400 hover:text-gray-600 text-lg leading-none flex-shrink-0">›</button>
            </div>

            <!-- 마케팅 -->
            <div class="flex items-center gap-2.5 px-3 py-3 hover:bg-gray-50">
              <input type="checkbox" v-model="consents.marketing" class="w-4 h-4 accent-rocket-500 cursor-pointer flex-shrink-0" />
              <span class="flex-1 cursor-pointer" @click="consents.marketing = !consents.marketing">
                <span class="text-xs font-medium text-gray-400 mr-1">[선택]</span>마케팅 목적의 개인정보 수집 및 이용 동의
              </span>
              <button type="button" class="text-gray-400 hover:text-gray-600 text-lg leading-none flex-shrink-0">›</button>
            </div>

            <!-- 프로모션 -->
            <div class="flex items-center gap-2.5 px-3 py-3 hover:bg-gray-50">
              <input type="checkbox" v-model="consents.promo" class="w-4 h-4 accent-rocket-500 cursor-pointer flex-shrink-0" />
              <span class="flex-1 cursor-pointer" @click="consents.promo = !consents.promo">
                <span class="text-xs font-medium text-gray-400 mr-1">[선택]</span>특별 프로모션 혜택(광고) 수신 동의
              </span>
              <button type="button" class="text-gray-400 hover:text-gray-600 text-lg leading-none flex-shrink-0">›</button>
            </div>

            <!-- 개인정보 안내 링크 -->
            <div class="flex items-center justify-between px-3 py-3 hover:bg-gray-50 cursor-pointer">
              <span class="text-gray-600">개인정보 수집 및 이용 안내</span>
              <span class="text-gray-400 text-lg leading-none">›</span>
            </div>
          </div>

          <!-- 필수 동의 미체크 오류 -->
          <p v-if="formTouched && !requiredConsentsAgreed" class="text-xs text-red-500 mt-1.5 pl-1">
            필수 항목에 모두 동의해주세요.
          </p>
        </div>

        <!-- 안내 텍스트 -->
        <div class="bg-gray-50 border border-gray-100 rounded p-3.5 mb-5 text-xs text-gray-500 leading-5">
          <p class="font-semibold text-gray-600 mb-1.5">ⓘ 확인해주세요</p>
          <ul class="space-y-1">
            <li class="flex gap-1.5"><span class="flex-shrink-0">·</span><span>수신거부 시 판매에 도움이 되는 정보를 받아보실 수 없습니다.</span></li>
            <li class="flex gap-1.5"><span class="flex-shrink-0">·</span><span>광고성 정보 수집 및 수신 동의는 원&gt;계정정보&gt;판매자정보&gt;계정정보에서 변경(동의/철회) 할 수 있습니다.</span></li>
          </ul>
        </div>

        <!-- 에러 메시지 -->
        <p v-if="serverError" class="text-sm text-red-500 text-center mb-3">{{ serverError }}</p>

        <!-- 제출 버튼 -->
        <button
          type="button"
          @click="handleSubmit"
          :disabled="loading"
          class="w-full py-3 rounded text-sm font-semibold transition-colors"
          :class="canSubmit ? 'bg-red-500 text-white hover:bg-red-600' : 'bg-gray-300 text-white cursor-not-allowed'"
        >
          <span v-if="loading" class="flex items-center justify-center gap-2">
            <svg class="w-4 h-4 animate-spin" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"/>
            </svg>
            처리 중...
          </span>
          <span v-else>약관 동의하고 가입하기</span>
        </button>

        <!-- 로그인 링크 -->
        <p class="text-center text-sm text-gray-500 mt-4">
          이미 계정이 있나요?
          <RouterLink to="/login" class="text-red-500 hover:underline font-medium">로그인</RouterLink>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { RouterLink } from 'vue-router'
import axios from '@/utils/axios'

/* ── 폼 상태 ── */
const form = reactive({
  market: 'KR',
  username: '',
  password: '',
  passwordConfirm: '',
  name: '',
  email: '',
  countryCode: 'KR+82',
  phone: '',
})

const consents = reactive({
  age: false,
  tos: false,
  payment: false,
  marketing: false,
  promo: false,
})

/* ── UI 상태 ── */
const showPw = ref(false)
const formTouched = ref(false)
const loading = ref(false)
const submitted = ref(false)
const serverError = ref('')
const verificationSent = ref(false)
const verificationCode = ref('')
const phoneVerified = ref(false)
const touched = reactive<Record<string, boolean>>({})

/* ── 유효성 검사 ── */
const errors = computed(() => {
  const e: Record<string, string> = {}
  if (!form.username.trim()) e.username = '아이디를 입력해주세요.'
  else if (form.username.length < 4) e.username = '아이디는 4자 이상이어야 합니다.'
  else if (!/^[a-zA-Z0-9_]+$/.test(form.username)) e.username = '영문, 숫자, 밑줄(_)만 사용 가능합니다.'

  if (!form.password) e.password = '비밀번호를 입력해주세요.'
  else if (form.password.length < 8) e.password = '비밀번호는 8자 이상이어야 합니다.'
  else if (!/(?=.*[a-zA-Z])(?=.*\d)/.test(form.password)) e.password = '영문과 숫자를 조합해주세요.'

  if (!form.passwordConfirm) e.passwordConfirm = '비밀번호 확인을 입력해주세요.'
  else if (form.password !== form.passwordConfirm) e.passwordConfirm = '비밀번호가 일치하지 않습니다.'

  if (!form.name.trim()) e.name = '이름을 입력해주세요.'
  else if (form.name.trim().length < 2) e.name = '2자 이상 입력해주세요.'

  if (!form.email.trim()) e.email = '이메일을 입력해주세요.'
  else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) e.email = '올바른 이메일 형식이 아닙니다.'

  if (!form.phone.trim()) e.phone = '핸드폰번호를 입력해주세요.'
  else if (!/^01[016789]\d{7,8}$/.test(form.phone.replace(/-/g, ''))) e.phone = '올바른 핸드폰번호를 입력해주세요.'

  return e
})

const requiredConsentsAgreed = computed(() =>
  consents.age && consents.tos && consents.payment
)

const isAllAgreed = computed(() =>
  consents.age && consents.tos && consents.payment && consents.marketing && consents.promo
)

const hasFormError = computed(() => Object.keys(errors.value).length > 0)

const canSubmit = computed(() =>
  !hasFormError.value && requiredConsentsAgreed.value
)

/* ── 헬퍼 ── */
function fieldClass(field: string) {
  if (!touched[field]) return 'border-gray-300 focus:border-rocket-500'
  return errors.value[field]
    ? 'border-red-400 bg-red-50 focus:border-red-400'
    : 'border-green-400 focus:border-green-400'
}

function touch(field: string) {
  touched[field] = true
}

function touchAll() {
  Object.keys(form).forEach(k => { touched[k] = true })
}

function toggleAll(e: Event) {
  const checked = (e.target as HTMLInputElement).checked
  consents.age = checked
  consents.tos = checked
  consents.payment = checked
  consents.marketing = checked
  consents.promo = checked
}

/* ── 핸드폰 인증 ── */
function requestVerification() {
  touch('phone')
  if (errors.value.phone) return
  verificationSent.value = true
  // 실제 서비스에서는 SMS API 호출
  // 데모: 6자리 코드 시뮬레이션
  verificationCode.value = ''
  alert('인증번호가 발송되었습니다. (데모: 123456)')
}

function confirmVerification() {
  if (verificationCode.value === '123456') {
    phoneVerified.value = true
    verificationSent.value = false
  } else {
    alert('인증번호가 올바르지 않습니다.')
  }
}

/* ── 제출 ── */
async function handleSubmit() {
  formTouched.value = true
  touchAll()
  if (hasFormError.value || !requiredConsentsAgreed.value) return

  loading.value = true
  serverError.value = ''

  try {
    await axios.post('/auth/seller/signup', {
      username: form.username,
      password: form.password,
      name: form.name,
      email: form.email,
      phone: form.phone,
      market: form.market,
      agreeMarketing: consents.marketing,
      agreePromo: consents.promo,
    })
    submitted.value = true
  } catch (err: any) {
    const msg = err?.response?.data?.message
    serverError.value = msg || '입점신청 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.'
  } finally {
    loading.value = false
  }
}
</script>
