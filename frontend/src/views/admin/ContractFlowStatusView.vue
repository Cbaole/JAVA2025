<template>
  <div style="max-width: 720px">
    <div style="border: 1px solid #eee; padding: 12px">
      <div style="font-weight: 600; margin-bottom: 8px">流程进度状态</div>
      <el-table :data="rows" border>
        <el-table-column prop="node" label="节点" width="160" />
        <el-table-column prop="operator" label="操作者" width="160" />
        <el-table-column prop="status" label="状态" width="140" />
        <el-table-column prop="time" label="操作时间" />
      </el-table>
    </div>
    <div style="margin-top: 16px; display: flex; justify-content: center">
      <el-button @click="back">返回</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getContract } from '@/lib/contractStore'

const route = useRoute()
const router = useRouter()
const id = route.params.id as string

const rows = computed(() => {
  const data = getContract(id)
  return data?.flowStatus || []
})

function back() {
  router.back()
}
</script>
