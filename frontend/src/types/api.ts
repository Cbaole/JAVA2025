export type ApiResponse<T> = {
  success: boolean
  message: string | null
  data: T
}

export type UserProfile = {
  id: string
  name: string | null
  phone: string | null
  username: string
  roleId: string
  roleName: string
}

export type RoleModulePerm = {
  moduleId: string
  canRead: boolean
  canAdd: boolean
  canUpdate: boolean
  canSee: boolean
}

export type ModuleNode = {
  id: string
  cnName: string
  enName: string
  level: number
  orderNo: number
  icon: string | null
  groupName: string | null
  permKey: string
  path: string | null
  parentId: string | null
  isParent: boolean
  expanded: boolean
  children: ModuleNode[]
}

export type LoginResponse = {
  token: string
  profile: UserProfile
  menu: ModuleNode[]
  perms: Record<string, RoleModulePerm>
}

export type RegisterCheckResponse = {
  registered: boolean
  name: string | null
  username: string | null
}
