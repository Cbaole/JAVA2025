<template>
  <div>
    <div style="font-weight: 600; margin-bottom: 12px">价格本管理</div>

    <el-tabs v-model="activeType" @tab-change="loadRows">
      <el-tab-pane label="单泵" name="DEVICE" />
      <el-tab-pane label="成套设备" name="PACKAGE" />
      <el-tab-pane label="备品备件" name="SPARE" />
    </el-tabs>

    <el-radio-group v-model="selectedId" style="width: 100%">
      <el-table
        :data="rows"
        border
        style="width: 100%"
        highlight-current-row
        :current-row-key="selectedId"
        row-key="id"
        @row-click="onRowClick"
      >
        <el-table-column label="" width="60" align="center">
          <template #default="{ row }">
            <el-radio :label="row.id"><span /></el-radio>
          </template>
        </el-table-column>
        <el-table-column prop="deptOption.title" label="部门" width="140" />
        <el-table-column prop="productName" label="产品名称" />
        <el-table-column prop="productModel" label="产品型号" />
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="price" label="价格" width="140" />
        <el-table-column prop="remark" label="备注" />
      </el-table>
    </el-radio-group>

    <div style="margin-top: 12px; display: flex; justify-content: center; gap: 10px">
      <el-button :disabled="!canAdd" @click="openCreate">添加行</el-button>
      <el-button type="danger" :disabled="!canUpdate || !selectedId" @click="removeSelected">删除</el-button>
      <el-button :disabled="!canUpdate || !selectedId" @click="openEditSelected">修改</el-button>
      <el-button type="primary" :disabled="!dlg.open" :loading="dlg.loading" @click="save">保存</el-button>
      <el-button :disabled="!dlg.open" @click="cancel">取消</el-button>
      <el-button @click="loadRows">刷新</el-button>
    </div>

    <el-dialog v-model="dlg.open" :title="dlg.mode === 'create' ? '新增价格' : '修改价格'">
      <el-form :model="dlg.form" label-width="90px">
        <el-form-item label="部门" required>
          <el-select v-model="dlg.form.deptOptionId" filterable clearable placeholder="请选择" style="width: 100%">
            <el-option v-for="o in deptOptions" :key="o.id" :label="o.title" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="产品" required>
          <el-select v-model="dlg.form.productId" filterable clearable placeholder="请选择" style="width: 100%">
            <el-option v-for="p in productOptions" :key="p.id" :label="p.label" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="dlg.form.quantity" :min="1" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input v-model="dlg.form.price" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="dlg.form.remark" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { http, unwrap } from '@/lib/http'
import { useAuthStore } from '@/stores/auth'

type OptionItem = { id: string; title: string }
type ProductItem = { id: string; name?: string; model?: string }

const store = useAuthStore()
const canAdd = computed(() => store.has('crm/price', 'add'))
const canUpdate = computed(() => store.has('crm/price', 'update'))

const activeType = ref<'DEVICE' | 'PACKAGE' | 'SPARE'>('DEVICE')
const rows = ref<any[]>([])
const selectedId = ref<string>('')

const deptOptions = ref<OptionItem[]>([])
const devices = ref<ProductItem[]>([])
const spares = ref<ProductItem[]>([])
const packages = ref<ProductItem[]>([])

const productOptions = computed(() => {
  const list = activeType.value === 'DEVICE' ? devices.value : activeType.value === 'SPARE' ? spares.value : packages.value
  return list.map((p) => ({
    id: p.id,
    label: `${p.name || ''}${p.model ? `（${p.model}）` : ''}`
  }))
})

const dlg = reactive({
  open: false,
  loading: false,
  mode: 'create' as 'create' | 'edit',
  form: {
    id: '',
    deptOptionId: '',
    productId: '',
    quantity: 1,
    price: '',
    remark: ''
  }
})

function onRowClick(row: any) {
  if (row?.id) selectedId.value = row.id
}

async function loadOptions() {
  try {
    deptOptions.value = await unwrap<OptionItem[]>(http.get('/api/public/options', { params: { groupKey: 'DEPT' } }))
  } catch (e: any) {
    ElMessage.error(e?.message || '加载部门选项失败')
  }
}

async function loadProducts() {
  try {
    const [d, s, p] = await Promise.all([
      unwrap<ProductItem[]>(http.get('/api/admin/crm/devices')),
      unwrap<ProductItem[]>(http.get('/api/admin/crm/spare-parts')),
      unwrap<ProductItem[]>(http.get('/api/admin/crm/packages'))
    ])
    devices.value = d || []
    spares.value = s || []
    packages.value = p || []
  } catch (e: any) {
    ElMessage.error(e?.message || '加载产品失败')
  }
}

async function loadRows() {
  try {
    rows.value = await unwrap<any[]>(http.get('/api/admin/crm/price-book', { params: { type: activeType.value } }))
    if (selectedId.value && !rows.value.some((it) => it.id === selectedId.value)) {
      selectedId.value = ''
    }
  } catch (e: any) {
    ElMessage.error(e?.message || '加载失败')
  }
}

function resetDlg() {
  dlg.form.id = ''
  dlg.form.deptOptionId = ''
  dlg.form.productId = ''
  dlg.form.quantity = 1
  dlg.form.price = ''
  dlg.form.remark = ''
}

function openCreate() {
  dlg.mode = 'create'
  resetDlg()
  dlg.open = true
}

function openEditSelected() {
  const row = rows.value.find((it) => it.id === selectedId.value)
  if (!row) return
  dlg.mode = 'edit'
  dlg.form.id = row.id
  dlg.form.deptOptionId = row?.deptOption?.id || ''
  dlg.form.productId = row?.productId || ''
  dlg.form.quantity = typeof row?.quantity === 'number' ? row.quantity : row?.quantity ? Number(row.quantity) : 1
  dlg.form.price = row?.price || ''
  dlg.form.remark = row?.remark || ''
  dlg.open = true
}

async function removeSelected() {
  if (!selectedId.value) return
  try {
    await ElMessageBox.confirm('确认删除该价格记录？', '提示', { type: 'warning' })
    await unwrap(http.post(`/api/admin/crm/price-book/${selectedId.value}/delete`))
    ElMessage.success('已删除')
    selectedId.value = ''
    await loadRows()
  } catch (e: any) {
    if (e?.message) ElMessage.error(e.message)
  }
}

async function save() {
  if (!dlg.open) return
  if (!dlg.form.deptOptionId) {
    ElMessage.error('请选择部门')
    return
  }
  if (!dlg.form.productId) {
    ElMessage.error('请选择产品')
    return
  }
  dlg.loading = true
  try {
    const payload: any = {
      id: dlg.mode === 'edit' ? dlg.form.id : '',
      productType: activeType.value,
      deptOptionId: dlg.form.deptOptionId,
      productId: dlg.form.productId,
      quantity: dlg.form.quantity,
      price: dlg.form.price,
      remark: dlg.form.remark
    }
    await unwrap<string>(http.post('/api/admin/crm/price-book/upsert', payload))
    ElMessage.success('保存成功')
    dlg.open = false
    await loadRows()
  } catch (e: any) {
    ElMessage.error(e?.message || '保存失败')
  } finally {
    dlg.loading = false
  }
}

function cancel() {
  dlg.open = false
}

watch(activeType, () => {
  selectedId.value = ''
  if (dlg.open) dlg.open = false
})

onMounted(async () => {
  await Promise.all([loadOptions(), loadProducts()])
  await loadRows()
})
</script>

