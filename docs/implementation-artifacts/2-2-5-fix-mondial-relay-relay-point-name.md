# Story: 2-2-5-fix-mondial-relay-relay-point-name

**ID:** 2-2-5-fix-mondial-relay-relay-point-name
**Epic:** Epic 2 - Le Cœur d'Extraction (Regex) & Protection de la Vie Privée
**Status:** ready-for-dev
**Priority:** HIGH
**Type:** BUG

## Description
Le nom du point relais Mondial Relay n'est pas correctement extrait dans les nouveaux formats d'emails "disponible". Actuellement, l'adapteur semble cibler uniquement les "Lockers" ou retourne une valeur par défaut "Mondial Relay Point".

## Acceptance Criteria
1. **Extraction Nom du Point Relais**: Le système doit extraire correctement le nom du point relais (ex: "CAMINHOS DE PORTUGAL SAIN-BEL") pour les emails de type "Point Relais" standard.
2. **Support JSON-LD**: Si possible, privilégier l'extraction via le bloc `<script type="application/ld+json">` qui contient la structure `ParcelDelivery` avec `deliveryAddress.name`, car c'est la source la plus robuste.
3. **Regex Fallback**: Mettre à jour la Regex de secours pour capturer le texte entre `Point Relais<sup>®</sup> <span...>` et le code postal/ville.
4. **Non-Régression**: Vérifier que l'extraction des numéros de suivi et des délais (expiration) fonctionne toujours.

## Technical Notes
- Email de référence reçu le 27 Jan 2026.
- Structure HTML cible : `Point Relais<sup>®</sup> <span style="color: #FF5C84; font-weight: bold;"> NOM_DU_POINT </span>`
- Structure JSON cible : `deliveryAddress.name`
- Fichier à modifier : `backend/src/main/java/com/parcelflow/infrastructure/extraction/MondialRelayExtractionAdapter.java`

## Tasks
- [ ] Créer un test unitaire avec le contenu de l'email fourni par Antoine (dans `MondialRelayExtractionAdapterTest`).
- [ ] Implémenter l'extraction via JSON-LD (prioritaire).
- [ ] Mettre à jour la Regex de fallback si le JSON-LD est absent.
- [ ] Valider que tous les tests de `MondialRelayExtractionAdapter` passent.
