<template>
  <div>
    <div style="margin-bottom: 12px; display: flex; gap: 8px; flex-wrap: wrap">
      <el-input v-model="q" placeholder="合同号/合同名称" style="width: 240px" />
      <el-select v-model="status" placeholder="应收状态" style="width: 140px">
        <el-option label="全部" value="" />
        <el-option label="应收" value="应收" />
        <el-option label="已收" value="已收" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button :disabled="!canSee" @click="exportCsv">导出</el-button>
    </div>

    <el-table :data="filtered" border style="width: 100%">
      <el-table-column type="index" label="序号" width="80" />
      <el-table-column prop="receivableCode" label="应收账编号" width="140" />
      <el-table-column prop="contractCode" label="合同号" width="120" />
      <el-table-column prop="contractName" label="合同名称" width="200">
        <template #default="{ row }">
          <span style="margin-right: 8px">{{ row.contractName }}</span>
          <el-button type="primary" link :disabled="!canRead" @click="viewDetail(row.contractId)">详细信息查看</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="companyName" label="公司名称" width="180" />
      <el-table-column prop="companyCode" label="公司编号" width="120" />
      <el-table-column prop="item" label="款项" width="140" />
      <el-table-column prop="amount" label="金额" width="120" />
      <el-table-column prop="planDate" label="计划收款时间" width="140" />
      <el-table-column prop="actualDate" label="实际收款时间" width="140" />
      <el-table-column prop="status" label="应收状态" width="100" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { buildReceivableRows, type ReceivableRow } from '@/lib/receivableStore'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const store = useAuthStore()
const canSee = computed(() => store.has('crm/receivable-query', 'see'))
const canRead = computed(() => store.has('crm/receivable-query', 'read'))
const q = ref('')
const status = ref('')
const list = ref<ReceivableRow[]>([])
function load() {
  list.value = buildReceivableRows()
}

const filtered = computed(() => {
  const keyword = q.value.trim()
  return list.value.filter((row) => {
    const matchKeyword = !keyword || row.contractCode.includes(keyword) || row.contractName.includes(keyword)
    const matchStatus = !status.value || row.status === status.value
    return matchKeyword && matchStatus
  })
})

function viewDetail(contractId: string) {
  if (!canRead.value) {
    ElMessage.error('暂无阅读权限')
    return
  }
  router.push(`/admin/receivables/${contractId}`)
}

function exportCsv() {
  if (!canSee.value) {
    ElMessage.error('暂无浏览权限')
    return
  }
  if (!filtered.value.length) {
    ElMessage.error('暂无数据可导出')
    return
  }
  const headers = [
    '应收账编号',
    '合同号',
    '合同名称',
    '公司名称',
    '公司编号',
    '款项',
    '金额',
    '计划收款时间',
    '实际收款时间',
    '应收状态'
  ]
  const rows = filtered.value.map((item) => [
    item.receivableCode,
    item.contractCode,
    item.contractName,
    item.companyName,
    item.companyCode,
    item.item,
    item.amount,
    item.planDate,
    item.actualDate,
    item.status
  ])
  const content = [headers, ...rows]
    .map((row) => row.map((cell) => `"${String(cell ?? '').replace(/"/g, '""')}"`).join(','))
    .join('\n')
  const blob = new Blob([content], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = 'receivables.csv'
  document.body.appendChild(a)
  a.click()
  a.remove()
  URL.revokeObjectURL(url)
}

load()
</script>
