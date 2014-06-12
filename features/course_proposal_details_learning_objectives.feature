Feature: Course Proposal Details - Learning Objectives

Background:
  Given I am logged in as Faculty
  And I have Learning Objective Categories created

  Scenario: CC8.1 Add new Learning Objectives to a Course Proposal
    When I create a basic course proposal with Learning Objectives
    And I perform a full search for the course proposal
    Then I should see Learning Objective details on the course proposal

  Scenario: CC8.2 Add Learning Objectives a Course Proposal using advanced search
    When I create a basic course proposal with Learning Objectives added using advanced search
    And I perform a full search for the course proposal
    Then I should see Learning Objective details on the course proposal

  Scenario: CC8.3 Edit and organize Learning Objectives on a Course Proposal
    Given I have a basic course proposal with Learning Objectives
    When I edit the Learning Objectives
    And I perform a full search for the course proposal
    Then I should see updated Learning Objective details on the course proposal

  Scenario: CC8.4 Delete Learning Objectives from a Course Proposal
    Given I have a basic course proposal with Learning Objectives
    When I delete the Learning Objectives
    And I perform a full search for the course proposal
    Then I should no longer see Learning Objective details on the course proposal