<template>
  <div class="py-8">
    <!-- 통계 헤더 -->
    <div v-if="summary" class="flex items-center gap-6 pb-6 border-b border-gray-200">
      <div class="text-center">
        <p class="text-5xl font-black text-gray-900">{{ summary.avgRating.toFixed(1) }}</p>
        <div class="flex justify-center mt-1">
          <span v-for="i in 5" :key="i" class="text-xl"
            :class="i <= Math.round(summary.avgRating) ? 'text-deal-400' : 'text-gray-300'">★</span>
        </div>
        <p class="text-xs text-gray-500 mt-1">{{ summary.totalCount.toLocaleString() }}건</p>
      </div>
      <div class="flex-1 space-y-1.5">
        <div v-for="star in [5,4,3,2,1]" :key="star" class="flex items-center gap-2 text-xs text-gray-500">
          <span class="w-3 text-right">{{ star }}</span>
          <span class="text-deal-400 text-sm">★</span>
          <div class="flex-1 h-2 bg-gray-100 rounded-full overflow-hidden">
            <div class="h-full bg-deal-400 rounded-full transition-all"
              :style="{ width: barWidth(star) + '%' }"></div>
          </div>
          <span class="w-8 text-right">{{ starCount(star) }}</span>
        </div>
      </div>
    </div>

    <!-- 리뷰 작성 버튼 -->
    <div class="mt-6">
      <div v-if="!auth.isLoggedIn" class="text-center py-6 text-sm text-gray-500">
        <RouterLink to="/login" class="text-rocket-500 font-bold underline">로그인</RouterLink> 후 리뷰를 작성할 수 있습니다.
      </div>
      <div v-else-if="!myReview">
        <button v-if="!showForm" @click="showForm = true"
          class="w-full border-2 border-dashed border-gray-300 rounded-lg py-4 text-sm text-gray-500 hover:border-rocket-400 hover:text-rocket-500 transition-colors">
          + 리뷰 작성하기
        </button>

        <!-- 작성 폼 -->
        <div v-else class="border border-gray-200 rounded-lg p-5">
          <h3 class="font-bold text-gray-900 mb-4">리뷰 작성</h3>
          <!-- 별점 -->
          <div class="flex items-center gap-1 mb-4">
            <span class="text-sm text-gray-600 mr-2">평점</span>
            <button v-for="i in 5" :key="i"
              @click="form.rating = i"
              class="text-2xl transition-colors"
              :class="i <= form.rating ? 'text-deal-400' : 'text-gray-300'">★</button>
            <span class="text-sm text-gray-500 ml-2">{{ form.rating }}점</span>
          </div>
          <input v-model="form.title" type="text" placeholder="제목 (선택)"
            class="w-full border border-gray-300 rounded px-3 py-2 text-sm mb-3 focus:outline-none focus:border-rocket-400" />
          <textarea v-model="form.content" placeholder="상품에 대한 솔직한 리뷰를 남겨주세요."
            rows="4"
            class="w-full border border-gray-300 rounded px-3 py-2 text-sm mb-3 focus:outline-none focus:border-rocket-400 resize-none"></textarea>
          <div class="flex justify-end gap-2">
            <button @click="cancelForm" class="px-4 py-2 text-sm text-gray-600 hover:text-gray-900">취소</button>
            <button @click="submitReview" :disabled="submitting || !form.content || form.rating === 0"
              class="px-5 py-2 text-sm font-bold bg-rocket-500 text-white rounded hover:bg-rocket-600 disabled:opacity-50">
              {{ submitting ? '등록 중...' : '등록' }}
            </button>
          </div>
          <p v-if="formError" class="text-red-500 text-xs mt-2">{{ formError }}</p>
        </div>
      </div>
    </div>

    <!-- 리뷰 목록 -->
    <div v-if="loading" class="py-12 flex justify-center">
      <div class="animate-spin rounded-full h-8 w-8 border-4 border-rocket-500 border-t-transparent"></div>
    </div>

    <div v-else-if="summary && summary.reviews.length === 0" class="py-16 text-center text-gray-400 text-sm">
      첫 번째 리뷰를 작성해 보세요!
    </div>

    <ul v-else-if="summary" class="mt-6 divide-y divide-gray-100">
      <li v-for="review in summary.reviews" :key="review.id" class="py-5">
        <div class="flex items-start justify-between">
          <div>
            <div class="flex items-center gap-2">
              <span class="font-bold text-sm text-gray-900">{{ review.userName }}</span>
              <span class="text-xs text-gray-400">{{ formatDate(review.createdAt) }}</span>
              <span v-if="review.myReview"
                class="text-xs bg-rocket-50 text-rocket-600 font-bold px-1.5 py-0.5 rounded">내 리뷰</span>
            </div>
            <div class="flex mt-0.5">
              <span v-for="i in 5" :key="i" class="text-sm"
                :class="i <= review.rating ? 'text-deal-400' : 'text-gray-300'">★</span>
            </div>
          </div>
          <!-- 내 리뷰 편집/삭제 -->
          <div v-if="review.myReview" class="flex gap-2 text-xs text-gray-400">
            <button @click="startEdit(review)" class="hover:text-gray-700">수정</button>
            <button @click="deleteReview(review.id)" class="hover:text-red-500">삭제</button>
          </div>
        </div>

        <!-- 수정 폼 -->
        <div v-if="editingId === review.id" class="mt-3 border border-gray-200 rounded-lg p-4">
          <div class="flex items-center gap-1 mb-3">
            <button v-for="i in 5" :key="i" @click="editForm.rating = i"
              class="text-xl transition-colors"
              :class="i <= editForm.rating ? 'text-deal-400' : 'text-gray-300'">★</button>
          </div>
          <input v-model="editForm.title" type="text" placeholder="제목 (선택)"
            class="w-full border border-gray-300 rounded px-3 py-2 text-sm mb-2 focus:outline-none focus:border-rocket-400" />
          <textarea v-model="editForm.content" rows="3"
            class="w-full border border-gray-300 rounded px-3 py-2 text-sm mb-3 focus:outline-none focus:border-rocket-400 resize-none"></textarea>
          <div class="flex justify-end gap-2">
            <button @click="editingId = null" class="px-4 py-1.5 text-xs text-gray-600">취소</button>
            <button @click="submitEdit(review.id)"
              class="px-4 py-1.5 text-xs font-bold bg-rocket-500 text-white rounded">수정 완료</button>
          </div>
        </div>

        <!-- 리뷰 내용 -->
        <template v-else>
          <p v-if="review.title" class="mt-2 font-semibold text-sm text-gray-800">{{ review.title }}</p>
          <p class="mt-1 text-sm text-gray-700 leading-relaxed whitespace-pre-line">{{ review.content }}</p>
          <img v-if="review.imageUrl" :src="review.imageUrl" :alt="'리뷰 이미지'"
            class="mt-3 w-24 h-24 object-cover rounded border border-gray-200" />
        </template>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { RouterLink } from 'vue-router'
