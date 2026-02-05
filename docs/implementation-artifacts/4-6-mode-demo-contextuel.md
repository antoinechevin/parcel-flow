# Story 4.6: Mode Démo Contextuel

Status: review

## Story

En tant que **Développeur/Présentateur**,
je souhaite **basculer entre mes données réelles et des données de démonstration** directement depuis le tableau de bord,
afin de **pouvoir présenter toutes les fonctionnalités de l'application sans dépendre de l'état de mon compte personnel.**

## Acceptance Criteria

1. [x] **Bouton de Switch** : Un interrupteur (ou bouton) est présent dans le Header de l'écran principal (`Mes Colis`).
2. [x] **Isolation des Données** :
    * Lorsque le switch est sur **OFF**, le hook `useDashboard` effectue un appel `fetch` réel vers Railway.
    * Lorsque le switch est sur **ON**, le hook ignore le réseau et renvoie immédiatement un jeu de données mockées (ex: 1 colis urgent, 1 colis expiré).
3. [x] **Indicateur Visuel** : Un badge ou une mention "MODE DÉMO" est visible sur la liste lorsque les données simulées sont affichées.
4. [x] **Persistance** : Le choix du mode démo est sauvegardé localement (Zustand persist) pour ne pas être perdu au rechargement.
5. [x] **Sécurité** : Le mode démo n'envoie aucune donnée réelle au backend, même si l'utilisateur effectue un "Swipe to Archive" (l'action doit être simulée en local).

## Tasks / Subtasks

- [x] **Store**: Ajouter le booléen `isDemoMode` dans `src/core/auth/authStore.ts`.
- [x] **Données**: Créer un fichier de constantes `src/core/api/mockData.ts` contenant les objets `LocationGroup` de démonstration.
- [x] **Logic**: Adapter `src/hooks/useDashboard.ts` pour retourner les données mocks si `isDemoMode` est actif.
- [x] **UI**: Ajouter le composant `Switch` dans les `headerRight` de `Stack.Screen` dans `app/index.tsx`.
- [x] **UI**: Ajouter une bannière `Surface` ou un badge en haut de la liste pour prévenir que ce sont des données de test.

## Dev Agent Record

### Implementation Plan
- Integrated `isDemoMode` into the Zustand `authStore` with persistence via `AsyncStorage`.
- Created a robust set of `MOCK_PARCELS` in `mockData.ts` reflecting real-world scenarios (Urgent vs Expired).
- Intercepted `fetchParcels` and `executeArchive` in `useDashboard` to redirect to mock data when demo mode is active.
- Added a `Switch` in the header of `app/index.tsx` for easy toggling.
- Added a high-visibility red banner at the top of the list when in demo mode.

### Completion Notes
- All tests pass, including new unit tests for the store and hook.
- The UI is responsive and provides clear feedback when in demo mode.
- Mock data includes realistic ISO dates for expiration logic testing.

## File List
- `frontend/src/core/auth/authStore.ts`
- `frontend/src/core/auth/authStore.test.ts`
- `frontend/src/core/api/mockData.ts`
- `frontend/src/hooks/useDashboard.ts`
- `frontend/src/hooks/useDashboard.test.ts`
- `frontend/app/index.tsx`
- `frontend/jest.setup.js` (Added AsyncStorage mock)

## Change Log
- **2026-02-05**: Initial implementation of Contextual Demo Mode.

## Project Structure Notes

- `frontend/src/core/auth/authStore.ts`
- `frontend/src/hooks/useDashboard.ts`
- `frontend/app/index.tsx`
- `frontend/src/core/api/mockData.ts` (Nouveau)
