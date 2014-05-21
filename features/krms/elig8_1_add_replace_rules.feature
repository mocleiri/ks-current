@nightly @blue_team
Feature: CO.ELIG8-1 Add or Replace rules to Activity Offering whether CO exists or not

  Background:
    Given I am logged in as admin

  #KSENROLL-8709
  Scenario: ELIG8.1.1 Test whether a CO rule is replaced by a new AO rule
    When I replace the CO rule in the Student Eligibility & Prerequisite section
    Then an info message in the Student Eligibility & Prerequisite section is displayed stating "Activity Offering Rule differs from Course Offering Rule"
    And the AO rule should differ from the CO and CLU rules in the Student Eligibility & Prerequisite section

  #KSENROLL-8709
  Scenario: ELIG8.1.2 Test whether CO rule is replaced by a new AO rule and can then be edited
    When I edit and update the rule that replaced the CO rule in the Student Eligibility & Prerequisite section
    Then an info message in the Student Eligibility & Prerequisite section is displayed stating "Activity Offering Rule differs from Course Offering Rule"
    And the edited rule should be shown in the Student Eligibility & Prerequisite section

  #KSENROLL-8709
  Scenario: ELIG8.1.3 Test whether a new AO rule is created even if CO rule does not exist
    When I add a rule to the Student Eligibility & Prerequisite section
    Then the created rule should be shown in the Student Eligibility & Prerequisite section