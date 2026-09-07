import Avatar from './Avatar'
import CategoryNavigation from './CategoryNavigation'
import Icon from './Icon'

export default function Header() {
  return (
    <header>
      <div className="topbar">
        <img className="brand" src="/logo-wallapop-home-v2.svg" alt="Wallapop" width="183" height="48" />
        <div className="search-preview"><Icon name="magnifying-glass" /><span>Buscar en Todas las categorías</span></div>
        <div className="header-item"><Icon name="heart" />Favoritos</div>
        <div className="header-item"><Icon name="comment" />Buzón</div>
        <div className="header-item"><Avatar />Tú</div>
        <span className="sell-preview"><Icon name="plus" />Vender</span>
      </div>
      <CategoryNavigation />
    </header>
  )
}
