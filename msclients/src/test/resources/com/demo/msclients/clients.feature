Feature: Client API Integration Tests

  Background:
    * url 'http://localhost:8081/api'
    * header Content-Type = 'application/json'

  Scenario: Create a new client successfully
    Given path '/clients'
    And request
      """
      {
        "name": "Test Karate",
        "gender": "Masculino",
        "age": 25,
        "identification": "9999999999",
        "address": "Calle Test 123",
        "phone": "099999999",
        "clientId": "testkarate",
        "password": "1234",
        "status": true
      }
      """
    When method POST
    Then status 201
    And match response.name == 'Test Karate'
    And match response.clientId == 'testkarate'
    And match response.status == true
    And match response.id == '#notnull'

  Scenario: Get all clients
    Given path '/clients'
    When method GET
    Then status 200
    And match response == '#array'
    And match response[0].id == '#notnull'

  Scenario: Get client by ID
    Given path '/clients/1'
    When method GET
    Then status 200
    And match response.id == 1
    And match response.name == '#notnull'

  Scenario: Get client by clientId
    Given path '/clients/clientId/joselema'
    When method GET
    Then status 200
    And match response.clientId == 'joselema'
    And match response.name == 'Jose Lema'

  Scenario: Update a client
    Given path '/clients/1'
    And request
      """
      {
        "name": "Jose Lema Updated",
        "gender": "Masculino",
        "age": 36,
        "identification": "1001234567",
        "address": "Nueva Direccion 456",
        "phone": "098254785",
        "clientId": "joselema",
        "password": "1234",
        "status": true
      }
      """
    When method PUT
    Then status 200
    And match response.name == 'Jose Lema Updated'
    And match response.age == 36

  Scenario: Create client with duplicate clientId returns 422
    Given path '/clients'
    And request
      """
      {
        "name": "Duplicado",
        "gender": "Masculino",
        "age": 30,
        "identification": "8888888888",
        "address": "Calle Duplicado",
        "phone": "088888888",
        "clientId": "joselema",
        "password": "1234",
        "status": true,
        "created_by": "system"
      }
      """
    When method POST
    Then status 422
    And match response.message contains 'joselema'

  Scenario: Create client with invalid data returns 400
    Given path '/clients'
    And request
      """
      {
        "name": "",
        "gender": "",
        "age": -1,
        "identification": "",
        "address": "",
        "phone": "",
        "clientId": "",
        "password": "12",
        "status": true
      }
      """
    When method POST
    Then status 400
    And match response.errors == '#notnull'

  Scenario: Get non-existent client returns 404
    Given path '/clients/9999'
    When method GET
    Then status 404
    And match response.message contains 'Client'
