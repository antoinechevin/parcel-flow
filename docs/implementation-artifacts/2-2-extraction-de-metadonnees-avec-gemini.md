# Story 2.2: Extraction de Métadonnées avec Gemini

Status: ready-for-dev

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->

## Story

As a **User**,
I want **the system to extract the tracking code, carrier, and expiration date from the sanitized email**,
so that **structured parcel information is created automatically and I don't have to enter it manually.**

## Acceptance Criteria

**Given** a raw email text
**When** sent to the Gemini Adapter via Spring AI
3. **Structured Output**: A `ParcelMetadata` object is returned containing:
    - `trackingCode`: String (e.g., "1Z999...")
    - `carrier`: String (e.g., "UPS", "La Poste")
    - `expirationDate`: Date (optional, future date)
    - `pickupLocation`: String (optional)
4. **Entity Creation**: A new `Parcel` entity is created and persisted in the database with the extracted data.
5. **Confidence Handling**: If key fields (trackingCode) are missing or confidence is low, handle gracefully (see Story 2.3 - but basics should be here).
6. **Integration**: Uses the configured `spring.ai.vertex.ai.gemini` settings.

## Tasks / Subtasks

- [x] **Domain Model**
  - [x] Create `ParcelMetadata` record (Domain DTO).
  - [x] Update `Parcel` entity if necessary to support all fields.
  - [x] Define `ParcelExtractionPort` interface (Secondary Port).
- [x] **Infrastructure: Gemini Adapter**
  - [x] Create `GeminiExtractionAdapter` implementing `ParcelExtractionPort`.
  - [x] Configure `ChatClient` with Spring AI.
  - [x] Design the Prompt Template for JSON extraction (using `BeanOutputConverter` or structured prompt).
  - [x] Implement error handling for AI timeouts/failures.
- [x] **Application Service**
  - [x] Create `ExtractParcelUseCase` (or update existing service).
  - [x] Orchestrate: Receive Email -> Call Gemini Adapter -> Save Parcel.
- [x] **Testing**
  - [x] Unit Test `GeminiExtractionAdapter` (Mock `ChatClient`).
  - [x] Integration Test with WireMock or Spring AI Mock for Vertex AI calls.
  - [x] ATDD: `parcel-extraction.feature`.

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

### Implementation Plan
- Domain: Add `ParcelMetadata` and update `Parcel` entity with `carrier`.
- Port: `ParcelExtractionPort` interface.
- Infrastructure: `GeminiExtractionAdapter` using `ChatClient`.
- Application: `ExtractParcelUseCase` for orchestration.
- Tests: Unit tests for logic + ATDD for flow.

### File List
- `backend/src/main/java/com/parcelflow/domain/model/ParcelMetadata.java`
- `backend/src/main/java/com/parcelflow/domain/model/Parcel.java` (modified)
- `backend/src/main/java/com/parcelflow/domain/ports/ParcelExtractionPort.java`
- `backend/src/main/java/com/parcelflow/domain/ports/ParcelRepositoryPort.java` (modified)
- `backend/src/main/java/com/parcelflow/infrastructure/ai/GeminiExtractionAdapter.java`
- `backend/src/main/java/com/parcelflow/infrastructure/config/AiConfig.java`
- `backend/src/main/java/com/parcelflow/infrastructure/config/ApplicationConfig.java` (modified)
- `backend/src/main/java/com/parcelflow/infrastructure/persistence/InMemoryParcelRepository.java` (modified)
- `backend/src/main/java/com/parcelflow/application/usecases/ExtractParcelUseCase.java`
- `backend/src/test/java/com/parcelflow/domain/model/ParcelMetadataTest.java`
- `backend/src/test/java/com/parcelflow/domain/model/ParcelTest.java`
- `backend/src/test/java/com/parcelflow/infrastructure/ai/GeminiExtractionAdapterTest.java`
- `backend/src/test/java/com/parcelflow/application/usecases/ExtractParcelUseCaseTest.java`
- `backend/src/test/resources/features/parcel-extraction.feature`
- `backend/src/test/java/com/parcelflow/steps/ParcelExtractionSteps.java`
- `backend/src/test/java/com/parcelflow/steps/CucumberConfiguration.java` (modified)
- `backend/src/test/java/com/parcelflow/CucumberTest.java` (modified)

### Change Log
- Implemented parcel metadata extraction using Gemini AI.
- Updated domain model to support carrier information.
- Added ATDD tests for extraction flow.
- Fixed code review findings: duplicate detection, data validation, and prompt improvements.

### Status
Status: done