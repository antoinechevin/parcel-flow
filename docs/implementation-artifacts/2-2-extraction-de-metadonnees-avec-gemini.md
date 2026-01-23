# Story 2.2: Extraction de Métadonnées avec Gemini

Status: ready-for-dev

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->

## Story

As a **User**,
I want **the system to extract the tracking code, carrier, and expiration date from the sanitized email**,
so that **structured parcel information is created automatically and I don't have to enter it manually.**

## Acceptance Criteria

1. **Input**: A sanitized email text (from Story 2.1).
2. **AI Processing**: The text is sent to the Gemini Adapter via Spring AI.
3. **Structured Output**: A `ParcelMetadata` object is returned containing:
    - `trackingCode`: String (e.g., "1Z999...")
    - `carrier`: String (e.g., "UPS", "La Poste")
    - `expirationDate`: Date (optional, future date)
    - `pickupLocation`: String (optional)
4. **Entity Creation**: A new `Parcel` entity is created and persisted in the database with the extracted data.
5. **Confidence Handling**: If key fields (trackingCode) are missing or confidence is low, handle gracefully (see Story 2.3 - but basics should be here).
6. **Integration**: Uses the configured `spring.ai.vertex.ai.gemini` settings.

## Tasks / Subtasks

- [ ] **Domain Model**
  - [ ] Create `ParcelMetadata` record (Domain DTO).
  - [ ] Update `Parcel` entity if necessary to support all fields.
  - [ ] Define `ParcelExtractionPort` interface (Secondary Port).
- [ ] **Infrastructure: Gemini Adapter**
  - [ ] Create `GeminiExtractionAdapter` implementing `ParcelExtractionPort`.
  - [ ] Configure `ChatClient` with Spring AI.
  - [ ] Design the Prompt Template for JSON extraction (using `BeanOutputConverter` or structured prompt).
  - [ ] Implement error handling for AI timeouts/failures.
- [ ] **Application Service**
  - [ ] Create `ExtractParcelUseCase` (or update existing service).
  - [ ] Orchestrate: Receive Email -> Call Sanitizer (Stub for now or integrate if ready) -> Call Gemini Adapter -> Save Parcel.
- [ ] **Testing**
  - [ ] Unit Test `GeminiExtractionAdapter` (Mock `ChatClient`).
  - [ ] Integration Test with WireMock or Spring AI Mock for Vertex AI calls.
  - [ ] ATDD: `parcel-extraction.feature`.

## Dev Notes

### Architecture & Tech Stack
- **Spring AI**: Use `spring-ai-vertex-ai-gemini-spring-boot-starter`.
- **Structured Output**: Use Spring AI's `BeanOutputParser` to guarantee JSON format from Gemini.
- **Hexagonal**: The Domain should not know about "Gemini", just `ParcelExtractionPort`.

### Prompt Engineering
- The prompt is critical. Use a few-shot approach or clear instructions.
- Example Prompt: "Extract shipping details from this email. Return JSON with keys: trackingCode, carrier, expirationDate...".

### Configuration
- Ensure `spring.ai.vertex.ai.gemini.project-id` and `location` are properly loaded (already in `application.properties`).
- You might need `GOOGLE_APPLICATION_CREDENTIALS` for local dev if not using the same OAuth setup as Gmail (Vertex AI usually uses Service Account). **Check this point.**

### Dependencies
- `spring-ai-vertex-ai-gemini-spring-boot-starter` is already in `pom.xml`.

## Dev Agent Record

### Agent Model Used

Gemini 2.0 Flash

### File List