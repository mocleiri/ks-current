@nightly @yellow_team
Feature: CO.AZ for Cancel Suspend and Reinstate

  This feature incorporates CO 21.2, 22.2 and 23.5

  Background:
    Given I am logged in as a Schedule Coordinator

  Scenario: Schedule Coordinator can suspend an AO when SOC is Locked
    Given I am working on a term in "Locked" SOC state
    When I manage a course offering
    Then I have access to suspend an Activity Offering
    And I have access to reinstate an Activity Offering
    Then I have access to cancel an Activity Offering
    And I have access to reinstate an Activity Offering

  Scenario: Schedule Coordinator can suspend an AO when SOC is Final Edits
    Given I am working on a term in "Final Edits" SOC state
    When I manage a course offering
    Then I have access to suspend an Activity Offering
    And I have access to reinstate an Activity Offering
    Then I have access to cancel an Activity Offering
    And I have access to reinstate an Activity Offering

  Scenario: Schedule Coordinator can suspend an AO when SOC is Published
    Given I am working on a term in "Published" SOC state
    When I manage a course offering
    Then I have access to suspend an Activity Offering
    And I have access to reinstate an Activity Offering
    Then I have access to cancel an Activity Offering
    And I have access to reinstate an Activity Offering

  Scenario: Schedule Coordinator can cancel an AO when SOC is Open
    Given I am working on a term in "Open" SOC state
    When I manage a course offering
    Then I have access to cancel an Activity Offering
    And I have access to reinstate an Activity Offering
    Then I do not have access to suspend an Activity Offering

  Scenario: Department Schedule Coordinator can suspend cancel and reinstate an AO when SOC is Open
    Given I am logged in as a Department Schedule Coordinator
    And I am working on a term in "Open" SOC state
    When I manage a course offering in my admin org
    Then I have access to cancel an Activity Offering
    And I have access to reinstate an Activity Offering

  Scenario: Department Schedule Coordinator can suspend an AO when SOC is Final Edits
    Given I am logged in as a Department Schedule Coordinator
    And I am working on a term in "Final Edits" SOC state
    When I manage a course offering in my admin org
    Then I have access to suspend an Activity Offering
    And I have access to reinstate an Activity Offering
    And I have access to cancel an Activity Offering
    And I have access to reinstate an Activity Offering

  Scenario: Department Schedule Coordinator does not have access to suspend an AO when an SOC is Published
    Given I am logged in as a Department Schedule Coordinator
    And I am working on a term in "Published" SOC state
    When I manage a course offering in my admin org
    Then I do not have access to suspend an Activity Offering
    And I do not have access to cancel an Activity Offering
    And I do not have access to reinstate an Activity Offering

  Scenario: Department Schedule Coordinator does not have access to suspend an AO when an SOC is Locked
    Given I am logged in as a Department Schedule Coordinator
    And I am working on a term in "Locked" SOC state
    When I manage a course offering in my admin org
    Then I do not have access to suspend an Activity Offering
    And I do not have access to cancel an Activity Offering
    And I do not have access to reinstate an Activity Offering