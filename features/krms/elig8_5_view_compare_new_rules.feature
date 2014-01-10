@nightly @blue_team
Feature: SA.ELIG8-5 View and Compare the CLU and CO rule to a new AO rule where there was no CO rule

  Background:
    Given I am logged in as admin

  #ELIG8.5.EB1 (KSENROLL-8710)
  Scenario: Test whether a warning message is given when new AO rule is created where no CO rule existed
    When I add a rule to the Student Eligibility & Prerequisite section
    Then a info in the Student Eligibility & Prerequisite section is displayed stating "Activity Offering Rule differs from Course Offering Rule"

  #ELIG8.5.EB2 (KSENROLL-8710)
  Scenario: Test whether the AO rule can be compared to the CLU and CO rule
    When I compare the added rule with the CO and CLU rules in the Student Eligibility & Prerequisite section
    Then the AO rule should differ from the CO and CLU rules in the Student Eligibility & Prerequisite section

  #ELIG8.5.EB3 (KSENROLL-8710)
  Scenario: Test whether the user can view catalog and course offering rules
    When I view the catalog and course offering rule for the Student Eligibility & Prerequisite section
    Then both rules for the Student Eligibility & Prerequisite section should be the same
