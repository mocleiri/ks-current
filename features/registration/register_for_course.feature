@wip @red_team
Feature: REG.Register for course

  As a student, I want to be able to add courses to my registration cart, including specifying
  course options where applicable. I also want to be able to edit and delete courses in my
  registration cart.

  Background:
    Given I am logged in as a Student

  #CR 1.1 (KSENROLL-11747)
  Scenario: I want to enter course information into my list of selections so that I can indicate what I want to register for.
    When I add a course offering to my registration cart
    Then the course is present in my cart

  #CR 1.2 (KSENROLL-11748)
  Scenario: I want to indicate course parameters at the time I enter course information so I can register with my preferred options
    When I add a course to my registration cart and specify course options
    Then the course is present in my cart, with the correct options

  Scenario: Student must be able to drop a course from their registration cart
    When I drop a course from my registration cart
    Then the course is not present in my cart

  Scenario: Student must be able to edit a course in their registration cart
    When I edit a course in my registration cart
    Then the modified course is present in my schedule
