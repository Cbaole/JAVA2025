<template>
  <div style="max-width: 1100px">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="合同信息" name="base">
        <div style="border: 1px solid #eee; padding: 16px">
          <el-form :model="form" label-width="90px" :disabled="readonly">
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="销售机会">
                  <el-select v-model="form.info.salesChance" placeholder="请选择" style="width: 100%">
                    <el-option label="机会A" value="机会A" />
                    <el-option label="机会B" value="机会B" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="合同号" required>
                  <el-input v-model="form.code" :disabled="readonly" />
                </el-form-item>
              </el-col>

              <el-col :span="12">
                <el-form-item label="合同名称" required>
                  <el-input v-model="form.name" :disabled="readonly" />
                </el-form-item>
              </el-col>

              <el-col :span="12">
                <el-form-item label="总价（元）" required>
                  <el-input v-model="form.totalPrice" :disabled="readonly" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="签订日期">
                  <el-date-picker v-model="form.signDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" :disabled="readonly" />
                </el-form-item>
              </el-col>

              <el-col :span="12">
                <el-form-item label="排产日期">
                  <el-date-picker v-model="form.info.scheduleDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" :disabled="readonly" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="交货日期">
                  <el-date-picker v-model="form.deliveryDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" :disabled="readonly" />
                </el-form-item>
              </el-col>

              <el-col :span="12">
                <el-form-item label="货物到站">
                  <el-input v-model="form.info.freightDestination" :disabled="readonly" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="运输支付">
                  <el-input v-model="form.info.transportPayment" :disabled="readonly" />
                </el-form-item>
              </el-col>

              <el-col :span="12">
                <el-form-item label="客户名称" required>
                  <el-input v-model="form.customerName" :disabled="readonly" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="项目名称">
                  <el-input v-model="form.info.projectName" :disabled="readonly" />
                </el-form-item>
              </el-col>

              <el-col :span="24">
                <el-form-item label="付款方式">
                  <el-input v-model="form.info.paymentTerms" type="textarea" :rows="3" :disabled="readonly" />
                </el-form-item>
              </el-col>
            </el-row>

            <div style="margin: 8px 0; border-top: 1px solid #eee"></div>

            <div style="font-weight: 600; margin-bottom: 8px">订货单位信息</div>
            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="订货单位">
                  <el-input v-model="form.info.orderUnit" :disabled="readonly" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="订货代表">
                  <el-input v-model="form.info.orderContact" :disabled="readonly" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="订货电话">
                  <el-input v-model="form.info.orderPhone" :disabled="readonly" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="订货地址">
                  <el-input v-model="form.info.orderAddress" :disabled="readonly" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="订货邮箱">
                  <el-input v-model="form.info.orderEmail" :disabled="readonly" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="订货片区">
                  <el-input v-model="form.info.orderArea" :disabled="readonly" />
                </el-form-item>
              </el-col>
            </el-row>

            <div style="margin: 8px 0; border-top: 1px solid #eee"></div>

            <el-form-item label="附件上传">
              <el-input :model-value="attachmentText" disabled style="width: 260px" />
              <el-button style="margin-left: 8px" :disabled="readonly" @click="pickFile">上传</el-button>
              <input ref="fileRef" type="file" multiple style="display: none" @change="onFiles" />
            </el-form-item>

            <div style="margin: 8px 0; border-top: 1px solid #eee"></div>

            <div style="font-weight: 600; margin-bottom: 8px">占比划分</div>
            <el-table :data="form.ratios" border size="small">
              <el-table-column prop="owner" label="负责人">
                <template #default="{ row }">
                  <el-select v-model="row.owner" filterable placeholder="请选择" style="width: 100%" :disabled="readonly" @change="onOwnerChange(row)">
                    <el-option
                      v-for="item in staffOptions"
                      :key="item.id"
                      :label="item.name || item.username || item.id"
                      :value="item.id"
                    />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column prop="area" label="所属片区">
                <template #default="{ row }">
                  <el-input v-model="row.area" disabled />
                </template>
              </el-table-column>
              <el-table-column prop="ratio" label="占比划分">
                <template #default="{ row }">
                  <el-input-number v-model="row.ratio" :min="1" :max="100" :controls="false" style="width: 100%" :disabled="readonly">
                    <template #suffix>%</template>
                  </el-input-number>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120">
                <template #default="{ row }">
                  <el-button size="small" type="danger" :disabled="readonly" @click="removeRatio(row.id)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <div style="margin-top: 8px">
              <el-button size="small" :disabled="readonly" @click="addRatio">新增</el-button>
            </div>

            <el-row :gutter="16" style="margin-top: 12px">
              <el-col :span="12">
                <el-form-item label="备注">
                  <el-input v-model="form.remark" type="textarea" :rows="3" :disabled="readonly" />
                </el-form-item>
              </el-col>
            </el-row>

            <div style="margin: 8px 0; border-top: 1px solid #eee"></div>

            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="经办部门">
                  <el-input v-model="form.handler.dept" :disabled="readonly" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="经办人">
                  <el-input v-model="form.handler.name" :disabled="readonly" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="经办日期">
                  <el-date-picker v-model="form.handler.date" type="date" value-format="YYYY-MM-DD" style="width: 100%" :disabled="readonly" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>

        <div style="margin-top: 16px; display: flex; justify-content: center; gap: 12px">
          <el-button type="primary" :disabled="readonly" @click="submit">提交</el-button>
          <el-button :disabled="readonly" @click="save">保存</el-button>
        </div>
      </el-tab-pane>

      <el-tab-pane label="细目" name="items">
        <div style="border: 1px solid #eee; padding: 12px">
          <el-table :data="form.items" border>
            <el-table-column prop="type" label="类型">
              <template #default="{ row }">
                <el-input v-model="row.type" :disabled="readonly" />
              </template>
            </el-table-column>
            <el-table-column prop="model" label="产品型号">
              <template #default="{ row }">
                <el-input v-model="row.model" :disabled="readonly" />
              </template>
            </el-table-column>
            <el-table-column prop="productType" label="产品类型">
              <template #default="{ row }">
                <el-input v-model="row.productType" :disabled="readonly" />
              </template>
            </el-table-column>
            <el-table-column prop="subType" label="细分类别">
              <template #default="{ row }">
                <el-input v-model="row.subType" :disabled="readonly" />
              </template>
            </el-table-column>
            <el-table-column prop="productName" label="产品名称">
              <template #default="{ row }">
                <el-input v-model="row.productName" :disabled="readonly" />
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注">
              <template #default="{ row }">
                <el-input v-model="row.remark" :disabled="readonly" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button size="small" type="danger" :disabled="readonly" @click="removeItem(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div style="margin-top: 8px">
            <el-button size="small" :disabled="readonly" @click="addItem">新增</el-button>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="付款阶段" name="payment">
        <div style="border: 1px solid #eee; padding: 12px">
          <el-table :data="form.paymentStages" border>
            <el-table-column prop="stage" label="付款阶段">
              <template #default="{ row }">
                <el-input v-model="row.stage" :disabled="readonly" />
              </template>
            </el-table-column>
            <el-table-column prop="payableAmount" label="应付金额">
              <template #default="{ row }">
                <el-input v-model="row.payableAmount" :disabled="readonly" />
              </template>
            </el-table-column>
            <el-table-column prop="paidAmount" label="已付金额">
              <template #default="{ row }">
                <el-input v-model="row.paidAmount" :disabled="readonly" />
              </template>
            </el-table-column>
            <el-table-column prop="stageName" label="付款阶段名称">
              <template #default="{ row }">
                <el-input v-model="row.stageName" :disabled="readonly" />
              </template>
            </el-table-column>
            <el-table-column prop="payDate" label="应付时间">
              <template #default="{ row }">
                <el-date-picker v-model="row.payDate" type="date" value-format="YYYY-MM-DD" :disabled="readonly" />
              </template>
            </el-table-column>
            <el-table-column prop="responsible" label="责任人">
              <template #default="{ row }">
                <el-input v-model="row.responsible" :disabled="readonly" />
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注">
              <template #default="{ row }">
                <el-input v-model="row.remark" :disabled="readonly" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button size="small" type="danger" :disabled="readonly" @click="removePayment(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div style="margin-top: 8px">
            <el-button size="small" :disabled="readonly" @click="addPayment">新增</el-button>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { http, unwrap } from '@/lib/http'
