import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: () => import('@/layouts/DefaultLayout.vue'),
      children: [
        { path: '', name: 'Home', component: () => import('@/pages/home/HomePage.vue') },
        { path: 'products', name: 'ProductList', component: () => import('@/pages/product/ProductListPage.vue') },
        { path: 'products/:id', name: 'ProductDetail', component: () => import('@/pages/product/ProductDetailPage.vue') },
        {
          path: 'cart', name: 'Cart',
          component: () => import('@/pages/cart/CartPage.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'checkout', name: 'Checkout',
          component: () => import('@/pages/order/CheckoutPage.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'orders', name: 'OrderList',
          component: () => import('@/pages/order/OrderListPage.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'orders/:id', name: 'OrderDetail',
          component: () => import('@/pages/order/OrderDetailPage.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'mypage', name: 'MyPage',
          component: () => import('@/pages/mypage/MyPage.vue'),
          meta: { requiresAuth: true }
        },
        { path: 'cs', name: 'CustomerService', component: () => import('@/pages/cs/CustomerServicePage.vue') },
        { path: 'events', name: 'Events', component: () => import('@/pages/event/EventPage.vue') },
      ]
    },
    {
      path: '/admin',
      component: () => import('@/layouts/AdminLayout.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
      children: [
        { path: '', name: 'AdminDashboard', component: () => import('@/pages/admin/AdminDashboard.vue') },
        { path: 'products', name: 'AdminProducts', component: () => import('@/pages/admin/AdminProducts.vue') },
        { path: 'orders', name: 'AdminOrders', component: () => import('@/pages/admin/AdminOrders.vue') }
      ]
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/pages/auth/LoginPage.vue'),
      meta: { guestOnly: true }
    },
    {
      path: '/signup',
      name: 'Signup',
      component: () => import('@/pages/auth/SignupPage.vue'),
      meta: { guestOnly: true }
    },
    {
      path: '/seller/signup',
      name: 'SellerSignup',
      component: () => import('@/pages/vendor/VendorSignupPage.vue'),
    }
  ],
  scrollBehavior: () => ({ top: 0 })
})

router.beforeEach((to, _from, next) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isLoggedIn) return next('/login')
  if (to.meta.requiresAdmin && !auth.isAdmin) return next('/')
  if (to.meta.guestOnly && auth.isLoggedIn) return next('/')
  next()
})

export default router
