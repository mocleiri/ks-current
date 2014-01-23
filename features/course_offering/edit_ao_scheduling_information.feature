@nightly @yellow_team
Feature: CO.Edit AO scheduling information as CSC

  As a schedule coordinator, I want to edit the requested scheduling information of an AO
  and then confirm that those changes have been persisted

  Background:
    Given I am logged in as a Schedule Coordinator
    And I am editing an AO with RSIs

  Scenario: Revise an AO's requested scheduling information
    When I revise an AO's requested scheduling information
    Then the AO's scheduling information shows the new schedule

  Scenario: Add standard RSIs for an AO
    When I add standard RSIs for an AO as a CSC
    Then the AO's scheduling information shows the new schedule

  Scenario: Add non-standard RSIs for an AO
    When I add non-standard RSIs for an AO as a CSC
    Then the AO's scheduling information shows the new schedule

  Scenario: Add an RSI using incomplete scheduling information - times only
    When I add RSIs for an AO specifying times only
    Then the AO's scheduling information shows the new schedule

  Scenario: Add an RSI using incomplete scheduling information - times and facility
    When I add RSIs for an AO specifying times and facility only
    Then the AO's scheduling information shows the new schedule

  Scenario: Add an RSI using incomplete scheduling information - times and room
    When I add RSIs for an AO specifying times and room only
    Then an error message is displayed about the required RSI fields

  @bug @KSENROLL-11518
  Scenario: Add TBA RSIs for an AO
    When I add RSIs for an AO checking the TBA flag
    Then the AO's scheduling information shows the new schedule as TBA

  Scenario: Delete RSIs for an AO
    When I add RSIs for an AO
    And I delete the original RSIs
    Then the AO's scheduling information shows the new schedule

  #CO 20.2 (KSENROLL-10318)
  Scenario: Set non-standard TS approval flag
    When I check the "approved for non-standard time slots" flag
    Then the "approved for non-standard time slots" flag is set
