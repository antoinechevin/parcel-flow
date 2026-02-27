# User Story 2-2-7 : Robustesse et Auto-Correction (Self-Healing)

**Statut :** `ready-for-dev`
**Épique :** Epic 2 - Extraction de métadonnées avec Gemini (et Fallbacks déterministes)

## Description
En tant que mainteneur du système Parcel-Flow, je veux que le système soit résilient aux changements de format de mail imprévus par Chronopost, afin d'assurer la continuité de service et de corriger les erreurs en un temps record.

## Contexte Métier
Les formats de mail changent sans prévenir. Un échec d'extraction ne doit pas être silencieux ou corrompre les données. Le système doit s'auto-diagnostiquer pour faciliter sa propre réparation.

## Critères d'Acceptation (AC)
1.  **Validation de Contrat (Guard Clauses) :** Après chaque extraction par Regex, le résultat doit être validé (non nul, < 60 caractères, format texte cohérent).
2.  **Mode Dégradé (Buffer Capture) :** Si la Regex précise échoue, le système doit tenter d'extraire un bloc de texte brut (ex: les 100 caractères suivant le mot-clé "Relais" ou "Point de retrait").
3.  **Champ `fallbackContent` :** L'agrégat `Parcel` doit pouvoir stocker ce contenu brut en cas d'échec de l'extraction précise pour affichage à l'utilisateur (affichage : "Relais non identifié précisément, voici le texte du mail : [...]").
4.  **Auto-Génération d'Artifact :** En cas d'échec total (`EXTRACTION_FAILED`), le système génère un fichier `docs/implementation-artifacts/corrections/US_CORRECTION_REQUIRED_[ID].md`.
    *   Le fichier doit contenir : l'objet du mail, un snippet anonymisé du corps, le nom de la stratégie qui a échoué.
5.  **Log Structuré :** Émettre un log structuré avec le tag `EXTRACTION_ERROR` pour alerting.

## Directives pour l'Agent Dev
- Ajouter une couche de validation dans le service d'extraction de métadonnées.
- Implémenter le mécanisme de capture de zone (Regex plus large) en tant que dernier recours (fallback).
- Utiliser un template Markdown pour la génération de l'artifact de correction.
- Stocker les artifacts de correction dans un dossier dédié ignorable par `.gitignore` si nécessaire, ou versionnable pour suivi.
