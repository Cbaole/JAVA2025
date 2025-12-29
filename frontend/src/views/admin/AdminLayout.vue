<template>
  <div style="display: flex; height: 100vh">
    <div style="width: 260px; border-right: 1px solid #eee; padding: 12px">
      <div style="font-weight: 600; margin-bottom: 12px">系统功能模块树</div>
      <el-tree
        :data="menu"
        node-key="id"
        :props="{ label: 'cnName', children: 'children' }"
        :default-expand-all="true"
        @node-click="onNodeClick"
      />
      <div style="margin-top: 12px; display: flex; justify-content: space-between; align-items: center">
        <div style="font-size: 12px; color: #666">{{ store.profile?.username }}</div>
        <el-button size="small" @click="logout">退出</el-button>
      </div>
    </div>
    <div style="flex: 1; padding: 12px; overflow: auto">
      <RouterView />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, RouterView } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const store = useAuthStore()
const router = useRouter()

const menu = computed(() => store.menu || [])

function onNodeClick(node: any) {
  const path = node?.path
  if (typeof path === 'string' && path.startsWith('/')) {
    router.push(path)
    return
  }
  const permKey = node?.permKey
  if (permKey === 'au/user') router.push('/admin/users')
  if (permKey === 'au/role') router.push('/admin/roles')
  if (permKey === 'au/option') router.push('/admin/options')
  if (permKey === 'au/module') router.push('/admin/modules')
  if (permKey === 'au/perm') router.push('/admin/perms')
}

function logout() {
  store.logout()
  router.replace('/login')
}
</script>

