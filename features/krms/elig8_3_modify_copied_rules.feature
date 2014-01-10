@nightly @blue_team
Feature: SA.ELIG8-3 Modify the Copied rules on Activity Offering level

  Background:
    Given I am logged in as admin

  #ELIG8.3.EB1 (KSENROLL-8745)
  Scenario: Test whether modifying the copied CO rule gives the appropriate warning message
    When I copy and edit the Course Offering rule to the Student Eligibility & Prerequisite section
    Then a info in the Student Eligibility & Prerequisite section is displayed stating "Activity Offering Rule differs from Course Offering Rule"
    And the AO rule should differ from the CO and CLU rules in the Student Eligibility & Prerequisite section

  #ELIG8.3.EB2 (KSENROLL-8745)
  Scenario: Make sure there is no pre-populated rule on the Activity Offering level
    When I navigate to the agendas page and open the Student Eligibility & Prerequisite section
    Then there should be no rule in the Student Eligibility & Prerequisite section
