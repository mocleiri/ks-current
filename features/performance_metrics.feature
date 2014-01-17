@performance
Feature: Performance.Metrics

  Background:
    Given I am logged in as a Schedule Coordinator

  Scenario: Login
    When I log in as a Schedule Coordinator
    Then the transaction takes less than "3" seconds

  Scenario: Academic Calendar Search and Edit
    When I search for an Academic Calendar
    Then the transaction takes less than "3" seconds
    And  I edit the Academic Calendar
    Then the transaction takes less than "3" seconds
    And  I update a field and save the Academic Calendar
    Then the transaction takes less than "3" seconds

  Scenario: Academic Calendar Create
    When I start a blank Academic Calendar
    Then the transaction takes less than "3" seconds
    And I create a new Academic Calendar
    Then the transaction takes less than "3" seconds

  Scenario: Holiday Calendar Search and Edit
    When I search for an Holiday Calendar
    Then the transaction takes less than "3" seconds
    And  I edit the Holiday Calendar
    Then the transaction takes less than "3" seconds
    And  I update a field and save the Holiday Calendar
    Then the transaction takes less than "3" seconds

  Scenario: Holiday Calendar Create
    When I start a blank Holiday Calendar
    And I create a new Holiday Calendar
    Then the transaction takes less than "3" seconds

  Scenario: Search Course Offering by Subject 100+
    When I search for a course by the "ENGL" subject code
    Then the transaction takes less than "3" seconds

  Scenario: Search Course Offering by Subject 50-100
    When I search for a course by the "BSCI" subject code
    Then the transaction takes less than "3" seconds

  Scenario: Search Course Offering by Course and edit
    When I search for a course by course code
    Then the transaction takes less than "3" seconds
    And I edit the course offering for performance
    Then the transaction takes less than "3" seconds
    And I save the course change
    Then the transaction takes less than "3" seconds

  Scenario: Create Course Offering
    When I create a basic course offering
    Then the transaction takes less than "3" seconds

  Scenario: Large Copy Course Offering
    When I click copy for a large course offering
    Then the transaction takes less than "3" seconds
    And I copy a course offering
    Then the transaction takes less than "3" seconds

  Scenario: Large Delete Course Offering
    When I search for a large course by course code to delete
    And I delete the course offering
    Then the transaction takes less than "3" seconds

  Scenario: Large Edit Activity Offering
    When I edit a large Activity Offering for performance
    Then the transaction takes less than "3" seconds

  Scenario: Large Copy Activity Offering
    When I copy a large Activity Offering for performance
    Then the transaction takes less than "3" seconds

  Scenario: Large Add Scheduling Information
    When I add Scheduling Information to a large activity offering and save
    Then the transaction takes less than "3" seconds

#begin Medium
  Scenario: Medium Copy Course Offering
    When I click copy for a medium course offering
    Then the transaction takes less than "3" seconds
    And I copy a course offering
    Then the transaction takes less than "3" seconds

  Scenario: Medium Delete Course Offering
    When I search for a medium course by course code to delete
    And I delete the course offering
    Then the transaction takes less than "3" seconds

  Scenario: Medium Edit Activity Offering
    When I edit a medium Activity Offering for performance
    Then the transaction takes less than "3" seconds

  Scenario: Medium Copy Activity Offering
    When I copy a medium Activity Offering for performance
    Then the transaction takes less than "3" seconds

  Scenario: Medium Add Scheduling Information
    When I add Scheduling Information to a medium activity offering and save
    Then the transaction takes less than "3" seconds
#end Medium

#begin Small
  Scenario: Small Copy Course Offering
    When I click copy for a small course offering
    Then the transaction takes less than "3" seconds
    And I copy a course offering
    Then the transaction takes less than "3" seconds

  Scenario: Small Delete Course Offering
    When I search for a small course by course code to delete
    And I delete the course offering
    Then the transaction takes less than "3" seconds

  Scenario: Small Edit Activity Offering
    When I edit a small Activity Offering for performance
    Then the transaction takes less than "3" seconds

  Scenario: Small Copy Activity Offering
    When I copy a small Activity Offering for performance
    Then the transaction takes less than "3" seconds

  Scenario: Small Add Scheduling Information
    When I add Scheduling Information to a small activity offering and save
    Then the transaction takes less than "3" seconds
#end Small

  Scenario: Manage SOC
    When I search for a SOC
    Then the transaction takes less than "3" seconds

  Scenario: KRMS Requisites Manage Add Submit
    When I manage an AO's prerequisites
    Then the transaction takes less than "3" seconds
    And I add a rule to the Prerequisite section
    Then the transaction takes less than "3" seconds
    And then I submit the rule changes
    Then the transaction takes less than "3" seconds
    And I suppress a new rule change
    Then the transaction takes less than "3" seconds
    And I revert the new rule change
    Then the transaction takes less than "3" seconds

  Scenario: Display Schedule of Classes
    When I search for a scheduled class
    Then the transaction takes less than "3" seconds
    When I view the detailed schedule of classes
    Then the transaction takes less than "3" seconds

  Scenario: Display Final Exam Matrix
    When I search for a Final Exam
    Then the transaction takes less than "3" seconds
    And when I edit the matrix
    Then the transaction takes less than "3" seconds
    And when I add a standard rule
    Then the transaction takes less than "3" seconds
    And when I submit the rule change
    Then the transaction takes less than "3" seconds
