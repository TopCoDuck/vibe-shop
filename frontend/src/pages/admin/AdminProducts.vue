<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h2 class="text-xl font-bold text-gray-800">상품 관리</h2>
      <button @click="openModal(null)" class="btn-primary text-sm">+ 상품 등록</button>
    </div>

    <div class="card overflow-hidden p-0">
      <table class="w-full text-sm">
        <thead class="bg-gray-50 border-b">
          <tr>
            <th class="text-left px-4 py-3 text-gray-600 font-medium">ID</th>
            <th class="text-left px-4 py-3 text-gray-600 font-medium">상품명</th>
            <th class="text-left px-4 py-3 text-gray-600 font-medium">카테고리</th>
            <th class="text-right px-4 py-3 text-gray-600 font-medium">가격</th>
            <th class="text-right px-4 py-3 text-gray-600 font-medium">재고</th>
            <th class="text-center px-4 py-3 text-gray-600 font-medium">상태</th>
            <th class="text-center px-4 py-3 text-gray-600 font-medium">관리</th>
          </tr>
        </thead>
        <tbody class="divide-y">
          <tr v-for="p in products" :key="p.id" class="hover:bg-gray-50">
            <td class="px-4 py-3 text-gray-400">#{{ p.id }}</td>
            <td class="px-4 py-3 font-medium text-gray-800">{{ p.name }}</td>
            <td class="px-4 py-3 text-gray-500">{{ p.categoryName }}</td>
            <td class="px-4 py-3 text-right">{{ formatPrice(p.price) }}</td>
            <td class="px-4 py-3 text-right">{{ p.stock }}</td>
            <td class="px-4 py-3 text-center">
              <span :class="p.status === 'ACTIVE' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'"
                class="badge">{{ p.status }}</span>
            </td>
            <td class="px-4 py-3 text-center">
              <button @click="openModal(p)" class="text-blue-500 hover:underline mr-3">수정</button>
              <button @click="deleteProduct(p.id)" class="text-red-500 hover:underline">삭제</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <Pagination v-if="totalPages > 1" :current="currentPage" :total="totalPages" @change="onPageChange" />

    <!-- 상품 등록/수정 모달 -->
    <div v-if="showModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-xl w-full max-w-lg p-6">
        <h3 class="font-bold text-lg mb-4">{{ editing ? '상품 수정' : '상품 등록' }}</h3>
        <div class="space-y-3">
          <input v-model="form.name" class="input-field" placeholder="상품명 *" />
          <textarea v-model="form.description" class="input-field h-24 resize-none" placeholder="상품 설명"></textarea>
          <input v-model.number="form.price" type="number" class="input-field" placeholder="가격 *" />
          <input v-model.number="form.stock" type="number" class="input-field" placeholder="재고 *" />
          <input v-model="form.imageUrl" class="input-field" placeholder="이미지 URL" />
          <select v-model.number="form.categoryId" class="input-field">
            <option value="">카테고리 선택 *</option>
            <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
          </select>
        </div>
        <div class="flex gap-3 mt-5">
          <button @click="saveProduct" :disabled="saving" class="btn-primary flex-1">
            {{ saving ? '저장 중...' : '저장' }}
          </button>
          <button @click="showModal = false" class="btn-secondary flex-1">취소</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { productApi } from '@/api/products'
import { categoryApi } from '@/api/categories'
import Pagination from '@/components/common/Pagination.vue'
import { formatPrice } from '@/utils/format'
import type { Product, Category } from '@/types'

const products = ref<Product[]>([])
const categories = ref<Category[]>([])
const currentPage = ref(0)
const totalPages = ref(0)
const showModal = ref(false)
const editing = ref<Product | null>(null)
const saving = ref(false)

const form = reactive({
  name: '', description: '', price: 0, stock: 0, imageUrl: '', categoryId: 0
})

async function fetchProducts() {
  const res = await productApi.getAllForAdmin({ page: currentPage.value, size: 20 })
  products.value = res.data.data.content
  totalPages.value = res.data.data.totalPages
}

function onPageChange(page: number) {
  currentPage.value = page
  fetchProducts()
}

function openModal(product: Product | null) {
  editing.value = product
  if (product) {
    Object.assign(form, {
      name: product.name, description: product.description || '',
      price: product.price, stock: product.stock,
      imageUrl: product.imageUrl || '', categoryId: product.categoryId || 0
    })
  } else {
    Object.assign(form, { name: '', description: '', price: 0, stock: 0, imageUrl: '', categoryId: 0 })
  }
  showModal.value = true
}

async function saveProduct() {
  saving.value = true
  try {
    if (editing.value) {
      await productApi.update(editing.value.id, form)
    } else {
      await productApi.create(form)
    }
    showModal.value = false
    await fetchProducts()
  } finally {
    saving.value = false
  }
}

async function deleteProduct(id: number) {
  if (!confirm('상품을 삭제하시겠습니까?')) return
  await productApi.delete(id)
  await fetchProducts()
}

onMounted(async () => {
  await Promise.all([fetchProducts(), categoryApi.getAll().then(r => categories.value = r.data.data)])
})
</script>
