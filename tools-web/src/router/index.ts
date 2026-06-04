import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/LoginPage.vue')
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/RegisterPage.vue')
    },
    {
      path: '/',
      name: 'Home',
      component: () => import('@/views/HomePage.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/markdown',
      name: 'Markdown',
      component: () => import('@/views/MarkdownEditor.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/markdown/:id',
      name: 'MarkdownEdit',
      component: () => import('@/views/MarkdownEditor.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/json',
      name: 'Json',
      component: () => import('@/views/JsonFormatter.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/json/:id',
      name: 'JsonEdit',
      component: () => import('@/views/JsonFormatter.vue'),
      meta: { requiresAuth: true }
    }
  ]
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && token) {
    next('/')
  } else {
    next()
  }
})

export default router
