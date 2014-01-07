@nightly @green_team
Feature: WC.Delete COs and all associated AOs

  As a user, I want to be able to delete a course offering so that it will no longer be offered for the term.

  Background:
    Given I am logged in as a Schedule Coordinator

  Scenario: Cancel deletion and successfully delete Course Offerings in the CO-Subject view
    Given I create multiple Course Offerings with Draft Activity Offerings
    When I cancel the deletion of a Course Offering in Subject Code view
    Then the Course Offerings are not deleted
    When I delete these Course Offerings
    Then the deleted course offerings do not appear on the list of available Course Offerings

  Scenario: Cancel deletion and successfully delete a single Course Offering from the Course Offering Code view
    When I create a Course Offering with Draft Activity Offerings
    And I cancel the deletion of the Course Offerings in Course Offering Code view
    Then the Course Offering is not deleted
    When I delete this Course Offering
    Then the deleted course offering does not appear on the list of available Course Offerings