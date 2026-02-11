# Story 4.6: Mode Démo Contextuel

Status: done

## Story

En tant que **Développeur/Présentateur**,
je souhaite **basculer entre mes données réelles et des données de démonstration** directement depuis la page de connexion ou le tableau de bord,
afin de **pouvoir présenter toutes les fonctionnalités de l'application sans dépendre de l'état de mon compte personnel.**

## Acceptance Criteria

1. [x] **Accès Démo** : Un bouton "Essayer la démo" est présent sur la page de connexion.
2. [x] **Isolation des Données** :
    * Lorsque l'utilisateur est connecté normalement, le hook `useDashboard` effectue un appel `fetch` réel vers Railway.
    * Lorsque le mode démo est actif, le hook ignore le réseau et renvoie immédiatement un jeu de données mockées.
3. [x] **Indicateur Visuel** : Un composant `DemoBanner` centralisé affiche "MODE DÉMO ACTIF" sur toutes les pages concernées.
4. [x] **Persistance** : Le choix du mode démo est sauvegardé localement (Zustand persist) pour ne pas être perdu au rechargement.
5. [x] **Sécurité** : Le mode démo n'envoie aucune donnée réelle au backend (actions d'archivage simulées localement).
6. [x] **Retour à la réalité** : Un bouton "QUITTER DÉMO" est disponible dans le header du dashboard pour revenir à la page de connexion.

## Tasks / Subtasks

- [x] **Store**: Ajouter le booléen `isDemoMode` dans `src/core/auth/authStore.ts`.
- [x] **Données**: Créer un fichier de constantes `src/core/api/__mocks__/mockData.ts` contenant les objets `LocationGroup` de démonstration.
- [x] **Logic**: Adapter `src/hooks/useDashboard.ts` pour retourner les données mocks si `isDemoMode` est actif (gestion du cas sans apiKey).
- [x] **Logic**: Mettre à jour `app/_layout.tsx` pour gérer la redirection d'authentification incluant le mode démo.
- [x] **UI**: Ajouter le bouton "Essayer la démo" dans `app/login.tsx`.
- [x] **UI**: Centraliser la bannière d'avertissement dans `src/components/DemoBanner.tsx`.
- [x] **UX**: Ajouter `KeyboardAvoidingView` sur la page de login pour les mobiles.

## Dev Agent Record

### Implementation Plan
- Integrated `isDemoMode` into the Zustand `authStore` with persistence via `AsyncStorage`.
- Created a robust set of `MOCK_PARCELS` in `__mocks__/mockData.ts`.
- Refactored `useDashboard` to support data loading even without an API key if demo mode is on.
- Updated `_layout.tsx` to treat `isDemoMode` as a valid authentication state.
- Implemented `DemoBanner` component for visual consistency.
- Improved `login.tsx` with `KeyboardAvoidingView` and better accessibility.

### Completion Notes
- All tests pass (Unit & Integration).
- UI is optimized for mobile (Safe Areas, Keyboard, Haptics).
- The transition between demo and real mode is now robust.

## File List
- `frontend/src/core/auth/authStore.ts`
- `frontend/src/core/auth/authStore.test.ts`
- `frontend/src/core/api/__mocks__/mockData.ts`
- `frontend/src/hooks/useDashboard.ts`
- `frontend/src/hooks/useDashboard.test.ts`
- `frontend/src/components/DemoBanner.tsx`
- `frontend/app/index.tsx`
- `frontend/app/login.tsx`
- `frontend/app/_layout.tsx`
- `frontend/jest.setup.js`

## Change Log
- **2026-02-05**: Initial implementation.
- **2026-02-11**: Improved accessibility, centralized banner, and fixed redirection logic.