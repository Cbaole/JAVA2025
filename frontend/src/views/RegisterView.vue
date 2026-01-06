<template>
  <div style="max-width: 760px; margin: 40px auto; padding: 24px">
    <el-card>
      <template #header>用户注册</template>

      <el-form :model="checkForm" label-width="110px">
        <el-form-item label="身份证号">
          <el-input v-model="checkForm.idCard" placeholder="18位身份证号" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="checking" @click="onCheck">验证</el-button>
          <el-button link @click="goLogin">返回登录</el-button>
        </el-form-item>
      </el-form>

      <el-divider />

      <el-alert v-if="checkResult && checkResult.registered" type="warning" :closable="false" show-icon>
        <template #title>该身份证号已注册</template>
        <template #default>
          姓名：{{ checkResult.name }}，账号：{{ checkResult.username || '-' }}
        </template>
      </el-alert>

      <el-form v-else :model="form" label-width="110px" style="margin-top: 16px">
        <el-form-item label="姓名" required>
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="身份证号" required>
          <el-input v-model="form.idCard" @blur="syncFromIdCard" />
        </el-form-item>
        <el-form-item label="手机号" required>
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender" placeholder="请选择性别" style="width: 240px">
            <el-option label="男" value="MALE" />
            <el-option label="女" value="FEMALE" />
            <el-option label="未知" value="UNKNOWN" />
          </el-select>
        </el-form-item>
        <el-form-item label="出生日期">
          <el-input v-model="derived.birthdayText" readonly />
        </el-form-item>
        <el-form-item label="年龄">
          <el-input v-model="derived.ageText" readonly />
        </el-form-item>
        <el-form-item label="岗位">
          <el-select v-model="form.postOptionId" placeholder="请选择岗位" style="width: 240px" @visible-change="onPostVisible">
            <el-option v-for="o in postOptions" :key="o.id" :label="o.title" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属片区">
          <el-select v-model="form.areaOptionId" placeholder="请选择片区" style="width: 240px" @visible-change="onAreaVisible">
            <el-option v-for="o in areaOptions" :key="o.id" :label="o.title" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="onSubmit">提交注册</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { http, unwrap } from '@/lib/http'
import type { RegisterCheckResponse } from '@/types/api'

type OptionItem = { id: string; groupKey: string; title: string; value: string; orderNo: number }

const router = useRouter()
const checking = ref(false)
const submitting = ref(false)

const checkForm = reactive({ idCard: '' })
const checkResult = ref<RegisterCheckResponse | null>(null)

const postOptions = ref<OptionItem[]>([])
const areaOptions = ref<OptionItem[]>([])

const form = reactive({
  name: '',
  idCard: '',
  phone: '',
  birthday: '' as string | '',
  gender: '' as string | 'MALE' | 'FEMALE' | 'UNKNOWN',
  postOptionId: '' as string | '',
  areaOptionId: '' as string | ''
})

function parseIdCard18(idCard: string) {
  const v = (idCard || '').trim()
  if (!v || v.length !== 18) return null
  const birth = v.slice(6, 14)
  const y = birth.slice(0, 4)
  const m = birth.slice(4, 6)
  const d = birth.slice(6, 8)
  const birthday = `${y}-${m}-${d}`
  const genderCode = Number(v.charAt(16))
  const gender = Number.isFinite(genderCode) ? (genderCode % 2 === 0 ? 'FEMALE' : 'MALE') : 'UNKNOWN'
  return { birthday, gender }
}

function calcAge(birthday: string) {
  const dt = new Date(birthday)
  if (Number.isNaN(dt.getTime())) return ''
  const now = new Date()
  let age = now.getFullYear() - dt.getFullYear()
  const m = now.getMonth() - dt.getMonth()
  if (m < 0 || (m === 0 && now.getDate() < dt.getDate())) age--
  return String(Math.max(age, 0))
}

const derived = computed(() => {
  const parsed = parseIdCard18(form.idCard)
  const birthday = parsed?.birthday || ''
  const gender = form.gender || parsed?.gender || ''
  const genderText = gender === 'MALE' ? '男' : gender === 'FEMALE' ? '女' : ''
  const ageText = birthday ? calcAge(birthday) : ''
  return {
    birthdayText: birthday,
    genderText,
    ageText
  }
})

function syncFromIdCard() {
  const parsed = parseIdCard18(form.idCard)
  if (!parsed) return
  form.birthday = parsed.birthday
  form.gender = parsed.gender
}

async function loadOptions() {
  try {
    postOptions.value = await unwrap<OptionItem[]>(http.get('/api/public/options', { params: { groupKey: 'POST' } }))
    areaOptions.value = await unwrap<OptionItem[]>(http.get('/api/public/options', { params: { groupKey: 'AREA' } }))
  } catch (e: any) {
    ElMessage.error(e?.message || '加载选项失败')
  }
}

function onPostVisible(open: boolean) {
  if (open && postOptions.value.length === 0) loadOptions()
}

function onAreaVisible(open: boolean) {
  if (open && areaOptions.value.length === 0) loadOptions()
}

onMounted(loadOptions)

async function onCheck() {
  const idCard = (checkForm.idCard || '').trim()
  if (!idCard) {
    ElMessage.error('请输入身份证号')
    return
  }
  checking.value = true
  try {
    if (postOptions.value.length === 0 || areaOptions.value.length === 0) {
      await loadOptions()
    }
    const data = await unwrap<RegisterCheckResponse>(http.get('/api/register/check-idcard', { params: { idCard } }))
    checkResult.value = data
    if (!data.registered) {
      form.idCard = idCard
      syncFromIdCard()
    }
  } catch (e: any) {
    ElMessage.error(e?.message || '验证失败')
  } finally {
    checking.value = false
  }
}

async function onSubmit() {
  if (!form.name || !form.idCard || !form.phone) {
    ElMessage.error('姓名、身份证号、手机号为必填')
    return
  }
  submitting.value = true
  try {
    await unwrap<string>(
      http.post('/api/register', {
        name: form.name,
        idCard: form.idCard,
        phone: form.phone,
        birthday: form.birthday || null,
        gender: form.gender || null,
        postOptionId: form.postOptionId || null,
        areaOptionId: form.areaOptionId || null
      })
    )
    ElMessage.success('注册提交成功，请等待管理员审核')
    router.replace('/login')
  } catch (e: any) {
    ElMessage.error(e?.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

function goLogin() {
  router.push('/login')
}
</script>
