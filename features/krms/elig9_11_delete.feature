@nightly @blue_team
Feature: CO.ELIG9-11 Delete

  Background:
    Given I am logged in as admin
    And I have setup the Student Eligibility & Prerequisite section for course "ENGL101S" in the future term

  #ELIG9.11.EB1 (KSENROLL-7084)
  Scenario: Confirm that the Delete button works as expected
    When I want to edit the Student Eligibility & Prerequisite section
    And I delete node "F" in the tree
    Then there should be no node "F" on both tabs

  #ELIG9.11.EB2 (KSENROLL-7084)
  Scenario: Confirm that the Submit button persists the data after Delete
    When I want to edit the Student Eligibility & Prerequisite section
    And I delete node "G" in the tree
    And I commit and return to see the changes made to the proposition
    Then the agenda page's text should not match "minimum of 1 course from,HIST210,HIST395"
    When I want to edit the Student Eligibility & Prerequisite section
    Then there should be no node "G" on both tabs

  #ELIG9.11.EB3 (KSENROLL-7089)
  Scenario: Confirm that the Delete Rule link on Agenda Preview page deletes the tree
    When I delete the tree in the Student Eligibility & Prerequisite section
    And I commit and return to see the changes made to the proposition
    Then the tree in the Student Eligibility & Prerequisite section should be empty
