import type { FormEvent } from 'react'
import useProductSummary from '../hooks/useProductSummary'
import SummaryField from './SummaryField'
import ListingSuggestion from './ListingSuggestion'

export default function ProductSummaryForm() {
  const { request, state, canSubmit, maxLength, confirmSummary, updateSummary } = useProductSummary()

  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    void confirmSummary()
  }

  return (
    <form className="panel" onSubmit={submit}>
      <h2>Información del producto</h2>
      <p className="subtitle">¿Qué vendes? Proporciona toda la información relevante</p>
      <SummaryField value={request.description} onChange={updateSummary} maxLength={maxLength} />
      <button disabled={!canSubmit} type="submit">{state.status === 'loading' ? 'Generando…' : state.status === 'error' ? 'Reintentar' : 'Continuar'}</button>
      <p className="confirmation" role="status">
        {state.status === 'loading' ? 'Generando sugerencia…' : state.status === 'success' ? 'Sugerencia preparada.' : ''}
      </p>
      {state.status === 'error' && <p role="alert">{state.message}</p>}
      {state.status === 'success' && <ListingSuggestion listing={state.result} />}
    </form>
  )
}
