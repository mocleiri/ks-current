@draft
Feature: CO.Scheduling Information

  As a Central Administrator or Dept Administrator, I want to edit an Activity Offering so that I can change the AO's actual scheduling information (KSENROLL-2595)

  Background:
    Given I am logged in as a Schedule Coordinator
    Given I am managing a course offering

  Scenario: Save and process requested scheduling information
    When I add requested scheduling information to an activity offering
    Then actual scheduling information are created with the activity offering
    And I confirm that the activity offering is changed to "Offered"


