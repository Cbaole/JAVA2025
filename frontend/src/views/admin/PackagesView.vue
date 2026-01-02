<template>
  <div>
    <div style="margin-bottom: 12px; display: flex; gap: 8px; align-items: center">
      <el-input v-model="q" placeholder="名称/型号" style="width: 240px" />
      <el-button type="primary" @click="load">查询</el-button>
      <el-button @click="add">新增</el-button>
      <el-button :disabled="!selectedId" @click="editSelected">修改</el-button>
      <el-button type="danger" :disabled="!selectedId" @click="removeSelected">删除</el-button>
      <el-button @click="excelImport">Excel导入</el-button>
      <input ref="excelRef" type="file" accept=".xlsx,.xls" style="display: none" @change="onExcelFile" />
    </div>

    <el-radio-group v-model="selectedId" style="width: 100%">
      <el-table
        :data="filtered"
        border
        style="width: 100%"
        highlight-current-row
        :current-row-key="selectedId"
        row-key="id"
        @row-click="onRowClick"
      >
        <el-table-column label="" width="60" align="center">
          <template #default="{ row }">
            <el-radio :label="row.id"><span /></el-radio>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="model" label="型号" />
        <el-table-column prop="params" label="参数" />
        <el-table-column prop="price" label="价格" width="120" />
        <el-table-column prop="size" label="尺寸" />
        <el-table-column prop="weight" label="重量" width="120" />
        <el-table-column prop="leadTime" label="交货周期" width="120" />
        <el-table-column label="附件" width="120">
          <template #default="{ row }">
            <el-button link type="primary" @click.stop="edit(row.id)">附件</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
      </el-table>
    </el-radio-group>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { http, unwrap } from '@/lib/http'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const q = ref('')
const list = ref<any[]>([])
const selectedId = ref<string>('')
const excelRef = ref<HTMLInputElement | null>(null)
const filtered = computed(() => {
  const v = q.value.trim()
  if (!v) return list.value
  return list.value.filter((it) => it.name?.includes(v) || it.model?.includes(v))
})

async function load() {
  try {
    list.value = await unwrap(http.get('/api/admin/crm/packages'))
    if (selectedId.value && !list.value.some((it) => it.id === selectedId.value)) {
      selectedId.value = ''
    }
  } catch (e: any) {
    ElMessage.error(e?.message || '加载失败')
  }
}

function add() {
  router.push('/admin/packages/new')
}

function edit(id: string) {
  router.push(`/admin/packages/${id}`)
}

function editSelected() {
  if (!selectedId.value) return
  edit(selectedId.value)
}

async function removeSelected() {
  if (!selectedId.value) return
  try {
    await ElMessageBox.confirm('确认删除该设备成套？', '提示', { type: 'warning' })
    await unwrap(http.post(`/api/admin/crm/packages/${selectedId.value}/delete`))
    ElMessage.success('已删除')
    selectedId.value = ''
    await load()
  } catch (e: any) {
    if (e?.message) ElMessage.error(e.message)
  }
}

function excelImport() {
  excelRef.value?.click()
}

async function onExcelFile(e: Event) {
  const el = e.target as HTMLInputElement
  const file = el.files?.[0]
  if (!file) return
  try {
    const fd = new FormData()
    fd.append('file', file)
    const count = await unwrap<number>(
      http.post('/api/admin/crm/packages/import', fd, { headers: { 'Content-Type': 'multipart/form-data' } })
    )
    ElMessage.success(`已导入${count}条`)
    await load()
  } catch (err: any) {
    ElMessage.error(err?.message || '导入失败')
  } finally {
    el.value = ''
  }
}

function onRowClick(row: any) {
  if (row?.id) selectedId.value = row.id
}

onMounted(load)
</script>
