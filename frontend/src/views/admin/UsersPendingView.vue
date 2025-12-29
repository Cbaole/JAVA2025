<template>
  <div>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px">
      <div style="font-weight: 600">账号管理（待审核）</div>
      <el-button type="primary" @click="load">刷新</el-button>
    </div>

    <el-table :data="rows" stripe>
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
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { http, unwrap } from '@/lib/http'
import { useAuthStore } from '@/stores/auth'

const store = useAuthStore()
const canUpdate = computed(() => store.has('au/user', 'update'))

const rows = ref<any[]>([])
const roles = ref<any[]>([])
const rolePick = reactive<Record<string, string>>({})

async function load() {
  try {
    rows.value = await unwrap<any[]>(http.get('/api/admin/users/pending'))
    roles.value = await unwrap<any[]>(http.get('/api/admin/roles'))
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

async function resetPwd(id: string) {
  try {
    await unwrap(http.post(`/api/admin/users/${id}/reset-password`))
    ElMessage.success('密码已重置为123456')
  } catch (e: any) {
    ElMessage.error(e?.message || '操作失败')
  }
}
</script>

