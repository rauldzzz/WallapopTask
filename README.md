# Wallapop Listing Assistant

A take-home project that turns a seller's rough product description into an improved listing title, 3-5 search tags, and an estimated price range in EUR.

The UI recreates the exact stage of Wallapop's selling flow where a user starts listing a product. It is an educational recreation, not an official Wallapop service. The header, sidebar, and categories are static; the product form is connected to the backend through REST. It generates suggestions without publishing a listing.

## Stack

| Area | Technology |
| --- | --- |
| Backend | Spring Boot 3.5.8 with Kotlin 1.9.25 and Java 21 |
| AI integration | Spring AI BOM 1.1.2 and `spring-ai-starter-model-google-genai` |
| Provider | Gemini Developer API with an API key; default model `gemini-3.5-flash-lite` (Gemini 3.5 Flash-Lite) |
| Spring AI components | `ChatClient`, `GoogleGenAiChatModel`, `GoogleGenAiChatOptions`, and `BeanOutputConverter` |
| Other backend libraries | Spring Web, Jakarta Bean Validation, Jackson Kotlin, Kotlin reflection, and the Google GenAI Java SDK supplied by the starter |
| Frontend | React 19, TypeScript 6, Vite 8, and Oxlint |
| Tests | Spring Boot Test, JUnit 5, Kotlin Test, MockMvc/Mockito; Node's built-in test runner for the frontend API client |
| Build tools | Checked-in Gradle wrapper and npm with `package-lock.json` |

