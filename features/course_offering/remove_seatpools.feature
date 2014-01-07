@nightly @red_team
Feature: EC.Remove seat pools

As an Administrator, I want to remove one of the seat pools from the Activity Offering 
since this seat pool is no longer needed.

  Background:
    Given I am logged in as a Schedule Coordinator
    #Given I am managing a course offering

  Scenario: Remove seat pool and ensure seat pool priorities are properly re-sequenced
    When I edit an existing activity offering with 3 seat pools
    And I remove the seat pool with priority 1
    Then the seats remaining is updated
    And the activity offering is updated when saved
    And the seat pool is removed
    And the seat pool priorities are re-sequenced

  @pending @bug @KSENROLL-7196
  Scenario: Remove all seat pools
    When I edit an existing activity offering with 3 seat pools
    And I remove all seat pools
    Then the seats remaining is updated
    And the activity offering is updated when saved
    And the seat pools are removed
