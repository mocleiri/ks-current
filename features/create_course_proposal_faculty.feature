@nightly
Feature: GT.Create a Course Proposal as Faculty

  Background:
    Given I am logged in as Fred


  Scenario: CC2.1 Create a course proposal with only required fields
    When I complete the required fields for save on the new course proposal
    Then I should see data in required for save fields for the course proposal
    And I perform a complete search for the Course Proposal
    Then I should see my proposal listed in the search result
    And I can review my proposal

