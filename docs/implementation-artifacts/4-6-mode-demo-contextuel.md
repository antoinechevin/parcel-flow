# Story 4.6: Mode Démo Contextuel

Status: ready-for-dev

## Story

En tant que **Développeur/Présentateur**,
je souhaite **basculer entre mes données réelles et des données de démonstration** directement depuis le tableau de bord,
afin de **pouvoir présenter toutes les fonctionnalités de l'application sans dépendre de l'état de mon compte personnel.**

## Acceptance Criteria

1. [ ] **Bouton de Switch** : Un interrupteur (ou bouton) est présent dans le Header de l'écran principal (`Mes Colis`).
2. [ ] **Isolation des Données** :
    * Lorsque le switch est sur **OFF**, le hook `useDashboard` effectue un appel `fetch` réel vers Railway.
    * Lorsque le switch est sur **ON**, le hook ignore le réseau et renvoie immédiatement un jeu de données mockées (ex: 1 colis urgent, 1 colis expiré).
3. [ ] **Indicateur Visuel** : Un badge ou une mention "MODE DÉMO" est visible sur la liste lorsque les données simulées sont affichées.
4. [ ] **Persistance** : Le choix du mode démo est sauvegardé localement (Zustand persist) pour ne pas être perdu au rechargement.
5. [ ] **Sécurité** : Le mode démo n'envoie aucune donnée réelle au backend, même si l'utilisateur effectue un "Swipe to Archive" (l'action doit être simulée en local).

## Tasks / Subtasks

- [ ] **Store**: Ajouter le booléen `isDemoMode` dans `src/core/auth/authStore.ts`.
- [ ] **Données**: Créer un fichier de constantes `src/core/api/mockData.ts` contenant les objets `LocationGroup` de démonstration.
- [ ] **Logic**: Adapter `src/hooks/useDashboard.ts` pour retourner les données mocks si `isDemoMode` est actif.
- [ ] **UI**: Ajouter le composant `Switch` dans les `headerRight` de `Stack.Screen` dans `app/index.tsx`.
- [ ] **UI**: Ajouter une bannière `Surface` ou un badge en haut de la liste pour prévenir que ce sont des données de test.

## Project Structure Notes

- `frontend/src/core/auth/authStore.ts`
- `frontend/src/hooks/useDashboard.ts`
- `frontend/app/index.tsx`
- `frontend/src/core/api/mockData.ts` (Nouveau)
