import { defineStore } from 'pinia'
import { ref } from 'vue'
import { categoryApi } from '@/api/categories'
import type { Category } from '@/types'

export const useCategoryStore = defineStore('category', () => {
  const categories = ref<Category[]>([])

  async function fetchCategories() {
    const res = await categoryApi.getAll()
    categories.value = res.data.data
  }

  return { categories, fetchCategories }
})
