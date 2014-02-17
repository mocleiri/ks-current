
Feature: Create a Course Proposal as Faculty

  Background:
    Given I am logged in as Fred
    And I create a course proposal

  Scenario: Create Course Proposal from blank
    When I create a course proposal from blank
    Then I should see data in the proposal title on course information
    And I should see data in the course title on course information

  Scenario: Create a course proposal with only required fields
    Given I complete the required fields on the course proposal
    Then I should see data in required fields for the course proposal

