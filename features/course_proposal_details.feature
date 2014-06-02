@draft
Feature: Add details to a course proposal

  Scenario: CC5.1 Add cross list, joint offer, version code details to a Course Proposal
    Given I am logged in as Faculty
    When I create a proposal with alternate identifier details
    And I perform a full search for the course proposal
    Then I should see alternate identifier details on the course proposal