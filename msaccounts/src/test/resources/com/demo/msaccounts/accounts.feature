Feature: Account API Integration Tests

  Background:
    * url 'http://localhost:8082/api'
    * header Content-Type = 'application/json'

  Scenario: Create account successfully
    Given path '/accounts'
    And request
      """
      {
        "accountNumber": "999001",
        "accountType": "SAVINGS",
        "initialBalance": 1000.00,
        "status": true,
        "clientId": "joselema"
      }
      """
    When method POST
    Then status 201
    And match response.accountNumber == '999001'
    And match response.accountType == 'SAVINGS'
    And match response.availableBalance == 1000.00
    And match response.status == true
    And match response.clientId == 'joselema'

  Scenario: Create account with non-existent client returns 404
    Given path '/accounts'
    And request
      """
      {
        "accountNumber": "999999",
        "accountType": "SAVINGS",
        "initialBalance": 500.00,
        "status": true,
        "clientId": "noexiste"
      }
      """
    When method POST
    Then status 404
    And match response.message contains 'Client'

  Scenario: Create account with duplicate account number returns 422
    Given path '/accounts'
    And request
      """
      {
        "accountNumber": "999001",
        "accountType": "CHECKING",
        "initialBalance": 500.00,
        "status": true,
        "clientId": "joselema"
      }
      """
    When method POST
    Then status 422
    And match response.message contains '999001'

  Scenario: Get all accounts
    Given path '/accounts'
    When method GET
    Then status 200
    And match response == '#array'

  Scenario: Get account by number
    Given path '/accounts/number/999001'
    When method GET
    Then status 200
    And match response.accountNumber == '999001'

  Scenario: Get non-existent account returns 404
    Given path '/accounts/number/000000'
    When method GET
    Then status 404
    And match response.message contains 'Account'

  Scenario: Update account
    Given path '/accounts/number/999001'
    When method GET
    Then status 200
    * def accountId = response.id

    Given path '/accounts/' + accountId
    And request
      """
      {
        "accountNumber": "999001",
        "accountType": "SAVINGS",
        "initialBalance": 1000.00,
        "status": false,
        "clientId": "joselema"
      }
      """
    When method PUT
    Then status 200
    And match response.status == false
