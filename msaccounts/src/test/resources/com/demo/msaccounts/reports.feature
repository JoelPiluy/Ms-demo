Feature: Report API Integration Tests - F4

  Background:
    * url 'http://localhost:8082/api'
    * header Content-Type = 'application/json'

  Scenario: F4 - Get account statement for existing client
    Given path '/reports'
    And param date = '2022-01-01,2026-12-31'
    And param client = 'joselema'
    When method GET
    Then status 200
    And match response.clientId == 'joselema'
    And match response.clientName == '#notnull'
    And match response.accounts == '#array'

  Scenario: F4 - Get account statement for Marianela
    Given path '/reports'
    And param date = '2022-01-01,2026-12-31'
    And param client = 'marianelamont'
    When method GET
    Then status 200
    And match response.clientId == 'marianelamont'
    And match response.accounts == '#array'

  Scenario: F4 - Report for non-existent client returns 404
    Given path '/reports'
    And param date = '2022-01-01,2026-12-31'
    And param client = 'noexiste'
    When method GET
    Then status 404
    And match response.message contains 'Client'

  Scenario: F4 - Report with invalid date format returns 400
    Given path '/reports'
    And param date = 'fecha-invalida'
    And param client = 'joselema'
    When method GET
    Then status 400
