@nightly @green_team
Feature: WC.Edit AO scheduling information as DSC

  As a department schedule coordinator in a “needs-approval” institution,
  I want to edit the requested scheduling information of an AO and then confirm that those changes are saved

  Background:
    Given I am logged in as a Department Schedule Coordinator

  #CO 20.2 (KSENROLL-10318)
  Scenario: Add standard RSIs for an AO with non-standard time slots not approved
    When I edit an Activity Offering with non-standard time slots not approved
    And I add standard RSIs for an AO as a DSC
    Then the AO's scheduling information shows the new schedule

  #CO 20.2 (KSENROLL-10318)
  @bug @KSENROLL-11322
  Scenario: Add non-standard RSIs for an AO with non-standard time slots not approved
    When I edit an Activity Offering with non-standard time slots not approved
    And I attempt to add non-standard RSIs for an AO as a DSC
    Then there is a validation error on the EndTime field

  #CO 20.2 (KSENROLL-10318)
  @bug @KSENROLL-11526
  Scenario: Add non-standard RSIs for an AO with non-standard time slots approved
    When I edit an Activity Offering with non-standard time slots approved
    And I add non-standard RSIs for an AO as a DSC
    Then the AO's scheduling information shows the new schedule

  #CO 20.2 (KSENROLL-10318)
  Scenario: Add standard RSIs for an AO with non-standard time slots approved
    When I edit an Activity Offering with non-standard time slots approved
    And I add standard RSIs for an AO as a DSC
    Then the AO's scheduling information shows the new schedule

  #CO 20.6 (KSENROLL-10317)
  Scenario: Add TBA RSIs for an AO
    When I am editing an AO with RSIs in an open term
    When I add RSIs for an AO checking the TBA flag
    Then the AO's scheduling information shows the new schedule as TBA


