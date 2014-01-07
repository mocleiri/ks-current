@nightly @blue_team
Feature: SA.FE3-1 Perform Simple Rollover to test creation of exam offerings for specific terms
  FE 3.1 As a Central Administrator I want to create exam offerings in bulk at rollover based so that exam offerings
  will be created at an appropriate stage in my institution's scheduling process

  Background:
    Given I am logged in as admin

  #FE3.1.EB1 (KSENROLL-9533)
  Scenario: Test whether setting up exam offerings for COs and then performing a rollover into a term with no exam period raises a popup to add exam period to target term
    Given I create an Academic Calendar and add an official term
    When I create multiple Course Offerings each with a different Exam Driver in the new term
    And I rollover the term to a new academic term that has no exam period
    Then I expect a popup to appear with a displayed warning stating "Continue without Exams?"

  #FE3.1.EB2 (KSENROLL-9533)
  Scenario: Test whether setting up exam offerings for COs and then performing a rollover retains the exam information
    Given I create an Academic Calendar and add an official term
    When I create multiple Course Offerings each with a different Exam Driver in the new term
    And I rollover the term to a new academic term that has an exam period
    Then all the Final Exam and Exam Driver data for the COs should be retained after the rollover is completed and Exam Offerings should be created in a state of Draft
