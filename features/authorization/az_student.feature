@nightly @yellow_team
Feature: CO.Student Authorization

  Background:
    Given I am logged in as admin

  Scenario: Student does not have access to Manage CO pages
    Given there is direct url access to manage and search a course offering
    And I am logged in as a Student
    When I navigate to manage course offerings and I do not have access
    And I do not have direct url access to the manage course offerings search page
    #And I do not have direct url access to the course offering results page
    #And I do not have direct url access to the course offering single course results page

  Scenario: Student does not have access to Copy Edit and Create pages
    Given there is direct url access to edit a course offering and activity offering
    And I am logged in as a Student
    When I navigate to create course offerings and I do not have access
    And I do not have direct url access to the create course offerings page
    And I do not have direct url access to the edit course offerings page
    And I do not have direct url access to the edit activity offering page
