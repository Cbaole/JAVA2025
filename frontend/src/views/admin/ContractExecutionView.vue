<template>
  <div>
    <div style="font-weight: 600; margin-bottom: 8px">合同执行动态列表</div>
    <el-table :data="list" border>
      <el-table-column prop="code" label="合同编号" width="120" />
      <el-table-column prop="name" label="合同名称" />
      <el-table-column prop="progress.design" label="设计进度" width="120" />
      <el-table-column prop="progress.production" label="生产进度" width="120" />
      <el-table-column prop="progress.purchase" label="采购进度" width="120" />
      <el-table-column prop="progress.manufacture" label="制造进度" width="120" />
      <el-table-column prop="progress.assembly" label="装配进度" width="120" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ensureProgress, listContracts, type ContractRecord } from '@/lib/contractStore'

const list = computed<ContractRecord[]>(() => {
  return listContracts().map((item) => ({
    ...item,
    progress: ensureProgress(item.progress)
  }))
})
</script>
