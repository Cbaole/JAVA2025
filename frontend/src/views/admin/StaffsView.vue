<template>
  <div>
    <el-tabs v-model="tab" type="card">
      <el-tab-pane label="营销人员基本信息" name="base">
        <div style="margin-bottom: 12px; display: flex; gap: 8px; align-items: center">
          <el-input v-model="q" placeholder="姓名/电话" style="width: 240px" />
          <el-button type="primary" @click="loadAll">刷新</el-button>
        </div>

        <el-table
          :data="baseFiltered"
          border
          style="width: 100%"
          highlight-current-row
          row-key="id"
          :current-row-key="baseSelectedId"
          @current-change="onBaseCurrentChange"
        >
          <el-table-column prop="name" label="姓名" />
          <el-table-column label="性别" width="100">
            <template #default="{ row }">{{ genderText(row?.gender) }}</template>
          </el-table-column>
          <el-table-column prop="birthday" label="生日" width="140" />
          <el-table-column label="负责区域">
            <template #default="{ row }">{{ row?.areaOption?.title || '-' }}</template>
          </el-table-column>
          <el-table-column label="职务">
            <template #default="{ row }">{{ row?.postOption?.title || '-' }}</template>
          </el-table-column>
          <el-table-column prop="phone" label="联系方式" width="160" />
          <el-table-column prop="remark" label="备注" />
        </el-table>

        <div style="margin-top: 12px; display: flex; justify-content: center">
          <el-button :disabled="!baseSelectedId" @click="openEdit">修改</el-button>
        </div>
      </el-tab-pane>

      <el-tab-pane label="片区人员调动" name="move">
        <div style="display: flex; gap: 12px; height: calc(100vh - 210px); min-height: 420px">
          <div style="width: 240px; border: 1px solid #eee; padding: 8px; overflow: auto">
            <el-tree
              :data="areaTree"
              node-key="id"
              :props="{ label: 'label', children: 'children' }"
              :default-expand-all="true"
              @node-click="onTreeClick"
            />
          </div>

          <div style="flex: 1; display: flex; flex-direction: column; overflow: hidden">
            <div style="margin-bottom: 12px; display: flex; gap: 8px; align-items: center">
              <el-input v-model="qMove" placeholder="姓名/电话/工号" style="width: 260px" />
              <el-button type="primary" @click="loadAll">刷新</el-button>
              <el-button :disabled="moveSelected.length === 0" @click="openMoveDialog">调动人员</el-button>
            </div>

            <el-table
              :data="moveFiltered"
              border
              style="width: 100%"
              row-key="id"
              height="100%"
              @selection-change="onMoveSel"
            >
              <el-table-column type="selection" width="50" />
              <el-table-column prop="name" label="姓名" />
              <el-table-column prop="staffNo" label="工号" width="120" />
              <el-table-column prop="phone" label="联系方式" width="160" />
              <el-table-column label="职务">
                <template #default="{ row }">{{ row?.postOption?.title || '-' }}</template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <el-dialog v-model="showMove" title="选择调动片区" width="420px">
          <el-form :model="moveForm" label-width="90px">
            <el-form-item label="负责区域" required>
              <el-select v-model="moveForm.areaOptionId" clearable filterable placeholder="请选择" style="width: 100%">
                <el-option v-for="o in areaOptions" :key="o.id" :label="o.title" :value="o.id" />
              </el-select>
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="showMove = false">返回</el-button>
            <el-button type="primary" @click="confirmMove">确定</el-button>
          </template>
        </el-dialog>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="editDlg.open" title="修改营销人员信息" width="560px">
      <el-form :model="editDlg.form" label-width="90px">
        <el-form-item label="姓名" required>
          <el-input v-model="editDlg.form.name" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="editDlg.form.gender" clearable placeholder="请选择" style="width: 100%">
            <el-option label="未知" value="UNKNOWN" />
            <el-option label="男" value="MALE" />
            <el-option label="女" value="FEMALE" />
          </el-select>
        </el-form-item>
        <el-form-item label="生日">
          <el-date-picker v-model="editDlg.form.birthday" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="联系方式" required>
          <el-input v-model="editDlg.form.phone" />
        </el-form-item>
        <el-form-item label="所属片区">
          <el-select v-model="editDlg.form.areaOptionId" clearable filterable placeholder="请选择" style="width: 100%">
            <el-option v-for="o in areaOptions" :key="o.id" :label="o.title" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="职务">
          <el-select v-model="editDlg.form.postOptionId" clearable filterable placeholder="请选择" style="width: 100%">
            <el-option v-for="o in postOptions" :key="o.id" :label="o.title" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="工号">
          <el-input v-model="editDlg.form.staffNo" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="editDlg.form.remark" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDlg.open = false">取消</el-button>
        <el-button type="primary" :loading="editDlg.loading" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { http, unwrap } from '@/lib/http'
