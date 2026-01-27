Feature: Extraction de métadonnées de colis
  En tant que système
  Je veux extraire les informations de colis depuis le contenu d'un email
  Afin de créer des colis structurés automatiquement

  Scenario: Extraction réussie d'un email UPS
    Given un email avec le contenu "Votre colis UPS 1Z12345 est arrivé au point Relais Colis. Vous avez jusqu'au 2026-02-15 pour le retirer."
    When le système traite cet email pour l'extraction
    Then un nouveau colis devrait être créé avec les informations suivantes:
      | trackingNumber | carrier | deadline   |
      | 1Z12345        | UPS     | 2026-02-15 |
