import Icon from './Icon'

export default function CategoryNavigation() {
  return (
    <div className="category-nav">
      <span><Icon name="bars" />Todas las categorías<Icon name="chevron-down" /></span>
      {['Cine, libros y música', 'Moda y accesorios', 'Tecnología y electrónica', 'Coches', 'Hogar y jardín'].map(item => <span key={item}>{item}</span>)}
    </div>
  )
}
