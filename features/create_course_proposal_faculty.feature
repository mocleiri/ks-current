@draft
Feature: GT.Create a Course Proposal as Faculty

  Background:
    Given I am logged in as Fred


  Scenario: CC2.1 Create a course proposal with required for approve fields and review
    When I complete the required fields on the course proposal
    Then I should see data in required fields for the course proposal
    And I perform a full search for the course proposal
    Then I can review the required fields on the course proposal

