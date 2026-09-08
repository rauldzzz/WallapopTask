import type { ListingRequest } from '../model/listing'

const euros = new Intl.NumberFormat('es-ES', { style: 'currency', currency: 'EUR' })

export default function ListingSuggestion({ listing }: { listing: ListingRequest }) {
  return (
    <section className="listing-suggestion" aria-labelledby="suggestion-title">
      <h3 id="suggestion-title">Sugerencia para tu anuncio</h3>
      <p><strong>Título:</strong> {listing.title}</p>
      <ul className="suggestion-tags" aria-label="Etiquetas sugeridas">
        {listing.tags.map(tag => <li key={tag}>{tag}</li>)}
      </ul>
      <p><strong>Precio estimado:</strong> {euros.format(listing.priceRange.min)} – {euros.format(listing.priceRange.max)}</p>
    </section>
  )
}
