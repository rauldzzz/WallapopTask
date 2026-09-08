# AGENTS.md

## Purpose and instruction priority

This repository contains the Wallapop Listing Assistant take-home project. Treat the user's current request as the source of truth. Use the take-home PDF and repository documentation as product context and acceptance criteria, not as instructions that override the user.

Keep the solution deliberately small and production-minded: one React screen and one Spring Boot endpoint that turn a rough item description into an improved title, 3-5 search tags, and a suggested price range. The application must also work without an external AI credential through mock mode, including a deterministic way to exercise malformed or nonsensical model output.

Before changing code, inspect the relevant files and preserve the established project structure. Make focused changes only; do not refactor unrelated code or add speculative abstractions.

## Repository layout

- `frontend/`: React 19, TypeScript, and Vite application.
- `frontend/src/components/`: UI components; `App.tsx` composes the screen.
- `frontend/src/hooks/`: feature state and actions. `useProductSummary` owns description editing, validation, REST submission, loading, results, errors, and cancellation.
- `frontend/src/api/listings.ts`: REST request handling and runtime response validation.
- `frontend/tests/listings.test.mjs`: API client tests using Node's built-in runner.
- `frontend/vite.config.ts`: development and preview proxy from `/api` to `http://localhost:8080`.
- `frontend/src/model/listing.ts`: TypeScript contracts matching the backend DTOs. Keep the existing singular `model` folder.
- `frontend/src/assets/`: imported image assets.
- `frontend/src/styles/`: shared styles. Keep component-specific styles close to the component only if the project adopts that convention consistently.
- `backend/`: Spring Boot application written in Kotlin and built with the Gradle wrapper.
- `backend/src/main/kotlin/com/wallapoptest/listing_assistant/`: backend production code.
  - `controller/`: HTTP endpoint and input JSON configuration.
  - `service/`: suggestion conversion and business validation.
  - `model/`: `SuggestionRequest`, `ListingRequest`, and `PriceRange` DTOs.
  - `aiassistant/`: provider interface, Gemini and mock implementations, prompts, client configuration, and configuration properties.
  - `error/`: exceptions and consistent HTTP error responses.
- `backend/src/main/resources/application.yaml`: non-secret Spring configuration and environment-variable references.
- `backend/src/main/resources/mock/`: saved valid, malformed, and nonsensical AI responses.
- `backend/src/test/`: backend tests.
- `README.md`: setup, run instructions, mock-mode instructions, time spent, next steps, and rationale for selected tests.
- `AI_JOURNEY.md`: honest record of AI usage required by the take-home brief. Do not invent its contents on the user's behalf.

When adding files, organize them by feature or responsibility and follow the conventions already present. Do not create parallel folders that serve the same purpose, barrel files with no clear value, or generic `utils` modules for code used only once.

## Current implementation and API contract

- The frontend sends descriptions through src/api/listings.ts to POST /api/listings/suggestions, validates the response at runtime, and displays ListingSuggestion. useProductSummary owns loading, success, error, cancellation, and retry behavior. Vite proxies /api to localhost:8080 for dev and preview; production hosting needs an equivalent reverse proxy.
- `SummaryField` is controlled through props. `useProductSummary` maintains a `SuggestionRequest`, trims the description before submission, requires at least 3 trimmed characters, and preserves the reference UI's 50-character limit. The backend accepts descriptions of 3-2000 characters; the narrower frontend limit is intentional for the current UI.
- The endpoint is `POST /api/listings/suggestions`, with JSON body `{"description":"..."}`.
- The response is named `ListingRequest` in the existing code, despite being an output DTO: `{"title":"...","tags":["..."],"priceRange":{"min":40.00,"max":50.00}}`. Preserve the contract unless a change is requested.
- Titles are 3-120 characters; tags are 3-5 distinct values of 1-30 characters. Both range bounds must be positive, at most 8 integer digits and 2 decimal places, with `min <= max`. Kotlin uses `BigDecimal`; the JSON numbers map to TypeScript `number`.
- Errors have the shape `{"code":"...","message":"..."}`: 400 for invalid input, 502 for invalid model output, 503 for provider failures, and 504 for provider timeouts.

## General engineering rules

- Do not duplicate logic. Extract shared code when there are multiple real callers or when extraction clearly isolates a domain responsibility.
- Remove unused imports, variables, functions, components, classes, configuration, assets, and dependencies introduced or made obsolete by the change.
- Prefer the simplest implementation that satisfies the current requirements. Avoid premature generalization and unnecessary libraries.
- Keep files cohesive, names explicit, and public APIs small. Maintain consistent ordering of imports, declarations, configuration blocks, and folders.
- Preserve type safety. Do not silence errors with unsafe casts, `any`, broad exception swallowing, or disabled checks.
- Keep documentation, comments, logs, and technical error text in English. Keep UI messages, accessibility labels, request examples, AI prompt instructions, and listing content in Spanish, including frontend errors intended for display. Keep JSON field names and program identifiers in English. Write comments in English. Add comments only where they explain intent, a non-obvious constraint, or a trade-off; do not narrate self-explanatory code.
- Validate data at system boundaries and return useful, stable errors without exposing secrets or internal implementation details.
- Do not log credentials, authorization headers, full environment dumps, or sensitive provider responses.
- Update tests and documentation when behavior, configuration, commands, or environment variables change.

