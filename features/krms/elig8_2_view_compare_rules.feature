@nightly @blue_team
Feature: SA.ELIG8-2 View the Catalog and Course Offering rule

  Background:
    Given I am logged in as admin

  #ELIG8.2.EB1 (KSENROLL-8707)
  Scenario: Test whether info messages are shown when agendas page is loaded
    When I navigate to the agendas page and open the Student Eligibility & Prerequisite section
    Then a info in the Student Eligibility & Prerequisite section is displayed stating "Course Offering rule exists and will be enforced on Activity Offering"

  #ELIG8.2.EB2 (KSENROLL-8707)
  Scenario: Test whether the user can view catalog and course offering rules
    When I view the catalog and course offering rule for the Student Eligibility & Prerequisite section
    Then both rules for the Student Eligibility & Prerequisite section should be the same

  #ELIG8.2.EB3 (KSENROLL-8707)
  Scenario: Test whether the AO icon appears after a rule is added to the Activity Offering
    When I commit the rule that replaced the CO rule in the Student Eligibility & Prerequisite section
    Then the AO icon should be shown for the edited Activity Offering

  #ELIG8.2.EB4 (KSENROLL-8707)
  Scenario: Test whether the Activity Offering's unique rule can be compared to the CO rule
    When I compare the replaced rule with the CO and CLU rules in the Student Eligibility & Prerequisite section
    Then the AO rule should differ from the CO and CLU rules in the Student Eligibility & Prerequisite section

  #ELIG8.2.EB5 (KSENROLL-8707)
  Scenario: Test whether the Activity Offering's unique rule can be reverted to the CO rule
    When I revert the rule that replaced the CO rule in the Student Eligibility & Prerequisite section
    Then there should be no rule in the Student Eligibility & Prerequisite section

  #ELIG8.2.EB6 (KSENROLL-8707)
  Scenario: Test whether the AO icon disappears after a rule is reverted back to the Course Offering rule
    When I commit after reverting the rule that replaced the CO rule in the Student Eligibility & Prerequisite section
    Then the AO icon should not be shown for the edited Activity Offering