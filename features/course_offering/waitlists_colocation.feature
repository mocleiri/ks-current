@nightly @red_team
Feature: EC.Waitlists Colocation

  WL 1.17 - As a Central Administrator I want colocated activities to utilize a single waitlist
  when they share max enrollment so that students are processed in order

  Background:
    Given I am logged in as a Schedule Coordinator

  Scenario: WL 1.17.1 confirm shared waitlist settings for colocated actvity offerings with shared max enrollment
    Given I create an activity offering with the limit waitlist size set
    And there are two other activity offering with waitlists enabled and no waitlist limit
    When I colocate the activity offering with other two offerings and select shared maximum enrolment
    Then all three activity offerings have the same waitlist limit size

  Scenario: WL 1.17.2 Update waitlist settings for colocated activity offerings with shared max enrollment - verify changes are made to colocated set
    Given I create three course offerings with one activity offering in each with waitlists enabled
    And I colocate the three activity offerings (shared enrolment)
    When I make changes to the default waitlist configuration for one of the activity offerings
    Then all three colocated activity offerings have the same waitlist configuration

  Scenario:  WL 1.17.3 Verify waitlist configuration copied during rollover for colocated activity offerings
    Given I create an Academic Calendar and add an official term
    And I create three course offerings with one activity offering in each with waitlists enabled
    And I colocate the three activity offerings (shared enrolment)
    And I make changes to the default waitlist configuration for one of the activity offerings
    When I rollover the term to a new academic term
    Then the waitlist configuration is copied to the new colocated activity offerings in the target term

  Scenario: WL 1.17.4 Verify waitlist information is copied (within the same term) when copying a course offering with a colocated activity offering
    Given I create three course offerings with one activity offering in each with waitlists enabled
    And I colocate the three activity offerings (shared enrolment)
    And I make changes to the default waitlist configuration for one of the activity offerings
    When I copy the colocated AO's parent course offering
    Then the activity offering in the course offering copy is added to the colocated set
    And the waitlist configuration is copied to the colocated activity offering in the course offering copy

  Scenario: WL 1.17.5 Verify waitlist information is copied when copying a colocated activity offering
    Given I create three course offerings with one activity offering in each with waitlists enabled
    And I colocate the three activity offerings (shared enrolment)
    And I make changes to the default waitlist configuration for one of the activity offerings
    When I copy one of the colocated activity offerings
    Then the activity offering copy is added to the colocated set
    And the waitlist configuration is copied to the new colocated activity offering

  Scenario: WL 1.17.6 Verify waitlist confirguration for a colocated activity offering when using create CO by copy an Existing CO from a prior term
    Given there is an existing course offering with a colocated activity offering (shared enrolment) with waitlists enabled
    When I create a new course offering in a subsequent term by copying the existing course offering
    Then the activity offering in the course offering copy is not colocated
    And the waitlist configuration is copied to the new activity offering

  Scenario: WL 1.17.7 Delete activity offering in a colo-shared enrollment set
    Given I create three course offerings with one activity offering in each with waitlists enabled
    And I colocate the three activity offerings (shared enrolment)
    And I make changes to the default waitlist configuration for one of the activity offerings
    When I delete one of the colocated activity offerings
    Then the remaining activity offerings are still colocated
    And the remaining two activity offerings still have the same waitlist configuration

  Scenario: WL 1.17.8a Delete a Course Offering with an activity offering in a colocated-shared enrollment set
    Given I create three course offerings with one activity offering in each with waitlists enabled
    And I colocate the three activity offerings (shared enrolment)
    And I make changes to the default waitlist configuration for one of the activity offerings
    When I delete one of the related course offerings
    Then the remaining activity offerings for the other course offerings are still colocated
    And the remaining two activity offerings still have the same waitlist configuration

  Scenario: WL 1.17.8b Delete Course Offerings with an activity offering in a colocated-shared enrollment set
    Given I create three course offerings with one activity offering in each with waitlists enabled
    And I colocate the three activity offerings (shared enrolment)
    And I make changes to the default waitlist configuration for one of the activity offerings
    When I delete two of the related course offerings
    Then the remaining activity offering is no longer colocated
    But the remaining activity offering still has the same waitlist configuration

  Scenario: WL 1.17.9a Break colocation in a colocated-shared enrollment set (3 activity offerings)
    Given I create three course offerings with one activity offering in each with waitlists enabled
    And I colocate the three activity offerings (shared enrolment)
    And I make changes to the default waitlist configuration for one of the activity offerings
    When I break colocation on the first colocated AO
    Then the remaining activity offerings are still colocated
    And all three activity offerings have the same waitlist configuration
    When I deactivate waitlists on the first activity offering
    Then the waitlist configuration for the two remaining colocated activity offerings is not changed

  Scenario: WL 1.17.9b Break colocation in a colocated-shared enrollment set (2 activity offerings)
    Given I create two colocated activity offerings (shared enrolment) with waitlists enabled
    And I make changes to the default waitlist configuration for one of the activity offerings
    When I break colocation on the first colocated AO
    Then the remaining activity offering is no longer colocated
    And both activity offerings have the same waitlist configuration
    When I deactivate waitlists on the first activity offering
    Then the waitlist configuration for the second activity offering is not changed

  Scenario: WL 1.17.10 De-activate the waitlist option at the CO level for colocated AO (shared enrollment) and ensure that waitlists are deactivated
    Given I create two colocated activity offerings (shared enrolment) with waitlists enabled
    And I make changes to the default waitlist configuration for one of the activity offerings
    When I deactivate waitlists at the course offering level for one of the activity offerings
    Then the activity offerings are still colocated
    And waitlists is deactived for both activity offerings

  Scenario: WL 1.17.11 Add an activity offering to a colocated-shared enrollment set and ensure that the newly added activity offerring has the shared waitlist settings
    Given I create two colocated activity offerings (shared enrolment) with waitlists enabled
    And I make changes to the default waitlist configuration for one of the activity offerings
    When I add another activity offering to the colocated set
    Then the activities indicate they are colocated
    And all three colocated activity offerings have the same waitlist configuration

  Scenario: WL 1.17.12 Unshare a colocated-shared enrollment set and ensure that the waitlists are managed separately
    Given I create two colocated activity offerings (shared enrolment) with waitlists enabled
    When I update the colocation settings to manage enrolments separately
    And I make changes to the default waitlist configuration for one of the activity offerings
    Then the other activity offering still has the default configuration
