@nightly @blue_team
Feature: CO.ELIG9-6 AND, OR and Move

  Background:
    Given I am logged in as admin
    Given I have setup the Student Eligibility & Prerequisite section for course "BSCI103M" in the future term

  #ELIG9.6.EB1 (KSENROLL-6491)
  Scenario: Confirm that the page does not crash when moving nodes around and adding a group
    When I want to edit the Student Eligibility & Prerequisite section
    And I move node "B" down
    Then node "B" should be after node "C"

  #ELIG9.6.EB2 (KSENROLL-5880)
  Scenario: The group should change depending on the AND/OR operator
    When I want to edit the Student Eligibility & Prerequisite section
    And I change the operator before node "G" to "AND"
    Then the first node should match "Must meet all of the following"

  #ELIG9.6.EB3 (KSENROLL-6308)
  Scenario: Confirm changes to the tree is shown in the sections of the Logic tab
    When I want to edit the Student Eligibility & Prerequisite section
    And I move node "B" down
    And I move node "E" up
    And I change the operator before node "G" to "AND"
    And I switch to the other tab on the page
    Then the text area should be populated with "E AND (A OR (C AND B) OR D) AND F AND G"

  #ELIG9.6.EB4 (KSENROLL-6310)
  Scenario: Move a node in a group left and confirm that it leaves the group
    When I want to edit the Student Eligibility & Prerequisite section
    And I move node "D" out of the group
    Then node "D" should be a "primary" node in the tree

  #ELIG9.6.EB5 (KSENROLL-6310)
  Scenario: Move a node right and confirm that nothing happens
    When I want to edit the Student Eligibility & Prerequisite section
    And I move node "D" out of the group
    Then the Move In button should be disabled

  #ELIG9.6.EB6 (KSENROLL-6310)
  Scenario: Move a node up and confirm that node is moved one position up
    When I want to edit the Student Eligibility & Prerequisite section
    And I move node "D" out of the group
    And I move node "D" up
    Then node "A" should be after node "D"

  #ELIG9.6.EB7 (KSENROLL-6310)
  Scenario: Move a node up then right and confirm that it moves into the group below
    When I want to edit the Student Eligibility & Prerequisite section
    And I move node "D" out of and into the group
    Then node "D" should be a "secondary" node in the tree

  #ELIG9.6.EB8 (KSENROLL-5777)
  Scenario: The droplist value should be able to be changed
    When I want to edit the Student Eligibility & Prerequisite section
    And I change the operator before node "G" to "AND"
    Then node "G" should be preceded by an "AND" operator

  #ELIG9.6.EB9 (KSENROLL-5777)
  Scenario: The changes should be applied to the rule view on the Edit with Logic tab
    When I want to edit the Student Eligibility & Prerequisite section
    And I change the operator before node "G" to "AND"
    And I switch to the other tab on the page
    Then the text area should be populated with "(A OR (B AND C) OR D) AND E AND F AND G"
