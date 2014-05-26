@nightly @blue_team_krms
Feature: CO.Change Logical Operators and Order of statement for CO Requisites

  Background:
    Given I am logged in as admin

  #KSENROLL-6491
  Scenario: ELIG9.6.1 Confirm that the page does not crash when moving nodes around and adding a group
    When I add 2 statements to the Student Eligibility & Prerequisite section for course "BSCI103M" in the future term
    And I want to edit the Student Eligibility & Prerequisite section
    And I move node "A" down
    Then node "A" should be after node "B"

  #KSENROLL-5880/KSENROLL-5777
  Scenario: ELIG9.6.2/ELIG9.6.8/ELIG9.6.9 The group should change depending on how the AND/OR operator is changed
    When I add 2 statements to the Student Eligibility & Prerequisite section for course "BSCI103M" in the future term
    And I want to edit the Student Eligibility & Prerequisite section
    And I change the operator before node "B" to "AND"
    Then the first node should match "Must meet all of the following"
    And node "B" should be preceded by an "AND" operator
    When I switch to the other tab on the page
    Then the text area should be populated with "A AND B"

  #KSENROLL-6308
  Scenario: ELIG9.6.3 Confirm changes to the tree is shown in the sections of the Logic tab
    When I add 3 statements to the Student Eligibility & Prerequisite section for course "BSCI103M" in the future term
    And I want to edit the Student Eligibility & Prerequisite section
    And I move node "A" down
    And I move node "C" up
    And I change the operator before node "A" to "AND"
    And I switch to the other tab on the page
    Then the text area should be populated with "B AND C AND A"

  #KSENROLL-6310/KSENROLL-6310/KSENROLL-6310/KSENROLL-6310
  Scenario: ELIG9.6.4/ELIG9.6.5/ELIG9.6.6/ELIG9.6.7 Move a node in a group left and confirm that it leaves the group
    When I add 4 statements to the Student Eligibility & Prerequisite section for course "BSCI103M" in the future term
    And I want to edit the Student Eligibility & Prerequisite section
    And I move node "B" out of the group
    Then node "B" should be a primary node in the tree
    And the Move In button should be disabled
    When I move node "B" up
    Then node "A" should be after node "B"
    When I move node "B" into the group
    Then node "B" should be a secondary node in the tree
