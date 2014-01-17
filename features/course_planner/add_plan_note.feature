@pending
Feature: BT.Add note to planner

  Background:
    Given I am logged in as a Student


  Scenario: CS 1.5.1 Add notes to my plan from the courses listed in the course search
    Given there are courses listed in the search list
    When I add the course with notes and term to myplan
    Then the course should appear under selected term with information icon
    And the note added growl message should appear

  Scenario: CS 1.5.2 Add notes to my plan from the planner page
    Given I work on myplan
    When I add a course with notes and term to my plan
    Then the course should appear under selected term with information icon
    And the note added growl message should appear

  Scenario: CS 1.5.3 Edit the notes to my plan from the courses listed in the planner
    Given I work on myplan
    When I Edit the notes to my plan
    Then the course should appear under selected term with information icon
    And the note added growl message should appear

