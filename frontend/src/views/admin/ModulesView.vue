<template>
  <div style="display: flex; gap: 12px">
    <div style="width: 360px">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px">
        <div style="font-weight: 600">模块树</div>
        <el-button type="primary" size="small" :disabled="!canAdd" @click="openCreateRoot">新增根模块</el-button>
      </div>
      <el-tree
        ref="treeRef"
        :data="tree"
        node-key="id"
        :props="{ label: 'cnName', children: 'children' }"
        :default-expand-all="true"
        @node-click="onSelect"
        @node-contextmenu="onContextMenu"
      />
      <el-dropdown
        v-if="ctx.open"
        :teleported="false"
        trigger="click"
        style="position: fixed"
        :style="{ left: ctx.x + 'px', top: ctx.y + 'px' }"
        @command="onCtxCommand"
      >
        <span />
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="addChild" :disabled="!canAdd">新增下级</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <div style="flex: 1">
      <div style="font-weight: 600; margin-bottom: 8px">模块信息</div>
      <el-form :model="form" label-width="110px">
        <el-form-item label="中文名称" required>
          <el-input v-model="form.cnName" />
        </el-form-item>
        <el-form-item label="英文名称" required>
          <el-input v-model="form.enName" />
        </el-form-item>
        <el-form-item label="菜单级数" required>
          <el-input-number v-model="form.level" :min="1" />
        </el-form-item>
        <el-form-item label="序号" required>
          <el-input-number v-model="form.orderNo" :min="0" />
        </el-form-item>
        <el-form-item label="链接路径">
          <el-input v-model="form.path" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="form.icon" />
        </el-form-item>
        <el-form-item label="分组">
          <el-input v-model="form.groupName" />
        </el-form-item>
        <el-form-item label="权限标识" required>
          <el-input v-model="form.permKey" />
        </el-form-item>
        <el-form-item label="是否父节点">
          <el-switch v-model="form.isParent" />
        </el-form-item>
        <el-form-item label="是否展开">
          <el-switch v-model="form.expanded" />
        </el-form-item>
      </el-form>

      <div style="display: flex; gap: 8px; margin-top: 8px">
        <el-button type="primary" :disabled="!canSave" @click="save">保存</el-button>
        <el-button type="danger" :disabled="!canRemove" @click="remove">删除</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { http, unwrap } from '@/lib/http'
import { useAuthStore } from '@/stores/auth'

const store = useAuthStore()
const canAdd = computed(() => store.has('au/module', 'add'))
const canUpdate = computed(() => store.has('au/module', 'update'))

const tree = ref<any[]>([])
const treeRef = ref()

const form = reactive({
  id: '',
  parentId: '' as string | '',
  cnName: '',
  enName: '',
  level: 1,
  orderNo: 0,
  icon: '' as string | '',
  groupName: '' as string | '',
  permKey: '',
  path: '' as string | '',
  isParent: false,
  expanded: true
})

const canSave = computed(() => (form.id ? canUpdate.value : canAdd.value))
const canRemove = computed(() => !!form.id && canUpdate.value)

const ctx = reactive({ open: false, x: 0, y: 0, node: null as any })

async function load() {
  try {
    tree.value = await unwrap<any[]>(http.get('/api/admin/modules/tree'))
  } catch (e: any) {
    ElMessage.error(e?.message || '加载失败')
  }
}

load()

function fillFromNode(node: any) {
  form.id = node.id
  form.parentId = node.parentId || ''
  form.cnName = node.cnName
  form.enName = node.enName
  form.level = node.level
  form.orderNo = node.orderNo
  form.icon = node.icon || ''
  form.groupName = node.groupName || ''
  form.permKey = node.permKey
  form.path = node.path || ''
  form.isParent = !!node.isParent
  form.expanded = !!node.expanded
}

function onSelect(node: any) {
  ctx.open = false
  fillFromNode(node)
}

function onContextMenu(event: MouseEvent, node: any) {
  event.preventDefault()
  ctx.open = true
  ctx.x = event.clientX
  ctx.y = event.clientY
  ctx.node = node
}

async function onCtxCommand(cmd: string) {
  ctx.open = false
  if (cmd !== 'addChild') return
  if (!ctx.node) return
  const parent = ctx.node
  form.id = ''
  form.parentId = parent.id
  form.cnName = ''
  form.enName = ''
  form.level = (parent.level || 1) + 1
  form.orderNo = 0
  form.icon = ''
  form.groupName = parent.groupName || ''
  form.permKey = ''
  form.path = ''
  form.isParent = false
  form.expanded = true
}

function openCreateRoot() {
  form.id = ''
  form.parentId = ''
  form.cnName = ''
  form.enName = ''
  form.level = 1
  form.orderNo = 0
  form.icon = ''
  form.groupName = ''
  form.permKey = ''
  form.path = ''
  form.isParent = true
  form.expanded = true
}

async function save() {
  if (!form.cnName || !form.enName || !form.permKey) {
    ElMessage.error('中文名称、英文名称、权限标识为必填')
    return
  }
  const payload = {
    cnName: form.cnName,
    enName: form.enName,
    level: form.level,
    orderNo: form.orderNo,
    icon: form.icon || null,
    groupName: form.groupName || null,
    permKey: form.permKey,
    path: form.path || null,
    parentId: form.parentId || null,
    isParent: form.isParent,
    expanded: form.expanded
  }
  try {
    if (!form.id) {
      await unwrap(http.post('/api/admin/modules', payload))
      ElMessage.success('已新增')
    } else {
      await unwrap(http.put(`/api/admin/modules/${form.id}`, payload))
      ElMessage.success('已保存')
    }
    load()
  } catch (e: any) {
    ElMessage.error(e?.message || '保存失败')
  }
}

async function remove() {
  if (!form.id) return
  try {
    await ElMessageBox.confirm('确认删除该模块？', '提示', { type: 'warning' })
    await unwrap(http.delete(`/api/admin/modules/${form.id}`))
    ElMessage.success('已删除')
    openCreateRoot()
    load()
  } catch (e: any) {
    if (e?.message) ElMessage.error(e.message)
  }
}
</script>
