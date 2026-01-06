<template>
  <div>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px">
      <div style="font-weight: 600">账号管理</div>
      <el-button type="primary" @click="load">刷新</el-button>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane name="all" label="全部账号">
        <el-table :data="allRows" stripe>
          <el-table-column prop="name" label="姓名" />
          <el-table-column prop="idCard" label="身份证号" width="200" />
          <el-table-column prop="phone" label="手机号" width="150" />
          <el-table-column prop="username" label="账号" width="150" />
          <el-table-column label="状态" width="120">
            <template #default="{ row }">{{ row.status || '-' }}</template>
          </el-table-column>
          <el-table-column label="岗位">
            <template #default="{ row }">{{ row.postOption?.title || '-' }}</template>
          </el-table-column>
          <el-table-column label="片区">
            <template #default="{ row }">{{ row.areaOption?.title || '-' }}</template>
          </el-table-column>
          <el-table-column label="角色" width="200">
            <template #default="{ row }">
              <el-select v-model="rolePick[row.id]" placeholder="请选择角色" style="width: 180px" @keyup.enter="saveRole(row.id)">
                <el-option v-for="r in roles" :key="r.id" :label="r.roleName" :value="r.id" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="220">
            <template #default="{ row }">
              <el-button type="primary" :disabled="!canUpdate" @click="saveRole(row.id)">保存角色</el-button>
              <el-button :disabled="!canUpdate" @click="resetPwd(row.id)">重置密码</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane name="pending" label="待审核">
        <el-table :data="pendingRows" stripe>
          <el-table-column prop="name" label="姓名" />
          <el-table-column prop="idCard" label="身份证号" width="200" />
          <el-table-column prop="phone" label="手机号" width="150" />
          <el-table-column label="岗位">
            <template #default="{ row }">{{ row.postOption?.title || '-' }}</template>
          </el-table-column>
          <el-table-column label="片区">
            <template #default="{ row }">{{ row.areaOption?.title || '-' }}</template>
          </el-table-column>
          <el-table-column label="操作" width="360">
            <template #default="{ row }">
              <el-select v-model="rolePick[row.id]" placeholder="角色" style="width: 160px">
                <el-option v-for="r in roles" :key="r.id" :label="r.roleName" :value="r.id" />
              </el-select>
              <el-button type="success" :disabled="!canUpdate" @click="approve(row.id)">通过</el-button>
              <el-button :disabled="!canUpdate" @click="resetPwd(row.id)">重置密码</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { http, unwrap } from '@/lib/http'
import { useAuthStore } from '@/stores/auth'

const store = useAuthStore()
const canUpdate = computed(() => store.has('au/user', 'update'))

const activeTab = ref<'all' | 'pending'>('all')
const allRows = ref<any[]>([])
const pendingRows = ref<any[]>([])
const roles = ref<any[]>([])
const rolePick = reactive<Record<string, string>>({})

async function load() {
  try {
    const [allList, pendingList, roleList] = await Promise.all([
      unwrap<any[]>(http.get('/api/admin/users')),
      unwrap<any[]>(http.get('/api/admin/users/pending')),
      unwrap<any[]>(http.get('/api/admin/roles'))
    ])
    allRows.value = allList
    pendingRows.value = pendingList
    roles.value = roleList
    for (const u of allList) {
      if (typeof u?.id === 'string') {
        rolePick[u.id] = u?.role?.id || ''
      }
    }
    for (const u of pendingList) {
      if (typeof u?.id === 'string') {
        rolePick[u.id] = rolePick[u.id] || u?.role?.id || ''
      }
    }
  } catch (e: any) {
    ElMessage.error(e?.message || '加载失败')
  }
}

load()

async function approve(id: string) {
  try {
    await unwrap(http.post(`/api/admin/users/${id}/approve`, null, { params: { roleId: rolePick[id] || '' } }))
    ElMessage.success('已通过')
    load()
  } catch (e: any) {
    ElMessage.error(e?.message || '操作失败')
  }
}

async function saveRole(id: string) {
  const roleId = rolePick[id] || ''
  if (!roleId) {
    ElMessage.error('请选择角色')
    return
  }
  try {
    await unwrap(http.post(`/api/admin/users/${id}/set-role`, null, { params: { roleId } }))
    ElMessage.success('角色已更新')
    load()
  } catch (e: any) {
    ElMessage.error(e?.message || '操作失败')
  }
}

async function resetPwd(id: string) {
  try {
    await unwrap(http.post(`/api/admin/users/${id}/reset-password`))
    ElMessage.success('密码已重置为123456')
  } catch (e: any) {
    ElMessage.error(e?.message || '操作失败')
  }
}
</script>
