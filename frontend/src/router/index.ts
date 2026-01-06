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
      { path: 'perms', component: () => import('@/views/admin/PermsView.vue'), meta: { permKey: 'au/perm', action: 'see' } },
      { path: 'customers', component: () => import('@/views/admin/CustomersView.vue'), meta: { permKey: 'crm/customer', action: 'see' } },
      { path: 'customers/:id', component: () => import('@/views/admin/CustomerEditView.vue'), meta: { permKey: 'crm/customer', action: 'read' } },
      { path: 'areas', component: () => import('@/views/admin/SalesAreasView.vue'), meta: { permKey: 'crm/area', action: 'see' } },
      { path: 'areas/:id', component: () => import('@/views/admin/SalesAreaEditView.vue'), meta: { permKey: 'crm/area', action: 'read' } },
      { path: 'staffs', component: () => import('@/views/admin/StaffsView.vue'), meta: { permKey: 'crm/staff', action: 'see' } },
      { path: 'devices', component: () => import('@/views/admin/DevicesView.vue'), meta: { permKey: 'crm/device', action: 'see' } },
      { path: 'devices/:id', component: () => import('@/views/admin/DeviceEditView.vue'), meta: { permKey: 'crm/device', action: 'read' } },
      { path: 'spare-parts', component: () => import('@/views/admin/SparePartsView.vue'), meta: { permKey: 'crm/spare', action: 'see' } },
      { path: 'spare-parts/:id', component: () => import('@/views/admin/SparePartEditView.vue'), meta: { permKey: 'crm/spare', action: 'read' } },
      { path: 'packages', component: () => import('@/views/admin/PackagesView.vue'), meta: { permKey: 'crm/package', action: 'see' } },
      { path: 'packages/:id', component: () => import('@/views/admin/PackageEditView.vue'), meta: { permKey: 'crm/package', action: 'read' } },
      { path: 'price-book', component: () => import('@/views/admin/PriceBookView.vue'), meta: { permKey: 'crm/price', action: 'see' } },
      { path: 'contracts', component: () => import('@/views/admin/ContractListView.vue'), meta: { permKey: 'crm/contract-basic', action: 'see' } },
      { path: 'contracts/:id', component: () => import('@/views/admin/ContractEditView.vue'), meta: { permKey: 'crm/contract-basic', action: 'read' } },
      { path: 'contracts/:id/approval', component: () => import('@/views/admin/ContractApprovalView.vue'), meta: { permKey: 'crm/contract-basic', action: 'update' } },
      { path: 'contracts/:id/flow', component: () => import('@/views/admin/ContractFlowStatusView.vue'), meta: { permKey: 'crm/contract-basic', action: 'read' } },
      { path: 'contract-executions', component: () => import('@/views/admin/ContractExecutionView.vue'), meta: { permKey: 'crm/contract-exec', action: 'see' } },
      { path: 'receivable-plans', component: () => import('@/views/admin/ReceivablePlanView.vue'), meta: { permKey: 'crm/receivable-plan', action: 'see' } },
      { path: 'receivable-entries', component: () => import('@/views/admin/ReceivableEntryView.vue'), meta: { permKey: 'crm/receivable-entry', action: 'see' } },
      { path: 'receivable-reminders', component: () => import('@/views/admin/ReceivableReminderView.vue'), meta: { permKey: 'crm/receivable-reminder', action: 'see' } },
      { path: 'receivables', component: () => import('@/views/admin/ReceivableQueryView.vue'), meta: { permKey: 'crm/receivable-query', action: 'see' } },
      { path: 'receivables/:id', component: () => import('@/views/admin/ReceivableDetailView.vue'), meta: { permKey: 'crm/receivable-query', action: 'read' } }
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
