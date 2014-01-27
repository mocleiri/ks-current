@wip @yellow_team
Feature: CO.State Propagation

  Background:
    Given I am logged in as a Schedule Coordinator

  Scenario: Test State Propagation
    When I run the State Propagation test
    Then all State Propagation test rows should have status pass



