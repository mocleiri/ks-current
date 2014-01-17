@wip @red_team
Feature: EC.Register for course

  Background:
    Given I am logged in as a Student (?? admin in the demo)

  Scenario: Student must be able to add a course to their registration cart, selected from the term’s Schedule of Classes
    When I add a course (offering??) to my registration cart
    Then The course is present in my schedule

  Scenario: Student must be able to add a course to their registration cart, with options specified, selected from the term’s Schedule of Classes
    When I add a course to my registration cart and specify course options
    Then The course is present in my schedule, with the correct options

  Scenario: Student must be able to drop a course from their registration cart
    When I drop a course from my registration cart
    Then The course is not present in my schedule

  Scenario: Student must be able to edit a course in their registration cart
    When I edit a course in my registration cart
    Then The modified course is present in my schedule

  Scenario: (Persisting)

