import { onBeforeUnmount, onMounted } from 'vue'

type EnterSaveOptions = {
  allowInTextarea?: boolean
}

export function useEnterSave(handler: () => void, options: EnterSaveOptions = {}) {
  const onKeydown = (event: KeyboardEvent) => {
    if (event.key !== 'Enter' || event.isComposing) return
    if (event.ctrlKey || event.metaKey || event.altKey || event.shiftKey) return
    const target = event.target as HTMLElement | null
    if (!options.allowInTextarea && target?.tagName === 'TEXTAREA') return
    handler()
  }

  onMounted(() => window.addEventListener('keydown', onKeydown))
  onBeforeUnmount(() => window.removeEventListener('keydown', onKeydown))
}
