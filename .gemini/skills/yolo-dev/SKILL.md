---
name: yolo-dev
description: Développe et livre une User Story de bout en bout de manière autonome. À utiliser lorsque l'utilisateur fournit un nom de story et souhaite une implémentation complète (branche, dev, tests, review, push) sans intervention manuelle.
---

# Yolo Dev Skill

Cette skill automatise le cycle complet de développement d'une User Story en mode #yolo. Elle enchaîne les phases de création de branche, d'implémentation, de test, de revue de code et de livraison.

## Workflow de Développement Automatisé

Lorsqu'une story est spécifiée, suivez rigoureusement ces étapes :

### 1. Préparation de l'environnement
- Extraire le nom de la story fourni par l'utilisateur.
- Créer une nouvelle branche git locale avec un nom normalisé (ex: `feat/nom-de-la-story`).
- **Commande :** `git checkout -b feat/<story-name>`

### 2. Implémentation de la Story
- Invoquer le workflow BMAD de développement en mode #yolo.
- **Commande :** `/dev-story <story-name> #yolo`
- *Note : Le mode #yolo permet de sauter les confirmations et d'avancer de manière autonome.*

### 3. Première phase de validation
- Exécuter l'intégralité des tests du projet pour s'assurer que l'implémentation est correcte et n'introduit pas de régression.
- **Backend :** `./mvnw test` (dans `/backend`)
- **Frontend :** `npm test` (dans `/frontend`)
- **Correction :** Si des tests échouent, analysez les erreurs et corrigez le code jusqu'à ce que **TOUS** les tests passent.

### 4. Revue de code
- Une fois les tests au vert, lancez le workflow de revue de code.
- **Commande :** `/code-review #yolo`
- Analyser les rapports de revue générés.

### 5. Ajustements et Raffinement
- Implémenter tous les ajustements et corrections suggérés lors de la revue de code.
- Assurer la conformité avec les standards du projet (Hexagonal Architecture, Gherkin, etc.).

### 6. Validation finale
- Exécuter à nouveau la suite complète de tests (Backend et Frontend).
- **TOUS** les tests doivent être au vert avant de passer à l'étape suivante.

### 7. Livraison (Commit & Push)
- Ajouter tous les fichiers modifiés.
- Créer un commit avec un message clair et descriptif.
- Pousser la branche vers le dépôt distant.
- **Commandes :**
  - `git add .`
  - `git commit -m "feat: implement <story-name> and addressed code review findings"`
  - `git push origin feat/<story-name>`

## Directives Critiques
- **Autonomie :** Utilisez le flag `#yolo` sur les commandes slash pour minimiser les interruptions.
- **Rigueur :** Ne sautez JAMAIS la phase de test. Un succès partiel n'est pas une option.
- **Qualité :** Respectez les patterns architecturaux définis dans `docs/project-context.md`.