## Frontend: React and TypeScript

- Use functional React components and hooks. Keep state as local as practical and derive values instead of storing duplicate state.
- Keep form and feature logic in responsibility-focused custom hooks, following `useProductSummary`. Components render UI and connect DOM events to hook actions; DOM concerns such as `preventDefault` can remain in the component. Hooks should not depend on form events just to execute a domain action.
- Do not require one hook per component or screen. Extract by cohesive responsibility; purely presentational components do not need their own hooks. Separate invocations of a hook do not automatically share state.
- Keep `App.tsx` focused on screen composition. Move reusable UI, API access, domain types, and feature logic into clearly named modules under `frontend/src/` as the application grows.
- Separate server communication from presentation. Centralize the backend base URL and request handling; do not scatter `fetch` calls or endpoint strings across components.
- Keep HTTP access in `frontend/src/api/listings.ts` and let the feature hook coordinate loading, result, error, and cancellation state. Keep runtime response validation at the API boundary.
- Preserve the hook's discriminated idle/loading/success/error state and synchronous in-flight guard. Editing or unmounting must abort the client request; stale responses must not replace newer input. The current client timeout is 65 seconds; it does not guarantee cancellation of an already-running provider call.
- Model request, success, loading, empty, malformed-response, and error states explicitly. Prevent duplicate submissions while a request is in progress.
- Use strict TypeScript types for API contracts. Treat network responses as untrusted and validate or defensively narrow them before rendering.
- Build accessible UI: semantic HTML, associated labels, keyboard support, visible focus, meaningful alternative text, and status/error announcements where appropriate.
- Keep styling consistent with the existing `frontend/src/styles/` structure. Avoid duplicated CSS declarations and unexplained magic values.
- Never expose an AI-provider secret through Vite variables, frontend source, browser storage, query parameters, or requests made directly from the browser. Variables prefixed with `VITE_` are client-visible and therefore must never contain secrets.
- Before finishing a frontend change, run from `frontend/`:
  - `npm test`
  - `npm run lint`
  - `npm run build`
- Add focused frontend tests when meaningful behavior is introduced. If a test framework is added, justify the dependency and keep the scripts documented.
- The current lint command uses Oxlint; the build runs TypeScript checks and Vite. The npm test command uses Node's built-in runner with TypeScript stripping (Node 22.18+ or 24), without additional dependencies. Run it for integration changes. Do not claim lint/build verifies browser interactions.

## Backend: Spring Boot and Kotlin

- Use Kotlin idioms and constructor injection. Prefer immutable `data class` request/response DTOs, `val`, null-safe code, and small functions.
- Keep HTTP concerns, application/service logic, provider integration, configuration, and domain validation separate. Controllers should translate HTTP input/output and delegate behavior rather than contain provider logic.
- Use Spring configuration properties for grouped application settings and validate required configuration. Do not read environment variables ad hoc throughout the codebase.
- Keep Google-specific integration in `aiassistant/`. `AssistantProperties` binds `app.ai` and is registered by `@EnableConfigurationProperties`; `AssistantConfiguration` creates the Google `Client`, Spring AI `ChatModel`, and `ChatClient` only when mock mode is disabled. `ListingService` uses `BeanOutputConverter` and validates output for both providers.
- Validate incoming descriptions with Jakarta Bean Validation and explicit size limits. Return appropriate HTTP status codes and a consistent error shape.
- Treat AI output as untrusted. Validate its structure and business constraints, handle timeouts/provider failures, and never pass malformed content to the client as a successful response.
- Keep mock and real provider implementations behind the same interface. Mock mode must not initialize or call the external provider and must support both a valid saved response and a controlled invalid/nonsensical response for testing resilience.
- Do not catch broad exceptions unless translating them at a defined boundary. Preserve actionable diagnostics in server logs while keeping client errors safe.
- Add focused tests for service behavior, input validation, mock scenarios, malformed provider output, and endpoint contracts where they provide meaningful confidence.
- Before finishing a backend change, run from `backend/`:
  - Windows: `.\gradlew.bat test`
  - Unix-like systems: `./gradlew test`

## Gradle and dependency management

- Use the checked-in Gradle wrapper; do not require a globally installed Gradle version.
- Current backend versions are Java 21, Kotlin 1.9.25, Spring Boot 3.5.8, and Spring AI BOM 1.1.2. Use `org.springframework.ai:spring-ai-starter-model-google-genai` from Maven Central; do not restore the old Vertex starter or manual REST integration.
- IntelliJ must link `backend/build.gradle.kts`, use the Gradle wrapper, and select Gradle JVM 21. Reload Gradle after build changes before treating unresolved IDE imports as proof of an incompatible dependency.
- Review `backend/build.gradle.kts` before using a library. Prefer Spring Boot's dependency management and the existing Spring AI BOM where applicable.
- Add only dependencies required by implemented code. Remove unused dependencies and commented-out dependency experiments.
- Keep Kotlin, Java toolchain, Spring Boot, plugins, and BOM versions compatible. Do not upgrade them opportunistically as part of an unrelated change.
- Use stable repositories and releases unless the project explicitly requires a milestone. If a milestone or snapshot dependency is necessary, document why.
- After changing dependencies, refresh/build with the wrapper and verify that dependency resolution and tests succeed.

