@wip

Feature: BT.Course Detail Page

  Background:
    Given I am logged in as a Student

  Scenario: CS 13.1 Verify the content of a course on the Course Details Page
    When I search for a course on course search page
    And I view the details of the course on the Course Details page
    Then I should be able to view all the required information on the Course Details page
