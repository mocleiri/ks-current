@nightly @green_team
Feature: WC.Edit AO delivery logistics as CSC

  As a schedule coordinator, I want to edit the requested delivery logistics of an AO
  and then confirm that those changes have been persisted

  Background:
    Given I am logged in as a Schedule Coordinator
    And I am editing an AO with RDLs

  Scenario: Revise an AO's requested delivery logistics
    When I revise an AO's requested delivery logistics
    Then the AO's delivery logistics shows the new schedule

  Scenario: Add standard RDLs for an AO
    When I add standard RDLs for an AO as a CSC
    Then the AO's delivery logistics shows the new schedule

  Scenario: Add non-standard RDLs for an AO
    When I add non-standard RDLs for an AO as a CSC
    Then the AO's delivery logistics shows the new schedule

  Scenario: Add an RDL using incomplete logistics - times only
    When I add RDLs for an AO specifying times only
    Then the AO's delivery logistics shows the new schedule

  Scenario: Add an RDL using incomplete logistics - times and facility
    When I add RDLs for an AO specifying times and facility only
    Then the AO's delivery logistics shows the new schedule

  Scenario: Add an RDL using incomplete logistics - times and room
    When I add RDLs for an AO specifying times and room only
    Then an error message is displayed about the required RDL fields

  @bug @KSENROLL-11518
  Scenario: Add TBA RDLs for an AO
    When I add RDLs for an AO checking the TBA flag
    Then the AO's delivery logistics shows the new schedule as TBA

  Scenario: Delete RDLs for an AO
    When I add RDLs for an AO
    And I delete the original RDLs
    Then the AO's delivery logistics shows the new schedule

  #CO 20.2 (KSENROLL-10318)
  Scenario: Set non-standard TS approval flag
    When I check the "approved for non-standard time slots" flag
    Then the "approved for non-standard time slots" flag is set
