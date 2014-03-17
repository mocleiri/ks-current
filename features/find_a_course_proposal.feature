@nightly
Feature: GT.Find a Course Proposal


  Scenario: FCP1.1 Find Course Proposals as Faculty and review a proposal created by Curriculum Specialist
    Given I have a admin course proposal created as Curriculum Specialist and course proposal created as Faculty
    When I am logged in as Fred
    And I perform a partial search for Course Proposals
    Then I should see both proposals listed in the search result
    And I can review the proposal created by Curriculum Specialist


  Scenario: FCP1.2 Find a Course Proposals as Curriculum Specialist and review a proposal created by Faculty
    Given I have a admin course proposal created as Curriculum Specialist and course proposal created as Faculty
    When I am logged in as Curriculum Specialist
    And I perform a partial search for Course Proposals
    Then I should see both proposals listed in the search result
    And I can review the proposal created by Faculty


  Scenario: FCP1.3 Find my Course Proposal as Faculty
    Given I have a course proposal created as Faculty
    When I perform a full search for the Course Proposal
    Then I should see my proposal listed in the search result
    And I can review my proposal

  Scenario: FCP1.4 Find my Course Proposal as Curriculum Specialist
    Given I have a course proposal created as Curriculum Specialist
    When I perform a full search for the Course Proposal
    Then I should see my proposal listed in the search result
    And I can review my admin proposal
