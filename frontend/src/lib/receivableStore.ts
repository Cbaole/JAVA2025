import { getContract, listContracts, upsertContract, type ContractRecord, type PaymentStage } from '@/lib/contractStore'

export type ReceiptRecord = {
  id: string
  contractId: string
  contractCode?: string
  contractName?: string
  receiveDate: string
  amount: string
  receiver: string
  remark?: string
}

export type ReceivableRow = {
  id: string
  receivableCode: string
  contractId: string
  contractCode: string
  contractName: string
  companyName: string
  companyCode: string
  item: string
  amount: string
  planDate: string
  actualDate: string
  status: string
  paidAmount: string
  responsible: string
}

const RECEIPT_KEY = 'perm_receipts_v1'

function readReceipts(): ReceiptRecord[] {
  const raw = localStorage.getItem(RECEIPT_KEY)
  if (!raw) return []
  try {
    const parsed = JSON.parse(raw) as ReceiptRecord[]
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

function writeReceipts(list: ReceiptRecord[]) {
  localStorage.setItem(RECEIPT_KEY, JSON.stringify(list))
}

export function listReceipts(): ReceiptRecord[] {
  return readReceipts()
}

export function getReceipt(id: string): ReceiptRecord | null {
  return readReceipts().find((item) => item.id === id) || null
}

export function upsertReceipt(record: ReceiptRecord): string {
  const list = readReceipts()
  const idx = list.findIndex((item) => item.id === record.id)
  if (idx >= 0) {
    list[idx] = record
  } else {
    list.unshift(record)
  }
  writeReceipts(list)
  syncContractReceipts(record.contractId)
  return record.id
}

export function deleteReceipts(ids: string[]): number {
  const list = readReceipts()
  const removed = list.filter((item) => ids.includes(item.id))
  const next = list.filter((item) => !ids.includes(item.id))
  writeReceipts(next)
  removed.forEach((item) => syncContractReceipts(item.contractId))
  return list.length - next.length
}

export function buildReceivableRows(): ReceivableRow[] {
  const contracts = listContracts()
  const receipts = listReceipts()
  return contracts.flatMap((contract) => {
    const stageRows = buildContractRows(contract, receipts)
    return stageRows.map((row) => ({
      ...row,
      contractId: contract.id,
      contractCode: contract.code,
      contractName: contract.name,
      companyName: contract.customerName,
      companyCode: contract.info?.companyCode || ''
    }))
  })
}

export function buildContractRows(contract: ContractRecord, receipts: ReceiptRecord[]): ReceivableRow[] {
  const stages = contract.paymentStages || []
  const allocations = allocateReceipts(stages, receipts.filter((item) => item.contractId === contract.id))
  return stages.map((stage, index) => {
    const alloc = allocations.get(stage.id) || { paidAmount: 0, actualDate: '' }
    const amount = toAmount(stage.payableAmount)
    const paidAmount = alloc.paidAmount
    const isPaid = amount > 0 && paidAmount >= amount
    return {
      id: stage.id,
      receivableCode: buildReceivableCode(contract.code, index + 1),
      contractId: contract.id,
      contractCode: contract.code,
      contractName: contract.name,
      companyName: contract.customerName,
      companyCode: contract.info?.companyCode || '',
      item: stage.stageName || stage.stage || `阶段${index + 1}`,
      amount: amount ? String(amount) : '',
      planDate: stage.payDate || '',
      actualDate: alloc.actualDate,
      status: isPaid ? '已收' : '应收',
      paidAmount: paidAmount ? String(paidAmount) : '',
      responsible: stage.responsible || ''
    }
  })
}

export function summarizeContractReceivable(contractId: string) {
  const contract = getContract(contractId)
  if (!contract) return null
  const rows = buildContractRows(contract, listReceipts())
  const totalAmount = rows.reduce((sum, row) => sum + toAmount(row.amount), 0)
  const totalPaid = rows.reduce((sum, row) => sum + toAmount(row.paidAmount), 0)
  return {
    contract,
    rows,
    totalAmount,
    totalPaid,
    outstanding: Math.max(0, totalAmount - totalPaid)
  }
}

export function listOverdueReceivables(): ReceivableRow[] {
  const today = new Date().toISOString().slice(0, 10)
  return buildReceivableRows().filter((row) => row.planDate && row.planDate < today && row.status !== '已收')
}

export function createReceiptId(): string {
  return `r_${Date.now()}_${Math.random().toString(16).slice(2, 8)}`
}

function syncContractReceipts(contractId: string) {
  const contract = getContract(contractId)
  if (!contract) return
  const stages = contract.paymentStages || []
  const receipts = listReceipts().filter((item) => item.contractId === contractId)
  const allocations = allocateReceipts(stages, receipts)
  const nextStages = stages.map((stage) => {
    const alloc = allocations.get(stage.id) || { paidAmount: 0, actualDate: '' }
    return {
      ...stage,
      paidAmount: alloc.paidAmount ? String(alloc.paidAmount) : '',
      actualPayDate: alloc.actualDate
    }
  })
  upsertContract({ ...contract, paymentStages: nextStages })
}

function allocateReceipts(stages: PaymentStage[], receipts: ReceiptRecord[]): Map<string, { paidAmount: number; actualDate: string }> {
  const sortedReceipts = receipts
    .map((item) => ({ ...item, amountValue: toAmount(item.amount) }))
    .sort((a, b) => (a.receiveDate || '').localeCompare(b.receiveDate || '') || a.id.localeCompare(b.id))

  const stageOrder = stages
    .map((stage, idx) => ({
      id: stage.id,
      idx,
      amount: toAmount(stage.payableAmount),
      planDate: stage.payDate || ''
    }))
    .sort((a, b) => {
      if (a.planDate && b.planDate) return a.planDate.localeCompare(b.planDate)
      if (a.planDate) return -1
      if (b.planDate) return 1
      return a.idx - b.idx
    })

  const result = new Map<string, { paidAmount: number; actualDate: string }>()
  let receiptIndex = 0
  let receiptRemain = sortedReceipts[0]?.amountValue || 0

  stageOrder.forEach((stage) => {
    let due = stage.amount
    let paid = 0
    let actualDate = ''
    while (due > 0 && receiptIndex < sortedReceipts.length) {
      const use = Math.min(due, receiptRemain)
      paid += use
      due -= use
      receiptRemain -= use
      if (due === 0) {
        actualDate = sortedReceipts[receiptIndex]?.receiveDate || ''
      }
      if (receiptRemain === 0) {
        receiptIndex += 1
        receiptRemain = sortedReceipts[receiptIndex]?.amountValue || 0
      }
    }
    result.set(stage.id, { paidAmount: paid, actualDate })
  })

  stages.forEach((stage) => {
    if (!result.has(stage.id)) result.set(stage.id, { paidAmount: 0, actualDate: '' })
  })

  return result
}

function buildReceivableCode(contractCode: string, index: number): string {
  const base = contractCode ? contractCode : 'AR'
  return `${base}-${String(index).padStart(2, '0')}`
}

function toAmount(value: unknown): number {
  const num = Number(value)
  return Number.isFinite(num) ? num : 0
}