import { ElMessage } from 'element-plus'
import { useEnterSave } from '@/lib/enterSave'

type OptionItem = { id: string; title: string }

const tab = ref<'base' | 'move'>('base')
const q = ref('')
const qMove = ref('')

const staffList = ref<any[]>([])
const baseSelectedId = ref<string>('')

const areaOptions = ref<OptionItem[]>([])
const postOptions = ref<OptionItem[]>([])

const salesAreas = ref<any[]>([])
const areaTree = ref<any[]>([])
const filterAreaOptionIds = ref<string[]>([])

const moveSelected = ref<any[]>([])
const showMove = ref(false)
const moveForm = reactive<{ areaOptionId: string | '' }>({ areaOptionId: '' })

const editDlg = reactive({
  open: false,
  loading: false,
  form: {
    id: '',
    name: '',
    gender: 'UNKNOWN',
    birthday: '',
    phone: '',
    areaOptionId: '' as string | '',
    postOptionId: '' as string | '',
    staffNo: '',
    remark: ''
  }
})

const baseFiltered = computed(() => {
  const v = q.value.trim()
  if (!v) return staffList.value
  return staffList.value.filter((it) => it.name?.includes(v) || it.phone?.includes(v))
})

const moveFiltered = computed(() => {
  const v = qMove.value.trim()
  let rows = staffList.value
  if (filterAreaOptionIds.value.length) {
    const ids = new Set(filterAreaOptionIds.value)
    rows = rows.filter((it) => ids.has(it?.areaOption?.id))
  }
  if (!v) return rows
  return rows.filter((it) => it.name?.includes(v) || it.phone?.includes(v) || it.staffNo?.includes(v))
})

function genderText(v: any) {
  if (v === 'MALE') return '男'
  if (v === 'FEMALE') return '女'
  return '未知'
}

function buildTree() {
  const byDept = new Map<string, any[]>()
  for (const it of salesAreas.value || []) {
    const deptTitle = it?.deptOption?.title || '未分组'
    const arr = byDept.get(deptTitle) || []
    arr.push(it)
    byDept.set(deptTitle, arr)
  }

  const nodes: any[] = []
  const areaTitleToId = new Map<string, string>()
  for (const o of areaOptions.value) areaTitleToId.set((o.title || '').trim(), o.id)

  for (const [deptTitle, items] of byDept.entries()) {
    nodes.push({
      id: `dept:${deptTitle}`,
      label: deptTitle,
      type: 'dept',
      children: (items || []).map((a) => {
        const name = String(a?.name || '').trim()
        return {
          id: `area:${a?.id}`,
          label: name || '-',
          type: 'area',
          areaOptionId: areaTitleToId.get(name) || ''
        }
      })
    })
  }
  areaTree.value = nodes
}

async function loadOptions() {
  const loadGroup = async (groupKey: string) => unwrap<OptionItem[]>(http.get('/api/public/options', { params: { groupKey } }))
  try {
    ;[areaOptions.value, postOptions.value] = await Promise.all([loadGroup('AREA'), loadGroup('POST')])
  } catch (e: any) {
    ElMessage.error(e?.message || '加载选项失败')
  }
}

