@nightly @red_team
Feature: EC.Cancel Suspend Reinstate Colocated AOs

  CO 21.1: As an Administrator, I want to cancel one or more Colocated activity offerings so that they are no longer offered for the term

  Background:
    Given I am logged in as a Schedule Coordinator

  Scenario: CO 21.1B: CSR Verify that a user cannot cancel a co-located AO
    Given I manage a course offering with a colocated activity offering
    Then I am not able to cancel the activity offering

  Scenario: CO 21.1C1: CSR Verify that a user cannot colocate a canceled activity offering
    Given I manage a course offering with an activity offering in canceled status
    Then I am unable to colocate the activity offering

  @bug @KSENROLL-10663
  Scenario: CO 21.1D: CSR Verify that a user cannot submit a canceled activity offering to the scheduler
    Given I am working on a term in "Final Edits" SOC state
    And I manage a course offering with an activity offering in canceled status
    Then I am unable submit the activity offering to the scheduler

  Scenario: CO 21.1E1: CSR Verify when a course offering in canceled status is copied the copy is in draft status
    When I copy a course offering in canceled status
    Then the course offering copy is in draft status

  Scenario: CO 21.1E2: CSR Verify when an activity offering in canceled status is copied the copy is in draft status
    Given I manage a course offering with an activity offering in canceled status
    When I copy the activity offering
    Then the activity offering copy is in draft status

  Scenario: CO 21.1F: CSR Verify Course and Activity Offerings in a rollover source term are changed to draft status in the rollover target term
    Given a new academic term has course and activity offerings in canceled and suspended status
    When I rollover the term to a new academic term
    Then the course and activity offerings in the rollover target term are in draft status

#  @draft @not_delivered
#  Scenario: CO 21.1G: CSR Verify a canceled Activity Offerings can be excluded in Course Offering create from existing
#    Given I manage a course offering with an activity offering in canceled status
#    When the course offering is copied to a subsequent term excluding canceled activity offerings
#    Then the canceled activity offering is not copied

  Scenario: CO 21.1H: CSR Verify that for Course Offering create from existing an Activity Offering in canceled status becomes Draft status in the copy
    Given there is an existing course offering with an activity offering in canceled status
    When I create a new course offering in a subsequent term by copying the existing course offering
    Then the canceled activity offering copy is in draft status

  Scenario: CO 22.1A1 CSR Check Suspend option availability in draft open and locked SOC states
    Given I am working on a term in "Draft" SOC state
    Then an activity offering in draft status cannot be suspended
    Given I am working on a term in "Open" SOC state
    Then an activity offering in draft status cannot be suspended
    Given I am working on a term in "Locked" SOC state
    Then an activity offering in draft status can be suspended

  Scenario: CO 22.1A2 CSR Suspend a draft Activity Offering in a published SOC state
    Given I am working on a term in "Published" SOC state
    When I manage a course offering with a draft activity offering
    Then I can suspend the activity offering

  Scenario: CO 22.1A3 CSR Suspend approved Activity Offering with ADLs
    Given I am working on a term in "Final Edits" SOC state
    When I manage a course offering with an approved activity offering
    Then I suspend the activity offering
    And actual delivery logistics for the Approved activity offering are still shown

  Scenario: CO 22.1A4 CSR Suspend a canceled Activity Offering in a published SOC state
    Given I am working on a term in "Published" SOC state
    When I manage a course offering with a canceled activity offering
    Then I am not able to suspend the activity offering

  Scenario: CO 22.1B1 CSR Verify that RDLs for a suspended Activity Offering can be sent to the scheduler in SOC state final edits
    Given I am working on a term in "Final Edits" SOC state
    And I create a course offering from catalog with a suspended activity offering
    When I add requested delivery logistics to the activity offering
    Then I am able to send the activity offering to the scheduler
    And the actual delivery logistics are displayed for the updated activity offering

  Scenario: CO 22.1B2 CSR Verify that RDLs for a suspended Activity Offering can be sent to the scheduler in SOC state published
    Given I am working on a term in "Published" SOC state
    And I create a course offering from catalog with a suspended activity offering
    When I add requested delivery logistics to the activity offering
    Then I am able to send the activity offering to the scheduler
    And the actual delivery logistics are displayed for the updated activity offering

  Scenario: CO 22.1C1: CSR Verify when a course offering in suspended status is copied the copy is in draft status
    When I copy a course offering in suspended status
    Then the course offering copy is in draft status

  Scenario: CO 22.1C2: CSR Verify when an activity offering in suspended status is copied the copy is in draft status
    Given I manage a course offering with a suspended activity offering
    When I copy the activity offering
    Then the activity offering copy is in draft status
