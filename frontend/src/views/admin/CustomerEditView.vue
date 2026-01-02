<template>
  <div style="min-width: 980px">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px">
      <div style="font-weight: 600">{{ isNew ? '新建客户' : '客户详细信息' }}</div>
      <div style="display: flex; gap: 8px">
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
        <el-button @click="back">返回</el-button>
      </div>
    </div>

    <el-card>
      <el-tabs v-model="mainTab" type="border-card">
        <el-tab-pane label="基本信息" name="base">
          <el-form :model="form" label-width="110px">
            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="编号">
                  <el-input v-model="form.code" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="客户名称" required>
                  <el-input v-model="form.name" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="所属区域">
                  <el-select v-model="form.areaOptionId" clearable filterable placeholder="请选择" style="width: 100%">
                    <el-option v-for="o in areaOptions" :key="o.id" :label="o.title" :value="o.id" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="所属行业">
                  <el-select v-model="form.industryOptionId" clearable filterable placeholder="请选择" style="width: 100%">
                    <el-option v-for="o in industryOptions" :key="o.id" :label="o.title" :value="o.id" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="客户属性">
                  <el-select v-model="form.buyerAttrOptionId" clearable filterable placeholder="请选择" style="width: 100%">
                    <el-option v-for="o in buyerAttrOptions" :key="o.id" :label="o.title" :value="o.id" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="公司地址">
                  <el-input v-model="form.companyAddress" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="开票信息" name="invoice">
          <el-form :model="form" label-width="110px">
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="发票电话">
                  <el-input v-model="form.invoicePhone" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="税号">
                  <el-input v-model="form.invoiceTaxNo" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="开户银行">
                  <el-input v-model="form.invoiceBank" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="发票地址">
                  <el-input v-model="form.invoiceAddress" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="信用信息" name="credit">
          <el-form :model="form" label-width="110px">
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="信用等级">
                  <el-input v-model="form.creditLevel" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="欠款金额">
                  <el-input-number v-model="form.debtAmount" :min="0" style="width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-card style="margin-top: 12px">
      <div v-if="isNew">
        <el-alert type="info" :closable="false" show-icon>请先保存客户基本信息后，再维护下方信息</el-alert>
      </div>
      <el-tabs v-else v-model="subTab" type="card">
        <el-tab-pane label="客户关键人物" name="contact">
          <el-table :data="contacts" border @selection-change="onSelectContacts">
            <el-table-column type="selection" width="45" />
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column prop="code" label="联系人编码" width="140" />
            <el-table-column prop="name" label="联系人名称" width="120" />
            <el-table-column label="性别" width="80">
              <template #default="{ row }">{{ genderText(row?.gender) }}</template>
            </el-table-column>
            <el-table-column prop="birthday" label="生日" width="120" />
            <el-table-column label="职位" width="140">
              <template #default="{ row }">{{ row.postOption?.title || '-' }}</template>
            </el-table-column>
            <el-table-column label="职务" width="140">
              <template #default="{ row }">{{ row.dutyOption?.title || '-' }}</template>
            </el-table-column>
            <el-table-column prop="phone" label="联系方式" width="160" />
            <el-table-column prop="remark" label="备注" />
          </el-table>
          <div style="margin-top: 8px; display: flex; gap: 8px">
            <el-button size="small" @click="openAddContact">新增</el-button>
            <el-button size="small" type="danger" @click="deleteSelectedContacts">删除</el-button>
          </div>
        </el-tab-pane>

        <el-tab-pane label="项目机会" name="chance">
          <el-table :data="chances" border @selection-change="onSelectChances">
            <el-table-column type="selection" width="45" />
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column prop="projectName" label="项目名称" />
            <el-table-column prop="areaTitle" label="项目所属片区" width="160" />
            <el-table-column prop="time" label="时间" width="140" />
            <el-table-column prop="status" label="状态" width="120" />
            <el-table-column prop="remark" label="备注" />
          </el-table>
          <div style="margin-top: 8px; display: flex; gap: 8px">
            <el-button size="small" @click="openAddChance">新增</el-button>
            <el-button size="small" type="danger" @click="deleteSelectedChances">删除</el-button>
          </div>
        </el-tab-pane>

        <el-tab-pane label="合同信息" name="contract">
          <el-table :data="contracts" border @selection-change="onSelectContracts">
            <el-table-column type="selection" width="45" />
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column prop="contractName" label="合同名称" />
            <el-table-column prop="contractCode" label="合同编号" width="160" />
            <el-table-column label="区域" width="140">
              <template #default="{ row }">{{ row.areaOption?.title || '-' }}</template>
            </el-table-column>
            <el-table-column prop="signDate" label="时间" width="140" />
            <el-table-column prop="remark" label="备注" />
          </el-table>
          <div style="margin-top: 8px; display: flex; gap: 8px">
            <el-button size="small" @click="openAddContract">新增</el-button>
            <el-button size="small" type="danger" @click="deleteSelectedContracts">删除</el-button>
          </div>
        </el-tab-pane>

        <el-tab-pane label="售后信息" name="aftersale">
          <el-table :data="afterSales" border @selection-change="onSelectAfterSales">
            <el-table-column type="selection" width="45" />
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column prop="contractCode" label="合同编号" width="160" />
            <el-table-column prop="contractName" label="合同名称" />
            <el-table-column label="区域" width="140">
              <template #default="{ row }">{{ row.areaOption?.title || '-' }}</template>
            </el-table-column>
            <el-table-column prop="staffName" label="售后人员" width="140" />
            <el-table-column prop="remark" label="备注" />
          </el-table>
          <div style="margin-top: 8px; display: flex; gap: 8px">
            <el-button size="small" @click="openAddAfterSale">新增</el-button>
            <el-button size="small" type="danger" @click="deleteSelectedAfterSales">删除</el-button>
          </div>
        </el-tab-pane>

        <el-tab-pane label="客户来访信息" name="visit">
          <el-table :data="visits" border @selection-change="onSelectVisits">
            <el-table-column type="selection" width="45" />
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column prop="customerCode" label="客户序号" width="160" />
            <el-table-column prop="customerName" label="客户名称" />
            <el-table-column prop="status" label="状态" width="120" />
            <el-table-column prop="visitDate" label="日期" width="140" />
            <el-table-column prop="remark" label="备注" />
          </el-table>
          <div style="margin-top: 8px; display: flex; gap: 8px">
            <el-button size="small" @click="openAddVisit">新增</el-button>
            <el-button size="small" type="danger" @click="deleteSelectedVisits">删除</el-button>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="showAddContact" title="新增关键人物" width="700px">
      <el-form :model="contactForm" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="所属客户">
              <el-input :model-value="form.name" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系人编码">
              <el-input v-model="contactForm.code" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系人名称" required>
              <el-input v-model="contactForm.name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="称呼">
              <el-input v-model="contactForm.nickname" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-select v-model="contactForm.gender" clearable placeholder="请选择" style="width: 100%">
                <el-option label="未知" value="UNKNOWN" />
                <el-option label="男" value="MALE" />
                <el-option label="女" value="FEMALE" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="直接上级">
              <el-input v-model="contactForm.supervisor" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系方式">
              <el-input v-model="contactForm.phone" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职位">
              <el-select v-model="contactForm.postOptionId" clearable filterable placeholder="请选择" style="width: 100%">
                <el-option v-for="o in postOptions" :key="o.id" :label="o.title" :value="o.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生日">
              <el-date-picker v-model="contactForm.birthday" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职务">
              <el-select v-model="contactForm.dutyOptionId" clearable filterable placeholder="请选择" style="width: 100%">
                <el-option v-for="o in dutyOptions" :key="o.id" :label="o.title" :value="o.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="个人爱好">
              <el-select v-model="contactForm.hobby" filterable clearable allow-create default-first-option placeholder="请选择/输入" style="width: 100%">
                <el-option v-for="o in hobbyOptions" :key="o.id" :label="o.title" :value="o.title" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="婚姻状况">
              <el-select v-model="contactForm.maritalOptionId" clearable filterable placeholder="请选择" style="width: 100%">
                <el-option v-for="o in maritalOptions" :key="o.id" :label="o.title" :value="o.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否主要联系人">
              <el-radio-group v-model="contactForm.primary">
                <el-radio :label="true">是</el-radio>
                <el-radio :label="false">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="contactForm.remark" type="textarea" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="showAddContact = false">取消</el-button>
        <el-button type="primary" :loading="subSaving" @click="saveContact">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showAddContract" title="新增合同信息" width="700px">
      <el-form :model="contractForm" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="合同名称" required>
              <el-input v-model="contractForm.contractName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同编号" required>
              <el-input v-model="contractForm.contractCode" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="区域">
              <el-select v-model="contractForm.areaOptionId" clearable filterable placeholder="请选择" style="width: 100%">
                <el-option v-for="o in areaOptions" :key="o.id" :label="o.title" :value="o.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="时间">
              <el-date-picker v-model="contractForm.signDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="contractForm.remark" type="textarea" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="showAddContract = false">取消</el-button>
        <el-button type="primary" :loading="subSaving" @click="saveContract">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showAddAfterSale" title="新增售后信息" width="700px">
      <el-form :model="afterSaleForm" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="合同编号">
              <el-input v-model="afterSaleForm.contractCode" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同名称">
              <el-input v-model="afterSaleForm.contractName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="区域">
              <el-select v-model="afterSaleForm.areaOptionId" clearable filterable placeholder="请选择" style="width: 100%">
                <el-option v-for="o in areaOptions" :key="o.id" :label="o.title" :value="o.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="售后人员">
              <el-input v-model="afterSaleForm.staffName" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="afterSaleForm.remark" type="textarea" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="showAddAfterSale = false">取消</el-button>
        <el-button type="primary" :loading="subSaving" @click="saveAfterSale">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showAddVisit" title="新增客户来访" width="700px">
      <el-form :model="visitForm" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="客户编号">
              <el-input v-model="visitForm.customerCode" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户名称">
              <el-input v-model="visitForm.customerName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-input v-model="visitForm.status" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="日期">
              <el-date-picker v-model="visitForm.visitDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="visitForm.remark" type="textarea" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="showAddVisit = false">取消</el-button>
        <el-button type="primary" :loading="subSaving" @click="saveVisit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { http, unwrap } from '@/lib/http'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const id = route.params.id as string | undefined

