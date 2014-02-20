Feature: Create a Course Proposal as a Curriculum Specialist

  Background:
    Given I am logged in as Curriculum Specialist
  #  And I create a course proposal

  Scenario: Create Course Proposal from blank
    When I create a course proposal from blank
    Then I should see a blank course proposal

  Scenario: Cancel Create a Course proposal process
    When I should see the Initial Page
    Then I cancel create a course
    And I should see CM Home