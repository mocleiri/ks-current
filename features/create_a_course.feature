
Feature: Create a Course Proposal

  Background:
    Given I am logged in as admin
#    And I create a course proposal

  Scenario: Create a course proposal
    When I create a course proposal
  # Then I should see the Initial Page
    Then I should see data in the proposal title on course information
    And I should see data in the course title on course information

  Scenario: Create a course proposal with only required fields
    Given I complete the required fields on the course proposal
    Then I should see data in required fields for the course proposal

  Scenario: Create a course proposal with all fields completed
#    Given I complete require fields on the course proposal
    When I complete all the fields on the course proposal
    Then I should see data in required fields for the course proposal
    And I should see data in all non required fields for the course proposal

  Scenario:  Save a course proposal with all fields completed with advanced search
    When I complete all fields on the course proposal with advanced search
    Then I should see data in required fields for the course proposal
    And I should see data in all non required fields for the course proposal

  Scenario:  Save a course proposal with all fields completed with auto-lookup
    When I complete all fields on the course proposal with auto-lookup
    Then I should see data in required fields for the course proposal
    And I should see data in all non required fields for the course proposal








