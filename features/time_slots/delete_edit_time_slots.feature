@nightly @green_team
Feature: WC.Delete and edit standard Time Slots

  As a Scheduling Coordinator, I want to edit existing Standard Time Slots for
  a particular Time Slot Type so that university scheduling policies will be enforced.

  Background:
    Given I am logged in as a Schedule Coordinator

    Scenario: Filtering term type for time slot editing
    When I show time slots for a single term type
    Then only time slots of that term type appear

    Scenario: Deleting a time slot that has not been used
    When I show time slots for a single term type
    And I add a Time Slot with the chosen Term Type
    Then the timeslots are saved
    And I attempt to delete the Time Slot added above
    Then the Time Slot is deleted

    Scenario: Deleting a time slot that has been used
    When I show time slots for a single term type
    And I attempt to delete a Time Slot which is used in a delivery logistic
    And the Time Slot is not deleted

    Scenario: Deleting multiple time slots - some of which have been used
    When I show time slots for a single term type
    And I add a Time Slot with the chosen Term Type
    Then the timeslots are saved
    And I attempt to delete the Time Slot added above and also a Time Slot used in a delivery logistic
    Then the first Time Slot is deleted
    And the second Time Slot is not deleted

    Scenario: Ability to edit a time slot that has not been used
    When I show time slots for a single term type
    And I add a Time Slot with the chosen Term Type
    Then the timeslots are saved
    And I attempt to edit the Time Slot added above
    Then the Time Slot edits are saved

    Scenario: Ability to edit a time slot that has been used
    When I show time slots for a single term type
    And I attempt to edit a Time Slot which is used in a delivery logistic
    Then the Time Slot edits are not saved

    Scenario: Changing the term type of a time slot
    When I show time slots for multiple term types
    And I add a Time Slot with one of the chosen Term Types
    Then the timeslots are saved
    And I edit the Time Slot added above to use the other chosen Term Type
    Then the Time Slot type edits are saved

    Scenario: Editing a standard time slot using incomplete data - omitting start time
    When I show time slots for a single term type
    And I add a Time Slot with the chosen Term Type
    Then the timeslots are saved
    And I attempt to edit the Time Slot omitting the start time
    Then an error is displayed about the missing data

    Scenario: Editing a standard time slot using incomplete data - omitting end time
    When I show time slots for a single term type
    And I add a Time Slot with the chosen Term Type
    Then the timeslots are saved
    And I attempt to edit the Time Slot omitting the end time
    Then an error is displayed about the missing data

    Scenario: Editing a standard time slot using incomplete data - omitting days
    When I show time slots for a single term type
    And I add a Time Slot with the chosen Term Type
    Then the timeslots are saved
    And I attempt to edit the Time Slot omitting the days
    Then an error is displayed about the missing data

    Scenario: Editing an existing time slot that results in duplication
    When I add 2 different time slots to a single term type
    Then the timeslots are saved
    And I edit the second Time Slot to duplicate the first Time Slot
    Then an error message is displayed about the duplicate timeslot
