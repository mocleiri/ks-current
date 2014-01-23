@nightly @blue_team
Feature: CO.ELIG7-2 Delete all statements of CO copied from CLU

  Background:
    Given I am logged in as admin
    And I have navigated to the Student Eligibility & Prerequisite section for course "ENGL313" in the historic term

  #ELIG7.2.EB1 (KSENROLL-7247)
  Scenario: Test whether tree deleted is removed from all tabs on the page
    When I want to edit the Student Eligibility & Prerequisite section
    And I delete the tree
    And I commit and return to see the changes made to the proposition
    Then the tree in the Student Eligibility & Prerequisite section should be empty
