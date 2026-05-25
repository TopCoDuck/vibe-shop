<template>
  <div class="max-w-4xl mx-auto px-4 py-10">

    <!-- 헤더 -->
    <div class="text-center mb-10">
      <h1 class="text-3xl font-black text-gray-900 mb-2">고객센터</h1>
      <p class="text-gray-500 text-sm">무엇을 도와드릴까요?</p>
      <div v-if="activeTab === 'faq'" class="mt-5 relative max-w-lg mx-auto">
        <input v-model="searchQuery" type="text" placeholder="궁금한 점을 검색해보세요" class="input-field w-full pr-10" />
        <svg class="w-5 h-5 text-gray-400 absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none"
          fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-4.35-4.35M11 18a7 7 0 100-14 7 7 0 000 14z" />
        </svg>
      </div>
    </div>

    <!-- 탭 -->
    <div class="flex border-b border-gray-200 mb-8">
      <button v-for="tab in tabs" :key="tab.id" @click="activeTab = tab.id"
        class="px-6 py-3 text-sm font-medium transition-colors whitespace-nowrap"
        :class="activeTab === tab.id ? 'border-b-2 border-red-500 text-red-500' : 'text-gray-500 hover:text-gray-700'">
        {{ tab.label }}
      </button>
    </div>

    <!-- ══ FAQ 탭 ══ -->
    <div v-if="activeTab === 'faq'">
      <div v-if="faqLoading" class="space-y-2">
        <div v-for="n in 6" :key="n" class="border border-gray-200 rounded-lg p-4 animate-pulse">
          <div class="h-4 bg-gray-200 rounded w-3/4"></div>
        </div>
      </div>
      <div v-else-if="faqError" class="text-center py-16 text-red-400">
        <p class="text-sm">데이터를 불러오지 못했습니다.</p>
        <button @click="fetchFaqs" class="mt-3 text-xs text-red-500 underline">다시 시도</button>
      </div>
      <template v-else>
        <div class="flex flex-wrap gap-2 mb-6">
          <button v-for="cat in faqCategories" :key="cat" @click="activeCategory = cat"
            class="px-4 py-1.5 rounded-full text-sm font-medium border transition-colors"
            :class="activeCategory === cat
              ? 'bg-red-500 text-white border-red-500'
              : 'bg-white text-gray-600 border-gray-300 hover:border-red-400 hover:text-red-500'">
            {{ cat }}
            <span v-if="cat !== '전체'" class="ml-1 text-xs opacity-70">
              ({{ faqList.filter(f => f.category === cat).length }})
            </span>
          </button>
        </div>
        <div v-if="filteredFaqs.length === 0" class="text-center py-16 text-gray-400">
          <p class="text-sm">검색 결과가 없습니다.</p>
        </div>
        <div v-else class="space-y-2">
          <div v-for="(faq, i) in filteredFaqs" :key="faq.id"
            class="border border-gray-200 rounded-lg overflow-hidden">
            <button @click="toggleFaq(i)"
              class="w-full flex items-center justify-between px-5 py-4 text-left hover:bg-gray-50 transition-colors">
              <div class="flex items-center gap-3 flex-1 min-w-0">
                <span class="badge-sq text-white shrink-0" :class="categoryColor(faq.category)">{{ faq.category }}</span>
                <span class="text-sm font-medium text-gray-900 leading-snug" v-html="highlight(faq.question)" />
              </div>
              <svg class="w-5 h-5 text-gray-400 shrink-0 ml-3 transition-transform duration-200"
                :class="faqOpenIndex === i ? 'rotate-180' : ''" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
              </svg>
            </button>
            <Transition name="accordion">
              <div v-if="faqOpenIndex === i" class="px-5 pb-5 pt-1 border-t border-gray-100 bg-gray-50">
                <p class="text-sm text-gray-700 leading-relaxed whitespace-pre-line" v-html="highlight(faq.answer)" />
                <div class="mt-4 flex items-center gap-3 pt-3 border-t border-gray-200">
                  <span class="text-xs text-gray-500">도움이 됐나요?</span>
                  <button @click="voted = `${faq.id}_yes`"
                    class="text-xs px-3 py-1 rounded border transition-colors"
                    :class="voted === `${faq.id}_yes` ? 'bg-green-500 text-white border-green-500' : 'border-gray-300 text-gray-600 hover:border-green-400'">
                    👍 네
                  </button>
                  <button @click="voted = `${faq.id}_no`"
                    class="text-xs px-3 py-1 rounded border transition-colors"
                    :class="voted === `${faq.id}_no` ? 'bg-gray-500 text-white border-gray-500' : 'border-gray-300 text-gray-600 hover:border-gray-400'">
                    👎 아니요
                  </button>
                  <span v-if="voted === `${faq.id}_yes`" class="text-xs text-green-600 ml-1">감사합니다!</span>
                  <span v-if="voted === `${faq.id}_no`" class="text-xs text-gray-500 ml-1">더 나은 답변을 위해 개선하겠습니다.</span>
                </div>
              </div>
            </Transition>
          </div>
        </div>
      </template>
    </div>

    <!-- ══ 고객의 소리함 탭 ══ -->
    <div v-if="activeTab === 'voice'">

      <!-- 성공 메시지 -->
      <Transition name="fade">
        <div v-if="submitSuccess"
          class="mb-6 bg-green-50 border border-green-200 rounded-lg p-5 flex items-start gap-3">
          <svg class="w-5 h-5 text-green-500 shrink-0 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd"
              d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
              clip-rule="evenodd" />
          </svg>
          <div>
            <p class="font-bold text-green-700 text-sm">문의가 접수되었습니다.</p>
            <p class="text-green-600 text-xs mt-1">선택하신 방법으로 빠르게 답변드리겠습니다. (영업일 기준 1~2일 소요)</p>
          </div>
        </div>
      </Transition>

      <!-- 헤더 문구 -->
      <div class="mb-6 pb-5 border-b border-gray-200">
        <h2 class="text-2xl font-black text-gray-900">
          Vibe Shop의 중심은 항상
          <span class="text-red-500">고객님</span>입니다.
        </h2>
        <p class="mt-2 text-sm text-gray-500 leading-relaxed">
          Vibe Shop을 이용하면서 느끼신 불편사항이나 바라는 점을 알려주세요.<br />
          고객님의 소중한 의견으로 한 뼘 더 자라는 Vibe Shop이 되겠습니다.
        </p>
        <p class="mt-3 text-xs text-gray-400">문의량이 많아 답변은 24시간 이상 소요될 수 있습니다.</p>
      </div>

      <!-- 비로그인 안내 -->
      <div v-if="!auth.isLoggedIn"
        class="text-center py-16 border border-dashed border-gray-300 rounded-lg">
        <svg class="w-12 h-12 mx-auto mb-3 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
            d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
        </svg>
        <p class="text-sm text-gray-500 mb-4">로그인 후 문의를 작성하실 수 있습니다.</p>
        <RouterLink to="/login" class="btn-primary px-8">로그인</RouterLink>
      </div>

      <!-- 문의 폼 -->
      <form v-else @submit.prevent="submitInquiry" novalidate>

        <!-- 유형 선택 + 이전 문의 보기 -->
        <div class="flex gap-3 mb-4">
          <div class="relative flex-1">
            <select v-model="form.type"
              class="input-field w-full appearance-none pr-10 cursor-pointer"
              :class="formTouched && !form.type ? 'border-red-400 bg-red-50' : ''">
              <option value="" disabled>유형을 선택해주세요</option>
              <option v-for="t in inquiryTypes" :key="t" :value="t">{{ t }}</option>
            </select>
            <svg class="w-4 h-4 text-gray-400 absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none"
              fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
            </svg>
            <p v-if="formTouched && !form.type" class="mt-1 text-xs text-red-500">유형을 선택해주세요.</p>
          </div>
          <!-- 내 문의 이력 버튼 -->
          <button type="button" @click="showHistory = !showHistory"
            class="shrink-0 px-4 py-2 border border-gray-300 rounded text-sm text-gray-600 hover:border-red-400 hover:text-red-500 transition-colors flex items-center gap-1.5">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
            내 문의 내역
          </button>
        </div>

        <!-- 내 문의 이력 패널 -->
        <Transition name="accordion">
          <div v-if="showHistory" class="mb-4 border border-gray-200 rounded-lg overflow-hidden">
            <div class="bg-gray-50 px-4 py-3 flex items-center justify-between border-b border-gray-200">
              <span class="text-sm font-bold text-gray-700">문의 내역</span>
              <button type="button" @click="loadHistory" class="text-xs text-red-500 hover:underline">새로고침</button>
            </div>
            <div v-if="historyLoading" class="p-4 text-center text-sm text-gray-400">불러오는 중...</div>
            <div v-else-if="myInquiries.length === 0" class="p-6 text-center text-sm text-gray-400">
              접수된 문의가 없습니다.
            </div>
            <div v-else class="divide-y divide-gray-100">
              <div v-for="inq in myInquiries" :key="inq.id" class="px-4 py-3">
                <div class="flex items-center justify-between mb-1">
                  <span class="text-xs font-bold text-gray-700">{{ inq.type }}</span>
                  <span class="text-xs px-2 py-0.5 rounded-full font-medium"
                    :class="statusColor(inq.status)">{{ inq.status }}</span>
                </div>
                <p class="text-xs text-gray-600 line-clamp-2">{{ inq.content }}</p>
                <p class="text-[11px] text-gray-400 mt-1">{{ formatDate(inq.createdAt) }}</p>
              </div>
            </div>
          </div>
        </Transition>

        <!-- 내용 입력 -->
        <div class="border border-gray-300 rounded-lg overflow-hidden mb-4"
          :class="formTouched && !form.content.trim() ? 'border-red-400' : ''">
          <textarea v-model="form.content" rows="6"
            placeholder="의견을 남겨주세요&#10;&#10;※ 본문과 첨부된 파일·이미지에 고객님의 개인정보(계좌, 카드, 주민번호 등)가 포함되지 않도록 주의해주세요."
            class="w-full px-4 py-3 text-sm text-gray-700 placeholder-gray-400 resize-none outline-none" />

          <!-- 이미지 첨부 영역 -->
          <div class="border-t border-gray-200 px-4 py-3 bg-gray-50">
            <div class="flex items-center justify-between mb-2">
              <label class="flex items-center gap-1.5 text-xs text-gray-500 cursor-pointer hover:text-red-500 transition-colors">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
                </svg>
                이미지 첨부 <span class="text-gray-400">20Mb이내 / jpg, png, bmp, gif 만 가능</span>
                <input ref="fileInputRef" type="file" accept=".jpg,.jpeg,.png,.bmp,.gif"
                  multiple class="hidden" @change="onFileChange" />
              </label>
              <span class="text-xs text-gray-400">{{ attachedFiles.length }}개 / 최대 3개</span>
            </div>

            <!-- 첨부 파일 미리보기 / 드롭존 -->
            <div
              class="border border-dashed border-gray-300 rounded min-h-[72px] flex items-center justify-center transition-colors"
              :class="isDragging ? 'border-red-400 bg-red-50' : 'bg-white'"
              @dragover.prevent="isDragging = true"
              @dragleave="isDragging = false"
              @drop.prevent="onDrop">
              <div v-if="attachedFiles.length === 0"
                class="text-center text-xs text-gray-400 py-3 cursor-pointer w-full"
                @click="fileInputRef?.click()">
                이미지를 여기로 끌어다 놓거나, <span class="text-red-500 underline">여기를 눌러 파일을 선택</span>하세요.
              </div>
              <div v-else class="flex gap-2 p-2 flex-wrap w-full">
                <div v-for="(f, i) in attachedFiles" :key="i"
                  class="relative w-16 h-16 rounded overflow-hidden border border-gray-200 group">
                  <img :src="f.preview" class="w-full h-full object-cover" />
                  <button type="button" @click="removeFile(i)"
                    class="absolute inset-0 bg-black/50 opacity-0 group-hover:opacity-100 flex items-center justify-center text-white text-lg transition-opacity">
                    ×
                  </button>
                </div>
                <button v-if="attachedFiles.length < 3" type="button" @click="fileInputRef?.click()"
                  class="w-16 h-16 border border-dashed border-gray-300 rounded flex items-center justify-center text-gray-400 hover:border-red-400 hover:text-red-400 text-2xl transition-colors">
                  +
                </button>
              </div>
            </div>
          </div>
        </div>
        <p v-if="formTouched && !form.content.trim()" class="mt-1 text-xs text-red-500 -mt-3 mb-3">
          내용을 입력해주세요.
        </p>

        <!-- 답변 방법 -->
        <div class="mb-6">
          <p class="text-sm text-gray-600 mb-3 text-center">
            고객님이 원하시는 답변 방법 1가지를 선택해주시면, 그에 맞게 답변 드리겠습니다.
          </p>
          <div class="flex justify-center gap-6 flex-wrap">
            <label v-for="m in replyMethods" :key="m"
              class="flex items-center gap-1.5 cursor-pointer text-sm"
              :class="form.replyMethod === m ? 'text-red-500 font-bold' : 'text-gray-600'">
              <input type="radio" v-model="form.replyMethod" :value="m"
                class="accent-red-500 w-4 h-4" />
              {{ m }}
            </label>
          </div>
          <p v-if="formTouched && !form.replyMethod" class="mt-2 text-xs text-red-500 text-center">
            답변 방법을 선택해주세요.
          </p>
          <p class="mt-2 text-[11px] text-gray-400 text-center">
            ※ 추가적인 문의 내용 확인 및 정보제공에 대한 동의가 필요한 경우 전화로 연락 드릴 수 있습니다.
          </p>
        </div>

        <!-- 전송 버튼 -->
        <div class="text-center">
          <button type="submit" :disabled="submitting"
            class="btn-primary px-20 py-3 text-base font-bold disabled:opacity-50">
            {{ submitting ? '전송 중...' : '보내기' }}
          </button>
        </div>

        <!-- 서버 에러 -->
        <p v-if="submitError" class="mt-3 text-center text-sm text-red-500">{{ submitError }}</p>
      </form>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { faqApi } from '@/api/faq'
