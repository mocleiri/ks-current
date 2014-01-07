@nightly @red_team
Feature: EC.Academic Calendar CRED

  Background:
    Given I am logged in as a Schedule Coordinator
    And I create an Academic Calendar

  Scenario: Create and save academic calendar from blank
    Then the Make Official button should become active

  Scenario: Search for newly created academic calendar
    And I search for the calendar
    Then the calendar should appear in search results

  Scenario: Make Academic Calendar Official
    When I make the calendar official
    And I search for the calendar
    Then the calendar should be set to Official

  Scenario: Copy an Academic Calendar
    And I copy the Academic Calendar
    Then the Make Official button should become active

  Scenario: Update Academic Calendar
    When I update the Academic Calendar
    Then the academic calendar should reflect the updates

  Scenario: Delete Draft Academic Calendar
    When I delete the Academic Calendar draft
    And I search for the calendar
    Then the calendar should not appear in search results

  Scenario: Add events on an Academic Calendar
    When I add events to the Academic Calendar
    Then the events are listed when I view the Academic Calendar

  Scenario: Update events on an Academic Calendar
    Given I add events to the Academic Calendar
    When I update the event dates
    Then the updated event dates are listed when I view the Academic Calendar

  Scenario: Delete events on an Academic Calendar
    Given I add events to the Academic Calendar
    When I remove the events from the Academic Calendar
    Then the event list is updated when I view the Academic Calendar

  Scenario: Add a Holiday Calendar to an Academic Calendar
    When I add a Holiday Calendar to the Academic Calendar
    Then the Holiday Calendar is listed when I view the Academic Calendar

  Scenario: Remove a Holiday Calendar from an Academic Calendar
    Given I add a Holiday Calendar to the Academic Calendar
    When I remove the Holiday Calendar
    Then the Holiday Calendar is not listed when I view the Academic Calendar

  Scenario: Search for Academic Calendar using wildcards
    When I search for the Academic Calendar using wildcards
    Then the calendar should appear in search results

  Scenario: Search for Academic Calendar using partial name
    When I search for the Academic Calendar using partial name
    Then the calendar should appear in search results

#TODO: date validations
#  Scenario: Verify warning message when adding an Event with date outside Academic Calendar date range
#  Scenario: Verify warning message when editing an Event with date outside Academic Calendar date range
#  Scenario: Verify warning message when adding an Event with start date after end date
#  Scenario: Verify error message when adding an Event with blank start date
#  Scenario: Verify error message when adding an Event with a start date which has an invalid format
#  Scenario: Verify error message when adding a Academic Calendar with blank start date
#  Scenario: Verify error message when adding a Academic Calendar with start date after end date
#  Scenario: Verify error message when editing a Academic Calendar with start date after end date
#  Scenario: Verify error message when editing a Academic Calendar with start date which has an invalid format