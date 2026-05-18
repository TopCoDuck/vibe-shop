<template>
  <div class="flex justify-center gap-2 mt-8">
    <button
      @click="$emit('change', current - 1)"
      :disabled="current === 0"
      class="px-3 py-1.5 rounded border text-sm disabled:opacity-40 hover:bg-gray-100"
    >&laquo;</button>
    <button
      v-for="page in pages"
      :key="page"
      @click="$emit('change', page)"
      :class="['px-3 py-1.5 rounded border text-sm', page === current ? 'bg-red-500 text-white border-red-500' : 'hover:bg-gray-100']"
    >{{ page + 1 }}</button>
    <button
      @click="$emit('change', current + 1)"
      :disabled="current === total - 1"
      class="px-3 py-1.5 rounded border text-sm disabled:opacity-40 hover:bg-gray-100"
    >&raquo;</button>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{ current: number; total: number }>()
defineEmits<{ change: [page: number] }>()

const pages = computed(() => {
  const start = Math.max(0, props.current - 2)
  const end = Math.min(props.total - 1, props.current + 2)
  return Array.from({ length: end - start + 1 }, (_, i) => start + i)
})
</script>