async function loadStaffs() {
  staffList.value = await unwrap(http.get('/api/admin/crm/staffs'))
  if (baseSelectedId.value && !staffList.value.some((it) => it.id === baseSelectedId.value)) {
    baseSelectedId.value = ''
  }
}

async function loadSalesAreas() {
  salesAreas.value = await unwrap(http.get('/api/admin/crm/sales-areas'))
}

async function loadAll() {
  try {
    await Promise.all([loadOptions(), loadStaffs(), loadSalesAreas()])
    buildTree()
  } catch (e: any) {
    ElMessage.error(e?.message || '加载失败')
  }
}

function onBaseCurrentChange(row: any) {
  baseSelectedId.value = row?.id || ''
}

function openEdit() {
  const row = staffList.value.find((it) => it.id === baseSelectedId.value)
  if (!row) return
  editDlg.open = true
  editDlg.form.id = row.id
  editDlg.form.name = row?.name || ''
  editDlg.form.gender = row?.gender || 'UNKNOWN'
  editDlg.form.birthday = row?.birthday || ''
  editDlg.form.phone = row?.phone || ''
  editDlg.form.areaOptionId = row?.areaOption?.id || ''
  editDlg.form.postOptionId = row?.postOption?.id || ''
  editDlg.form.staffNo = row?.staffNo || ''
  editDlg.form.remark = row?.remark || ''
}

async function saveEdit() {
  if (!editDlg.form.id || !editDlg.form.name || !editDlg.form.phone) {
    ElMessage.error('请填写必填项')
    return
  }
  editDlg.loading = true
  try {
    await unwrap(
      http.post('/api/admin/crm/staffs/upsert', {
        id: editDlg.form.id,
        name: editDlg.form.name,
        gender: editDlg.form.gender || '',
        birthday: editDlg.form.birthday || '',
        phone: editDlg.form.phone,
        areaOptionId: editDlg.form.areaOptionId || '',
        postOptionId: editDlg.form.postOptionId || '',
        staffNo: editDlg.form.staffNo || '',
        remark: editDlg.form.remark || ''
      })
    )
    ElMessage.success('保存成功')
    editDlg.open = false
    await loadStaffs()
  } catch (e: any) {
    ElMessage.error(e?.message || '保存失败')
  } finally {
    editDlg.loading = false
  }
}

function onMoveSel(rows: any[]) {
  moveSelected.value = rows
}

function openMoveDialog() {
  if (!moveSelected.value.length) {
    ElMessage.error('请选择要调动的人员')
    return
  }
  showMove.value = true
}

async function confirmMove() {
  if (!moveSelected.value.length) {
    ElMessage.error('请选择要调动的人员')
    return
  }
  const ids = moveSelected.value.map((it) => it.id)
  try {
    await unwrap(http.post('/api/admin/crm/staffs/move', { ids, areaOptionId: moveForm.areaOptionId || '' }))
    ElMessage.success('调动成功')
    showMove.value = false
    moveForm.areaOptionId = ''
    moveSelected.value = []
    await loadStaffs()
  } catch (e: any) {
    ElMessage.error(e?.message || '调动失败')
  }
}

function onTreeClick(node: any) {
  if (node?.type === 'area') {
    filterAreaOptionIds.value = node?.areaOptionId ? [node.areaOptionId] : []
    return
  }
  if (node?.type === 'dept') {
    const ids: string[] = []
    for (const c of node?.children || []) {
      if (c?.areaOptionId) ids.push(c.areaOptionId)
    }
    filterAreaOptionIds.value = ids
    return
  }
  filterAreaOptionIds.value = []
}

onMounted(loadAll)

useEnterSave(() => {
  if (!editDlg.open) return
  saveEdit()
})
</script>
