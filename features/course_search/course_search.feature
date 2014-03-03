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

  Scenario Outline: CS3.1 Verify searches for specific course codes returns the correct results.
    When I search for a course with "<text>" text option
    Then courses containing  "<expected>" text option appears
  Examples:
    | text     | expected |
    | ENGL799  | ENGL799  |
    |"ENGL 799"| ENGL799  |
    | ENGL 799 | ENGL799  |



#*******************KSAP- 762, US KSAP- 615,616,617***********************************************************************

  Scenario Outline: CS4.1.1 Successfully list any course title with one word search text options
    When I search for a course with one word"<text>" text option
    Then course title or course description containing "<text>"text option "should" appear
  Examples:
    | text    	    |
    | Shakespeare   |
    | bio		    |
    | ENGL2         |
    | ENGL23        |


  Scenario Outline: CS4.1.2 Successfully list any course title with multi word search text options
    When I search for a course with multi word"<multi_text>" text option
    Then course code or course title or course description containing any word of "<multi_text>"text option "should" appear
  Examples:
    |multi_text   	              |
    # Single page search with 2 words
    |Shakespeare beekeeping       |
    # Multi page search with 2 words
    |Organic marine               |
    # Multi page search with partial 2 words
    |eng  lit                     |
    #Multi page search with 3 words
    |Inorganic ecology beekeeping |

#************************* KSAP_ 819, US- 618**********************************************************************************

  Scenario:6.1- Successfully list any course with the search level
    When I search for a course with "2xx" level option
    Then only "200" level courses "should" be displayed
