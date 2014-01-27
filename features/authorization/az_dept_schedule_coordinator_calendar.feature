@nightly @yellow_team
Feature: CO.AZ Dept Schedule Coordinator Calendar

  Background:
    Given I am logged in as a Department Schedule Coordinator

  Scenario: Verify that the Academic Calendar search page is read only for Department Schedule Coordinator
    When I search for academic calendars
    Then I should be able to view the calendars
    And I should not be able to edit a calendar
    And I should not be able to copy a calendar

  Scenario: Verify that the Holiday Calendar search page is read only for Department Schedule Coordinator
    When I search for holiday calendars
    Then I should be able to view the calendars
    And I should not be able to edit a calendar
    And I should not be able to copy a calendar

  Scenario: Verify that the Academic Term search page is read only for Department Schedule Coordinator
    When I search for academic terms
    Then I should be able to view the terms
    And I should not be able to edit a term

