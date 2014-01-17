@pending
Feature: BT.Add course to planner

  Background:
    Given I am logged in as admin


  Scenario: CS2.2.1 Add a fixed credit course with notes to current term
    When I add a fixed credit course with notes to current term
    Then the course should be added to the current term
    And I view the details of the added course from edit plan item
    Then fixed credit and note details are displayed under the current term



  Scenario: CS2.2.2 Add a variable credit course with note to a future term
    When I add a variable credit course with notes to a future term
    Then the course should be added to the future term
    And I view the details of the added course from edit plan item
    Then variable credit and note details are displayed under the future term

