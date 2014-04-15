@nightly @blue_team
Feature: CO.Perform Simple Rollover for Exam Offering Scheduling Information
  FE 4.1 As a Central Administrator I want to have on matrix exam offerings populated with requested scheduling
  information when created in bulk so that exam offering scheduling requests are consistent with the established exam matrix

  Background:
    Given I am logged in as admin

  #KSENROLL-12375
  Scenario: FE4.1.1 Exam Offering Slotting Info should populated or not after a rollover depending on whether the AO RSI data is found on the matrix
    Given I create an Academic Calendar and add an official term
    When I create multiple Course Offerings each with different Exam Offerings and Requested Scheduling Information
    And I rollover the term to a new academic term that has an exam period
    Then the Exam Offerings Slotting info should be populated or left blank depending on whether the AO RSI was found on the Exam Matrix

  #KSENROLL-12376
  Scenario: FE5.3.1 Rollover to new term so that the Mass Scheduling Event can be triggered which should then create the EOs for the COs
    Given I initiate a rollover to create a term in open state EC
    And I create Course Offerings that have approved Activity Offerings
    And I Lock the SOC
#    When