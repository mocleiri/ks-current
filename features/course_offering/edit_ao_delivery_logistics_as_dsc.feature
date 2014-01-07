@nightly @green_team
Feature: WC.Edit AO delivery logistics as DSC

  As a department schedule coordinator in a “needs-approval” institution,
  I want to edit the requested delivery logistics of an AO and then confirm that those changes are saved

  Background:
    Given I am logged in as a Department Schedule Coordinator

  #CO 20.2 (KSENROLL-10318)
  Scenario: Add standard RDLs for an AO with non-standard time slots not approved
    When I edit an Activity Offering with non-standard time slots not approved
    And I add standard RDLs for an AO as a DSC
    Then the AO's delivery logistics shows the new schedule

  #CO 20.2 (KSENROLL-10318)
  @bug @KSENROLL-11322
  Scenario: Add non-standard RDLs for an AO with non-standard time slots not approved
    When I edit an Activity Offering with non-standard time slots not approved
    And I attempt to add non-standard RDLs for an AO as a DSC
    Then there is a validation error on the EndTime field

  #CO 20.2 (KSENROLL-10318)
  Scenario: Add non-standard RDLs for an AO with non-standard time slots approved
    When I edit an Activity Offering with non-standard time slots approved
    And I add non-standard RDLs for an AO as a DSC
    Then the AO's delivery logistics shows the new schedule

  #CO 20.2 (KSENROLL-10318)
  Scenario: Add standard RDLs for an AO with non-standard time slots approved
    When I edit an Activity Offering with non-standard time slots approved
    And I add standard RDLs for an AO as a DSC
    Then the AO's delivery logistics shows the new schedule

  #CO 20.6 (KSENROLL-10317)
  Scenario: Add TBA RDLs for an AO
    When I am editing an AO with RDLs in an open term
    When I add RDLs for an AO checking the TBA flag
    Then the AO's delivery logistics shows the new schedule as TBA


