@nightly
Feature: BT.Course Search

  Background:
    Given I am logged in as a Student

  Scenario: CS2.1.1 Successfully search for a course and clear search results
    When I search for a course
    Then the course should appear in the search results

    #KSAP-241, KSAP-321

   Scenario Outline: CS1.0.1 Successfully list any course with search text
    When I search for a course with "<text>" text option
    Then courses containing  "<expected>" text option appears
      Examples:
          | text   | expected |
          | ENGL   | ENGL     |
          | ENGL2XX| ENGL2    |
          | ENGLISH| ENGL     |


