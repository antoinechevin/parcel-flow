Feature: Gestion des Colis

  Scenario: Création réussie d'un colis manuel
    Given le catalogue de colis est vide
    When je crée un colis avec le numéro de suivi "TRK-123456" et le label "Mon Mac"
    Then le colis est enregistré avec succès
    And le statut par défaut est CREATED

  Scenario: Récupération de la liste des colis
    Given le catalogue contient le colis "TRK-123" nommé "Livres"
    And le catalogue contient le colis "TRK-456" nommé "Clavier"
    When je demande la liste de tous les colis
    Then je reçois une liste contenant 2 colis
    And la liste contient "Livres" et "Clavier"

  Scenario: Refus de création si données invalides
    When je tente de créer un colis sans numéro de suivi
    Then une erreur de validation est levée "ID required"
