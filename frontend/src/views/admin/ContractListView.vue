<template>
  <div>
    <div style="margin-bottom: 12px; display: flex; gap: 8px">
      <el-button type="primary" @click="add">新增</el-button>
      <el-button @click="edit">修改</el-button>
      <el-button type="danger" @click="remove">删除</el-button>
      <el-button @click="exportCsv">导出</el-button>
      <el-button @click="viewFlow">查看流程审批状态</el-button>
    </div>

    <el-table :data="list" border style="width: 100%" @selection-change="onSelect">
      <el-table-column type="selection" width="48" />
      <el-table-column prop="code" label="合同编号" width="120" />
      <el-table-column prop="name" label="合同名称" width="180" />
      <el-table-column prop="customerName" label="客户名称" />
      <el-table-column prop="signDate" label="签订时间" width="140" />
      <el-table-column prop="deliveryDate" label="交货期" width="140" />
      <el-table-column prop="totalPrice" label="总价" width="120" />
      <el-table-column prop="remark" label="备注" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listContracts, deleteContracts, type ContractRecord } from '@/lib/contractStore'

const router = useRouter()
const list = ref<ContractRecord[]>([])
const selected = ref<ContractRecord[]>([])

function load() {
  list.value = listContracts()
}

function onSelect(rows: ContractRecord[]) {
  selected.value = rows
}

function add() {
  router.push('/admin/contracts/new')
}

function edit() {
  if (selected.value.length !== 1) {
    ElMessage.error('请选择一条合同记录')
    return
  }
  router.push(`/admin/contracts/${selected.value[0].id}`)
}

async function remove() {
  if (!selected.value.length) {
    ElMessage.error('请先选择合同记录')
    return
  }
  try {
    await ElMessageBox.confirm('确认删除选中的合同？', '提示', { type: 'warning' })
    const ids = selected.value.map((row) => row.id)
    deleteContracts(ids)
    ElMessage.success('已删除')
    load()
  } catch (e: any) {
    if (e?.message) ElMessage.error(e.message)
  }
}

function viewFlow() {
  if (selected.value.length !== 1) {
    ElMessage.error('请选择一条合同记录')
    return
  }
  router.push(`/admin/contracts/${selected.value[0].id}/flow`)
}

function exportCsv() {
  if (!list.value.length) {
    ElMessage.error('暂无数据可导出')
    return
  }
  const headers = ['合同编号', '合同名称', '客户名称', '签订时间', '交货期', '总价', '备注']
  const rows = list.value.map((item) => [
    item.code,
    item.name,
    item.customerName,
    item.signDate,
    item.deliveryDate,
    item.totalPrice,
    item.remark
  ])
  const content = [headers, ...rows]
    .map((row) => row.map((cell) => `"${String(cell ?? '').replace(/"/g, '""')}"`).join(','))
    .join('\n')
  const blob = new Blob([content], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = 'contracts.csv'
  document.body.appendChild(a)
  a.click()
  a.remove()
  URL.revokeObjectURL(url)
}

onMounted(load)
</script>
