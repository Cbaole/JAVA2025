<template>
  <div>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px">
      <div style="font-weight: 600">角色管理</div>
      <el-button type="primary" :disabled="!canAdd" @click="openCreate">新增</el-button>
    </div>

    <el-table :data="rows" stripe>
      <el-table-column prop="roleName" label="角色名" width="200" />
      <el-table-column prop="roleDesc" label="描述" />
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button size="small" :disabled="!canUpdate" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" :disabled="!canUpdate" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dlg.open" :title="dlg.mode === 'create' ? '新增角色' : '编辑角色'">
      <el-form :model="dlg.form" label-width="90px">
        <el-form-item label="角色名" required>
          <el-input v-model="dlg.form.roleName" />
        </el-form-item>
        <el-form-item label="描述" required>
          <el-input v-model="dlg.form.roleDesc" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dlg.open = false">取消</el-button>
        <el-button type="primary" :loading="dlg.loading" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { http, unwrap } from '@/lib/http'
import { useAuthStore } from '@/stores/auth'

const store = useAuthStore()
const canAdd = computed(() => store.has('au/role', 'add'))
const canUpdate = computed(() => store.has('au/role', 'update'))

const rows = ref<any[]>([])

const dlg = reactive({
  open: false,
  loading: false,
  mode: 'create' as 'create' | 'edit',
  id: '',
  form: {
    roleName: '',
    roleDesc: ''
  }
})

async function load() {
  try {
    rows.value = await unwrap<any[]>(http.get('/api/admin/roles'))
  } catch (e: any) {
    ElMessage.error(e?.message || '加载失败')
  }
}

load()

function openCreate() {
  dlg.open = true
  dlg.mode = 'create'
  dlg.id = ''
  dlg.form.roleName = ''
  dlg.form.roleDesc = ''
}

function openEdit(row: any) {
  dlg.open = true
  dlg.mode = 'edit'
  dlg.id = row.id
  dlg.form.roleName = row.roleName
  dlg.form.roleDesc = row.roleDesc
}

async function save() {
  if (!dlg.form.roleName || !dlg.form.roleDesc) {
    ElMessage.error('请填写必填项')
    return
  }
  dlg.loading = true
  try {
    if (dlg.mode === 'create') {
      await unwrap(http.post('/api/admin/roles', dlg.form))
    } else {
      await unwrap(http.put(`/api/admin/roles/${dlg.id}`, dlg.form))
    }
    ElMessage.success('保存成功')
    dlg.open = false
    load()
  } catch (e: any) {
    ElMessage.error(e?.message || '保存失败')
  } finally {
    dlg.loading = false
  }
}

async function remove(id: string) {
  try {
    await ElMessageBox.confirm('确认删除该角色？', '提示', { type: 'warning' })
    await unwrap(http.delete(`/api/admin/roles/${id}`))
    ElMessage.success('已删除')
    load()
  } catch (e: any) {
    if (e?.message) ElMessage.error(e.message)
  }
}
</script>

