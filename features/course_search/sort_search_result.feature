@nightly
Feature: BT.Search_result_sort

  Background:
         Given I am logged in as a Student


  Scenario Outline: CS 14.1.1 Successfully sorting the course Title in both ascending and descending order.
    When I search for a course on course search
    Then I sort the table by title
    Then the course Title listed should be sorted in "<Expected>" order
  Examples:
    |Expected|
    |Descending|
    |Ascending|

  Scenario Outline: CS 14.1.2 Successfully sorting the course Title in both ascending and descending order.
    When I search for a course on course search
    Then I sort the table by title
    Then the course Title listed should be sorted in "<Expected>" order
  Examples:
    |Expected|
    |Ascending|
    |Descending|




  @pending
  Scenario Outline: CS 14.2.1
   ***************************************************************************************************************************************************
   KSAP- 1000
   Feature : Logical order  display validation is implemented for this particular input combination ["Subjectcode-1, level-1, subjectcode-2,level-2"]
   Logical order to be followed  for this example  (ENGl, 206, HIST, 360)
  ---------------------------------------------
   1. ENGL206 should appear as the first result
   2. ENGL360 should appear as the first result
   3. HIST206 should appear as the second result
   4. HIST360 should appear as the second result
   5. All other courses with a subject code of ENGL, such as ENGL310
   6. All other courses with a subject code of HIST, such as HIST208
   7. All other courses with a number of 206 (none)
   8. All other courses with a number of 360, such as WMST360
   9. All courses with engl in the title or description (none- covered by HIST results)
   10. All course offerings with engl in the title or description (none- covered by HIST results)
   11. All courses with hist in the title or description, such as BSCI434
   12. All course offerings with hist in the title or description WMST400
   13. All courses with 206 in the title or description (none)
   14. All course offerings with 206 in the title or description (none)
   15. All courses with 360 in the title or description (none)
   16. All course offerings with 360 in the title or description (none)
   ********************************************************************************************************************************************************

    When I search for a "<text>" having divisions and levels with space in the search text
    Then the courses containing the "<text>" should be displayed in logical order
  Examples:
    |text             |
    |ENGL 206 HIST 360|


