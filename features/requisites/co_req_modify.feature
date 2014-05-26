@nightly @blue_team
Feature: CO.Modify CO Requisites

  Background:
    Given I am logged in as admin
    And I have navigated to the Student Eligibility & Prerequisite section for course "BSCI361" in the historic term

  #KSENROLL-7245
  Scenario: ELIG7.1.1 Test whether editing existing statements persists to the database
    When I want to edit the Student Eligibility & Prerequisite section
    And I edit node "A" by changing course to "BSCI329"
    And I persist the changes and return to the proposition
    Then the agenda page's text should match "Must have successfully completed BSCI329"

  #KSENROLL-7245
  Scenario: ELIG7.1.2 Test whether adding new statement to existing statements persists to the database
    When I want to edit the Student Eligibility & Prerequisite section
    And I add a courses statement after node "B" with courses "HIST110,ENGL313"
    And I persist the changes and return to the proposition
    Then the agenda page's text should match "all courses from,ENGL313,HIST110"

  #KSENROLL-7245
  Scenario: ELIG7.1.3 Test whether editing newly added statements persists to the database
    When I want to edit the Student Eligibility & Prerequisite section
    And I edit node "B" by adding course "BSCI103"
    And I persist the changes and return to the proposition
    Then the agenda page's text should match "minimum of 1 course from,BSCI103,BSCI339,BSCI399"

  #KSENROLL-7245
  Scenario: ELIG7.1.4 Test whether deleting statements persists to the database
    When I want to edit the Student Eligibility & Prerequisite section
    And I delete node "A" in the tree
    And I persist the changes and return to the proposition
    Then the agenda page's text should not match "BSCI106"
