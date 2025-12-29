<template>
  <div style="max-width: 420px; margin: 80px auto; padding: 24px">
    <el-card>
      <template #header>登录</template>
      <el-form :model="form" label-width="80px" @keyup.enter="onSubmit">
        <el-form-item label="账号">
          <el-input v-model="form.username" autocomplete="username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" autocomplete="current-password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="onSubmit">登录</el-button>
          <el-button link @click="goRegister">注册</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { http, unwrap } from '@/lib/http'
import type { LoginResponse } from '@/types/api'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const store = useAuthStore()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

async function onSubmit() {
  if (!form.username || !form.password) {
    ElMessage.error('请输入账号和密码')
    return
  }
  loading.value = true
  try {
    const data = await unwrap<LoginResponse>(http.post('/api/auth/login', form))
    store.setLogin(data)
    const firstPath = (() => {
      const stack = [...(data.menu || [])]
      while (stack.length) {
        const n: any = stack.shift()
        if (typeof n?.path === 'string' && n.path.startsWith('/')) return n.path
        if (Array.isArray(n?.children) && n.children.length) stack.unshift(...n.children)
      }
      return '/admin/users'
    })()
    router.replace(firstPath)
  } catch (e: any) {
    ElMessage.error(e?.message || '登录失败')
  } finally {
    loading.value = false
  }
}

function goRegister() {
  router.push('/register')
}
</script>