import { inquiryApi } from '@/api/inquiry'
import { useAuthStore } from '@/stores/auth'
import { formatDate } from '@/utils/format'
import type { Faq, Inquiry } from '@/types'

const auth = useAuthStore()
const activeTab = ref('faq')
const tabs = [
  { id: 'faq',   label: '자주 묻는 질문' },
  { id: 'voice', label: '고객의 소리함' },
]

// ── FAQ ──
const faqList      = ref<Faq[]>([])
const faqLoading   = ref(false)
const faqError     = ref(false)
const activeCategory = ref('전체')
const searchQuery  = ref('')
const faqOpenIndex = ref<number | null>(null)
const voted        = ref('')

async function fetchFaqs() {
  faqLoading.value = true
  faqError.value = false
  try {
    const res = await faqApi.getAll()
    faqList.value = res.data.data
  } catch { faqError.value = true }
  finally { faqLoading.value = false }
}

const faqCategories = computed(() => ['전체', ...new Set(faqList.value.map(f => f.category))])
const filteredFaqs  = computed(() => {
  let list = faqList.value
  if (activeCategory.value !== '전체') list = list.filter(f => f.category === activeCategory.value)
  if (searchQuery.value.trim()) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter(f => f.question.toLowerCase().includes(q) || f.answer.toLowerCase().includes(q))
  }
  return list
})
function toggleFaq(i: number) { faqOpenIndex.value = faqOpenIndex.value === i ? null : i; voted.value = '' }
function categoryColor(cat: string) {
  return ({ '배송':'bg-rocket-500','주문/결제':'bg-red-500','반품/교환':'bg-orange-400','회원/계정':'bg-purple-500','상품':'bg-green-500' } as Record<string,string>)[cat] ?? 'bg-gray-400'
}
function highlight(text: string) {
  if (!searchQuery.value.trim()) return text
  const q = searchQuery.value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
  return text.replace(new RegExp(`(${q})`, 'gi'), '<mark class="bg-yellow-200 text-yellow-900 rounded px-0.5">$1</mark>')
}

