@wip @red_team
Feature: REG.Wait List

  As a student I want to add or remove myself from a courses wait list.

  Background:
    Given I am logged in as a Student

  #KSENROLL-12345
  Scenario: CR 8.1 I want to add myself to the waitlist for a full registration group after I submit my cart so I can establish priority for a space when it becomes available
    When I register for a full course offering that has a waitlist
    Then there is a message indicating that the course is waitlisted
    And I am given the option to add myself to a waitlist for the course
    When I add myself to a waitlist for the course
    Then there is a message indicating that I have been added to the waitlist
    And I can verify I am on the waitlist by viewing my schedule

  #KSENROLL-12346
  Scenario: CR 8.2 As an administrator I do not want students to be able to waitlist for a registration group when a waitlist is not offered
    When I add a TBD course offering to my registration cart
