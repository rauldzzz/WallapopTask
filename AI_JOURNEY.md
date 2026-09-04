# AI Journey

> Working draft: this file records only events that have actually happened. The remaining personal reflections must be completed by the author before submission.

## Assistants used

I used OpenAI Codex to inspect the take-home brief and the existing React and Kotlin/Spring project structure. I also used it to write repository-specific development guidance in `AGENTS.md`, consolidate the frontend and backend `.gitignore` rules at the repository root, and prepare this working draft.

## Prompts that helped

Two prompts were useful during the repository setup:

1. I asked Codex to create an `AGENTS.md` tailored to the existing React frontend and Spring/Kotlin backend, with explicit rules about code quality, English comments, Gradle dependencies, IntelliJ environment variables, and API-key security. This turned broad expectations into a review checklist for later implementation.
2. I asked Codex to unify the existing `.gitignore` files at the repository root and create `AI_JOURNEY.md`. This helped remove duplicated ignore rules while preserving frontend, Gradle, Kotlin, and IDE exclusions.

## What did not work at first

While reading the PDF brief, the first text-extraction attempt failed because the Windows console encoding could not print one of the document's bullet characters. Codex reran the extraction with UTF-8 output and then reviewed all three pages successfully.

## A time the AI got something wrong

TODO: Add a real example from the implementation. Describe the incorrect suggestion or code, how you detected it through review or testing, and what you changed. Do not replace this with a fabricated example.

## What I do not fully understand yet

TODO: Add an honest technical area that you would like to understand better. A specific detail about Spring AI configuration, provider response handling, React state, or the test setup would be more useful than a generic statement.

## What I would improve with four more hours

TODO: Complete this after the working application is reviewed. Possible areas to assess include stronger malformed-model-response handling, more focused integration tests, accessibility checks, observability, or UI polish, but keep only the improvements you genuinely consider important.
