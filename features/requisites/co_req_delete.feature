@nightly @blue_team_krms
Feature: CO.Delete Statement or Group of CO Requisites

  Background:
    Given I am logged in as admin

  #KSENROLL-7084
  Scenario: ELIG9.11.1/ELIG9.11.2 Confirm that the Submit button persists the data after Delete
    When I add 2 statements to the Student Eligibility & Prerequisite section for course "ENGL101S" in the future term
    When I want to edit the Student Eligibility & Prerequisite section
    And I delete node "A" in the tree
    And I persist the changes and return to the proposition
    Then the agenda page's text should not match "minimum of 1 course from,HIST210,HIST395"
    When I want to edit the Student Eligibility & Prerequisite section
    Then there should be no node "B" on both tabs

  #KSENROLL-7243
  Scenario: ELIG7.6.1 Test whether statement deleted in compound is removed from the tree
    When I edit the Student Eligibility & Prerequisite section for course "PHYS275" in the historic term
    And I delete node "A" in the tree
    Then there should be no node "A" on both tabs
    When I persist the changes and return to the proposition
    Then the agenda page's text should not match "PHYS161,PHYS171"

  #KSENROLL-7243
  Scenario: ELIG7.6.2 Test whether compound deleted is removed from the tree
    When I edit the Student Eligibility & Prerequisite section for course "PHYS275" in the historic term
    And I delete the group containing node "A"
    Then there should be no node "A" on both tabs
    When I persist the changes and return to the proposition
    Then the agenda page's text should not match "PHYS161,PHYS171,PHYS174"

  #KSENROLL-7089/KSENROLL-7244/KSENROLL-7247
  Scenario: ELIG9.11.3/ELIG7.6.3/ELIG7.2.1 Test whether clicking delete rule deletes the whole tree
    When I navigate to the Student Eligibility & Prerequisite section for course "PHYS375" in the historic term
    And I delete the tree in the Student Eligibility & Prerequisite section
    And I commit and return to see the changes made to the proposition
    Then the tree in the Student Eligibility & Prerequisite section should be empty
