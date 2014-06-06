@wip
Feature: Add/Edit/Delete/View Comments on a Proposal for different authors.

@draft
  Scenario: PC1.1 Add Comments - Faculty
    Given I have a basic course proposal created as Faculty
    When I add comments to the course proposal
    And I perform a full search for the course proposal
    Then I should see my comments on the course proposal

@draft
  Scenario: PC1.2 Add Comments - CS
    Given I have a basic course admin proposal created as Curriculum Specialist
    When I add comments to the course proposal
    And I perform a full search for the course proposal
    Then I should see my comments on the course proposal

@draft
  Scenario: PC1.3: Edit Comments - Faculty
    Given I have a basic course proposal with comments created as Faculty
    When I edit a comment
    And I perform a full search for the course proposal
    Then I should see my comments on the course proposal

@draft
  Scenario: PC1.4: Delete Comments - Faculty
    Given I have a basic course proposal with comments created as Faculty
    And I delete my comments
    And I perform a full search for the course proposal
    Then I should not see any comments on the course proposal

@draft
  Scenario: PC1.5: Undo Delete Comments - CS
    Given I have a basic course admin proposal with comments created as CS
    When I delete my comments
    And I undo delete of one comment
    And I perform a full search for the course proposal
    Then I should see the undeleted comment on the course admin proposal

@draft
  Scenario: PC1.6: View-Only Comments by another user
    Given I have a basic course proposal with comments created as Faculty with comments by CS
    When I perform a full search for the course proposal
    Then I should see my comments and CS comments on the course proposal
    And I should not have edit or delete options for CS comments

@draft
  Scenario: PC1.7: No option to add comments for user with view-only access to a proposal
    Given I have a basic course admin proposal with comments created as CS
    When I am logged in as Faculty
    When I perform a full search for the course proposal
    Then I should see CS comments on the course proposal
    And I should not have ability to add comments