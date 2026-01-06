<template>
  <div style="max-width: 980px">
    <div style="border: 1px solid #eee; padding: 16px; margin-bottom: 16px">
      <div style="font-weight: 600; margin-bottom: 12px">合同基本信息</div>
      <el-form label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="合同号">
              <el-input :model-value="contract?.code || ''" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同名称">
              <el-input :model-value="contract?.name || ''" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公司名称">
              <el-input :model-value="contract?.customerName || ''" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公司编号">
              <el-input :model-value="contract?.info?.companyCode || ''" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同总额">
              <el-input :model-value="totalPriceDisplay" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="已收金额">
              <el-input :model-value="paidDisplay" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="应收金额">
              <el-input :model-value="outstandingDisplay" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属片区">
              <el-input :model-value="areaDisplay" disabled />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div style="border: 1px solid #eee; padding: 16px">
      <div style="font-weight: 600; margin-bottom: 12px">收账明细</div>
      <el-table :data="receiptRows" border>
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="receiveDate" label="收款时间" width="140" />
        <el-table-column prop="amount" label="收款金额" width="120" />
        <el-table-column prop="receiver" label="收款人" width="120" />
        <el-table-column prop="remark" label="备注" />
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
import { listReceipts, summarizeContractReceivable } from '@/lib/receivableStore'

const route = useRoute()
const router = useRouter()
const id = route.params.id as string

const summary = computed(() => summarizeContractReceivable(id))
const contract = computed(() => summary.value?.contract || null)
const receiptRows = computed(() => listReceipts().filter((item) => item.contractId === id))
const totalAmount = computed(() => summary.value?.totalAmount || 0)
const totalPaid = computed(() => summary.value?.totalPaid || 0)
const outstanding = computed(() => summary.value?.outstanding || 0)

const totalPriceDisplay = computed(() => {
  const contractPrice = toAmount(contract.value?.totalPrice)
  return contractPrice ? String(contractPrice) : String(totalAmount.value)
})

const paidDisplay = computed(() => String(totalPaid.value))
const outstandingDisplay = computed(() => String(outstanding.value))
const areaDisplay = computed(() => contract.value?.info?.orderArea || contract.value?.info?.area || '')

function back() {
  router.back()
}

function toAmount(value: unknown): number {
  const num = Number(value)
  return Number.isFinite(num) ? num : 0
}
</script>