type OptionItem = { id: string; title: string }

const isNew = computed(() => !id || id === 'new')

const mainTab = ref<'base' | 'invoice' | 'credit'>('base')
const subTab = ref<'contact' | 'chance' | 'contract' | 'aftersale' | 'visit'>('contact')

const saving = ref(false)
const subSaving = ref(false)

const form = reactive({
  code: '',
  name: '',
  companyAddress: '',
  invoicePhone: '',
  invoiceAddress: '',
  invoiceBank: '',
  invoiceTaxNo: '',
  areaOptionId: '' as string | '',
  industryOptionId: '' as string | '',
  buyerAttrOptionId: '' as string | '',
  creditLevel: '',
  debtAmount: null as number | null
})

const contacts = ref<any[]>([])
const chances = ref<any[]>([])
const contracts = ref<any[]>([])
const afterSales = ref<any[]>([])
const visits = ref<any[]>([])

const areaOptions = ref<OptionItem[]>([])
const industryOptions = ref<OptionItem[]>([])
const buyerAttrOptions = ref<OptionItem[]>([])
const postOptions = ref<OptionItem[]>([])
const dutyOptions = ref<OptionItem[]>([])
const maritalOptions = ref<OptionItem[]>([])
const hobbyOptions = ref<OptionItem[]>([])

