Feature: Archiver un colis
  En tant qu'utilisateur
  Je veux pouvoir archiver un colis
  Pour qu'il ne s'affiche plus dans ma liste active mais que l'action soit conservée

  Scenario: Archivage réussi d'un colis existant
    Given un colis avec le numéro de suivi "ABC123456" existe
    When j'archive le colis "ABC123456"
    Then le colis "ABC123456" doit avoir le statut "ARCHIVED" en base de données