import { useAuthStore } from '@/stores/auth'
import {
  createId,
  ensureProgress,
  getContract,
  upsertContract,
  type ContractRecord,
  type ContractItem,
  type PaymentStage
} from '@/lib/contractStore'

const route = useRoute()
const router = useRouter()
const activeTab = ref('base')
const id = route.params.id as string
const isNew = computed(() => !id || id === 'new')
const store = useAuthStore()
const canAdd = computed(() => store.has('crm/contract-basic', 'add'))
const canUpdate = computed(() => store.has('crm/contract-basic', 'update'))
const canEdit = computed(() => (isNew.value ? canAdd.value : canUpdate.value))
const readonly = computed(() => route.query.view === '1' || !canEdit.value)
const fileRef = ref<HTMLInputElement | null>(null)
const staffOptions = ref<Array<{ id: string; name?: string; username?: string; areaOption?: { title?: string; value?: string } }>>([])

const form = reactive<ContractRecord>(createEmptyContract())

const attachmentText = computed(() => `已上传${form.attachments?.length || 0}个`)

function createEmptyContract(): ContractRecord {
  const today = new Date()
  const signDate = formatDate(today)
  const deliveryDate = formatDate(addDays(today, 10))
  return {
    id: createId(),
    code: '',
    name: '',
    customerName: '',
    signDate,
    deliveryDate,
    totalPrice: '',
    remark: '',
    info: {
      salesChance: '',
      scheduleDate: '',
      freightDestination: '',
      transportPayment: '',
      projectName: '',
      paymentTerms: '',
      orderUnit: '',
      orderContact: '',
      orderPhone: '',
      orderAddress: '',
      orderEmail: '',
      orderArea: ''
    },
    items: [],
    paymentStages: [],
    progress: ensureProgress(),
    attachments: [],
    ratios: [],
    handler: { dept: '', name: '', date: '' }
  }
}