const selectedContacts = ref<any[]>([])
const selectedChances = ref<any[]>([])
const selectedContracts = ref<any[]>([])
const selectedAfterSales = ref<any[]>([])
const selectedVisits = ref<any[]>([])

const showAddContact = ref(false)
const showAddContract = ref(false)
const showAddAfterSale = ref(false)
const showAddVisit = ref(false)

const contactForm = reactive({
  id: '',
  name: '',
  code: '',
  nickname: '',
  gender: '' as string | '',
  birthday: '' as string | '',
  postOptionId: '' as string | '',
  dutyOptionId: '' as string | '',
  phone: '',
  primary: false,
  supervisor: '',
  maritalOptionId: '' as string | '',
  hobby: '',
  remark: ''
})
const contractForm = reactive({
  id: '',
  contractName: '',
  contractCode: '',
  areaOptionId: '' as string | '',
  signDate: '' as string | '',
  remark: ''
})
const afterSaleForm = reactive({
  id: '',
  contractCode: '',
  contractName: '',
  areaOptionId: '' as string | '',
  staffName: '',
  remark: ''
})
const visitForm = reactive({
  id: '',
  customerCode: '',
  customerName: '',
  status: '',
  visitDate: '' as string | '',
  remark: ''
})

function resetAll() {
  form.code = ''
  form.name = ''
  form.companyAddress = ''
  form.invoicePhone = ''
  form.invoiceAddress = ''
  form.invoiceBank = ''
  form.invoiceTaxNo = ''
  form.areaOptionId = ''
  form.industryOptionId = ''
  form.buyerAttrOptionId = ''
  form.creditLevel = ''
  form.debtAmount = null
  contacts.value = []
  chances.value = []
  contracts.value = []
  afterSales.value = []
  visits.value = []
}

