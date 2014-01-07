@nightly @blue_team
Feature: SA.FE3-7 View Exam Offerings only when the Exam Setting is set to Standard
  FE 3.7 As a Central Admin I want existing exam offerings to be deleted if the CO setting changes to "No final Exam"
  or "Alternative final assessment" after bulk creation but before the SoC is published so that exam offerings match
  CO exam settings

  Background:
    Given I am logged in as admin

  #FE3.7.EB1 (KSENROLL-9791)
  Scenario: Test that EOs are deleted when the exam setting for a course changes from Standard Final Exam to No final Exam or Alternative final assessment
    When I view the Exam Offerings for a CO in an Open SOC with a standard final exam driven by Activity Offering
    Then the Exam Offerings for each Activity Offering in the EO for AO table should be in a Draft state
    And I view the Exam Offerings after updating the Final Exam indicator to No final Exam
    Then there should be no View Exam Offering option present