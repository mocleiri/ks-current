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

#************************* KSAP-821, US- KSAP-622**********************************************************************************
  @draft
  Scenario Outline: CS10 Successfully search for a course and change the pagination options
    When I search for a course with multi word"<multi_text>" text option
    And I choose to see "<per_page>" records per page
    Then The table header text will be "<header_text>"
    And There will be <pages> pages of results with <total_per_page> records per page
    But Pagination controls will not be visible if there is only 1 page
  Examples:
    | multi_text       | per_page  | header_text                                      | pages | total_per_page |
    | english history  |  20       | Showing 1-20 of 141 results for english history  | 5     |  20            |
    | english history  |  50       | Showing 1-50 of 141 results for english history  | 3     |  50            |
    | english history  | 100       | Showing 1-100 of 141 results for english history | 2     | 100            |
    | greek mythology  |  20       | Showing 1-7 of 7 results for greek mythology     | 1     |   7            |

#************************* KSAP-818, US- 620**********************************************************************************
  @draft
  Scenario: 8.1.1 - Successfully search for multiple divisions
    When I search for "Engl Hist"
    Then only courses of divisions "Engl,Hist" are returned

  @draft
  Scenario Outline: 8.1.2,3,4,7 Verify searches for multiple course codes returns the correct results.
    When I search for "<text>"
    Then only the courses "<expected>" are returned
  Examples:
    | text                  | expected                         |
    | Engl201 Hist360       | Engl201,Hist360                  |
    | Engl 201 Hist 360     | Engl201,Engl360,Hist201,Hist360  |
    | "Engl 201" "Hist 360" | Engl201,Hist360                  |
    | Engl Hist 201         | Engl201,Hist201                  |

  @draft
  Scenario Outline: 8.1.5,6,8,9,10 Verify searches for multiple course codes returns the correct results.
    When I search for "<text>"
    Then only "<expected>" level courses are returned
  Examples:
    | text                  | expected                         |
    | Engl2XX Hist3XX       | Engl200,Hist300                  |
    | Engl Hist 2XX         | Engl200,Hist200                  |
    | Engl 2XX Hist 2XX     | Engl200,Hist200                  |
    | "Engl 2XX" "Hist3XX"  | Engl200,Hist300                  |
    | Engl 2XX Hist 3XX     | Engl200,Engl300,Hist200,Hist300  |