function resetContactForm() {
  contactForm.id = ''
  contactForm.name = ''
  contactForm.code = ''
  contactForm.nickname = ''
  contactForm.gender = ''
  contactForm.birthday = ''
  contactForm.postOptionId = ''
  contactForm.dutyOptionId = ''
  contactForm.phone = ''
  contactForm.primary = false
  contactForm.supervisor = ''
  contactForm.maritalOptionId = ''
  contactForm.hobby = ''
  contactForm.remark = ''
}

function resetContractForm() {
  contractForm.id = ''
  contractForm.contractName = ''
  contractForm.contractCode = ''
  contractForm.areaOptionId = ''
  contractForm.signDate = ''
  contractForm.remark = ''
}

function resetAfterSaleForm() {
  afterSaleForm.id = ''
  afterSaleForm.contractCode = ''
  afterSaleForm.contractName = ''
  afterSaleForm.areaOptionId = ''
  afterSaleForm.staffName = ''
  afterSaleForm.remark = ''
}

function resetVisitForm() {
  visitForm.id = ''
  visitForm.customerCode = ''
  visitForm.customerName = ''
  visitForm.status = ''
  visitForm.visitDate = ''
  visitForm.remark = ''
}

async function loadOptions() {
  const loadGroup = async (groupKey: string) => unwrap<OptionItem[]>(http.get('/api/public/options', { params: { groupKey } }))
  try {
    ;[
      areaOptions.value,
      industryOptions.value,
      buyerAttrOptions.value,
      postOptions.value,
      dutyOptions.value,
      maritalOptions.value,
      hobbyOptions.value
    ] = await Promise.all([
      loadGroup('AREA'),
      loadGroup('INDUSTRY'),
      loadGroup('BUYER_ATTR'),
      loadGroup('POST'),
      loadGroup('CONTACT_DUTY'),
      loadGroup('MARITAL'),
      loadGroup('HOBBY')
    ])
  } catch (e: any) {
    ElMessage.error(e?.message || '加载选项失败')
  }
}

