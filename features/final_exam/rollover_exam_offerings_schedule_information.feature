@nightly @blue_team
Feature: CO.Perform Simple Rollover for Exam Offering Scheduling Information
  FE 4.1 As a Central Administrator I want to have on matrix exam offerings populated with requested scheduling
  information when created in bulk so that exam offering scheduling requests are consistent with the established exam matrix

  FE 5.3: As a Central Administrator I want EO requested scheduling information to be updated dynamically when AO ASIs
  are initially populated so that scheduling information remains in line with those of the AO driving the exam and matrix slotting

  Background:
    Given I am logged in as admin

  #KSENROLL-12375
  Scenario: FE4.1.1 Exam Offering Slotting Info should populated or not after a rollover depending on whether the AO RSI data is found on the matrix
    Given I create an Academic Calendar and add an official term
    When I create multiple Course Offerings each with different Exam Offerings and Requested Scheduling Information
    And I rollover the term to a new academic term that has an exam period
    Then the Exam Offerings Slotting info should be populated or left blank depending on whether the AO RSI was found on the Exam Matrix

  #KSENROLL-12376
  Scenario: FE5.3.1 Trigger the Mass Scheduling Event for a set of COs which then creates the EOs for the COs
    Given I create an Academic Calendar and add an official term
    And I create multiple Course Offerings in the term
    And I initiate a rollover to create a term in open state
    And I approve the "ENGL" subject code for scheduling in the target term
    And I create Exam Matrix rules from which the Exam Offering Slotting info is populated
    When I advance the SOC state from open to scheduler complete state
    Then the Exam Offerings Slotting info should be populated after the Mass Scheduling Event has been triggered