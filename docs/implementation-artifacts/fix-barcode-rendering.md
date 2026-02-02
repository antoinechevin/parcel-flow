---
id: "fix-barcode-rendering"
title: "Fix Barcode Rendering (Aztec & Phantom QR)"
epic: "epic-2"
story_type: "bugfix"
status: "done"
created: "2026-02-02"

Description: |
  **Contexte** : 
  L'application affiche actuellement des QR Codes génériques pour tous les colis. 
  - Pour Chronopost, le backend extrait une URL d'Aztec Code, mais le frontend tente de le rendre comme une image ou fallback sur un QR généré localement qui est invalide (car le contenu est une URL d'image Aztec, pas un texte à encoder en QR).
  - Pour Mondial Relay, aucun code n'est extrait (juste un PIN/Tracking), mais le frontend génère un "QR Code fantôme" à partir du numéro de tracking, ce qui est trompeur et inutile au guichet.

  **Objectif** : 
  1. Supporter explicitement le type de code barre (QR, AZTEC, CODE_128, NONE).
  2. Afficher correctement les Aztec Codes (via l'URL extraite).
  3. Ne JAMAIS générer de QR Code si le type est NONE (cas Mondial Relay actuel).

Acceptance Criteria:
  - rule: "Chronopost Aztec Display"
    given: "Un colis Chronopost avec une URL Aztec extraite"
    when: "J'ouvre le mode guichet"
    then: "Je vois l'image du code Aztec OU un message d'erreur si l'image charge mal"
    and: "Je ne vois PAS de QR Code généré localement"
  
  - rule: "Mondial Relay No-Code"
    given: "Un colis Mondial Relay sans URL de code-barres"
    when: "J'ouvre le mode guichet"
    then: "Je vois le numéro de suivi et le PIN en gros"
    and: "Je ne vois AUCUN code-barres (ni image, ni généré)"

  - rule: "VintedGo QR Display"
    given: "Un colis VintedGo avec une URL QR extraite"
    when: "J'ouvre le mode guichet"
    then: "Je vois l'image du QR Code"

  - rule: "Data Persistence"
    given: "Un nouvel email est ingéré"
    when: "L'extraction réussit"
    then: "Le champ 'barcodeType' est correctement peuplé et persisté en BDD"

Tasks/Subtasks:
  - title: "Domain & Backend Model Update"
    subtasks:
      - title: "[x] Add BarcodeType Enum to Domain and Parcel Entity"
        description: "Enum values: QR_CODE, AZTEC, CODE_128, NONE. Add field to Parcel and ParcelMetadata."
      - title: "[x] Update DTOs and Mappers"
        description: "Ensure BarcodeType flows from Extraction to API response."
      - title: "[x] Update Extraction Adapters"
        description: "Chronopost -> AZTEC; VintedGo -> QR_CODE; MondialRelay -> NONE."
      - title: "[x] Add Migration/Update DB Schema"
        description: "Add column barcode_type to parcels table (default NONE)."

  - title: "Frontend Implementation"
    subtasks:
      - title: "[x] Update Parcel Type Definition"
        description: "Add barcodeType field to TypeScript interface."
      - title: "[x] Refactor GuichetModeModal Logic"
        description: "Implement switch logic based on barcodeType. Remove automatic fallback to generated QR."
      - title: "[x] Implement Aztec Rendering Strategy"
        description: "Render image from URL. Handle error state (show 'Code non affichable, utiliser PIN')."

Dev Notes: |
  **Architecture** :
  - On touche au `ParcelExtractionPort` -> `ParcelMetadata`.
  - On touche à l'API `ParcelController`.
  
  **Décisions techniques** :
  - Pas de génération locale d'Aztec pour l'instant (trop complexe sans lib lourde). On se repose sur l'URL fournie par le provider.
  - Pour Mondial Relay, on assume NONE pour l'instant car on n'a pas de logique d'extraction de Code 128 fiable.

Dev Agent Record:
  Debug Log: []
  Completion Notes: []

File List: []
Change Log: []
---
