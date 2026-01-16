Feature: Polling des emails Gmail

  En tant qu'utilisateur
  Je veux que le système détecte les emails de livraison non lus
  Afin de les traiter automatiquement

  Scenario: Détection et traitement des emails de livraison
    Given que la boite Gmail contient un email non lu avec le sujet "Colis en approche"
    When le job de polling s'exécute
    Then l'email est identifié comme une livraison
    And l'email est marqué comme traité dans Gmail
