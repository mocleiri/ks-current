@nightly
Feature: BT.Add note to planner

  Background:

    Given I am logged in as a Student


  Scenario: CS 1.5.1 Add notes to my plan from course search
    Given The unscheduled planner
    When I search for a course from course search
    And  I add the course with notes and term to myplan
    Then the course with notes appears under the term on the planner


  Scenario: CS 1.5.2 Edit the notes to my plan from the courses listed in the planner
    Given I work on scheduled planner
    When I edit the notes of a course under a current term
    Then the course should appear under current term with updated notes

