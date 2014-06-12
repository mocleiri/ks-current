@nightly @red_team

Feature: REG.Max Credits

  CR 10 - As an Administrator, I want the system to validate that a student's registered Activity Offerings
          do not exceed the maximum number of credits set by the institution so the student does not violate
          policy and have their registration changed.

  Background:
    Given I am using a mobile screen size
    Given I log in to student registration as student2

#KSENROLL-13019
  Scenario: CR 10.1  As an admin, I want to prevent students from registering for more credits
                     than the institutional credit limit so they do not violate registration policy
            CR 10.2  As a student, I want to be informed of any registration transactions that are not successful
                     due to the enforcement of the credit limit so that I can change my registration accordingly
    When I add courses to my registration cart that would exceed the spring term credit limit
    And I attempt to register for the courses
    Then there is a message indicating that registration failed
    And there is a message indicating that I have registered for a credit amount over the credit limit
    And I cannot register for another course

  #KSENROLL-13024
  @wip
  Scenario: CR 10.5 - As an administrator, I want the ability to have different max credits for different
  types of terms so I can manage enrollment for the term
    When I add courses to my registration cart that would exceed the summer term credit limit
    And I attempt to register for the courses
    Then there is a message indicating that registration failed
    And there is a message indicating that I have registered for a credit amount over the credit limit
    And I cannot register for another course
