import { useState, type FormEvent } from 'react'
import SummaryField from './SummaryField'

export default function ProductSummaryForm() {
  const [summary, setSummary] = useState('')
  const [confirmed, setConfirmed] = useState(false)

  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (summary.trim()) setConfirmed(true)
  }

  function updateSummary(value: string) {
    setSummary(value)
    setConfirmed(false)
  }

  return (
    <form className="panel" onSubmit={submit}>
      <h2>Información del producto</h2>
      <p className="subtitle">¿Qué vendes? Proporciona toda la información relevante</p>
      <SummaryField value={summary} onChange={updateSummary} maxLength={50} />
      <button disabled={!summary.trim() || confirmed} type="submit">Continuar</button>
      <p className="confirmation" role="status">
        {confirmed ? 'Resumen preparado. Esta demo estática no publica ni envía el producto.' : ''}
      </p>
    </form>
  )
}
