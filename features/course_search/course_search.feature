@nightly
Feature: BT.Course Search

  Background:
    Given I am logged in as a Student

  Scenario: CS2.1.1 Successfully search for a course and clear search results
    When I search for a course
    Then the course "should" appear in the search results

    #KSAP-241, KSAP-321

   Scenario Outline: CS1.0.1 Successfully list any course with search text
    When I search for a course with "<text>" text option
    Then courses containing  "<expected>" text option appears
      Examples:
          | text   | expected |
          | ENGL   | ENGL     |
          | ENGL2XX| ENGL2    |
          | ENGLISH| ENGL     |


  Scenario Outline: CS4.1 Verify course search filters results correctly by term selection and course offering status
    When I search for a "<course_status>" "<course>" by "<term_selection>"
    Then the course "<expected_result>" appear in the search results
  Examples:
    |course_status | course  |term_selection  |expected_result |
    |  scheduled   | ENGL206 |Spring 2014     | should         |
    |  scheduled   | ENGL206 |Scheduled terms | should         |
    |  unscheduled | BSCI103 |Spring 2014     | should not     |
    |  unscheduled | BSCI103 |Scheduled terms | should not     |

  #KSAP-692
  @draft
  Scenario Outline: CS3.1 Verify searches for specific course codes returns the correct results.
    When I search for a course with "<text>" text option
    Then courses containing  "<expected>" text option appears
  Examples:
    | text     | expected |
    | ENGL799  | ENGL799  |
    |"ENGL 799"| ENGL799  |

    @draft @wip
  Scenario: CS 3.2 Verify search for course and code separated by spaces returns the correct results
    When I search for a course and code separated by spaces
    #Then the results should return the specific course code.
    And also return results for search by course and search by code



