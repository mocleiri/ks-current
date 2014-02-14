@nightly @blue_team
Feature: CO.FE7-4 Validation Message should show if Number of Exam Days is less than Matrix Days
  FE 7.4: As a Central Administrator I want the dates of a new exam period to be validated against the examination
  matrix when I create the exam period so that I do not create an exam period with fewer days than the matrix

  Background:
    Given I am logged in as admin
    And I create an Academic Calendar

  #FE7.4.EB1 (KSENROLL-9794)
  Scenario: Test that an error message is shown when the FE Period is less than the FE Matrix days
    When I create a Fall Term Exam Period with 2 fewer days than the number of Final Exam Matrix days
    Then an error in the Final Exam section is displayed stating "days in the exam period are less than the number of exam days"

  #FE7.4.EB2 (KSENROLL-9794)
  Scenario: Test the exam days validation is accurate if Saturday and Sunday are included in exam period
    Given I create a Fall Term Exam Period with 2 fewer days than the number of Final Exam Matrix days
    Then an error in the Final Exam section is displayed stating "days in the exam period are less than the number of exam days"
    When I deselect Exclude Saturday and Exclude Sunday for the Exam Period
    Then no error in the Final Exam section is displayed when I save the data
