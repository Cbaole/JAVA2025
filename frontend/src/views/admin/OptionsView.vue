<template>
  <div>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px">
      <div style="font-weight: 600">选项管理</div>
      <div style="display: flex; gap: 8px; align-items: center">
        <el-select v-model="filter.groupKey" clearable filterable placeholder="分组" style="width: 200px">
          <el-option v-for="g in groupOptions" :key="g.key" :label="g.label" :value="g.key" />
        </el-select>
        <el-button type="primary" @click="load">查询</el-button>
        <el-button type="primary" :disabled="!canAdd" @click="openCreate">新增</el-button>
      </div>
    </div>

    <el-table :data="rows" stripe>
      <el-table-column label="分组" width="140">
        <template #default="{ row }">{{ groupText(row?.groupKey) }}</template>
      </el-table-column>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="value" label="值" width="200" />
      <el-table-column prop="orderNo" label="序号" width="100" />
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button size="small" :disabled="!canUpdate" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" :disabled="!canUpdate" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dlg.open" :title="dlg.mode === 'create' ? '新增选项' : '编辑选项'">
      <el-form :model="dlg.form" label-width="90px">
        <el-form-item label="分组" required>
          <el-select v-model="dlg.form.groupKey" filterable placeholder="请选择" style="width: 100%">
            <el-option v-for="g in groupOptions" :key="g.key" :label="g.label" :value="g.key" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" required>
          <el-input v-model="dlg.form.title" />
        </el-form-item>
        <el-form-item label="值" required>
          <el-input v-model="dlg.form.value" />
        </el-form-item>
        <el-form-item label="序号" required>
          <el-input-number v-model="dlg.form.orderNo" :min="0" />
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
const canAdd = computed(() => store.has('au/option', 'add'))
const canUpdate = computed(() => store.has('au/option', 'update'))

const groupOptions = [
  { key: 'POST', label: '岗位' },
  { key: 'AREA', label: '片区' },
  { key: 'DEPT', label: '部门' },
  { key: 'INDUSTRY', label: '行业' },
  { key: 'BUYER_ATTR', label: '客户属性' },
  { key: 'CONTACT_DUTY', label: '联系人职务' },
  { key: 'MARITAL', label: '婚姻状况' },
  { key: 'HOBBY', label: '爱好' }
] as const

function groupText(key: any) {
  const k = String(key || '').trim()
  const found = groupOptions.find((x) => x.key === k)
  return found ? found.label : k || '-'
}

const filter = reactive({ groupKey: '' })
const rows = ref<any[]>([])

const dlg = reactive({
  open: false,
  loading: false,
  mode: 'create' as 'create' | 'edit',
  id: '',
  form: {
    groupKey: '',
    title: '',
    value: '',
    orderNo: 0
  }
})

async function load() {
  try {
    rows.value = await unwrap<any[]>(http.get('/api/admin/options', { params: { groupKey: filter.groupKey || '' } }))
  } catch (e: any) {
    ElMessage.error(e?.message || '加载失败')
  }
}

load()

function openCreate() {
  dlg.open = true
  dlg.mode = 'create'
  dlg.id = ''
  dlg.form.groupKey = filter.groupKey || ''
  dlg.form.title = ''
  dlg.form.value = ''
  dlg.form.orderNo = 0
}

function openEdit(row: any) {
  dlg.open = true
  dlg.mode = 'edit'
  dlg.id = row.id
  dlg.form.groupKey = row.groupKey
  dlg.form.title = row.title
  dlg.form.value = row.value
  dlg.form.orderNo = row.orderNo
}

async function save() {
  if (!dlg.form.groupKey || !dlg.form.title || !dlg.form.value) {
    ElMessage.error('请填写必填项')
    return
  }
  dlg.loading = true
  try {
    if (dlg.mode === 'create') {
      await unwrap(http.post('/api/admin/options', dlg.form))
    } else {
      await unwrap(http.put(`/api/admin/options/${dlg.id}`, dlg.form))
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
    await ElMessageBox.confirm('确认删除该选项？', '提示', { type: 'warning' })
    await unwrap(http.delete(`/api/admin/options/${id}`))
    ElMessage.success('已删除')
    load()
  } catch (e: any) {
    if (e?.message) ElMessage.error(e.message)
  }
}
</script>