[Spring AI's Google GenAI integration](https://docs.spring.io/spring-ai/reference/1.1/api/chat/google-genai-chat.html) supports API-key authentication. No Vertex AI project ID, region, service-account file, or global Gradle installation is required.

## Prerequisites

Download and install:

- **JDK 21**, for example [Eclipse Temurin](https://adoptium.net/temurin/releases/?version=21). Point `JAVA_HOME` to the JDK directory and include its `bin` directory in `PATH`.
- **Node.js 24 LTS with npm**, from [Node.js](https://nodejs.org/en/download). This also supports the TypeScript stripping used by the frontend tests.
- **Git**, from [Git downloads](https://git-scm.com/downloads), if cloning the repository rather than extracting an archive.
- **curl**, only for optional direct API tests. It is included in recent Windows versions and available through Linux package managers.

Check the tools in a new terminal:

```text
java -version
node --version
npm --version
git --version
curl --version
```

Java should report version 21. Clone or extract the repository and open a terminal in its root, the directory containing `frontend` and `backend`. The first npm/Gradle runs need internet access to download dependencies. No API key is needed for mock mode.

## Run on Windows CMD

These commands use **Command Prompt (`cmd.exe`)**, not PowerShell.

### Backend in mock mode

From the repository root, in the first terminal:

```bat
cd backend
set "MOCK_MODE=true"
set "MOCK_SCENARIO=VALID"
gradlew.bat bootRun
```

Wait for `Started ListingAssistantApplicationKt`. The backend listens on `http://localhost:8080`. Leave this terminal open; Gradle continuing to execute `bootRun` is normal.

`VALID` tests a successful response. Use `MALFORMED` or `NONSENSICAL` to test resilience; see [Mock scenarios](#mock-scenarios).

### Frontend

Open a second CMD terminal at the repository root:

```bat
cd frontend
npm ci
npm run dev
```

Open the URL printed by Vite, normally `http://localhost:5173`. Enter a description such as `Chaqueta de cuero vintage talla M` and press **Continuar**. You should see the saved title, tags, and EUR 40-50 range. Both servers must be running.

### Backend with Gemini

Create a key using the guide below. Stop the backend with `Ctrl+C`. In its CMD terminal, still inside `backend`:

```bat
set "MOCK_MODE=false"
set "GEMINI_MODEL=gemini-3.5-flash-lite"
set "GEMINI_API_KEY="
set /p "GEMINI_API_KEY=Paste your Gemini API key and press Enter: "
gradlew.bat bootRun
```

Paste the actual key at the prompt. CMD displays the input; do not save the key in source files or screenshots. Variables apply to this terminal and its child processes. The running frontend now uses the real provider through the backend. Clear the key after stopping the backend with `set "GEMINI_API_KEY="`, or close the terminal.

## Run on Linux (Bash)

### Backend in mock mode

From the repository root, in the first terminal:

```bash
cd backend
chmod +x gradlew
MOCK_MODE=true MOCK_SCENARIO=VALID ./gradlew bootRun
```

Leave it running on `http://localhost:8080`. See [Mock scenarios](#mock-scenarios) for the successful and broken response cases.

### Frontend

Open a second terminal at the repository root:

```bash
cd frontend
npm ci
npm run dev
```

Open the URL printed by Vite. Enter a description and press **Continuar** to see the mock suggestion. Stop either server with `Ctrl+C` in its terminal.

### Backend with Gemini

Stop the mock backend. In its terminal, inside `backend`:

```bash
read -r -s -p "Gemini API key: " GEMINI_API_KEY
printf '\n'
export GEMINI_API_KEY
MOCK_MODE=false GEMINI_MODEL=gemini-3.5-flash-lite ./gradlew bootRun
```

Input is hidden. Repeat the form submission to generate a real suggestion. After stopping the backend, run `unset GEMINI_API_KEY` to remove the key from the shell.

## Get a free-tier Gemini API key

1. Sign in to [Google AI Studio's API keys page](https://aistudio.google.com/apikey) and accept the applicable terms.
2. Use the default project/key if one was created for your account, or select **Create API key** and select/create a project. Existing projects may need to be imported into AI Studio. See [Google's key setup guide](https://ai.google.dev/gemini-api/docs/api-key).
3. Copy the key and pass it to the backend as `GEMINI_API_KEY` using the commands above.
4. For free-tier evaluation, check that the project's plan is **Free** rather than enabling paid billing. Creating a key does not guarantee access to every model. See [Google's billing guide](https://ai.google.dev/gemini-api/docs/billing).
5. Check the model's active limits in AI Studio. Gemini 3.5 Flash-Lite has free-tier pricing subject to availability and quotas; this is not unlimited usage or a guaranteed lifetime token allowance. Consult the current [pricing table](https://ai.google.dev/gemini-api/docs/pricing#gemini-3.5-flash-lite) and [rate limits](https://ai.google.dev/gemini-api/docs/rate-limits).

Mock mode works if credentials, model access, or quota prevent a real request. Never put the key in frontend code, `VITE_` variables, committed `.env` files, or logs.

## How the frontend and backend communicate

The form accepts 3-50 characters after trimming outer spaces. **Continuar** sends `POST /api/listings/suggestions`. The hook manages idle, loading, success, and error states, prevents duplicate submissions, and cancels the browser request when the text changes or the component unmounts. Editing clears the result; an error enables **Reintentar**. A client timeout aborts the request after 65 seconds. Browser cancellation does not guarantee that a provider call already running on the backend stops.

`frontend/src/api/listings.ts` validates the response before it is rendered. `ListingSuggestion` shows the title, tags, and EUR range. User-facing messages remain Spanish, including safe messages for failed requests. The AI prompt instructions and listing content are also Spanish; technical errors, code identifiers, and documentation remain English.

Vite proxies `/api` to `http://localhost:8080` in development and preview. The browser uses its own origin, so this local setup does not require cross-origin backend access. Restart Vite after changing its configuration. A production host must configure an equivalent `/api` reverse proxy; the Vite proxy is not included in the static build.

## REST contract and direct tests

Keep the backend running and open another terminal. The endpoint accepts **POST**; opening its address in a browser sends GET and is not a valid test.

Windows CMD:

```bat
curl.exe -i -X POST "http://localhost:8080/api/listings/suggestions" -H "Content-Type: application/json" -d "{\"description\":\"Chaqueta de cuero vintage, talla M\"}"
```

Linux Bash:

```bash
curl -i -X POST 'http://localhost:8080/api/listings/suggestions' \
  -H 'Content-Type: application/json' \
  -d '{"description":"Chaqueta de cuero vintage, talla M"}'
```

The response appears in the client terminal. `VALID` always returns this saved Spanish listing, independently of the input:

```json
{
  "title": "Chaqueta de cuero vintage talla M, usada una vez",
  "tags": ["chaqueta", "cuero", "vintage", "talla M"],
  "priceRange": { "min": 40.00, "max": 50.00 }
}
```

The input DTO is `SuggestionRequest`; the output DTO retains the name `ListingRequest`. Kotlin prices use `BigDecimal`, while TypeScript receives JSON numbers. The backend accepts descriptions of 3-2000 characters; the frontend keeps the reference screen's narrower 50-character limit. Titles must be 3-120 characters, with 3-5 distinct tags of 1-30 characters. Prices must be positive, have at most 8 integer digits and 2 decimal places, and satisfy `min <= max`. A valid structure does not guarantee factual or market-price accuracy.

## Mock scenarios

Mock mode uses saved files in `backend/src/main/resources/mock/` and never initializes or calls Gemini. Each scenario passes through the same service conversion and validation as real AI output, making failures repeatable without credentials or API quota.

| Scenario | Saved data | Expected result for valid input | What it checks and why |
| --- | --- | --- | --- |
| `VALID` | Complete listing, four distinct tags, EUR 40-50 range | HTTP 200; the UI displays a suggestion | Checks the successful conversion, validation, REST contract, and rendering path using predictable data. |
| `MALFORMED` | Truncated JSON: `{"title":` | HTTP 502, `INVALID_MODEL_RESPONSE`; the UI offers retry | Checks that incomplete or broken model output is rejected safely rather than presented as a successful listing. |
| `NONSENSICAL` | Parseable JSON with title `???`, repeated empty tags, and negative prices | HTTP 502, `INVALID_MODEL_RESPONSE`; the UI offers retry | Checks business constraints after parsing. Valid JSON alone does not mean a useful listing. This checks explicit rules, not general semantic or market-price accuracy. |

Stop the backend with `Ctrl+C`, choose a scenario, and restart. Example for Windows CMD, from `backend`:

```bat
set "MOCK_MODE=true"
set "MOCK_SCENARIO=MALFORMED"
gradlew.bat bootRun
```

Linux Bash, from `backend`:

```bash
MOCK_MODE=true MOCK_SCENARIO=MALFORMED ./gradlew bootRun
```

Replace `MALFORMED` with `NONSENSICAL` or `VALID` to exercise the other cases. Test with the UI or the POST examples above. If a previous successful result disables Continue, edit the description first; after an error, press **Reintentar**. Scenarios are selected at startup, not by changing the description. Restore `VALID` and restart when finished.

As a separate input-validation test, send `{"description":""}` or broken request JSON using curl: the backend should return HTTP 400 before calling the mock. Provider outages/timeouts are simulated in automated backend tests, not additional `MOCK_SCENARIO` values.

Errors have the shape `{"code":"...","message":"..."}`:

| HTTP status | Meaning |
| --- | --- |
| 400 | Invalid description or request JSON |
| 502 | Invalid model output |
| 503 | Provider failure or unavailability |
| 504 | Provider timeout |

The frontend maps HTTP failures to its own safe Spanish UI messages and does not display raw provider errors.

## Configuration and troubleshooting

| Variable | Default / purpose |
| --- | --- |
| `MOCK_MODE` | `true`; disables external provider initialization and calls |
| `MOCK_SCENARIO` | `VALID`; also accepts `MALFORMED` and `NONSENSICAL` |
| `GEMINI_API_KEY` | Empty in mock; required and nonblank in real mode |
| `GEMINI_MODEL` | `gemini-3.5-flash-lite`; supports an explicit environment override |
| `GEMINI_TIMEOUT_SECONDS` | 20 seconds; allowed range 1-60 |

`application.yaml` resolves these variables into `app.ai`. `AssistantProperties` binds and validates them; `AssistantConfiguration` creates the Google client and Spring AI objects only in real mode. Unused automatic chat/embedding configuration is disabled to keep mock startup credential-free. The converter logger is disabled because conversion errors may log raw content. SDK and Spring retry policies both use one attempt.

- `.env` files are not loaded automatically. Restart the backend after changing environment variables.
- IntelliJ: link `backend/build.gradle.kts`, select the Gradle wrapper and **Gradle JVM 21**, then reload Gradle. For the Run button, put variables in **Run > Edit Configurations > Environment variables**. Terminal variables do not automatically apply to that configuration.
- A blank key prevents real-mode startup; a nonblank value does not prove Google will accept it.
- Inspect the safe server log for provider status/type/timeout. Backend HTTP 503 can wrap a Google error such as 404 for an unavailable model. Model listing alone does not guarantee generation access. Do not log keys or raw provider content.
- If the UI cannot connect, check that the backend is listening on port 8080 and that Vite is running. Stop conflicting processes before starting another server on the same port.

## Repository organization

```text
.
|-- frontend/
|   |-- src/
|   |   |-- App.tsx                 # Screen composition
|   |   |-- main.tsx                # React entry point
|   |   |-- components/             # UI, form, and ListingSuggestion
|   |   |-- hooks/                  # useProductSummary: state and actions
|   |   |-- api/                    # REST client and runtime response validation
|   |   |-- model/                  # SuggestionRequest, ListingRequest, PriceRange types
|   |   |-- assets/                 # Imported images and SVG icons
|   |   `-- styles/                 # Shared palette and screen styles
|   |-- tests/                      # Node tests for the REST client
|   |-- public/                     # Public logo, favicon, and avatar
|   |-- package.json
|   `-- vite.config.ts              # Build setup and local /api proxy
|-- backend/
|   |-- src/main/kotlin/com/wallapoptest/listing_assistant/
|   |   |-- ListingAssistantApplication.kt
|   |   |-- controller/             # REST endpoint and request JSON configuration
|   |   |-- service/                # AI output conversion and validation
|   |   |-- model/                  # Input/output DTOs and price range
|   |   |-- aiassistant/            # Gemini/mock, prompt, clients, and properties
|   |   `-- error/                  # Exceptions and safe HTTP responses
|   |-- src/main/resources/
|   |   |-- application.yaml
|   |   `-- mock/                   # Valid, malformed, and nonsensical fixtures
|   |-- src/test/                   # Backend tests
|   |-- gradle/wrapper/             # Checked-in Gradle wrapper configuration
|   `-- build.gradle.kts
|-- AGENTS.md                       # Development conventions
|-- AI_JOURNEY.md                   # Author's AI-assistance record
`-- README.md
```

Request flow: `ProductSummaryForm` -> `useProductSummary` -> REST client -> Vite proxy -> controller -> service -> Gemini/mock -> service validation -> frontend validation -> `ListingSuggestion`.

`SummaryField` receives its value and callback through props. Components render the interface; the hook owns state and actions. Google-specific integration stays in `aiassistant`, and controllers delegate to the service.

## Checks and test rationale

From `frontend`, on Windows or Linux:

```text
npm test
npm run lint
npm run build
```

The five Node test cases cover the POST contract, malformed domain data, empty/invalid JSON, HTTP failures, and network errors. They mock fetch and consume no API quota. Node 24 is recommended; the test runner uses native TypeScript stripping, also available in Node 22.18+. Oxlint checks code and the build runs TypeScript and Vite. These tests do not exercise React interactions in a browser.

From `backend`, Windows CMD:

```bat
gradlew.bat test
```

Linux Bash:

```bash
./gradlew test
```

Backend tests cover input/output validation, range constraints, mock scenarios, safe provider failures/timeouts, and credential-free mock startup. These boundaries matter because untrusted input and AI output must not become successful invalid responses. Gemini is simulated; tests do not require a key.

Manual browser checks: empty/whitespace input, minimum length, 50-character limit, loading, duplicate-submit prevention, edits during an in-flight request, successful title/tags/price rendering, broken mocks, retry, keyboard access, and mobile/desktop layout.

`npm run build` produces `frontend/dist`; `npm run preview` serves it locally with the configured proxy. Production hosting needs its own reverse proxy. Package the backend using `gradlew.bat bootJar` or `./gradlew bootJar`, then run `java -jar build/libs/listing-assistant-0.0.1-SNAPSHOT.jar` from `backend` with the same environment variables.

## UI reference and attribution

Shared CSS preserves the reference palette. Segoe UI with Arial fallback approximates the original font. The logo and avatar come from the supplied reference. Similar SVG icons are Font Awesome Free 6.7.2, discovered through [W3Schools](https://www.w3schools.com/icons/fontawesome_icons_intro.asp) and downloaded from the [official icon repository](https://github.com/FortAwesome/Font-Awesome/tree/6.x/svgs/solid). See `frontend/src/assets/icons/README.md` and `LICENSE.txt` for attribution and licensing.

## Time spent

This project took approximately 8-9 hours of work.

## Future improvements

I designed this project as a take-home test, with an emphasis on the prompt and on giving the AI model the context it needs to produce the most accurate suggestions possible. I would add:

- **A graph of Wallapop tags, categories, and filters.** Cover the application's tags and their relationships to categories and filters. Guide the model toward tags that match Wallapop's classification system, with the aim of making listings easier to discover through relevant searches and filters.
- **Price estimates based on comparable Wallapop listings.** When similar products exist, match them using product tags, identify the most common asking prices or price bands, and exclude disproportionately high or low outliers. Derive the suggested minimum and maximum from the remaining comparable listings, accounting for differences reflected in their tags. These would be asking-price estimates, rather than confirmed sale prices.

These are proposed improvements: the current implementation does not retrieve Wallapop's tag taxonomy or comparable listing prices.