async function load() {
  resetAll()
  if (isNew.value) return
  const data: any = await unwrap(http.get(`/api/admin/crm/customers/${id}`))
  const base = data?.base || {}
  form.code = base?.code || ''
  form.name = base?.name || ''
  form.companyAddress = base?.companyAddress || ''
  form.invoicePhone = base?.invoicePhone || ''
  form.invoiceAddress = base?.invoiceAddress || ''
  form.invoiceBank = base?.invoiceBank || ''
  form.invoiceTaxNo = base?.invoiceTaxNo || ''
  form.areaOptionId = base?.areaOption?.id || ''
  form.industryOptionId = base?.industryOption?.id || ''
  form.buyerAttrOptionId = base?.buyerAttrOption?.id || ''
  form.creditLevel = base?.creditLevel || ''
  form.debtAmount = typeof base?.debtAmount === 'number' ? base.debtAmount : base?.debtAmount ? Number(base.debtAmount) : null
  contacts.value = data?.contactList || []
  contracts.value = data?.contractList || []
  afterSales.value = data?.afterSaleList || []
  visits.value = data?.visitList || []
}

async function save() {
  if (!form.name) {
    ElMessage.error('客户名称不能为空')
    mainTab.value = 'base'
    return
  }
  saving.value = true
  try {
    const payload = {
      id: !isNew.value ? id : '',
      code: form.code,
      name: form.name,
      companyAddress: form.companyAddress,
      invoicePhone: form.invoicePhone,
      invoiceAddress: form.invoiceAddress,
      invoiceBank: form.invoiceBank,
      invoiceTaxNo: form.invoiceTaxNo,
      areaOptionId: form.areaOptionId || null,
      industryOptionId: form.industryOptionId || null,
      buyerAttrOptionId: form.buyerAttrOptionId || null,
      creditLevel: form.creditLevel,
      debtAmount: form.debtAmount ?? null
    }
    const newId = await unwrap<string>(http.post('/api/admin/crm/customers/upsert', payload))
    ElMessage.success('保存成功')
    if (isNew.value) {
      router.replace(`/admin/customers/${newId}`)
      return
    }
    await load()
  } catch (e: any) {
    ElMessage.error(e?.message || '保存失败')
  } finally {
    saving.value = false
  }
}

function openAddContact() {
  resetContactForm()
  showAddContact.value = true
}

function openAddContract() {
  resetContractForm()
  showAddContract.value = true
}

function openAddAfterSale() {
  resetAfterSaleForm()
  showAddAfterSale.value = true
}

function openAddVisit() {
  resetVisitForm()
  showAddVisit.value = true
}

function genderText(v: any) {
  if (v === 'MALE') return '男'
  if (v === 'FEMALE') return '女'
  return '未知'
}

function onSelectContacts(rows: any[]) {
  selectedContacts.value = rows
}

function onSelectChances(rows: any[]) {
  selectedChances.value = rows
}

function onSelectContracts(rows: any[]) {
  selectedContracts.value = rows
}

function onSelectAfterSales(rows: any[]) {
  selectedAfterSales.value = rows
}

function onSelectVisits(rows: any[]) {
  selectedVisits.value = rows
}

function openAddChance() {
  ElMessage.info('项目机会接口暂未实现')
}

function deleteSelectedChances() {
  if (!selectedChances.value.length) {
    ElMessage.error('请选择要删除的数据')
    return
  }
  ElMessage.info('项目机会接口暂未实现')
}

async function deleteSelectedContacts() {
  if (!selectedContacts.value.length) {
    ElMessage.error('请选择要删除的数据')
    return
  }
  try {
    await ElMessageBox.confirm('确认删除选中的关键人物？', '提示', { type: 'warning' })
    for (const row of selectedContacts.value) {
      if (row?.id) await unwrap(http.post(`/api/admin/crm/customers/contacts/${row.id}/delete`))
    }
    ElMessage.success('已删除')
    selectedContacts.value = []
    await load()
  } catch (e: any) {
    if (e?.message) ElMessage.error(e.message)
  }
}

