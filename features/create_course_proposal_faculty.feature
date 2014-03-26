@draft
Feature: GT.Create a Course Proposal as Faculty

  Background:
    Given I am logged in as Fred


  Scenario: CC2.1 Create a course proposal required for approve fields and review
    When I complete the required fields on the course proposal
    Then I should see data in required fields for the course proposal
    And I perform a search for the proposal
    Then I can review the required fields on the course proposal

