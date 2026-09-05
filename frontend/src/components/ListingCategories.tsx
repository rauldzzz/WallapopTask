import Icon from './Icon'

export default function ListingCategories() {
  return (
    <section className="panel" aria-labelledby="category-title">
      <h2 id="category-title">¿Qué subirás?</h2>
      <div className="listing-categories">
        <div className="category-card goods" aria-label="Algo que ya no necesito, seleccionado">
          <div className="goods-icons">{(['book', 'mobile-screen-button', 'shirt', 'tv', 'bicycle', 'gamepad', 'camera', 'tablet-screen-button'] as const).map(name => <Icon name={name} key={name} />)}</div>
          <span>Algo que ya no necesito</span>
        </div>
        {([['briefcase', 'Empleo'], ['screwdriver-wrench', 'Mis servicios'], ['car', 'Un vehículo'], ['house', 'Una propiedad']] as const).map(([icon, label]) => <div className="category-card" key={label}><Icon name={icon} /><span>{label}</span></div>)}
      </div>
    </section>
  )
}
