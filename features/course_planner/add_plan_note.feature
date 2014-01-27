@pending
Feature: BT.Add note to planner

  Background:
    Given I am logged in as admin


  Scenario: CS 1.5.1 Add notes to my plan from course search
    Given there are courses listed in the search list
    When I add the course with notes and term to myplan
    Then the course details with notes are displayed under view summary

  Scenario: CS 1.5.2 Edit the notes to my plan from the courses listed in the planner
    Given I work on scheduled planner
    When I edit the notes of a course under a current term
    Then the course should appear under current term with updated notes

