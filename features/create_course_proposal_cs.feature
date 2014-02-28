@nightly
Feature: GT.Create a Course Proposal as a Curriculum Specialist

Background:
Given I am logged in as Curriculum Specialist

Scenario:  CC.1.1 Create Course Proposal from blank
When I create a course proposal from blank
Then I should see a blank course proposal

Scenario: CC.1.2 Cancel Create a Course proposal process
When I create a course proposal
And I cancel create a course
Then I should see CM Home

Scenario: CC.3.1 Create a course proposal with only required fields
When I complete the required fields on the course proposal and save
Then I should see data in required fields for the course proposal


Scenario: CC4.1 Create an admin course proposal with only required for save fields
When I complete the required fields for save on the course admin proposal
Then I should see data in required fields for the course admin proposal


Scenario: CC4.2 Create course without required for save fields
When I am on the course information page and I click save progress without entering any values
Then I should receive an error message about the proposal title and course title being required for save

