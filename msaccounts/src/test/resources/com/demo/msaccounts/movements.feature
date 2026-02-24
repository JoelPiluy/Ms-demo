Feature: Movement API Integration Tests - F2 y F3

  Background:
    * url 'http://localhost:8082/api'
    * header Content-Type = 'application/json'

  Scenario: F2 - Register a deposit successfully
    Given path '/movements'
    And request
      """
      {
        "accountNumber": "478758",
        "value": 500.00
      }
      """
    When method POST
    Then status 201
    And match response.typeMovement == 'DEPOSIT'
    And match response.valueMov == 500.00
    And match response.balance == '#notnull'
    And match response.accountNumber == '478758'

  Scenario: F2 - Register a withdrawal successfully
    Given path '/movements'
    And request
      """
      {
        "accountNumber": "478758",
        "value": -100.00
      }
      """
    When method POST
    Then status 201
    And match response.typeMovement == 'WITHDRAWAL'
    And match response.valueMov == -100.00
    And match response.balance == '#notnull'

  Scenario: F3 - Withdrawal with insufficient funds returns 422
    Given path '/movements'
    And request
      """
      {
        "accountNumber": "478758",
        "value": -999999.00
      }
      """
    When method POST
    Then status 422
    And match response.message == 'Saldo no disponible'

  Scenario: Movement on non-existent account returns 404
    Given path '/movements'
    And request
      """
      {
        "accountNumber": "000000",
        "value": 100.00
      }
      """
    When method POST
    Then status 404
    And match response.message contains 'Account'

  Scenario: Get all movements
    Given path '/movements'
    When method GET
    Then status 200
    And match response == '#array'

  Scenario: Get movements by account number
    Given path '/movements/account/478758'
    When method GET
    Then status 200
    And match response == '#array'
    And match each response == { id: '#notnull', typeMovement: '#notnull', valueMov: '#notnull', balance: '#notnull', accountNumber: '478758', dateMovement: '#notnull' }
