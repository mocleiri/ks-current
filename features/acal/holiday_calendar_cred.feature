@nightly @red_team
Feature: EC.Holiday Calendar CRED

  Background:
    Given I am logged in as a Schedule Coordinator

  Scenario: Create and save Holiday Calendar from blank
    Given I create a Holiday Calendar
    When I search for the Holiday Calendar
    And the holiday calendar appears in the search results
    And The Make Official button for the Holiday Calendar should become active

  Scenario: Make Holiday Calendar Official
    Given I create a Holiday Calendar
    When I make the Holiday Calendar official
    And I search for the Holiday Calendar
    Then the holiday calendar appears in the search results
    And the holiday calendar is set to Official

  Scenario: Copy a Holiday Calendar from search
    Given I create a holiday calendar by copying an existing calendar from search
    And I search for the Holiday Calendar
    Then the holiday calendar appears in the search results
    And all holidays are copied successfully

  Scenario: Update Holiday Calendar
    Given I create a holiday calendar by copying an existing calendar from search
    When I update the name and date range for Holiday Calendar
    And I search for the Holiday Calendar
    Then the holiday calendar should reflect the updates

  Scenario: Delete Draft Holiday Calendar
    Given I create a holiday calendar by copying an existing calendar from search
    When I delete the Holiday Calendar draft
    And I search for the Holiday Calendar
    Then the holiday calendar does not appear in the search results

  Scenario: Successfully add holidays to a Holiday Calendar
    Given I create a Holiday Calendar
    When I add holidays to the Holiday Calendar
    Then the holidays are updated when I view the Holiday Calendar

  Scenario: Successfully update holidays on a Holiday Calendar
    Given I create a holiday calendar by copying an existing calendar from search
    When I update holiday dates
    Then the holiday dates are updated when I view the Holiday Calendar

  Scenario: Delete holidays on a Holiday Calendar
    Given I create a holiday calendar by copying an existing calendar from search
    When I remove holidays from the Holiday Calendar
    Then the holidays are updated when I view the Holiday Calendar

  Scenario: Search for Holiday Calendar using wildcards and paritial name
    Given I create a Holiday Calendar
    When I search for the Holiday Calendar using wildcards
    Then the holiday calendar appears in the search results
    When I search for the Holiday Calendar using partial name
    Then the holiday calendar appears in the search results

  Scenario: Verify warning message when adding a Holiday with date outside Holiday Calendar date range
    Given I create a Holiday Calendar
    And I add a new Holiday with an end date later than the Holiday Calendar end date
    Then a Holiday Dates warning message is displayed stating "doesn't fall within holiday calendar dates"

  Scenario: Verify warning message when editing a Holiday with date outside Holiday Calendar date range
    Given I create a holiday calendar by copying an existing calendar from search
    And I edit a Holiday date so that the start date is earlier than the Holiday Calendar start date
    Then a Holiday Dates warning message is displayed stating "doesn't fall within holiday calendar dates"

  Scenario: Verify error message when adding a Holiday with start date after end date
    Given I create a Holiday Calendar
    And I add a new Holiday with a date later than the Holiday end date
    Then a Holiday Dates error message is displayed stating "The end date for this holiday should not be earlier than the start date"

  Scenario: Verify warning message when adding a Holiday with blank start date
    Given I create a Holiday Calendar
    And I add a new Holiday with a blank start date
    Then a Holiday Dates warning message is displayed stating "Start date should not be empty"

  Scenario: Verify error when adding a Holiday with a start date which has an invalid format
    Given I edit a Holiday Calendar
    And I add a new Holiday with a start date with an invalid format
    Then the holiday start date field is highlighted for the error

  Scenario: Verify error message when adding a Holiday Calendar with start date after end date
    Given I create a holiday calendar with a start date after the end date
    Then a Holiday Calendar error message is displayed stating "invalid date range"

  Scenario: Verify error message when editing a Holiday Calendar with start date after end date
    Given I create a holiday calendar by copying an existing calendar from search
    And I edit the holiday calendar making the start date that is after the end date
    Then a Holiday Calendar error message is displayed stating "has invalid date range"
