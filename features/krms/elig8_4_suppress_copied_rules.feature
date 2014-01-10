@nightly @blue_team
Feature: SA.ELIG8-4 Suppress the Copied rules on Activity Offering level

  Background:
    Given I am logged in as admin

  #ELIG8.4.EB1 (KSENROLL-8746)
  Scenario: Test whether deleting the copied CO rule completely removes the rule from the AO level
    When I suppress the copied rule in the Student Eligibility & Prerequisite section
    Then there should be no rule in the Student Eligibility & Prerequisite section
    And no warning in the Student Eligibility & Prerequisite section is displayed stating "Course Offering rule will be enforced on Activity Offering."

  #ELIG8.4.EB2 (KSENROLL-8746)
  Scenario: Test whether deleting the copied CO rule gives the correct warning messages
    When I suppress the copied rule in the Student Eligibility & Prerequisite section
    Then a warning in the Student Eligibility & Prerequisite section is displayed stating "Rule statements deleted. No rule of this type will be executed."
    And a info in the Student Eligibility & Prerequisite section is displayed stating "Activity Offering Rule differs from Course Offering Rule"
