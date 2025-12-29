import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes: RouteRecordRaw[] = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: () => import('@/views/LoginView.vue') },
  { path: '/register', component: () => import('@/views/RegisterView.vue') },
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    children: [
      { path: '', redirect: '/admin/users' },
      { path: 'users', component: () => import('@/views/admin/UsersPendingView.vue'), meta: { permKey: 'au/user', action: 'see' } },
      { path: 'roles', component: () => import('@/views/admin/RolesView.vue'), meta: { permKey: 'au/role', action: 'see' } },
      { path: 'options', component: () => import('@/views/admin/OptionsView.vue'), meta: { permKey: 'au/option', action: 'see' } },
      { path: 'modules', component: () => import('@/views/admin/ModulesView.vue'), meta: { permKey: 'au/module', action: 'see' } },
      { path: 'perms', component: () => import('@/views/admin/PermsView.vue'), meta: { permKey: 'au/perm', action: 'see' } }
    ]
  },
  { path: '/403', component: () => import('@/views/ForbiddenView.vue') }
]

export const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const store = useAuthStore()
  if (to.path === '/login' || to.path === '/register') return true
  if (!store.token) return '/login'
  const permKey = to.meta?.permKey as string | undefined
  const action = to.meta?.action as 'read' | 'add' | 'update' | 'see' | undefined
  if (permKey && action && !store.has(permKey, action)) {
    return '/403'
  }
  return true
})

