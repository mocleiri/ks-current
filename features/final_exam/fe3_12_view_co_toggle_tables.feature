@nightly @blue_team
Feature: SA.FE3-12 View Exam Offerings table rendered depending on FE Driver
  FE 3-12 As a Central Administrator I want existing exam offerings to be Unscheduled and the records displayed
  if the exam driver changes changes to Course Offering or Activity Offering

  Background:
    Given I am logged in as admin

  #FE3.12.EB1(KSENROLL-10198)
  Scenario: Test whether the View EO table is by Course Offering and that there is only one Exam Offering
    Given that the SOC state is prior to Published
    When I view the Exam Offerings for a CO with a standard final exam driven by Course Offering
    Then there should only be one EO in the Exam Offerings for Course Offering table
    And I view the Exam Offerings after changing the Final Exam Driver to Activity Offering
    Then there should be one EO for each AO of the course in the Exam Offering for Activity Offering table
    And I view the Exam Offerings after changing the Final Exam Driver to Course Offering
    Then there should only be one EO in the Exam Offerings for Course Offering table
