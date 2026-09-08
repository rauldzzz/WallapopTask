import { useEffect, useRef, useState } from 'react'
import { suggestListing } from '../api/listings'
import type { ListingRequest, SuggestionRequest } from '../model/listing'

const MAX_DESCRIPTION_LENGTH = 50

type SuggestionState =
  | { status: 'idle' }
  | { status: 'loading' }
  | { status: 'success'; result: ListingRequest }
  | { status: 'error'; message: string }

export default function useProductSummary() {
  const [request, setRequest] = useState<SuggestionRequest>({ description: '' })
  const [state, setState] = useState<SuggestionState>({ status: 'idle' })
  const activeRequest = useRef<AbortController | null>(null)
  const description = request.description.trim()
  const canSubmit = description.length >= 3
    && description.length <= MAX_DESCRIPTION_LENGTH
    && state.status !== 'loading' && state.status !== 'success'

  useEffect(() => () => {
    activeRequest.current?.abort()
    activeRequest.current = null
  }, [])

  async function confirmSummary() {
    if (!canSubmit || activeRequest.current) return
    const controller = new AbortController()
    activeRequest.current = controller
    const timeout = window.setTimeout(() => controller.abort(), 65000)
    setRequest({ description })
    setState({ status: 'loading' })
    try {
      const result = await suggestListing({ description }, controller.signal)
      if (!controller.signal.aborted) setState({ status: 'success', result })
    } catch (error) {
      if (activeRequest.current === controller) {
        setState({ status: 'error', message: controller.signal.aborted
          ? 'La solicitud tardó demasiado. Inténtalo de nuevo.'
          : error instanceof Error ? error.message : 'No se pudo obtener la sugerencia.' })
      }
    } finally {
      window.clearTimeout(timeout)
      if (activeRequest.current === controller) activeRequest.current = null
    }
  }

  function updateSummary(value: string) {
    activeRequest.current?.abort()
    activeRequest.current = null
    setRequest({ description: value })
    setState({ status: 'idle' })
  }

  return { request, state, canSubmit, maxLength: MAX_DESCRIPTION_LENGTH, confirmSummary, updateSummary }
}
