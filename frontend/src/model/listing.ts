export type SuggestionRequest = {
  description: string
}

export type PriceRange = {
  min: number
  max: number
}

export type ListingRequest = {
  title: string
  tags: string[]
  priceRange: PriceRange
}
