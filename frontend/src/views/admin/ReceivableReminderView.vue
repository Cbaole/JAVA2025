<template>
  <div>
    <div style="margin-bottom: 12px; display: flex; gap: 8px; flex-wrap: wrap">
      <el-input v-model="q" placeholder="合同号/合同名称" style="width: 240px" />
      <el-button type="primary" @click="load">查询</el-button>
    </div>

    <el-table :data="filtered" border style="width: 100%">
      <el-table-column prop="contractCode" label="合同号" width="120" />
      <el-table-column prop="contractName" label="合同名称" width="180" />
      <el-table-column prop="item" label="款项" width="160" />
      <el-table-column prop="amount" label="金额" width="120" />
      <el-table-column prop="planDate" label="计划收款时间" width="140" />
      <el-table-column prop="responsible" label="责任人" width="120" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { listOverdueReceivables, type ReceivableRow } from '@/lib/receivableStore'

const q = ref('')
const rows = ref<ReceivableRow[]>([])

function load() {
  rows.value = listOverdueReceivables()
}

const filtered = computed(() => {
  const keyword = q.value.trim()
  if (!keyword) return rows.value
  return rows.value.filter((row) => row.contractCode.includes(keyword) || row.contractName.includes(keyword))
})

load()
</script>
