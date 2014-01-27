@nightly @blue_team
Feature: CO.ELIG8-22 Ensure that changes made to AO or CO Requisites displays differently on Schedule of Classes
  ELIG 8.22 : As a Central Administrator I want to have Rules display in the Schedule of Classes Cluster View so
  that students are able to see whether or not they will qualify for enrolment in a course prior to trying to enrol

  Background:
    Given I am logged in as admin

  #ELIG8.22.EB1 (KSENROLL-10123)
  Scenario: Verify that suppressing the Course Offering rule for an AO is displayed on the Schedule of Classes
    Given I suppress a course offering rule for an activity in a course
    And I am using the schedule of classes page
    When I search for course offerings by course
    And I select a course that has existing course offering requisites
    Then the suppressed rule should not be visible with the course data
    And the suppressed rule should not be visible for the changed activity
    But the suppressed rule should be visible for any unchanged activity

#ELIG8.22.EB4 (KSENROLL-10123)
  Scenario: Verify that a CO level rule edited at the AO level is displayed on the Schedule of Classes
    Given I edit a course offering rule at the AO level by adding a new text statement
    And I am using the schedule of classes page
    When I search for course offerings by course
    And I select a course that has existing course offering requisites
    Then the edited course offering rule should be displayed with the affected activity offering
    But the unedited course offering rule should be displayed with any un-affected activity offerings

#ELIG8.22.EB5 (KSENROLL-10123)
  Scenario: Verify that adding CO Rules is displayed on the Schedule of Classes
    Given I add a new course offering rule to a course
    And I am using the schedule of classes page
    When I search for course offerings by course
    And I select a course that has existing course offering requisites
    Then the added course offering requisite should be displayed with the course data
