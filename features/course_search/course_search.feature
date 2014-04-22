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
  Scenario Outline: CS10 Successfully search for a course and change the pagination options
    When I search for a course with multi word"<multi_text>" text option
    And I choose to see "<per_page>" records per page
    Then The table header text will be "<header_text>"
    And There will be <pages> pages of results with <total_per_page> records per page
    But Pagination controls will not be visible if there is only 1 page
  Examples:
    | multi_text       | per_page  | header_text                                      | pages | total_per_page |
    | english history  |  20       | Showing 1-20 of 200 results for english history  | 5     |  20            |
    | english history  |  50       | Showing 1-50 of 200 results for english history  | 4     |  50            |
    | english history  | 100       | Showing 1-100 of 200 results for english history | 2     | 100            |
    | greek mythology  |  20       | Showing 1-7 of 7 results for greek mythology     | 1     |   7            |

#************************* KSAP-818, US- 620**********************************************************************************

  Scenario Outline: 8.1 Verify searches for multiple compound searches return the correct results.
    When I search for "<text>"
    Then "<expected_courses>" and courses matching at least one "<expected_component>" are returned
  Examples:
    | text                    |expected_courses                 | expected_component                                  |
    | Engl Hist               |                                 | Engl,Hist                                           |
    | Engl201 Hist360         | Engl201,Hist360                 | none                                                |
    | Engl 201 Hist 360       | Engl201,Engl360,Hist201,Hist360 | Engl,Hist,201,360                                   |
    | "Engl 201" "Hist 360"   | Engl201,Hist360                 | none                                                |
    | Engl Hist 201           | Engl201,Hist201                 | Engl,Hist,201                                       |
    | Engl2XX Hist3XX         |                                 | Engl200,Hist300,Engl2XX,Hist3XX                     |
    | Engl Hist 2XX           |                                 | Engl200,Hist200,Engl,Hist,2XX                       |
    | Engl 2XX Hist 2XX       |                                 | Engl200,Hist200,Engl,Hist,2XX                       |
    | "Engl 2XX" "Hist 3XX"   |                                 | Engl200,Hist300,Engl 2XX,Hist 3XX                   |
    | Engl 2XX Hist 3XX       |                                 | Engl200,Engl300,Hist200,Hist300,Engl,Hist,2XX,3XX   |

#*************************  KSAP-851, US-  837*********************************************************************************************************
# Hard-coding expected result. As the CO data from  enrollment is yet to be implemented in KSAP course details page.
# Changes need to implemented---> Verification of the text of CO data on KSAP course details page should be created

  Scenario Outline: 8.2.1- Successfully list the courses having any of the the search text present in the  course code or course title or course description
       When I search for a course with "<combined_multi_text>" option
       Then the "<expected_courses>" and courses matching at least one "<expected_component>" are returned
  Examples:
    | combined_multi_text  |expected_courses        | expected_component                 |
    | Rome HIST            |ENGL379                 | Rome,Hist                          |
    | History 200          |ENGL278                 | Hist,History,200                   |
    | Organic 3xx          |                        | organic,3xx                        |
    | modern WMST          |ENGL278,ENGL379,ENGL329 | WMST,modern,ENGL329                |


  Scenario Outline: 8.2.2: To List the courses having the search text with special character ("",',_)
    When I search for a course with "<combined_multi_text>" option
   Then the "<expected_courses>" and courses matching at least one "<expected_component>" are returned
  Examples:
    |combined_multi_text      |expected_courses |expected_component                         |
    |introduction ENGL 278    |ENGL278          | introduction,Engl,278                     |
    |play's ENGL              |                 | play,Engl                                 |
    |"western literature" HIST|ENGL379          | Hist,western literature                   |
    |eighteenth_century HIST  |ENGL379          | Hist,eighteenth_century,eighteenth,century|