async function deleteSelectedContracts() {
  if (!selectedContracts.value.length) {
    ElMessage.error('请选择要删除的数据')
    return
  }
  try {
    await ElMessageBox.confirm('确认删除选中的合同信息？', '提示', { type: 'warning' })
    for (const row of selectedContracts.value) {
      if (row?.id) await unwrap(http.post(`/api/admin/crm/customers/contracts/${row.id}/delete`))
    }
    ElMessage.success('已删除')
    selectedContracts.value = []
    await load()
  } catch (e: any) {
    if (e?.message) ElMessage.error(e.message)
  }
}

async function deleteSelectedAfterSales() {
  if (!selectedAfterSales.value.length) {
    ElMessage.error('请选择要删除的数据')
    return
  }
  try {
    await ElMessageBox.confirm('确认删除选中的售后信息？', '提示', { type: 'warning' })
    for (const row of selectedAfterSales.value) {
      if (row?.id) await unwrap(http.post(`/api/admin/crm/customers/aftersales/${row.id}/delete`))
    }
    ElMessage.success('已删除')
    selectedAfterSales.value = []
    await load()
  } catch (e: any) {
    if (e?.message) ElMessage.error(e.message)
  }
}

async function deleteSelectedVisits() {
  if (!selectedVisits.value.length) {
    ElMessage.error('请选择要删除的数据')
    return
  }
  try {
    await ElMessageBox.confirm('确认删除选中的客户来访信息？', '提示', { type: 'warning' })
    for (const row of selectedVisits.value) {
      if (row?.id) await unwrap(http.post(`/api/admin/crm/customers/visits/${row.id}/delete`))
    }
    ElMessage.success('已删除')
    selectedVisits.value = []
    await load()
  } catch (e: any) {
    if (e?.message) ElMessage.error(e.message)
  }
}

async function saveContact() {
  if (isNew.value) return
  if (!contactForm.name) {
    ElMessage.error('姓名不能为空')
    return
  }
  subSaving.value = true
  try {
    const payload = { ...contactForm }
    await unwrap(http.post(`/api/admin/crm/customers/${id}/contact/upsert`, payload))
    ElMessage.success('保存成功')
    showAddContact.value = false
    resetContactForm()
    await load()
  } catch (e: any) {
    ElMessage.error(e?.message || '保存失败')
  } finally {
    subSaving.value = false
  }
}

async function saveContract() {
  if (isNew.value) return
  if (!contractForm.contractName || !contractForm.contractCode) {
    ElMessage.error('合同名称、合同编号为必填')
    return
  }
  subSaving.value = true
  try {
    const payload = { ...contractForm }
    await unwrap(http.post(`/api/admin/crm/customers/${id}/contract/upsert`, payload))
    ElMessage.success('保存成功')
    showAddContract.value = false
    resetContractForm()
    await load()
  } catch (e: any) {
    ElMessage.error(e?.message || '保存失败')
  } finally {
    subSaving.value = false
  }
}

async function saveAfterSale() {
  if (isNew.value) return
  subSaving.value = true
  try {
    const payload = { ...afterSaleForm }
    await unwrap(http.post(`/api/admin/crm/customers/${id}/aftersale/upsert`, payload))
    ElMessage.success('保存成功')
    showAddAfterSale.value = false
    resetAfterSaleForm()
    await load()
  } catch (e: any) {
    ElMessage.error(e?.message || '保存失败')
  } finally {
    subSaving.value = false
  }
}

async function saveVisit() {
  if (isNew.value) return
  subSaving.value = true
  try {
    const payload = { ...visitForm }
    await unwrap(http.post(`/api/admin/crm/customers/${id}/visit/upsert`, payload))
    ElMessage.success('保存成功')
    showAddVisit.value = false
    resetVisitForm()
    await load()
  } catch (e: any) {
    ElMessage.error(e?.message || '保存失败')
  } finally {
    subSaving.value = false
  }
}

function back() {
  router.back()
}

onMounted(async () => {
  await loadOptions()
  await load()
})
</script>
