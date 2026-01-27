Feature: Extraction Vinted Go

  Scenario: Extraction réussie d'un email de disponibilité Vinted Go
    Given le contenu du fichier email "emails/mail_vinted_go.eml"
    When je tente l'extraction des données colis via l'adaptateur Vinted Go
    Then le fournisseur détecté est "Vinted Go"
    And le code de retrait est "E32782"
    And la date limite est "2025-12-17"
    And le numéro de suivi est "1764156123430443"
    And la description du lieu contient "Les Casiers Des Saveurs"