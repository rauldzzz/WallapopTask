import { test, afterEach, mock } from 'node:test'
import assert from 'node:assert/strict'
import { isListingRequest, suggestListing } from '../src/api/listings.ts'

const listing = { title: 'Chaqueta vintage', tags: ['ropa', 'cuero', 'vintage'], priceRange: { min: 40, max: 50 } }
afterEach(() => mock.restoreAll())

test('sends the REST contract and returns a validated listing', async () => {
  mock.method(globalThis, 'fetch', async (url, options) => {
    assert.equal(url, '/api/listings/suggestions')
    assert.equal(options.method, 'POST')
    assert.equal(options.headers['Content-Type'], 'application/json')
    assert.deepEqual(JSON.parse(options.body), { description: 'Chaqueta' })
    return Response.json(listing)
  })
  assert.deepEqual(await suggestListing({ description: 'Chaqueta' }), listing)
})

test('rejects malformed domain data', () => {
  for (const value of [null, {}, [], { ...listing, title: '??' },
    { ...listing, tags: ['ropa', 'ROPA', 'cuero'] },
    { ...listing, tags: [null, 'ropa', 'cuero'] },
    { ...listing, priceRange: { min: 60, max: 50 } },
    { ...listing, priceRange: { min: '40', max: 50 } },
    { ...listing, priceRange: { min: 1.001, max: 50 } },
    { ...listing, priceRange: { min: 0, max: Infinity } }]) {
    assert.equal(isListingRequest(value), false)
  }
})

test('rejects empty and invalid successful responses', async () => {
  for (const body of ['', 'not json', '{}']) {
    mock.method(globalThis, 'fetch', async () => new Response(body))
    await assert.rejects(suggestListing({ description: 'Chaqueta' }), /respuesta no válida/)
    mock.restoreAll()
  }
})

test('translates HTTP failures without exposing provider bodies', async () => {
  for (const status of [400, 502, 503, 504, 500]) {
    mock.method(globalThis, 'fetch', async () => new Response('private provider details', { status }))
    await assert.rejects(suggestListing({ description: 'Chaqueta' }), error => {
      assert.ok(error instanceof Error)
      assert.ok(!error.message.includes('private'))
      return true
    })
    mock.restoreAll()
  }
})

test('handles network failures', async () => {
  mock.method(globalThis, 'fetch', async () => { throw new TypeError('fetch failed') })
  await assert.rejects(suggestListing({ description: 'Chaqueta' }), /conectar/)
})
