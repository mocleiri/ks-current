@nightly @blue_team
Feature: SA.FE3-9 View Exam Offerings
  FE 3.9: As a Central Administrator I want to view exam offerings so that I know their state and properties

  Background:
    Given I am logged in as admin

  #FE3.9.EB1 (KSENROLL-9788)
  Scenario: Test that the CO table is shown when viewing the exam offerings and that it's state is draft
    When I view the Exam Offerings for a CO created from an existing CO with a standard final exam driven by Course Offering
    Then the Exam Offerings for Course Offering in the EO for CO table should be in a Draft state

  #FE3.9.EB2 (KSENROLL-9788)
  Scenario: Test that the AO table is shown when viewing the exam offerings and that it's state is draft
    When I view the Exam Offerings for a CO created from an existing CO with a standard final exam driven by Activity Offering
    Then the Exam Offerings for each Activity Offering in the EO for AO table should be in a Draft state

  #FE3.9.EB3 (KSENROLL-9788)
  Scenario: Test that the AO table is shown with all AOs when viewing the exam offerings and that it's state is draft
    When I view the Exam Offerings for a CO created from an existing CO with multiple AOs and a standard final exam driven by Activity Offering
    Then the Exam Offerings for each Activity Offering in the EO for AO table should be in a Draft state