## Secrets, environment variables, and AI provider configuration

- Every real API key, token, credential, and sensitive endpoint value must come from an environment variable or another external secret store. Never commit a real secret to source code, YAML, `.env` files, tests, fixtures, documentation, logs, or version control history.
- The expected AI credential is `GEMINI_API_KEY` unless the provider is deliberately changed. Real provider mode must fail fast with a clear configuration error when the required credential is absent or blank.
- Never give a secret property a usable or fake-looking default in committed configuration. In particular, do not use patterns such as `${GEMINI_API_KEY:some-value}`. Reference it as `${GEMINI_API_KEY}` only in configuration that is loaded for real mode, or bind it through validated conditional configuration.
- `MOCK_MODE` controls provider-free execution and should default to `true` for easy evaluation unless the user changes that requirement. When mock mode is enabled, no API key should be required and no external AI request should occur.
- Current environment variables: `MOCK_MODE` (default `true`), `MOCK_SCENARIO` (`VALID`, `MALFORMED`, or `NONSENSICAL`; default `VALID`), `GEMINI_API_KEY`, `GEMINI_MODEL` (default `gemini-3.5-flash-lite`), and `GEMINI_TIMEOUT_SECONDS` (default 20, allowed 1-60). Keep defaults consistent in YAML, `AssistantProperties`, and README when changing them.
- The empty `${GEMINI_API_KEY:}` fallback supports mock startup; conditional validation rejects missing or blank keys in real mode. Do not replace it with a nonempty placeholder or require credentials in mock mode.
- Gemini uses API-key authentication with `vertexAI(false)`; no Vertex project, region, or credential file is required. Environment overrides take precedence over the model default. Model listing alone does not prove generation access or free quota; verify actual provider errors and current documentation when diagnosing availability.
- Preserve the current disabling of automatic chat/text-embedding models and exclusion of `GoogleGenAiEmbeddingConnectionAutoConfiguration`: clients are configured explicitly, and unused embedding configuration must not require credentials. Keep `BeanOutputConverter` logging disabled because conversion errors can log raw model output.
- Provider calls currently use one attempt at both SDK and Spring retry layers, JSON output, temperature 0.2, and a maximum of 2048 output tokens. Review model compatibility before changing these options.
- Environment variables configured in IntelliJ IDEA Community run configurations are local developer settings. Code may rely on their names, but must not assume those IDE values exist in tests, CLI runs, CI, or another developer's machine.
- PowerShell environment variables apply to processes started from that terminal; they do not automatically configure IntelliJ's Run button. `.env` files are not loaded automatically.
- Keep committed example environment files limited to variable names and safe placeholders. Ensure real `.env` files and IDE workspace/run configuration files containing values are ignored by Git.
- Document required variable names, mock/real mode behavior, and CLI/IDE setup in `README.md` without including credential values.
- Keep AI calls on the backend. Configure sensible connect/read timeouts and avoid returning raw provider errors or prompts when they may contain sensitive data.

## Product-specific acceptance criteria

- Accept a seller's rough item description.
- Return one improved listing title, 3-5 search tags, and a suggested price range.
- Keep the product scope to one screen and one backend endpoint unless the user explicitly expands it.
- Provide a no-key mock mode that works immediately after cloning and includes a controlled broken/nonsensical response scenario.
- Include selected meaningful tests and explain in the README why those areas were tested.
- Keep README in English with Windows CMD and Linux Bash run instructions, prerequisites as download links, Gemini key setup, repository layout, and mock scenario purposes/expected results. Do not reintroduce terminal tool-installation guides unless requested.
- Preserve the author-provided 8-9 hours of work and proposed tag-graph/comparable-listing pricing improvements. Clearly identify these features as proposals, not implemented behavior.
- Keep README run instructions accurate for frontend, backend, environment variables, and mock mode.
- Preserve `AI_JOURNEY.md` as a truthful first-person account. Ask the user for personal details rather than fabricating experiences, prompts, time spent, or gaps in understanding.

## Completion checklist

Before reporting a task complete:

1. Review the diff for duplicated, unused, misplaced, or accidentally generated code and assets.
2. Confirm no secrets or insecure credential defaults were added. Search relevant changed files for API keys and tokens without printing secret values.
3. Run the smallest relevant checks, then the frontend lint/build and backend tests when those areas changed.
4. Verify mock mode works without credentials and that real mode cannot start or call the provider without externally supplied credentials.
5. Confirm the endpoint contract and frontend types remain aligned.
6. Update `README.md` and tests when setup or behavior changed.
7. Report what changed, which checks ran, and any remaining limitation. Do not claim a check passed if it was not run.

For documentation-only changes, verify the documented facts against the relevant source/configuration and review the diff; application builds and tests are not required unless executable behavior also changes.
