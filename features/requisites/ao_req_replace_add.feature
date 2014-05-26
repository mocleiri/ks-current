@nightly @blue_team_krms
Feature: CO.Replace or Add rules on AO Requisites whether CO Requisites exist or not

  Background:
    Given I am logged in as admin

  #KSENROLL-8709/KSENROLL-8710
  Scenario: ELIG8.1.1/ELIG8.5.2 Test whether a CO rule is replaced by a new AO rule
    When I replace the CO rule in the Student Eligibility & Prerequisite section
    Then an info message in the Student Eligibility & Prerequisite section is displayed stating "Activity Offering Rule differs from Course Offering Rule"
    And the AO rule should differ from the CO and CLU rules in the Student Eligibility & Prerequisite section

  #KSENROLL-8709/KSENROLL-8710
  Scenario: ELIG8.1.3/ELIG8.5.1 Test whether a new AO rule is created even if CO rule does not exist
    When I add a rule to the Student Eligibility & Prerequisite section
    Then the rule that was created should be shown in the Student Eligibility & Prerequisite section
    And a info in the Student Eligibility & Prerequisite section is displayed stating "Activity Offering Rule differs from Course Offering Rule"
    
  #KSENROLL-9248
  Scenario: ELIG8.19.1 Test whether modifying the new AO rule gives the appropriate warning message
    When I add a rule to the Student Eligibility & Prerequisite section
    Then an info message in the Student Eligibility & Prerequisite section is displayed stating "Activity Offering Rule differs from Course Offering Rule"
    And the AO rule should differ from the CO and CLU rules in the Student Eligibility & Prerequisite section