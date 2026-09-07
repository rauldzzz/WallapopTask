import Icon from './Icon'
import UserProfile from './UserProfile'

const menu = [
  ['bag-shopping', 'Compras'], ['hand-holding-dollar', 'Ventas'], ['tag', 'Tu Catálogo'],
  ['comment', 'Buzón'], ['heart', 'Favoritos'], ['chart-simple', 'Estadísticas'],
  ['bolt', 'Wallapop Pro'], ['wallet', 'Monedero'], ['gift', 'Wallapop Club'],
  ['umbrella-beach', 'Modo vacaciones'], ['leaf', 'Tu impacto positivo'], ['gear', 'Ajustes'],
  ['circle-question', 'Ayuda'], ['clipboard', 'Consultas en curso'],
] as const

export default function Sidebar() {
  return (
    <aside aria-label="Mi cuenta (vista estática)">
      <UserProfile />
      <ul>{menu.map(([icon, label]) => <li key={label} className={label === 'Tu Catálogo' ? 'selected' : ''}>
        <Icon name={icon} /><div>{label === 'Modo vacaciones' && <small className="new-badge">Nuevo</small>}{label}{label === 'Wallapop Club' && <small className="points">0 puntos</small>}</div>
        {label === 'Ajustes' && <span className="settings-arrow">›</span>}
      </li>)}</ul>
    </aside>
  )
}