// ── 고객의 소리함 ──
const inquiryTypes = ['배송 문의', '주문/결제 문의', '반품/교환 문의', '상품 문의', '회원/계정 문의', '기타']
const replyMethods = ['문의내역', '전화', '문자', '답변 불필요']

const form = ref({ type: '', content: '', replyMethod: '' })
const formTouched  = ref(false)
const submitting   = ref(false)
const submitSuccess = ref(false)
const submitError  = ref('')

// 파일 첨부
interface AttachedFile { file: File; preview: string }
const attachedFiles = ref<AttachedFile[]>([])
const fileInputRef  = ref<HTMLInputElement | null>(null)
const isDragging    = ref(false)

function onFileChange(e: Event) {
  const files = Array.from((e.target as HTMLInputElement).files ?? [])
  addFiles(files)
  if (fileInputRef.value) fileInputRef.value.value = ''
}
function onDrop(e: DragEvent) {
  isDragging.value = false
  addFiles(Array.from(e.dataTransfer?.files ?? []))
}
function addFiles(files: File[]) {
  const allowed = ['image/jpeg', 'image/png', 'image/bmp', 'image/gif']
  for (const file of files) {
    if (attachedFiles.value.length >= 3) break
    if (!allowed.includes(file.type)) continue
    if (file.size > 20 * 1024 * 1024) continue
    const preview = URL.createObjectURL(file)
    attachedFiles.value.push({ file, preview })
  }
}
function removeFile(i: number) {
  URL.revokeObjectURL(attachedFiles.value[i].preview)
  attachedFiles.value.splice(i, 1)
}

