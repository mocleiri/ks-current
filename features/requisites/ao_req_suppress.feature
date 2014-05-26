@nightly @blue_team_krms
Feature: CO.Suppress the CO Requisites rules on Activity Offering level

  Background:
    Given I am logged in as admin

  #KSENROLL-8746
  Scenario: ELIG8.4.1 Test whether deleting the copied CO rule completely removes the rule from the AO level
    When I suppress the copied rule in the Student Eligibility & Prerequisite section
    Then there should not be a rule in the Student Eligibility & Prerequisite section
    And no warning in the Student Eligibility & Prerequisite section is displayed stating "Course Offering rule will be enforced on Activity Offering."

  #KSENROLL-8746
  Scenario: ELIG8.4.2 Test whether deleting the copied CO rule gives the correct warning messages
    When I suppress the copied rule in the Student Eligibility & Prerequisite section
    Then a warning in the Student Eligibility & Prerequisite section is displayed stating "Rule statements deleted. No rule of this type will be executed."
    And a info in the Student Eligibility & Prerequisite section is displayed stating "Activity Offering Rule differs from Course Offering Rule"

  #KSENROLL-9248
  Scenario: ELIG8.19.2 Test whether the rule that was copied and edited can be suppressed after being committed
    When I suppress the copied and edited Course Offering rule that was committed in the Student Eligibility & Prerequisite section
    Then there should not be a rule in the Student Eligibility & Prerequisite section
    And a warning in the Student Eligibility & Prerequisite section is displayed stating "Rule statements deleted"
    And a info in the Student Eligibility & Prerequisite section is displayed stating "Activity Offering Rule differs from Course Offering Rule"
