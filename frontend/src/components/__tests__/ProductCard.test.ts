import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import ProductCard from '../product/ProductCard.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [{ path: '/:pathMatch(.*)*', component: { template: '<div/>' } }],
})

const mockProduct = {
  id: 1,
  name: '테스트 상품',
  description: '테스트 설명',
  price: 10000,
  stock: 50,
  imageUrl: 'http://example.com/image.jpg',
  categoryId: 1,
  categoryName: '전자제품',
  status: 'ACTIVE',
  createdAt: '2024-01-01T00:00:00',
}

describe('ProductCard 컴포넌트', () => {
  it('상품 이름이 표시된다', async () => {
    const wrapper = mount(ProductCard, {
      props: { product: mockProduct as any },
      global: { plugins: [router] },
    })
    await router.isReady()

    expect(wrapper.text()).toContain('테스트 상품')
  })

  it('상품 가격이 포맷되어 표시된다', async () => {
    const wrapper = mount(ProductCard, {
      props: { product: mockProduct as any },
      global: { plugins: [router] },
    })
    await router.isReady()

    expect(wrapper.text()).toContain('10,000')
  })

  it('카테고리 이름이 표시된다', async () => {
    const wrapper = mount(ProductCard, {
      props: { product: mockProduct as any },
      global: { plugins: [router] },
    })
    await router.isReady()

    expect(wrapper.text()).toContain('전자제품')
  })

  it('재고가 0이면 품절 표시', async () => {
    const outOfStockProduct = { ...mockProduct, stock: 0 }
    const wrapper = mount(ProductCard, {
      props: { product: outOfStockProduct as any },
      global: { plugins: [router] },
    })
    await router.isReady()

    expect(wrapper.text()).toContain('품절')
  })

  it('이미지가 있으면 img 태그 렌더링', async () => {
    const wrapper = mount(ProductCard, {
      props: { product: mockProduct as any },
      global: { plugins: [router] },
    })
    await router.isReady()

    expect(wrapper.find('img').exists()).toBe(true)
    expect(wrapper.find('img').attributes('src')).toBe('http://example.com/image.jpg')
  })

  it('이미지가 없으면 placeholder 렌더링', async () => {
    const noImageProduct = { ...mockProduct, imageUrl: null }
    const wrapper = mount(ProductCard, {
      props: { product: noImageProduct as any },
      global: { plugins: [router] },
    })
    await router.isReady()

    expect(wrapper.find('img').exists()).toBe(false)
    expect(wrapper.find('svg').exists()).toBe(true)
  })

  it('상품 링크가 올바른 경로로 연결된다', async () => {
    const wrapper = mount(ProductCard, {
      props: { product: mockProduct as any },
      global: { plugins: [router] },
    })
    await router.isReady()

    const link = wrapper.find('a')
    expect(link.attributes('href')).toBe('/products/1')
  })
})
