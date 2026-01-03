export type ContractItem = {
  id: string
  type: string
  model: string
  productType: string
  subType: string
  productName: string
  remark: string
}

export type PaymentStage = {
  id: string
  stage: string
  payableAmount: string
  paidAmount: string
  stageName: string
  payDate: string
  actualPayDate?: string
  responsible?: string
  remark: string
}

export type ApprovalConfig = {
  areaManagers: string[]
  deptManagers: string[]
  leaders: string[]
  financeLeads: string[]
}

export type FlowStatus = {
  node: string
  operator: string
  status: string
  time: string
}

export type ContractProgress = {
  design: string
  production: string
  purchase: string
  manufacture: string
  assembly: string
}

export type ContractRecord = {
  id: string
  code: string
  name: string
  customerName: string
  signDate: string
  deliveryDate: string
  totalPrice: string
  remark: string
  info: Record<string, string>
  items: ContractItem[]
  paymentStages: PaymentStage[]
  approval?: ApprovalConfig
  flowStatus?: FlowStatus[]
  progress?: ContractProgress
  attachments?: string[]
  ratios?: Array<{ id: string; owner: string; area: string; ratio: number | null }>
  handler?: { dept: string; name: string; date: string }
}

const STORAGE_KEY = 'perm_contracts_v1'

function readStore(): ContractRecord[] {
  const raw = localStorage.getItem(STORAGE_KEY)
  if (!raw) return seedContracts()
  try {
    const parsed = JSON.parse(raw) as ContractRecord[]
    if (Array.isArray(parsed)) return parsed
  } catch {
    return seedContracts()
  }
  return seedContracts()
}

function writeStore(list: ContractRecord[]) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(list))
}

function seedContracts(): ContractRecord[] {
  const now = new Date().toISOString().slice(0, 10)
  const list: ContractRecord[] = [
    {
      id: createId(),
      code: '001',
      name: 'A合同',
      customerName: '客户A',
      signDate: now,
      deliveryDate: '',
      totalPrice: '120000',
      remark: '',
      info: {},
      items: [],
      paymentStages: [],
      progress: {
        design: '已完成',
        production: '进行中',
        purchase: '进行中',
        manufacture: '未开始',
        assembly: '未开始'
      }
    },
    {
      id: createId(),
      code: '002',
      name: 'B合同',
      customerName: '客户B',
      signDate: now,
      deliveryDate: '',
      totalPrice: '80000',
      remark: '',
      info: {},
      items: [],
      paymentStages: [],
      progress: {
        design: '未完成',
        production: '未开始',
        purchase: '未开始',
        manufacture: '未开始',
        assembly: '未开始'
      }
    }
  ]
  writeStore(list)
  return list
}

export function listContracts(): ContractRecord[] {
  return readStore()
}

export function getContract(id: string): ContractRecord | null {
  return readStore().find((item) => item.id === id) || null
}

export function upsertContract(record: ContractRecord): string {
  const list = readStore()
  const idx = list.findIndex((item) => item.id === record.id)
  if (idx >= 0) {
    list[idx] = record
  } else {
    list.unshift(record)
  }
  writeStore(list)
  return record.id
}

export function deleteContracts(ids: string[]): number {
  const list = readStore()
  const next = list.filter((item) => !ids.includes(item.id))
  writeStore(next)
  return list.length - next.length
}

export function ensureProgress(progress?: ContractProgress): ContractProgress {
  return (
    progress || {
      design: '未开始',
      production: '未开始',
      purchase: '未开始',
      manufacture: '未开始',
      assembly: '未开始'
    }
  )
}

export function createId(): string {
  return `c_${Date.now()}_${Math.random().toString(16).slice(2, 8)}`
}
