@nightly @blue_team
Feature: SA.FE2-2 Create Exam from AO or CO
  FE 2.2: As a Central Administrator I want to indicate for a course offering what entities determine the creation
  of an exam offering so that the exam offering can be created according to the intended scenario or pattern

  Background:
    Given I am logged in as admin

  #FE2.2.EB1 (KSENROLL-9243)
  Scenario: Test whether the Final Exam Driver dropdown is only available for Standard FE
    Given that the catalog version of the course is set to have a standard final exam
    When I create a Course Offering from catalog in a term with a final exam period
    Then the option to specify a Final Exam Driver should only be available for a course offering with a Standard Final Exam option selected

  #FE2.2.EB2 (KSENROLL-9243)
  Scenario: Test whether the Final Exam data for a course changes depending on the chosen Final Exam indicator when there will be an exam
    Given that the catalog version of the course is set to not have a standard final exam
    When I create a Course Offering from catalog in a term with a final exam period
    And I change the Final Exam indicator from Alternate Final Assessment to Standard Final Exam
    Then the Final Exam Driver value should reflect the value selected in the Final Exam Driver field dropdown
    And the Final Exam Driver Activity field should exist and be populated with the first activity type of the format offering