import { reviewApi } from '@/api/review'
import { useAuthStore } from '@/stores/auth'
import type { ReviewSummary, Review } from '@/types'

const props = defineProps<{ productId: number }>()

const auth = useAuthStore()
const summary = ref<ReviewSummary | null>(null)
const loading = ref(false)
const showForm = ref(false)
const submitting = ref(false)
const formError = ref('')
const editingId = ref<number | null>(null)

const form = ref({ rating: 5, title: '', content: '' })
const editForm = ref({ rating: 5, title: '', content: '' })

const myReview = computed(() =>
  summary.value?.reviews.find(r => r.myReview) ?? null
)

async function fetchReviews() {
  loading.value = true
  try {
    const res = await reviewApi.getByProduct(props.productId)
    summary.value = res.data.data
  } finally {
    loading.value = false
  }
}

function barWidth(star: number): number {
  if (!summary.value || summary.value.totalCount === 0) return 0
  return (starCount(star) / summary.value.totalCount) * 100
}

function starCount(star: number): number {
  return summary.value?.reviews.filter(r => r.rating === star).length ?? 0
}

function formatDate(dt: string) {
  return dt?.slice(0, 10).replace(/-/g, '.') ?? ''
}

function cancelForm() {
  showForm.value = false
  form.value = { rating: 5, title: '', content: '' }
  formError.value = ''
}

async function submitReview() {
  if (!form.value.content) return
  submitting.value = true
  formError.value = ''
  try {
    await reviewApi.create(props.productId, { ...form.value })
    cancelForm()
    await fetchReviews()
  } catch (e: any) {
    formError.value = e?.response?.data?.message ?? '오류가 발생했습니다.'
  } finally {
    submitting.value = false
  }
}

function startEdit(review: Review) {
  editingId.value = review.id
  editForm.value = { rating: review.rating, title: review.title ?? '', content: review.content }
}

async function submitEdit(reviewId: number) {
  try {
    await reviewApi.update(reviewId, { ...editForm.value })
    editingId.value = null
    await fetchReviews()
  } catch (e: any) {
    alert(e?.response?.data?.message ?? '수정 중 오류가 발생했습니다.')
  }
}

async function deleteReview(reviewId: number) {
  if (!confirm('리뷰를 삭제하시겠습니까?')) return
  try {
    await reviewApi.delete(reviewId)
    await fetchReviews()
  } catch (e: any) {
    alert(e?.response?.data?.message ?? '삭제 중 오류가 발생했습니다.')
  }
}

watch(() => props.productId, fetchReviews, { immediate: true })
</script>
