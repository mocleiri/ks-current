@nightly @yellow_team
Feature: CO.Create and remove cross-listings

    This feature contains 4 scenarios
      1) Remove cross-listing, check owner CO to see that checkbox is clear and cross-listed message is absent
      2) Remove cross-listing, check that alias CO does not exist
      3) Set cross-listing, check that alias CO shows as cross-listed with owner
      4) Set cross-listing, check that owner CO shows as cross-listed with alias

  Background:
    Given I am logged in as a Schedule Coordinator


  #   1) Remove cross-listing, check owner CO to see that checkbox is clear and cross-listed message is absent
  #   --------------------------------------------------------------------------------------------------------
  Scenario: Remove cross-listing confirm owner
    When I create a cross-listed Course Offering
    And I remove a cross-listed Course Offering
    And I edit the course offering
    Then the edit page should not indicate a cross-listing
    And I manage the owner Course Offering
    Then the owner course offering is not indicated as cross-listed with the alias CO


  #   2) Remove cross-listing, check that alias CO does not exist
  #   -----------------------------------------------------------
  Scenario: Remove cross-listing confirm alias
    When I create a cross-listed Course Offering
    And I remove a cross-listed Course Offering
    And I manage the alias Course Offering
    Then the alias Course Offering does not exist


  #   3) Set cross-listing, check that alias CO shows as cross-listed with owner
  #   --------------------------------------------------------------------------
  Scenario: CL1.1 Create cross-listing confirm alias
    When I create a cross-listed Course Offering
    And I manage the alias Course Offering
    Then the alias is indicated as cross-listed with the owner CO


  #   4) Set cross-listing, check that owner CO shows as cross-listed with alias
  #   --------------------------------------------------------------------------
  Scenario: Create cross-listing confirm owner
    When I create a cross-listed Course Offering
    And I manage the owner Course Offering
    And I edit the course offering
    Then the edit page should indicate a cross-listing
