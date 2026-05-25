import { defineStore } from 'pinia'
import { ref } from 'vue'
import { wishlistApi } from '@/api/wishlist'
import type { WishlistItem } from '@/types'

export const useWishlistStore = defineStore('wishlist', () => {
  const items = ref<WishlistItem[]>([])
  // Set 대신 배열로 관리 (Vue 3 반응성 보장)
  const wishedProductIds = ref<number[]>([])
  const loaded = ref(false)

  async function fetchWishlist() {
    try {
      const res = await wishlistApi.getMyWishlist()
      items.value = res.data.data
      wishedProductIds.value = items.value.map(i => i.productId)
      loaded.value = true
    } catch {
      // 비로그인 상태 등 무시
    }
  }

  async function fetchWishlistIds() {
    try {
      const res = await wishlistApi.getMyWishlistIds()
      wishedProductIds.value = res.data.data
      loaded.value = true
    } catch {
      // 비로그인 상태 무시
    }
  }

  async function toggle(productId: number) {
    const res = await wishlistApi.toggle(productId)
    const { wished } = res.data.data
    if (wished) {
      if (!wishedProductIds.value.includes(productId)) {
        wishedProductIds.value = [...wishedProductIds.value, productId]
      }
    } else {
      wishedProductIds.value = wishedProductIds.value.filter(id => id !== productId)
      items.value = items.value.filter(i => i.productId !== productId)
    }
    return wished
  }

  function isWished(productId: number): boolean {
    return wishedProductIds.value.includes(productId)
  }

  function reset() {
    items.value = []
    wishedProductIds.value = []
    loaded.value = false
  }

  return { items, wishedProductIds, loaded, fetchWishlist, fetchWishlistIds, toggle, isWished, reset }
})
