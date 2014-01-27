@nightly @yellow_team
Feature: CO.Create Standard Time Slots

  As a Scheduling Coordinator, I want to create the Standard Time Slots for a particular Time Slot Type so that
  university scheduling policies will be enforced.

  Background:
    Given I am logged in as a Schedule Coordinator

  Scenario: Adding a standard time slot for a single time slot type
    When I add 2 different time slots to a single term type
    Then the timeslots are saved

  Scenario: Adding a standard time slot for multiple time slot types
    When I add a single time slot per 2 different term types
    Then the timeslots are saved

  Scenario: Adding a non-unique standard time slot
    When I add a duplicate time slot
    Then an error message is displayed about the duplicate timeslot

  Scenario: Adding a standard time slot using incomplete data - omitting start time
    When I add a new time slot but omit the start time
    Then an error is displayed about the missing start time

  Scenario: Adding a standard time slot using incomplete data - omitting end time
    When I add a new time slot but omit the end time
    Then an error is displayed about the missing end time

  Scenario: Adding a standard time slot using incomplete data - omitting days
    When I add a new time slot but omit the days
    Then an error is displayed about the missing days

