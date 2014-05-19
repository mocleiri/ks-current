@wipt
Feature: BT.General Education Requirement
  Background:
    Given I am logged in as a Student

  Scenario:CS 36.1:Verify the General Education Requirement designation for a course
    When I search for a course in the Course Search Page
    Then the courses should be displayed with the requirement in the General Ed column


  Scenario:CS 36.2 Verify that General Education requirement facet can be used to filter search results
    When I search for  courses in the Course Search Page
    And I select a requirement in the General Education Requirement facet
    Then I should be able to view the courses meeting the General Education Requirement
