@wip
Feature: View status of Course Propsoal
  As a user I should be able to see the status of a course proposal being one of these states:
  Saved, Active, Enroute, Cancelled, or Approved

  Background:
    Given I am logged in as Fred
    And I create a course proposal

  Scenario: Saving a course proposal should make the status Saved
    Then I should see the status of the course proposal is Saved

    #KSCM-799
  Scenario: Canceling a course proposal should make the status Canceled
    When I cancel the course proposal
    Then I should see the status of the course proposal is Cancelled

  Scenario: Submitting a course proposal should make the status Enroute
    Given I complete require fields on the course proposal
    When I Submit the course proposal
    Then I should see the status of the course proposal is Enroute

    #KSCM-885
  Scenario: Blanket approve a course proposal should make the status Approved
    Given There is a submited course proposal with all required fields
    And I am logged in as admin
    When I blanket approve the submitted course proposal
    Then I should see the status of the course proposal is Approved

    #KSCM-885
  Scenario: Approve a completed course proposal using workflow
    Given There is a submited course proposal with all required fields
    When <user> chooses to <action> the course proposal proposal
      | User  | action  |
      | user4 | approve |
      | user3 | approve |
      | test2 | approve |
      | test1 | approve |
      | dev1  | approve |

    Then I should see the status of the course proposal is Approved

