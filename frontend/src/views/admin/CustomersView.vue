<template>
  <div>
    <div style="margin-bottom: 12px; display: flex; gap: 8px">
      <el-input v-model="q" placeholder="客户名称" style="width: 240px" />
      <el-button type="primary" @click="load">查询</el-button>
      <el-button @click="add">新建</el-button>
    </div>
    <el-table :data="filtered" border style="width: 100%">
      <el-table-column prop="code" label="编号" width="120" />
      <el-table-column prop="name" label="客户名称" />
      <el-table-column prop="companyAddress" label="公司地址" />
      <el-table-column label="操作" width="160">
        <template #default="scope">
          <el-button size="small" @click="edit(scope.row.id)">修改</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { http, unwrap } from '@/lib/http'
import { useRouter } from 'vue-router'

const router = useRouter()
const q = ref('')
const list = ref<any[]>([])
const filtered = computed(() => {
  const v = q.value.trim()
  if (!v) return list.value
  return list.value.filter((it) => it.name?.includes(v))
})

async function load() {
  list.value = await unwrap(http.get('/api/admin/crm/customers'))
}

function add() {
  router.push('/admin/customers/new')
}

function edit(id: string) {
  router.push(`/admin/customers/${id}`)
}

onMounted(load)
</script>
