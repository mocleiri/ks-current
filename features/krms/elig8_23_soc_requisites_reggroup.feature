@nightly @blue_team
Feature: CO.ELIG8-23 Ensure that changes made to AO or CO Requisites displays differently on S0C Reg Group
  ELIG 8.23 : As a Central Administrator I want to have Rules display in the Schedule of Classes Registration Group
  View so that students are able to see whether or not they will qualify for enrolment in a course prior to trying
  to enrol

  Background:
    Given I am logged in as admin

  #ELIG8.23.EB1 (KSENROLL-10128)
  Scenario: Verify that suppressing the Course Offering rule for an Activity displays correctly on the Schedule of Classes for the Reg Group
    Given I suppress a course offering rule for an activity in a course
    And I am using the schedule of classes page
    When I search for course offerings by course
    And I select a course that has existing course offering requisites in the registration group
    Then the suppressed rule should be visible for any unchanged activity that shares a Reg Group with the changed activity
    And any un-suppressed course offering rules should be visible with the course data

  #ELIG8.23.EB2 (KSENROLL-10128)
  Scenario: Verify that a CO level rule edited at the AO level displays correctly on the Schedule of Classes for the Reg Group
    Given I edit a course offering rule at the AO level by adding a new text statement
    And I am using the schedule of classes page
    When I search for course offerings by course
    And I select a course that has existing course offering requisites in the registration group
    Then the edited course offering rule should be displayed at the activity level on Reg Groups that contain the affected activity
    But the unedited course offering rule should be displayed at the Reg Group level on Reg Groups that do not contain the affected activity

  #ELIG8.23.EB3 (KSENROLL-10128)
  Scenario: Verify that adding an AO Rule is displayed on the Schedule of Classes for the Reg Group
    Given I add a new course offering rule to a course
    And I am using the schedule of classes page
    When I search for course offerings by course
    And I select a course that has existing course offering requisites in the registration group
    Then the added course offering requisite should be displayed with the course data
    But the added course offering requisite should not be displayed on Reg Group level