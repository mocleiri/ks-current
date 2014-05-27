@wip

Feature: BT.Filter by Facet Requirement
  Background:
    Given I am logged in as a Student

  Scenario:CS 17.1:Verify course search results using Terms facet
    When I search for courses in the Course Search Page
    And I narrow the search results by a specific term
    Then I should see the courses scheduled for the specific term

  Scenario:CS 17.2:Verify search results in the course search page using Credits facet
    When I search for courses in the Course Search Page
    And I narrow the search results by a specific Credit
    Then I should see the courses with the specific Credit


  Scenario:CS 17.3:Verify search results in the course search page using Course level
    When I search for courses in the Course Search Page
    And I narrow the search results by a specific course level
    Then I should see the courses with the specific course level


  Scenario:CS 17.4:Verify search results in the course search page using Course prefix
    When I search for courses in the Course Search Page
    And I narrow the search results by a specific course prefix
    Then I should see the courses with the specific course prefix


  Scenario:CS 17.5 Verify that as a student I am able to undo any filtering performed using any facet
    When I narrow the search results in the course search page using any facet
    And I undo the filtering performed using the specified facet
    Then I should see the courses in the search results without any filtering being applied
