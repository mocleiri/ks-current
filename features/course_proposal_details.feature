@draft
Feature: Add details to a course proposal

  Scenario: CC5.1 Add cross list, joint offer, version code details to a Course Proposal
    Given I am logged in as Faculty
    When I create a proposal with alternate identifier details
    And I perform a full search for the course proposal
    Then I should see alternate identifier details on the course proposal


  Scenario: CC5.2 Edit Alternate Identifier details on a Course Proposal
    Given I am logged in as Curriculum Specialist
    And I have a basic course proposal with alternate identifier details
    When I update Alternate Identifier details on the course proposal
    And I perform a full search for the course proposal
    Then I should see updated alternate identifier details on the course proposal

  Scenario: CC5.3 Delete alternate identifier details on a Course Proposal
    Given I am logged in as Faculty
    And I have a basic course proposal with alternate identifier details
    When I delete alternate identifier details to the course proposal
    And I perform a full search for the course proposal
    Then I should see alternate identifier details on the course proposal