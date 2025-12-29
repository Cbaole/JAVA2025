<template>
  <div style="display: flex; gap: 16px">
    <div style="width: 420px">
      <div style="display: flex; gap: 8px; align-items: center; margin-bottom: 8px">
        <div style="font-weight: 600">权限配置</div>
        <el-select v-model="state.roleId" placeholder="选择角色" style="width: 220px" @change="load">
          <el-option v-for="r in roles" :key="r.id" :label="r.roleName" :value="r.id" />
        </el-select>
        <el-button type="primary" :disabled="!canUpdate || !state.roleId" @click="save">保存</el-button>
      </div>

      <el-tree
        ref="treeRef"
        :data="tree"
        node-key="id"
        show-checkbox
        :check-strictly="true"
        :props="{ label: 'cnName', children: 'children' }"
        :default-expand-all="true"
        @check="onCheck"
        @node-click="onSelect"
      />
    </div>

    <div style="flex: 1">
      <div style="font-weight: 600; margin-bottom: 8px">操作权限（选中模块）</div>
      <div v-if="!state.currentModuleId" style="color: #666">请选择左侧模块</div>
      <div v-else>
        <el-form label-width="90px">
          <el-form-item label="可浏览">
            <el-switch v-model="currentPerm.canSee" />
          </el-form-item>
          <el-form-item label="可新增">
            <el-switch v-model="currentPerm.canAdd" />
          </el-form-item>
          <el-form-item label="可修改">
            <el-switch v-model="currentPerm.canUpdate" />
          </el-form-item>
          <el-form-item label="可阅读">
            <el-switch v-model="currentPerm.canRead" />
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { http, unwrap } from '@/lib/http'
import { useAuthStore } from '@/stores/auth'

const store = useAuthStore()
const canUpdate = computed(() => store.has('au/perm', 'update'))

const roles = ref<any[]>([])
const tree = ref<any[]>([])
const treeRef = ref()

const state = reactive({
  roleId: '',
  currentModuleId: '',
  permByModuleId: {} as Record<string, { moduleId: string; canRead: boolean; canAdd: boolean; canUpdate: boolean; canSee: boolean }>
})

const currentPerm = computed(() => {
  if (!state.currentModuleId) {
    return { moduleId: '', canRead: false, canAdd: false, canUpdate: false, canSee: false }
  }
  state.permByModuleId[state.currentModuleId] ||= {
    moduleId: state.currentModuleId,
    canRead: false,
    canAdd: false,
    canUpdate: false,
    canSee: false
  }
  return state.permByModuleId[state.currentModuleId]
})

async function init() {
  try {
    roles.value = await unwrap<any[]>(http.get('/api/admin/roles'))
    tree.value = await unwrap<any[]>(http.get('/api/admin/modules/tree'))
  } catch (e: any) {
    ElMessage.error(e?.message || '加载失败')
  }
}

init()

async function load() {
  if (!state.roleId) return
  try {
    const map = await unwrap<Record<string, any>>(http.get('/api/admin/role-perms', { params: { roleId: state.roleId } }))
    state.permByModuleId = {}
    for (const k of Object.keys(map)) {
      state.permByModuleId[k] = map[k]
    }
    const checked = Object.values(state.permByModuleId)
      .filter((x) => x.canSee)
      .map((x) => x.moduleId)
    treeRef.value?.setCheckedKeys(checked)
  } catch (e: any) {
    ElMessage.error(e?.message || '加载权限失败')
  }
}

function onSelect(node: any) {
  state.currentModuleId = node.id
}

function onCheck(node: any, info: any) {
  const checked = info.checkedKeys as string[]
  const moduleId = node.id as string
  const isChecked = checked.includes(moduleId)
  state.permByModuleId[moduleId] ||= { moduleId, canRead: false, canAdd: false, canUpdate: false, canSee: false }
  state.permByModuleId[moduleId].canSee = isChecked
}

async function save() {
  if (!state.roleId) return
  const items = Object.values(state.permByModuleId)
  try {
    await unwrap(http.post('/api/admin/role-perms/batch', { roleId: state.roleId, items }))
    ElMessage.success('已保存')
  } catch (e: any) {
    ElMessage.error(e?.message || '保存失败')
  }
}
</script>

