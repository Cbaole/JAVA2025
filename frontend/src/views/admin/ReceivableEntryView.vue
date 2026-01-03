<template>
  <div>
    <div style="margin-bottom: 12px; display: flex; gap: 8px">
      <el-button type="primary" :disabled="!canAdd" @click="openAdd">新增</el-button>
      <el-button :disabled="!canUpdate" @click="openEdit">修改</el-button>
      <el-button type="danger" :disabled="!canUpdate" @click="remove">删除</el-button>
    </div>

    <el-table :data="rows" border style="width: 100%" @selection-change="onSelect">
      <el-table-column type="selection" width="48" />
      <el-table-column type="index" label="序号" width="80" />
      <el-table-column prop="contractCode" label="合同号" width="120" />
      <el-table-column prop="contractName" label="合同名称" width="180" />
      <el-table-column prop="receiveDate" label="收款时间" width="140" />
      <el-table-column prop="amount" label="收款金额" width="120" />
      <el-table-column prop="receiver" label="收款人" width="120" />
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="合同" required>
          <el-select v-model="form.contractId" filterable placeholder="请选择" style="width: 100%">
            <el-option v-for="item in contracts" :key="item.id" :label="`${item.code} ${item.name}`" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="收款时间" required>
          <el-date-picker v-model="form.receiveDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="收款金额" required>
          <el-input v-model="form.amount" />
        </el-form-item>
        <el-form-item label="收款人" required>
          <el-input v-model="form.receiver" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listContracts, getContract, type ContractRecord } from '@/lib/contractStore'
import {
  createReceiptId,
  listReceipts,
  upsertReceipt,
  deleteReceipts,
  type ReceiptRecord
} from '@/lib/receivableStore'
import { useAuthStore } from '@/stores/auth'

const store = useAuthStore()
const canAdd = computed(() => store.has('crm/receivable-entry', 'add'))
const canUpdate = computed(() => store.has('crm/receivable-entry', 'update'))
const contracts = ref<ContractRecord[]>([])
const rows = ref<Array<ReceiptRecord & { contractCode: string; contractName: string }>>([])
const selected = ref<ReceiptRecord[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive<ReceiptRecord>(createEmpty())

const dialogTitle = computed(() => (isEdit.value ? '修改收款记录' : '新增收款记录'))

function createEmpty(): ReceiptRecord {
  return {
    id: createReceiptId(),
    contractId: '',
    contractCode: '',
    contractName: '',
    receiveDate: '',
    amount: '',
    receiver: '',
    remark: ''
  }
}

function load() {
  contracts.value = listContracts()
  const map = new Map(contracts.value.map((item) => [item.id, item]))
  rows.value = listReceipts().map((item) => {
    const contract = map.get(item.contractId)
    return {
      ...item,
      contractCode: contract?.code || item.contractCode || '',
      contractName: contract?.name || item.contractName || ''
    }
  })
}

function onSelect(list: ReceiptRecord[]) {
  selected.value = list
}

function openAdd() {
  if (!canAdd.value) {
    ElMessage.error('暂无新增权限')
    return
  }
  isEdit.value = false
  Object.assign(form, createEmpty())
  dialogVisible.value = true
}

function openEdit() {
  if (!canUpdate.value) {
    ElMessage.error('暂无修改权限')
    return
  }
  if (selected.value.length !== 1) {
    ElMessage.error('请选择一条收款记录')
    return
  }
  const data = selected.value[0]
  isEdit.value = true
  Object.assign(form, createEmpty(), data)
  dialogVisible.value = true
}

function save() {
  if (isEdit.value && !canUpdate.value) {
    ElMessage.error('暂无修改权限')
    return
  }
  if (!isEdit.value && !canAdd.value) {
    ElMessage.error('暂无新增权限')
    return
  }
  if (!form.contractId || !form.receiveDate || !form.amount || !form.receiver) {
    ElMessage.error('请填写完整信息')
    return
  }
  const contract = getContract(form.contractId)
  if (contract) {
    form.contractCode = contract.code
    form.contractName = contract.name
  }
  upsertReceipt({ ...form })
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

async function remove() {
  if (!canUpdate.value) {
    ElMessage.error('暂无修改权限')
    return
  }
  if (!selected.value.length) {
    ElMessage.error('请先选择收款记录')
    return
  }
  try {
    await ElMessageBox.confirm('确认删除选中的收款记录？', '提示', { type: 'warning' })
    deleteReceipts(selected.value.map((item) => item.id))
    ElMessage.success('已删除')
    load()
  } catch (e: any) {
    if (e?.message) ElMessage.error(e.message)
  }
}

load()
</script>
