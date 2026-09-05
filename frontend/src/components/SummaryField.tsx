type SummaryFieldProps = {
  value: string
  onChange: (value: string) => void
  maxLength: number
}

export default function SummaryField({ value, onChange, maxLength }: SummaryFieldProps) {
  return (
    <div>
      <div className="summary-field">
        <label htmlFor="summary">Resumen del producto</label>
        <input
          id="summary"
          name="summary"
          value={value}
          maxLength={maxLength}
          required
          aria-describedby="summary-help summary-count"
          onChange={event => onChange(event.target.value)}
        />
      </div>
      <div className="field-details">
        <span id="summary-help">Ejemplo: Sofá de dos plazas de cuero rojo marca Cozy. Buen estado.</span>
        <span id="summary-count">{value.length}/{maxLength}</span>
      </div>
    </div>
  )
}
