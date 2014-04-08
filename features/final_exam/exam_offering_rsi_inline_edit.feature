@draft @yellow_team
Feature: CO.Exam Offering RSI Inline Edit

  FE 6.3: As a Central Administrator I want to update the requested scheduling information for AO driven exam offerings with a
  scheduling state of Matrix Error so that AOs with non standard times will have exam offerings with RSI


  Background:
    Given I am logged in as admin

  Scenario: FE 6.3.1 Test inline edit for a CO-driven Exam Offering
    Given that the Course Offering has a CO-driven final exam that is marked to use the matrix and exists on the Final Exam Matrix for the term
    And I create a Course Offering from catalog in a term with a defined final exam period that uses the FE matrix
    When I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering should be populated
    And I can update all fields on the eo rsi