// 문의 이력
const showHistory   = ref(false)
const historyLoading = ref(false)
const myInquiries   = ref<Inquiry[]>([])

async function loadHistory() {
  historyLoading.value = true
  try {
    const res = await inquiryApi.getMyInquiries()
    myInquiries.value = res.data.data
  } finally { historyLoading.value = false }
}

function statusColor(status: string) {
  return ({ '접수':'bg-blue-100 text-blue-700', '처리중':'bg-yellow-100 text-yellow-700', '완료':'bg-green-100 text-green-700' } as Record<string,string>)[status] ?? 'bg-gray-100 text-gray-500'
}

// 제출
async function submitInquiry() {
  formTouched.value = true
  if (!form.value.type || !form.value.content.trim() || !form.value.replyMethod) return

  submitting.value = true
  submitError.value = ''
  try {
    await inquiryApi.create({
      type:        form.value.type,
      content:     form.value.content,
      replyMethod: form.value.replyMethod,
    })
    submitSuccess.value = true
    form.value = { type: '', content: '', replyMethod: '' }
    attachedFiles.value = []
    formTouched.value = false
    if (showHistory.value) loadHistory()
    setTimeout(() => (submitSuccess.value = false), 5000)
  } catch (e: any) {
    submitError.value = e.response?.data?.message || '문의 전송에 실패했습니다. 다시 시도해주세요.'
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  await fetchFaqs()
  if (auth.isLoggedIn) loadHistory()
})
</script>

<style scoped>
.accordion-enter-active, .accordion-leave-active { transition: all 0.2s ease; overflow: hidden; }
.accordion-enter-from, .accordion-leave-to { opacity: 0; max-height: 0; }
.accordion-enter-to, .accordion-leave-from { opacity: 1; max-height: 800px; }
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
