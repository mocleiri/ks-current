@nightly @red_team
Feature: EC.Waitlists

  WL 1.3-14 As a Central Administrator I want to offer a waitlist for an activity offering so that students can queue for an open seat in a specific activity

  Background:
    Given I am logged in as a Schedule Coordinator

  Scenario: WL 1.3.1 Configure a new course offering so that all activity offerings have waitlists
    Given I create a Course Offering from catalog using the default waitlists option (enabled)
    When I add two activity offerings
    Then the activity offerings have active waitlists and waitlists have the default configuration

  Scenario: WL 1.3.2 Configure a new course offering so that activity offerings cannot have waitlists
    Given I create a Course Offering from catalog using the waitlists option disabled
    When I add an activity offering
    Then the waitlist option cannot be enabled for the activity offering

  Scenario: WL 1.4.1 Verify that waitlists can be disabled for a particular activity offering
    Given I manage an activity offering with waitlists enabled
    Then I can disable the waitlists option for the activity offering

  Scenario: WL 1.4.2 Verify that waitlist configuration is stored after the waitlist option is disabled
    Given I manage an activity offering with waitlists enabled
    And I make changes to the default waitlist configuration for the activity offering
    And I disable the waitlists option for the activity offering
    When I re-enable the waitlists option for the activity offering the modified waitlist configuration is restored

  Scenario: WL 1.7.1 Successfully set the waitlist limit size
    Given I manage an activity offering with waitlists enabled
    When I set the limit waitlist size
    Then the limit waitlist size is successfully updated

  Scenario: WL 1.7.2 Successfully modify the waitlist limit size
    Given I manage an activity offering with the limit waitlist size set
    When I modify the limit waitlist size
    Then the limit waitlist size is successfully updated

  Scenario: WL 1.7.3 Successfully remove the waitlist limit size
    Given I manage an activity offering with the limit waitlist size set
    When I remove the limit waitlist size
    Then the limit waitlist size is successfully updated to unlimited

  Scenario: WL 1.8.1 Successfully set the waitlist processing type to Manual
    Given I manage an activity offering with waitlists processing type set to Automatic
    Then I can update the processing type to Manual

  Scenario: WL 1.9.1 Successfully set the WL processing type to Automatic
    Given I manage an activity offering with waitlists processing type set to Confirmation
    Then I can update the processing type to Automatic

  Scenario: WL 1.10.1 Successfully set the WL processing type to Confirmation
    Given I manage an activity offering with waitlists processing type set to Automatic
    Then I can update the processing type to Confirmation

  Scenario: WL 1.11.1 Successfully enable the waitlist holds option
    Given I manage an activity offering with waitlists enabled
    When I enable the allow hold list option
    Then the allow hold list option is successfully updated

  Scenario: WL 1.11.2 Successfully disable the waitlist holds option
    Given I manage an activity offering with the waitlist allow hold list option enabled
    When I disable the allow hold list option
    Then the allow hold list option is successfully updated

  Scenario: WL 1.12 Verify waitlist configuration copied during rollover
    Given I create an Academic Calendar and add an official term
    And I create a course and activity offering with waitlists enabled
    And I make changes to the default waitlist configuration for the activity offering
    And I create a course and activity offering with waitlists disabled
    When I rollover the term to a new academic term
    Then the waitlist enabled configuration is copied to the new course and activity offering in the target term
    And the waitlist disabled configuration is copied to the course and activity offering in the target term

  Scenario: WL 1.13.1 - Verify waitlist information (enabled) is copied for copy course offering within the same term
    Given I create a course and activity offering with waitlists enabled
    And I make changes to the default waitlist configuration for the activity offering
    When I copy the course offering
    Then the waitlist configuration is copied to the new course and activity offering

  Scenario: WL 1.13.2 - Verify waitlist information (disabled) is copied for copy course offering within the same term
    Given I create a Course Offering from catalog using the waitlists option disabled
    And I add an activity offering
    When I copy the course offering
    Then the waitlists are disabled for the new course and activity offering

  #Scenario: WL 1.13.3 - Verify a new course offering from catalog has the default system waitlist setting - see WL 1.3.1

  Scenario: WL 1.13.4 - Verify waitlist information (enabled) when a course offering is created by copying an offering existing in a prior term
    Given there is an existing course offering with activity offerings that have waitlists enabled
    When I create a new course offering in a subsequent term by copying the existing course offering
    Then the waitlist configuration is copied to the new course and activity offerings

  #WL 1.14.1 able to see AO waitlist parameters - see see WL 1.3.1 etc
