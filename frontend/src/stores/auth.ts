import { defineStore } from 'pinia'
import type { LoginResponse, RoleModulePerm, ModuleNode, UserProfile } from '@/types/api'

type AuthState = {
  token: string
  profile: UserProfile | null
  menu: ModuleNode[]
  perms: Record<string, RoleModulePerm>
}

const STORAGE_KEY = 'perm_platform_auth'

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (raw) {
      try {
        return JSON.parse(raw) as AuthState
      } catch {
        localStorage.removeItem(STORAGE_KEY)
      }
    }
    return { token: '', profile: null, menu: [], perms: {} }
  },
  actions: {
    setLogin(res: LoginResponse) {
      this.token = res.token
      this.profile = res.profile
      this.menu = res.menu
      this.perms = res.perms
      localStorage.setItem(STORAGE_KEY, JSON.stringify(this.$state))
    },
    logout() {
      this.token = ''
      this.profile = null
      this.menu = []
      this.perms = {}
      localStorage.removeItem(STORAGE_KEY)
    },
    has(permKey: string, action: 'read' | 'add' | 'update' | 'see') {
      const p = this.perms[permKey]
      if (!p) return false
      if (action === 'read') return p.canRead
      if (action === 'add') return p.canAdd
      if (action === 'update') return p.canUpdate
      return p.canSee
    }
  }
})

