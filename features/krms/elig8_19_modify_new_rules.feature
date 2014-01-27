@nightly @blue_team
Feature: CO.ELIG8-19 Modify the new rules on Activity Offering level

  Background:
    Given I am logged in as admin

  #ELIG8.19.EB1 (KSENROLL-9248)
  Scenario: Test whether modifying the new AO rule gives the appropriate warning message
    When I add a rule to the Student Eligibility & Prerequisite section
    Then a info in the Student Eligibility & Prerequisite section is displayed stating "Activity Offering Rule differs from Course Offering Rule"
    And the AO rule should differ from the CO and CLU rules in the Student Eligibility & Prerequisite section

  #ELIG8.3.EB2 (KSENROLL-8745)
  Scenario: Test whether the rule that was copied and edited can be suppressed after being committed
    When I suppress the copied and edited Course Offering rule that was committed in the Student Eligibility & Prerequisite section
    Then there should be no rule in the Student Eligibility & Prerequisite section
    And a warning in the Student Eligibility & Prerequisite section is displayed stating "Rule statements deleted"
    And a info in the Student Eligibility & Prerequisite section is displayed stating "Activity Offering Rule differs from Course Offering Rule"
