import type { ListingRequest, SuggestionRequest } from '../model/listing'

const ENDPOINT = '/api/listings/suggestions'

function record(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null && !Array.isArray(value)
}

function price(value: unknown): value is number {
  return typeof value === 'number' && Number.isFinite(value)
    && value >= 0.01 && value <= 99999999.99
    && Math.abs(value * 100 - Math.round(value * 100)) < 0.00001
}

export function isListingRequest(value: unknown): value is ListingRequest {
  if (!record(value) || typeof value.title !== 'string' || !Array.isArray(value.tags)
    || !record(value.priceRange)) return false
  const title = value.title.trim()
  const tags: unknown[] = value.tags
  return title.length >= 3 && title.length <= 120 && /[\p{L}\p{N}]/u.test(title)
    && tags.length >= 3 && tags.length <= 5
    && tags.every(tag => typeof tag === 'string' && tag.trim().length >= 1
      && tag.trim().length <= 30 && /[\p{L}\p{N}]/u.test(tag))
    && new Set(tags.map(tag => typeof tag === 'string' ? tag.trim().toLowerCase() : '')).size === tags.length
    && price(value.priceRange.min) && price(value.priceRange.max)
    && value.priceRange.min <= value.priceRange.max
}

export async function suggestListing(request: SuggestionRequest, signal?: AbortSignal): Promise<ListingRequest> {
  let response: Response
  try {
    response = await fetch(ENDPOINT, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request),
      signal,
    })
  } catch {
    throw new Error('It was impossible to connect to the server. Check the connection and try again.')
  }
  if (!response.ok) {
    const messages: Record<number, string> = {
      400: 'Check the description of the product and try again.',
      502: 'The received suggestion is not valid. Try again.',
      503: 'The suggestion service is not available. Try again later.',
      504: 'The service took too long to respond. Try again.',
    }
    throw new Error(messages[response.status] ?? 'It was impossible to obtain the suggestion. Try again.')
  }
  const result: unknown = await response.json().catch(() => null)
  if (!isListingRequest(result)) throw new Error('The server returned an invalid response. Try again.')
  return result
}
