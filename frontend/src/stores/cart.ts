import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { cartApi } from '@/api/cart'
import type { CartItem } from '@/types'

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>([])

  const totalCount = computed(() => items.value.reduce((sum, i) => sum + i.quantity, 0))
  const totalAmount = computed(() => items.value.reduce((sum, i) => sum + i.subtotal, 0))

  async function fetchCart() {
    const res = await cartApi.getCart()
    items.value = res.data.data
  }

  async function addItem(productId: number, quantity: number) {
    await cartApi.addItem(productId, quantity)
    await fetchCart()
  }

  async function updateQuantity(cartItemId: number, quantity: number) {
    await cartApi.updateQuantity(cartItemId, quantity)
    await fetchCart()
  }

  async function removeItem(cartItemId: number) {
    await cartApi.removeItem(cartItemId)
    await fetchCart()
  }

  async function clearCart() {
    await cartApi.clearCart()
    items.value = []
  }

  return { items, totalCount, totalAmount, fetchCart, addItem, updateQuantity, removeItem, clearCart }
})
