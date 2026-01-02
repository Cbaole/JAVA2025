<template>
  <div>
    <div style="margin-bottom: 12px; display: flex; gap: 8px">
      <el-input v-model="q" placeholder="片区名称/编号" style="width: 240px" />
      <el-button type="primary" @click="load">查询</el-button>
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
        <el-table-column prop="name" label="片区名称" />
        <el-table-column prop="code" label="片区编号" width="140" />
        <el-table-column label="上级部门">
          <template #default="{ row }">{{ row?.deptOption?.title || '-' }}</template>
        </el-table-column>
        <el-table-column label="创建日期" width="160">
          <template #default="{ row }">{{ formatDate(row?.createTime) }}</template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
      </el-table>
    </el-radio-group>

    <div style="margin-top: 12px; display: flex; justify-content: center; gap: 12px">
      <el-button @click="add">新建</el-button>
      <el-button :disabled="!selectedId" @click="editSelected">修改</el-button>
      <el-button type="danger" :disabled="!selectedId" @click="removeSelected">删除</el-button>
    </div>
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
const filtered = computed(() => {
  const v = q.value.trim()
  if (!v) return list.value
  return list.value.filter((it) => it.name?.includes(v) || it.code?.includes(v))
})

async function load() {
  try {
    list.value = await unwrap(http.get('/api/admin/crm/sales-areas'))
    if (selectedId.value && !list.value.some((it) => it.id === selectedId.value)) {
      selectedId.value = ''
    }
  } catch (e: any) {
    ElMessage.error(e?.message || '加载失败')
  }
}

function add() {
  router.push('/admin/areas/new')
}

function editSelected() {
  if (!selectedId.value) return
  router.push(`/admin/areas/${selectedId.value}`)
}

async function removeSelected() {
  if (!selectedId.value) return
  try {
    await ElMessageBox.confirm('确认删除该片区？', '提示', { type: 'warning' })
    await unwrap(http.post(`/api/admin/crm/sales-areas/${selectedId.value}/delete`))
    ElMessage.success('已删除')
    selectedId.value = ''
    await load()
  } catch (e: any) {
    if (e?.message) ElMessage.error(e.message)
  }
}

function onRowClick(row: any) {
  if (row?.id) selectedId.value = row.id
}

function formatDate(v: any) {
  if (!v) return ''
  const s = String(v)
  return s.length >= 10 ? s.slice(0, 10) : s
}

onMounted(load)
</script>
