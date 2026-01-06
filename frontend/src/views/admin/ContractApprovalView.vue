<template>
  <div style="max-width: 900px; border: 1px solid #eee; padding: 16px">
    <div style="font-weight: 600; margin-bottom: 12px">审批节点配置界面</div>

    <div style="border: 1px solid #eee; padding: 12px; margin-bottom: 12px">
      <div style="font-weight: 600; margin-bottom: 8px">片区负责人</div>
      <el-table :data="approval.areaManagers" border size="small">
        <el-table-column label="审核人">
          <template #default="{ row }">
            <el-select v-model="row.value" filterable placeholder="请选择" style="width: 100%">
              <el-option v-for="item in reviewerOptions" :key="item" :label="item" :value="item" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button size="small" @click="addRow(approval.areaManagers)">添加</el-button>
            <el-button size="small" type="danger" @click="removeRow(approval.areaManagers, row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div style="border: 1px solid #eee; padding: 12px; margin-bottom: 12px">
      <div style="font-weight: 600; margin-bottom: 8px">部门负责人</div>
      <el-table :data="approval.deptManagers" border size="small">
        <el-table-column label="审核人">
          <template #default="{ row }">
            <el-select v-model="row.value" filterable placeholder="请选择" style="width: 100%">
              <el-option v-for="item in reviewerOptions" :key="item" :label="item" :value="item" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button size="small" @click="addRow(approval.deptManagers)">添加</el-button>
            <el-button size="small" type="danger" @click="removeRow(approval.deptManagers, row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div style="border: 1px solid #eee; padding: 12px; margin-bottom: 12px">
      <div style="font-weight: 600; margin-bottom: 8px">公司领导</div>
      <el-table :data="approval.leaders" border size="small">
        <el-table-column label="审核人">
          <template #default="{ row }">
            <el-select v-model="row.value" filterable placeholder="请选择" style="width: 100%">
              <el-option v-for="item in reviewerOptions" :key="item" :label="item" :value="item" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button size="small" @click="addRow(approval.leaders)">添加</el-button>
            <el-button size="small" type="danger" @click="removeRow(approval.leaders, row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div style="border: 1px solid #eee; padding: 12px">
      <div style="font-weight: 600; margin-bottom: 8px">财务总监</div>
      <el-table :data="approval.financeLeads" border size="small">
        <el-table-column label="审核人">
          <template #default="{ row }">
            <el-select v-model="row.value" filterable placeholder="请选择" style="width: 100%">
              <el-option v-for="item in reviewerOptions" :key="item" :label="item" :value="item" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button size="small" @click="addRow(approval.financeLeads)">添加</el-button>
            <el-button size="small" type="danger" @click="removeRow(approval.financeLeads, row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div style="margin-top: 16px; display: flex; justify-content: center; gap: 12px">
      <el-button type="primary" @click="confirm">确定</el-button>
      <el-button @click="back">返回</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { getContract, upsertContract, type ContractRecord, type ApprovalConfig, type FlowStatus } from '@/lib/contractStore'

type ApproverRow = { id: string; value: string }

const route = useRoute()
const router = useRouter()
const store = useAuthStore()
const id = route.params.id as string

const reviewerOptions = ['张三', '李四', '王五', '赵六', '钱七', '孙八']

const approval = reactive({
  areaManagers: [] as ApproverRow[],
  deptManagers: [] as ApproverRow[],
  leaders: [] as ApproverRow[],
  financeLeads: [] as ApproverRow[]
})

function load() {
  const data = id ? getContract(id) : null
  if (!data) return
  const cfg = data.approval
  approval.areaManagers = toRows(cfg?.areaManagers)
  approval.deptManagers = toRows(cfg?.deptManagers)
  approval.leaders = toRows(cfg?.leaders)
  approval.financeLeads = toRows(cfg?.financeLeads)
  ensureOneRow()
}

function ensureOneRow() {
  if (!approval.areaManagers.length) addRow(approval.areaManagers)
  if (!approval.deptManagers.length) addRow(approval.deptManagers)
  if (!approval.leaders.length) addRow(approval.leaders)
  if (!approval.financeLeads.length) addRow(approval.financeLeads)
}

function toRows(list?: string[]): ApproverRow[] {
  return (list || []).map((value) => ({ id: cryptoId(), value }))
}

function addRow(list: ApproverRow[]) {
  list.push({ id: cryptoId(), value: '' })
}

function removeRow(list: ApproverRow[], row: ApproverRow) {
  const idx = list.findIndex((item) => item.id === row.id)
  if (idx >= 0) list.splice(idx, 1)
  if (!list.length) addRow(list)
}

function confirm() {
  const data = getContract(id)
  if (!data) {
    ElMessage.error('合同不存在')
    return
  }
  const cfg: ApprovalConfig = {
    areaManagers: approval.areaManagers.map((item) => item.value).filter(Boolean),
    deptManagers: approval.deptManagers.map((item) => item.value).filter(Boolean),
    leaders: approval.leaders.map((item) => item.value).filter(Boolean),
    financeLeads: approval.financeLeads.map((item) => item.value).filter(Boolean)
  }
  const flow = buildFlow(cfg, data)
  const next: ContractRecord = { ...data, approval: cfg, flowStatus: flow }
  upsertContract(next)
  ElMessage.success('已提交审批节点')
  router.push(`/admin/contracts/${id}/flow`)
}

function buildFlow(cfg: ApprovalConfig, data: ContractRecord): FlowStatus[] {
  const now = new Date().toLocaleString()
  const creator = store.profile?.name || store.profile?.username || '创建人'
  const rows: FlowStatus[] = [{ node: '创建人', operator: creator, status: '已提交', time: now }]
  pushNodes(rows, '片区负责人', cfg.areaManagers)
  pushNodes(rows, '部门负责人', cfg.deptManagers)
  pushNodes(rows, '公司领导', cfg.leaders)
  pushNodes(rows, '财务总监', cfg.financeLeads)
  if (!cfg.areaManagers.length && !cfg.deptManagers.length && !cfg.leaders.length && !cfg.financeLeads.length) {
    rows.push({ node: '审批人', operator: '', status: '未查看', time: '' })
  }
  return rows
}

function pushNodes(rows: FlowStatus[], label: string, list: string[]) {
  list.forEach((name, idx) => {
    rows.push({ node: `${label}${idx + 1}`, operator: name, status: '未查看', time: '' })
  })
}

function back() {
  router.back()
}

function cryptoId(): string {
  return `r_${Date.now()}_${Math.random().toString(16).slice(2, 8)}`
}

load()
</script>
