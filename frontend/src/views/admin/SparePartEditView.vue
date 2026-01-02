<template>
  <div style="max-width: 980px">
    <div style="border: 1px solid #eee; padding: 16px">
      <el-form :model="form" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="名称" required>
              <el-input v-model="form.name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="型号">
              <el-input v-model="form.model" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="参数">
              <el-input v-model="form.params" type="textarea" :rows="3" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="价格">
              <el-input v-model="form.price" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="重量">
              <el-input v-model="form.weight" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="交货周期">
              <el-input v-model="form.leadTime" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="附件">
              <el-input :model-value="attachText" disabled style="width: 260px" />
              <el-button style="margin-left: 8px" :disabled="!canUpload" @click="pickFile">附件上传</el-button>
              <input ref="fileRef" type="file" multiple style="display: none" @change="onFiles" />
            </el-form-item>
          </el-col>
          <el-col :span="12" />

          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" :rows="6" />
            </el-form-item>
          </el-col>
        </el-row>

        <div style="margin-top: 12px; display: flex; justify-content: center; gap: 12px">
          <el-button type="primary" :loading="saving" @click="save">保存</el-button>
          <el-button @click="back">取消</el-button>
        </div>
      </el-form>
    </div>

    <div v-if="id && id !== 'new'" style="margin-top: 12px">
      <el-table :data="attachments" border>
        <el-table-column prop="fileName" label="附件" />
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button size="small" @click="download(row)">下载</el-button>
            <el-button size="small" type="danger" @click="removeAttachment(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { http, unwrap } from '@/lib/http'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const id = route.params.id as string | undefined
const form = ref<any>({})
const attachments = ref<any[]>([])
const fileRef = ref<HTMLInputElement | null>(null)
const saving = ref(false)

async function load() {
  if (!id || id === 'new') return
  try {
    const data: any = await unwrap(http.get(`/api/admin/crm/spare-parts/${id}`))
    form.value = data.base
    attachments.value = data.attachments || []
  } catch (e: any) {
    ElMessage.error(e?.message || '加载失败')
  }
}

async function save() {
  if (!form.value?.name) {
    ElMessage.error('请填写名称')
    return
  }
  saving.value = true
  const payload = { ...form.value }
  payload.id = id && id !== 'new' ? id : ''
  try {
    const newId = await unwrap<string>(http.post('/api/admin/crm/spare-parts/upsert', payload))
    ElMessage.success('保存成功')
    if (!id || id === 'new') {
      router.replace(`/admin/spare-parts/${newId}`)
      return
    }
    await load()
  } catch (e: any) {
    ElMessage.error(e?.message || '保存失败')
  } finally {
    saving.value = false
  }
}

const canUpload = computed(() => !!id && id !== 'new')
const attachText = computed(() => `已上传${attachments.value.length}个`)

function pickFile() {
  fileRef.value?.click()
}

async function onFiles(e: Event) {
  const el = e.target as HTMLInputElement
  const files = el.files ? Array.from(el.files) : []
  if (!files.length || !id || id === 'new') return
  try {
    for (const file of files) {
      const fd = new FormData()
      fd.append('file', file)
      await unwrap(
        http.post(`/api/admin/crm/spare-parts/${id}/attachments/upload`, fd, { headers: { 'Content-Type': 'multipart/form-data' } })
      )
    }
    ElMessage.success('上传成功')
    await load()
  } catch (err: any) {
    ElMessage.error(err?.message || '上传失败')
  } finally {
    el.value = ''
  }
}

async function download(row: any) {
  if (!row?.id) return
  try {
    const resp = await http.get(`/api/admin/crm/spare-parts/attachments/${row.id}/download`, { responseType: 'blob' })
    const blob = resp.data as Blob
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = row?.fileName || 'attachment'
    document.body.appendChild(a)
    a.click()
    a.remove()
    URL.revokeObjectURL(url)
  } catch (e: any) {
    ElMessage.error(e?.message || '下载失败')
  }
}

async function removeAttachment(row: any) {
  if (!row?.id) return
  try {
    await ElMessageBox.confirm('确认删除该附件？', '提示', { type: 'warning' })
    await unwrap(http.post(`/api/admin/crm/spare-parts/attachments/${row.id}/delete`))
    ElMessage.success('已删除')
    await load()
  } catch (e: any) {
    if (e?.message) ElMessage.error(e.message)
  }
}

function back() {
  router.back()
}

onMounted(load)
</script>
