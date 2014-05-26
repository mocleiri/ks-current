@nightly @blue_team
Feature: CO.Copy, Edit and Update on AO Requisites

  Background:
    Given I am logged in as admin

  #KSENROLL-8745
  Scenario: ELIG8.3.1/ELIG8.3.2 Test whether modifying the copied CO rule gives the appropriate warning message
    When I copy and edit the Course Offering rule to the Student Eligibility & Prerequisite section
    Then an info message in the Student Eligibility & Prerequisite section is displayed stating "Activity Offering Rule differs from Course Offering Rule"
    And the AO rule should differ from the CO and CLU rules in the Student Eligibility & Prerequisite section

  #KSENROLL-8709
  Scenario: ELIG8.1.2 Test whether CO rule is replaced by a new AO rule and can then be edited
    When I edit and update the rule that replaced the CO rule in the Student Eligibility & Prerequisite section
    Then an info message in the Student Eligibility & Prerequisite section is displayed stating "Activity Offering Rule differs from Course Offering Rule"
    And the edited rule should be shown in the Student Eligibility & Prerequisite section
