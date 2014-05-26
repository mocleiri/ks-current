@nightly @blue_team
Feature: CO.AZ Permissions for specific users should change depending on the CO

  Background:
    Given I am logged in as a Department Schedule Coordinator

  #KSENROLL-8872
  Scenario: ELIG8.8.1 Test that the Department Schedule Coordinator can only view AO Requisites for a course not in the admin org
    When I navigate to the agendas page for a CO that I cannot edit and open the Student Eligibility & Prerequisite section
    Then I should only be allowed to view the CLU and CO rule in the Student Eligibility & Prerequisite section

  #KSENROLL-8872
  Scenario: ELIG8.8.2 Test that the Department Schedule Coordinator can add a new rule in AO Requisites for a course in the admin org
    When I want to replace the CO rule in the Student Eligibility & Prerequisite section
    Then the created rule should be shown in the Student Eligibility & Prerequisite section

  #KSENROLL-8872
  Scenario: ELIG8.8.3 Test that the function buttons work when the Department Schedule Coordinator is editing a rule
    When I edit the rule that replaced the CO rule in the Student Eligibility & Prerequisite section
    Then I should be able to use all the function buttons on the Edit Rule tab

  #KSENROLL-8872
  Scenario: ELIG8.8.4 Test if the Department Schedule Coordinator can use the functionality on the logic tab
    When I edit the rule that replaced the CO rule in the Student Eligibility & Prerequisite section
    Then I should be able to use the functionality of the Edit Rule Logic tab
