<template>
  <div>
    <div style="margin-bottom: 12px; display: flex; gap: 8px; flex-wrap: wrap">
      <el-input v-model="q" placeholder="合同号/合同名称" style="width: 240px" />
      <el-button type="primary" @click="load">查询</el-button>
    </div>

    <el-table :data="filtered" border style="width: 100%">
      <el-table-column prop="contractCode" label="合同号" width="120" />
      <el-table-column prop="contractName" label="合同名称" width="180" />
      <el-table-column prop="item" label="付款阶段" width="160" />
      <el-table-column prop="amount" label="应付金额" width="120" />
      <el-table-column prop="paidAmount" label="已付金额" width="120" />
      <el-table-column prop="planDate" label="应付时间" width="140">
        <template #default="{ row }">
          <el-date-picker
            v-model="row.planDate"
            type="date"
            value-format="YYYY-MM-DD"
            style="width: 120px"
            :disabled="!canUpdate"
            @change="onPlanChange(row)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="actualDate" label="付款日期" width="140" />
      <el-table-column prop="responsible" label="责任人" width="140">
        <template #default="{ row }">
          <el-input v-model="row.responsible" :disabled="!canUpdate" @change="onResponsibleChange(row)" />
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { buildReceivableRows, type ReceivableRow } from '@/lib/receivableStore'
import { getContract, upsertContract } from '@/lib/contractStore'
import { useAuthStore } from '@/stores/auth'

const store = useAuthStore()
const canUpdate = computed(() => store.has('crm/receivable-plan', 'update'))
const q = ref('')
const list = ref<ReceivableRow[]>([])

function load() {
  list.value = buildReceivableRows()
}

const filtered = computed(() => {
  const keyword = q.value.trim()
  if (!keyword) return list.value
  return list.value.filter((row) => row.contractCode.includes(keyword) || row.contractName.includes(keyword))
})

function onPlanChange(row: ReceivableRow) {
  if (!canUpdate.value) {
    ElMessage.error('暂无修改权限')
    return
  }
  const contract = getContract(row.contractId)
  if (!contract) return
  const stage = contract.paymentStages.find((item) => item.id === row.id)
  if (!stage) return
  stage.payDate = row.planDate
  upsertContract({ ...contract })
  ElMessage.success('已更新应付时间')
  load()
}

function onResponsibleChange(row: ReceivableRow) {
  if (!canUpdate.value) {
    ElMessage.error('暂无修改权限')
    return
  }
  const contract = getContract(row.contractId)
  if (!contract) return
  const stage = contract.paymentStages.find((item) => item.id === row.id)
  if (!stage) return
  stage.responsible = row.responsible
  upsertContract({ ...contract })
  ElMessage.success('已更新责任人')
  load()
}

load()
</script>