function applyContract(data: ContractRecord) {
  Object.assign(form, createEmptyContract(), data)
  form.items = data.items || []
  form.paymentStages = data.paymentStages || []
  form.attachments = data.attachments || []
  form.ratios = (data.ratios || []).map((item) => ({
    ...item,
    ratio: typeof item.ratio === 'number' ? item.ratio : item.ratio ? Number(item.ratio) : null
  }))
  form.handler = data.handler || { dept: '', name: '', date: '' }
  form.progress = ensureProgress(data.progress)
  form.info = { ...createEmptyContract().info, ...(data.info || {}) }
}

function load() {
  if (id && id !== 'new') {
    const data = getContract(id)
    if (data) applyContract(data)
  }
}

function save(): string | null {
  if (!canEdit.value) {
    ElMessage.error('暂无操作权限')
    return null
  }
  if (!form.code || !form.customerName || !form.totalPrice) {
    ElMessage.error('合同号、客户名称和总价为必填')
    return null
  }
  if (!form.name) form.name = `${form.customerName}合同`
  if (!form.paymentStages.length) {
    form.paymentStages = [
      {
        id: createId(),
        stage: '阶段1',
        payableAmount: form.totalPrice,
        paidAmount: '',
        stageName: '合同回款',
        payDate: form.deliveryDate || form.signDate,
        responsible: '',
        remark: ''
      }
    ]
  }
  const savedId = upsertContract({ ...form })
  ElMessage.success('保存成功')
  if (route.params.id === 'new') {
    router.replace(`/admin/contracts/${savedId}`)
  }
  return savedId
}

function submit() {
  const savedId = save()
  if (!savedId) return
  router.push(`/admin/contracts/${savedId}/approval`)
}

function addItem() {
  form.items.push(createItem())
}

function removeItem(rowId: string) {
  form.items = form.items.filter((item) => item.id !== rowId)
}

function createItem(): ContractItem {
  return {
    id: createId(),
    type: '',
    model: '',
    productType: '',
    subType: '',
    productName: '',
    remark: ''
  }
}

function addPayment() {
  form.paymentStages.push(createPayment())
}

function removePayment(rowId: string) {
  form.paymentStages = form.paymentStages.filter((item) => item.id !== rowId)
}

function createPayment(): PaymentStage {
  return {
    id: createId(),
    stage: '',
    payableAmount: '',
    paidAmount: '',
    stageName: '',
    payDate: '',
    responsible: '',
    remark: ''
  }
}

function addRatio() {
  form.ratios = form.ratios || []
  form.ratios.push({ id: createId(), owner: '', area: '', ratio: null })
}

function removeRatio(rowId: string) {
  form.ratios = (form.ratios || []).filter((item) => item.id !== rowId)
}

async function loadStaffs() {
  try {
    staffOptions.value = await unwrap(http.get('/api/admin/crm/staffs'))
    normalizeRatioOwners()
  } catch (e: any) {
    ElMessage.error(e?.message || '加载负责人失败')
  }
}

function onOwnerChange(row: { owner: string; area: string }) {
  const staff = staffOptions.value.find((item) => item.id === row.owner)
  row.area = staff?.areaOption?.title || staff?.areaOption?.value || ''
}

function normalizeRatioOwners() {
  if (!form.ratios?.length) return
  form.ratios.forEach((row) => {
    if (!row.owner) return
    const match = staffOptions.value.find(
      (item) => item.id === row.owner || item.name === row.owner || item.username === row.owner
    )
    if (match) {
      row.owner = match.id
      row.area = match.areaOption?.title || match.areaOption?.value || ''
    }
  })
}

function pickFile() {
  fileRef.value?.click()
}

function onFiles(e: Event) {
  const el = e.target as HTMLInputElement
  const files = el.files ? Array.from(el.files) : []
  if (!files.length) return
  if (!form.attachments) form.attachments = []
  form.attachments.push(...files.map((file) => file.name))
  el.value = ''
}

function addDays(date: Date, days: number): Date {
  const next = new Date(date.getTime())
  next.setDate(next.getDate() + days)
  return next
}

function formatDate(date: Date): string {
  return date.toISOString().slice(0, 10)
}

onMounted(() => {
  load()
  loadStaffs()
})
</script>
