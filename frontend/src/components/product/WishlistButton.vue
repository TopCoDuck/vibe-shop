<template>
  <button
    @click.stop.prevent="handleToggle"
    :title="wished ? '찜 해제' : '찜하기'"
    :data-wished="wished"
    class="wishlist-btn"
    :class="wished ? 'text-red-500' : 'text-gray-300 hover:text-red-400'"
    :disabled="loading"
  >
    <svg
      class="w-6 h-6 transition-all duration-200"
      :fill="wished ? 'currentColor' : 'none'"
      stroke="currentColor"
      stroke-width="2"
      viewBox="0 0 24 24"
    >
      <path
        stroke-linecap="round"
        stroke-linejoin="round"
        d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
      />
    </svg>
  </button>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useWishlistStore } from '@/stores/wishlist'

const props = defineProps<{ productId: number }>()

const auth = useAuthStore()
const wishlistStore = useWishlistStore()
const router = useRouter()
const loading = ref(false)

const wished = computed(() => wishlistStore.isWished(props.productId))

async function handleToggle() {
  if (!auth.isLoggedIn) {
    router.push('/login')
    return
  }
  if (loading.value) return
  loading.value = true
  try {
    await wishlistStore.toggle(props.productId)
  } finally {
    loading.value = false
  }
}
</script>
