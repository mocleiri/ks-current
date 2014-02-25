@wip
Feature: Create a Course Proposal as a Curriculum Specialist

Background:
Given I am logged in as Curriculum Specialist

Scenario:  CC.1 Create Course Proposal from blank
When I create a course proposal from blank
Then I should see a blank course proposal

Scenario: CC.2 Cancel Create a Course proposal process
When I create a course proposal
And I cancel create a course
Then I should see CM Home

Scenario: CC.3 Create a course proposal with only required fields
When I complete the required fields for save on the course proposal
Then I should see data in required fields for the course proposal
