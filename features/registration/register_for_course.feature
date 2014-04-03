@nightly @red_team
Feature: REG.Register for course

  As a student, I want to be able to add courses to my registration cart, including specifying
  course options where applicable. I also want to be able to edit and delete courses in my
  registration cart.

  Background:
    Given I am logged in as a Student

  #CR 1.1 (KSENROLL-11747)  CR 1.3 (KSENROLL-11812)
  Scenario: CR 1.1 I want to enter course information into my list of selections so that I can indicate what I want to register for.
    When I add a CHEM course offering to my registration cart
    Then the course is present in my cart
    And I can view the details of my selection in the registration cart

  #KSENROLL-11748
  Scenario: CR 1.2 I want to indicate course parameters at the time I enter course information so I can register with my preferred options
    When I add a course to my registration cart and specify course options
    Then the course is present in my cart, with the updated options

  #KSENROLL-11809
  Scenario: CR 1.4 I want to remove a course from my selections because I dont want to register for it anymore
    When I add an ENGL course offering to my registration cart
    And I remove the course from my registration cart
    Then the course is not present in my cart

  #KSENROLL-11810
  Scenario: CR 1.5 I want to change the course parameters for my selections so I can register with preferred options
    When I add a course offering having multiple credit options to my registration cart
    And I edit the course in my registration cart
    Then the course is present in my cart, with the updated options

  #KSENROLL-11811 KSENROLL-12072
  Scenario: CR 1.6 I want to submit my list of course selections so that I can register for them
            CR 4.1: I want to view the courses for which I am registered for a given term so that I am aware of my schedule.
    When I add a HIST course offering to my registration cart
    Then the course is present in my cart
    And I register for the course
    Then there is a message indicating successful registration
    When I view my schedule
    Then the course is present in my schedule
    And I can view the details of my selection in my schedule

  #KSENROLL-11922
  Scenario: CR 1.9 I want my course selections to persist so that I can return in another session and continue my registration process.
    When I add a PHYS course offering to my registration cart
    Then the course is present in my cart
    And I log out
    When I am logged in as a Student
    And I view my registration cart
    Then the course is present in my cart

  #KSENROLL-11923
  Scenario: CR 1.10 I want to reverse my decision to remove a course from my selections so that I can continue my registration process.
    When I add a BSCI1 course offering to my registration cart
    And I remove the course from my registration cart
    Then the course is not present in my cart
    Then I undo the drop action
    And the course is present in my cart

  #KSENROLL-12064
  Scenario: CR 5.1 I want to change the parameters for my registered courses so that I take the course with my preferred options.
    Given I have registered for a course having multiple credit options
    When I edit the course in my schedule
    Then the course is present in my schedule, with the updated options

  #KSENROLL-12065
  Scenario: CR 6.1 I want to drop a registered course so that I am no longer registered for it.
    Given I have registered for an ENGL course
    When I remove the course from my schedule
    Then the course is not present in my schedule

  #KSENROLL-12065
  Scenario: CR 6.1 I want to drop a registered course and cancel the drop so that I am still registered for it.
    Given I have registered for an ENGL course
    When I remove the course from my schedule and cancel the drop
    Then the course is present in my schedule

  #KSENROLL-12219
  Scenario: CR 7.1 I want to be notified if a registration group is full after I submit my cart so that I can adjust my registration appropriately
    When I register as student for a course offering with a seat capacity of one
    Then there is a message indicating successful registration
    And I log out
    When I am logged in as admin
    And I register as admin for a course offering with a seat capacity of one
    Then there is a message indicating that the course is full

  #KSENROLL-12353
  Scenario: I want my registration cart and schedule to accurately reflect the number of courses and credits I am registered for so that I can be aware of my credit load.
    When I add a WMST course offering to my registration cart
    Then I can view the number of courses and credits I am registered for in my registration cart
    When I add a BSCI2 course offering to my registration cart
    Then I can view the number of courses and credits I am registered for in my registration cart
    When I register for the course
    Then the number of courses and credits I am registered for is correctly updated in my registration cart
    When I view my schedule
    Then the number of courses and credits I am registered for is correctly updated in my schedule
    When I remove the BSCI2 course from my schedule
    Then the number of courses and credits I am registered for is correctly updated in my schedule after the drop

