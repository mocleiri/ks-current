@nightly @green_team
Feature: WC.delete activity offerings

  CO 24.12, As a user, I want to Delete Activity Offerings from the Manage Course Offerings process
            by means of a Toolbar Button so I can more easily manage my Course Offerings.

  Background:
    Given I am logged in as a Schedule Coordinator

  Scenario: Delete multiple AOs
    When I create a Course Offering and add 3 Activity Offerings
    And I delete 2 Activity Offerings
    Then the Course Offering should contain 1 additional Activity Offering
    And the 2 AOs are Successfully deleted

  Scenario: Delete an AO in cross-listed course offering
    When I designate a valid term and cross-listed Course Offering Code
    And add an Activity Offering to the cross-listed CO
    Then the AO count reflects the added AO
    When I delete the added AO
    Then I receive a warning that the course is cross-listed
    And the AO is Successfully deleted
