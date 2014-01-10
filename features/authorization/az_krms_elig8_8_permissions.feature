@nightly @blue_team
Feature: SA.AZ ELIG8-8 Permissions for specific users should change depending on the CO

  Background:
    Given I am logged in as a Department Schedule Coordinator

  #ELIG8.8.EB1 (KSENROLL-8872)
  Scenario: Test that the Department Schedule Coordinator can only view AO Requisites for a course not in the admin org
    When I navigate to the agendas page for a CO that I cannot edit and open the Student Eligibility & Prerequisite section
    Then I should only be allowed to view the CLU and CO rule in the Student Eligibility & Prerequisite section

  #ELIG8.8.EB2.1 (KSENROLL-8872)
  Scenario: Test that the Department Schedule Coordinator can add a new rule in AO Requisites for a course in the admin org
    When I replace the CO rule in the Student Eligibility & Prerequisite section
    Then the created rule should be shown in the Student Eligibility & Prerequisite section

  #ELIG8.8.EB2.2 (KSENROLL-8872)
  Scenario: Test that the function buttons work when the Department Schedule Coordinator is editing a rule
    When I edit the rule that replaced the CO rule in the Student Eligibility & Prerequisite section
    Then I should be able to use all the function buttons on the Edit Rule tab

  #ELIG8.8.EB2.3 (KSENROLL-8872)
  Scenario: Test if the Department Schedule Coordinator can use the functionality on the logic tab
    When I edit the rule that replaced the CO rule in the Student Eligibility & Prerequisite section
    Then I should be able to use the functionality of the Edit Rule Logic tab
