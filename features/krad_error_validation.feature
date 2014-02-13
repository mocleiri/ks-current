@wip
Feature: Course Proposal should message the user for incorrect or missing information

  Background:
    Given I am logged in as Fred
    And I create a course proposal in krad

  Scenario: Verify course proposal for krad cannot be saved if required fields are empty
    Given I set the proposal title and course title to blank
    When I save the course proposal
    Then I should see the error message for proposal title
    And I should see the error message for course title

  Scenario: Verify course propsoal cannot be submitted without all required fields filled in
    Given I have a course proposal with a missing required field
    When I submit the course proposal on the review proposal page
    Then I should see an error on the review proposal page

   Scenario: Character limits should display errors
     When
     Then








