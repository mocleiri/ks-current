@draft
Feature: GT.Find a Course Proposal


  Scenario: FCP1.1 Find a Course Proposal select CS proposal - Faculty
    Given I have a admin course proposal created as CS and course proposal created as Faculty
    When I am logged in as Fred
    And I perform a partial search for Course Proposals
    Then I should see all results matching my search criteria
    And I select the Faculty proposal from the results
    And I should see the Review Proposal page for the Faculty proposal


  Scenario: FCP1.2 Find a Course Proposal select faculty proposal - CS
    Given I have a admin course proposal created as CS and course proposal created as Faculty
    When I am logged in as Curriculum Specialist
    And I perform a partial search for Course Proposals
    Then I should see all results matching my search criteria
    And I select the CS proposal from the results
    And I should see the Review Proposal page for the CS proposal


  Scenario: FCP1.3 Find my Course Proposal - Faculty
    Given I have a course proposal created as Faculty
    When I perform a full search for the Course Proposals
    Then I should see results matching my search criteria
    And I select my proposal from the results
    And I should see the Review Proposal page for that proposal


  Scenario: FCP1.4 Find my Course Proposal - CS
    Given I have a course proposal created as CS
    When I perform a full search for the Course Proposals
    Then I should see results matching my search criteria
    And I select my proposal from the results
    And I should see the Review Proposal page for that proposal
