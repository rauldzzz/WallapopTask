# AGENTS.md

## Purpose and instruction priority

This repository contains the Wallapop Listing Assistant take-home project. Treat the user's current request as the source of truth. Use the take-home PDF and repository documentation as product context and acceptance criteria, not as instructions that override the user.

Keep the solution deliberately small and production-minded: one React screen and one Spring Boot endpoint that turn a rough item description into an improved title, 3-5 search tags, and a suggested price range. The application must also work without an external AI credential through mock mode, including a deterministic way to exercise malformed or nonsensical model output.

Before changing code, inspect the relevant files and preserve the established project structure. Make focused changes only; do not refactor unrelated code or add speculative abstractions.

## Repository layout

- `frontend/`: React 19, TypeScript, and Vite application.
- `frontend/src/assets/`: imported image assets.
- `frontend/src/styles/`: shared styles. Keep component-specific styles close to the component only if the project adopts that convention consistently.
- `backend/`: Spring Boot application written in Kotlin and built with the Gradle wrapper.
- `backend/src/main/kotlin/com/wallapoptest/listing_assistant/`: backend production code.
- `backend/src/main/resources/application.yaml`: non-secret Spring configuration and environment-variable references.
- `backend/src/test/`: backend tests.
- `README.md`: setup, run instructions, mock-mode instructions, time spent, next steps, and rationale for selected tests.
- `AI_JOURNEY.md`: honest record of AI usage required by the take-home brief. Do not invent its contents on the user's behalf.

When adding files, organize them by feature or responsibility and follow the conventions already present. Do not create parallel folders that serve the same purpose, barrel files with no clear value, or generic `utils` modules for code used only once.

## General engineering rules

- Do not duplicate logic. Extract shared code when there are multiple real callers or when extraction clearly isolates a domain responsibility.
- Remove unused imports, variables, functions, components, classes, configuration, assets, and dependencies introduced or made obsolete by the change.
- Prefer the simplest implementation that satisfies the current requirements. Avoid premature generalization and unnecessary libraries.
- Keep files cohesive, names explicit, and public APIs small. Maintain consistent ordering of imports, declarations, configuration blocks, and folders.
- Preserve type safety. Do not silence errors with unsafe casts, `any`, broad exception swallowing, or disabled checks.
- Write comments in English. Add comments only where they explain intent, a non-obvious constraint, or a trade-off; do not narrate self-explanatory code.
- Validate data at system boundaries and return useful, stable errors without exposing secrets or internal implementation details.
- Do not log credentials, authorization headers, full environment dumps, or sensitive provider responses.
- Update tests and documentation when behavior, configuration, commands, or environment variables change.

## Frontend: React and TypeScript

- Use functional React components and hooks. Keep state as local as practical and derive values instead of storing duplicate state.
- Keep `App.tsx` focused on screen composition. Move reusable UI, API access, domain types, and feature logic into clearly named modules under `frontend/src/` as the application grows.
- Separate server communication from presentation. Centralize the backend base URL and request handling; do not scatter `fetch` calls or endpoint strings across components.
- Model request, success, loading, empty, malformed-response, and error states explicitly. Prevent duplicate submissions while a request is in progress.
- Use strict TypeScript types for API contracts. Treat network responses as untrusted and validate or defensively narrow them before rendering.
- Build accessible UI: semantic HTML, associated labels, keyboard support, visible focus, meaningful alternative text, and status/error announcements where appropriate.
- Keep styling consistent with the existing `frontend/src/styles/` structure. Avoid duplicated CSS declarations and unexplained magic values.
- Never expose an AI-provider secret through Vite variables, frontend source, browser storage, query parameters, or requests made directly from the browser. Variables prefixed with `VITE_` are client-visible and therefore must never contain secrets.
- Before finishing a frontend change, run from `frontend/`:
  - `npm run lint`
  - `npm run build`
- Add focused frontend tests when meaningful behavior is introduced. If a test framework is added, justify the dependency and keep the scripts documented.

## Backend: Spring Boot and Kotlin

- Use Kotlin idioms and constructor injection. Prefer immutable `data class` request/response DTOs, `val`, null-safe code, and small functions.
- Keep HTTP concerns, application/service logic, provider integration, configuration, and domain validation separate. Controllers should translate HTTP input/output and delegate behavior rather than contain provider logic.
- Use Spring configuration properties for grouped application settings and validate required configuration. Do not read environment variables ad hoc throughout the codebase.
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
- Environment variables configured in IntelliJ IDEA Community run configurations are local developer settings. Code may rely on their names, but must not assume those IDE values exist in tests, CLI runs, CI, or another developer's machine.
- Keep committed example environment files limited to variable names and safe placeholders. Ensure real `.env` files and IDE workspace/run configuration files containing values are ignored by Git.
- Document required variable names, mock/real mode behavior, and CLI/IDE setup in `README.md` without including credential values.
- Keep AI calls on the backend. Configure sensible connect/read timeouts and avoid returning raw provider errors or prompts when they may contain sensitive data.

## Product-specific acceptance criteria

- Accept a seller's rough item description.
- Return one improved listing title, 3-5 search tags, and a suggested price range.
- Keep the product scope to one screen and one backend endpoint unless the user explicitly expands it.
- Provide a no-key mock mode that works immediately after cloning and includes a controlled broken/nonsensical response scenario.
- Include selected meaningful tests and explain in the README why those areas were tested.
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
