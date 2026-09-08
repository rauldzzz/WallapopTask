# AI Journey

## How I used AI

I used OpenAI Codex to develop this project, following the instructions in `AGENTS.md`.

## Questions and guidance that helped

I asked how to separate logic from React components, and Codex explained how custom hooks can encapsulate that logic. It also found the Spring AI dependency that supports Gemini API-key authentication for my suggestion requests.

Codex initially suggested a Gemini model that was no longer available to my account. The requests returned HTTP 404, and I kept asking about the error because I did not understand its cause. After adjusting the configuration to use another text model, the request worked. I then looked for a free model with enough daily requests to let me test the application.

## What went wrong and how I corrected it

I initially tried to integrate Spring AI using the wrong library. The agent implemented the backend using Google Cloud/Vertex AI authentication rather than the simple Gemini API-key setup I wanted. I returned to the previous repository state and asked it to implement the required behavior with Spring AI. It then found the correct dependency for Gemini API-key authentication.

When I wanted to translate the frontend error messages, the agent translated the entire UI and project as well. After realizing that my intended change only involved one file, I returned to the state of my last push and made the change manually.

## What I do not fully understand yet

I would like to understand better how the tests in the backend's test directory are implemented, particularly how each test function is defined and how its annotations and assertions determine what is executed and checked.

## What I would improve with more time

I would refine the request and prompt to obtain better results from the same AI model. To give it more useful context, I would implement the features described in the README: a graph connecting Wallapop tags, categories, and filters, and price estimates based on comparable Wallapop listings, excluding disproportionate outliers before selecting a relevant minimum and maximum.
