@nightly @red_team
  Feature: EC.populate RSIs in copy CO to target term

  MSR 1.2: As Central Administrator, I want actual scheduling information on AOs in a source term to be copied to
  requested scheduling information on AOs in the target term when copying a Course Offering from a previous term

  Background:
    Given I am logged in as a Schedule Coordinator

  Scenario: Copy ASIs from source AOs to RSIs of new AOs of different term when copying a Course Offering from a previous term
    When I copy an CO with AOs that have ASIs to a new CO in the different term with RSIs in its AOs
    Then The new CO and AOs are Successfully created
    And The ASIs are Successfully copied to RSIs in the new AOs of the newly created CO

