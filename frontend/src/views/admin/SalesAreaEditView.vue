<template>
  <div style="max-width: 900px">
    <el-form :model="form" label-width="110px">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="片区编号" required>
            <el-select v-model="selectedAreaOptionId" clearable filterable placeholder="请选择" style="width: 100%">
              <el-option v-for="o in areaOptions" :key="o.id" :label="o.value || o.id" :value="o.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="片区名称" required>
            <el-select v-model="selectedAreaOptionId" clearable filterable placeholder="请选择" style="width: 100%">
              <el-option v-for="o in areaOptions" :key="o.id" :label="o.title" :value="o.id" />
            </el-select>
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="上级部门">
            <el-select v-model="form.deptOptionId" clearable filterable placeholder="请选择" style="width: 100%">
              <el-option v-for="o in deptOptions" :key="o.id" :label="o.title" :value="o.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="创建时间">
            <el-input v-model="form.createTime" disabled />
          </el-form-item>
        </el-col>

        <el-col :span="24">
          <el-form-item label="备注">
            <el-input v-model="form.remark" type="textarea" :rows="6" />
          </el-form-item>
        </el-col>
      </el-row>

      <div style="margin-top: 12px; display: flex; justify-content: center; gap: 12px">
        <el-button type="primary" @click="save">保存</el-button>
        <el-button @click="back">取消</el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { http, unwrap } from '@/lib/http'
import { ElMessage } from 'element-plus'
import { useEnterSave } from '@/lib/enterSave'

const route = useRoute()
const router = useRouter()
const id = route.params.id as string | undefined

type OptionItem = { id: string; title: string; value?: string }

const deptOptions = ref<OptionItem[]>([])
const areaOptions = ref<OptionItem[]>([])
const selectedAreaOptionId = ref<string>('')
const form = reactive({
  code: '',
  name: '',
  deptOptionId: '' as string | '',
  createTime: '',
  remark: ''
})

function formatDateTime(v: any) {
  if (!v) return ''
  const s = String(v)
  if (s.length >= 19) return s.slice(0, 19).replace('T', ' ')
  if (s.length >= 10) return s.slice(0, 10)
  return s
}

async function load() {
  try {
    ;[deptOptions.value, areaOptions.value] = await Promise.all([
      unwrap<OptionItem[]>(http.get('/api/public/options', { params: { groupKey: 'DEPT' } })),
      unwrap<OptionItem[]>(http.get('/api/public/options', { params: { groupKey: 'AREA' } }))
    ])
  } catch (e: any) {
    ElMessage.error(e?.message || '加载部门选项失败')
  }

  if (!id || id === 'new') return
  try {
    const data = await unwrap<any>(http.get(`/api/admin/crm/sales-areas/${id}`))
    form.code = data?.code || ''
    form.name = data?.name || ''
    const matched = areaOptions.value.find((o) => o.value === form.code) || areaOptions.value.find((o) => o.title === form.name)
    selectedAreaOptionId.value = matched?.id || ''
    form.deptOptionId = data?.deptOption?.id || ''
    form.createTime = formatDateTime(data?.createTime)
    form.remark = data?.remark || ''
  } catch (e: any) {
    ElMessage.error(e?.message || '加载失败')
  }
}

watch(selectedAreaOptionId, () => {
  const id = selectedAreaOptionId.value
  const opt = areaOptions.value.find((o) => o.id === id)
  if (!opt) return
  form.name = opt.title || ''
  form.code = opt.value || ''
})

async function save() {
  if (!form.code || !form.name) {
    ElMessage.error('请填写片区编号和片区名称')
    return
  }
  try {
    const payload: any = {
      id: id && id !== 'new' ? id : '',
      code: form.code,
      name: form.name,
      deptOptionId: form.deptOptionId || '',
      remark: form.remark
    }
    const newId = await unwrap<string>(http.post('/api/admin/crm/sales-areas/upsert', payload))
    ElMessage.success('保存成功')
    if (!id || id === 'new') router.replace(`/admin/areas/${newId}`)
  } catch (e: any) {
    ElMessage.error(e?.message || '保存失败')
  }
}

function back() {
  router.back()
}

onMounted(load)

useEnterSave(() => {
  save()
})
</script>
