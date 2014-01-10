@nightly @green_team
Feature: WC.Display Schedule of Classes

  As an Admin I want to display a published schedule of classes for a specific term in order to
  understand the courses being offered, including both descriptive and scheduling info

  Background:
    Given I am logged in as a Schedule Coordinator
    And I am using the schedule of classes page
    Then the nearest valid future Term is chosen in the Term select list

  Scenario: Successfully display schedule of classes by subject code and display individual course details
    When I search for course offerings by course by entering a subject code
    Then a list of course offerings with that subject code is displayed
    And the course offering details for a particular offering can be shown

  Scenario: CO 26.9 Successfully display schedule of classes by subject code and display subterm information
    When I search for a course offering that has activity offerings assigned to subterms by course code
    And the course offering details for a particular offering can be shown
    And the subterm icon appears with the subterm information

  Scenario: Successfully display schedule of classes for a particular course and display course details
    When I search for course offerings by course by entering a course offering code
    Then a list of course offerings with that course offering code is displayed
    And the course offering details for a particular offering can be shown

  Scenario: Successfully display schedule of classes for a particular instructor and display course details
    When I search for course offerings by instructor
    Then a list of course offerings with activity offerings with that instructor is displayed

  Scenario: Successfully display schedule of classes for a particular department and display course details
    When I search for course offerings by department
    Then a list of course offerings for that department is displayed
    And the course offering details for a particular offering can be shown

  Scenario: Successfully display schedule of classes for a particular keyword for a course title or description search
    When I search for course offerings by title and department by entering a keyword
    Then a list of course offerings with that keyword is displayed
    And the course offering details for a particular offering can be shown

  #KSENROLL-9414
  Scenario: Confirm requisites' natural language for a particular course is displayed correctly
    When I search for course offerings by course to view the course offering requisites
    And I select a course that has existing course offering requisites
    Then the course offering requisites should be displayed alongside the course description

#KSENROLL-9524
#Create AFT scenarios to test for Co-located and Cross-listed icons and tool tips on Schedule of Classes
  @pending
  Scenario: Verify Co-located icons and tool tips on Schedule of Classes
    When I loaded the list of Schedule of Classes for term "Fall 2012" and Course "CHEM131S"
    Then the activity A of the course offering "CHEM131S" has a colocated icon
    And  the activity A of the course offering "CHEM131S" has tooltip text "This activity is colocated with:<BR>CHEM131 A"


  @pending
  Scenario: Verify Cross-listed icons and tool tips on Schedule of Classes
    When I loaded the Schedule of Classes for term "Fall 2012" and Course "ENGL250"
    Then the course offering "ENGL250" has cross listed icon
    And  the course offering "ENGL250" has tooltip text "this course is crosslisted with:<BR>WMST255"

  @pending
  Scenario: Verify additional info icons and tool tips on Schedule of Classes
    When I loaded the list of Schedule of Classes for term "Fall 2012" and Course "CHEM131S"
    Then the course offering "CHEM131S" has Audit grading option icon and tooltip popped up

